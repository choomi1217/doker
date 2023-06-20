import os
from datetime import timedelta, datetime

from airflow.models import DAG, TaskInstance, BaseOperator
from datetime import datetime
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.postgres.operators.postgres import PostgresOperator
from airflow.operators.docker_operator import DockerOperator
import pendulum
from decimal import Decimal
from osgeo import gdal
import os

#####
#
# 1. tb_sat_img 테이블에서 crt_dt가 오늘로부터 이틀전인 데이터를 모두 찾음
# 2. file_uld_sn으로 TB_file_uld를 조회
# 3. 테이블의 file_path 속 tiff 파일들을 GDAL로 타일링
# tif는 upload에 타일링 뜨면 tiles에
# tiles/{file_nm}/level
#####

postgress_conn_id = 'my_postgres_connection'
two_days_ago = datetime.now() - timedelta(days=2)

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2023, 5, 1, tzinfo=pendulum.timezone("Asia/Seoul")),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 0,
    'retry_delay': timedelta(minutes=1)
}

with DAG(
        'tile',
        default_args=default_args,
        description='tile',
        schedule_interval='*/10 * * * *',  # 10분마다
        catchup=False
) as dag:
    # 1 : tb_sat_img 테이블에서 crt_dt가 오늘로부터 이틀전인 데이터를 모두 찾음
    def _find_data_two_days_ago(**context):
        pg_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
        connection = pg_hook.get_conn()
        cursor = connection.cursor()

        sql = f'''SELECT file_uld_sn as file_uld_sn
                  FROM tb_sat_img 
                  WHERE crt_dt > '{two_days_ago}'
                  GROUP BY file_uld_sn
               '''
        print('_find_data_two_days_ago sql : ' + sql)
        cursor.execute(sql)
        sources = cursor.fetchall()

        result = []

        for source in sources:
            for i, value in enumerate(source):
                if isinstance(value, Decimal):
                    value = int(value)
                    print('_find_data_two_days_ago value : ' + str(value))
                result.append(value)

        context['task_instance'].xcom_push(key='two_days_ago_data', value=result)

    find_data_two_days_ago = PythonOperator(
        task_id='find_data_two_days_ago',
        python_callable=_find_data_two_days_ago,
        provide_context=True,
        dag=dag
    )

    # 2 : 1 에서 조회한 file_uld_sn으로 tb_file_uld를 조회
    def _get_file_uld_sn(**context):
        data = context['task_instance'].xcom_pull(task_ids='find_data_two_days_ago', key='two_days_ago_data')

        pg_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
        connection = pg_hook.get_conn()
        cursor = connection.cursor()

        result = []
        for file_uld_sn in data:
            sql = f'''SELECT file_path_nm
                       FROM tb_file_uld
                       WHERE file_uld_sn = {file_uld_sn}'''
            print('_get_file_uld_sn sql : ' + sql)
            cursor.execute(sql)
            sources = cursor.fetchall()
            result.append(sources)

        context['task_instance'].xcom_push(key='get_file_uld_sn', value=result)


    get_file_uld_sn = PythonOperator(
        task_id='get_file_uld_sn',
        python_callable=_get_file_uld_sn,
        provide_context=True,
        dag=dag
    )


    # 3. get_file_uld_sn 에서 조회한 file_path 속 tiff 파일들을 GDAL로 타일링
    def _make_tile(**context):
        # 타일 크기
        tile_size_x = 256
        tile_size_y = 256
        file_uld_list = context['task_instance'].xcom_pull(task_ids='get_file_uld_sn')
        for row in file_uld_list:
            dataset = gdal.Open(row)
            if dataset is not None:
                width = dataset.RasterXSize
                height = dataset.RasterYSize
                for i in range(0, width, tile_size_x):
                    for j in range(0, height, tile_size_y):
                        tile_filename = "tile_{}_{}.tif".format(int(i/tile_size_x), int(j/tile_size_y))
                        tile_filepath = os.path.join('../share_folder/tiles', tile_filename)
                        gdal.Translate(tile_filepath, dataset, srcWin=[i, j, tile_size_x, tile_size_y])
                dataset = None
            else:
                print("Could not open the image file")




    make_tile = PythonOperator(
        task_id='get_file_path_list',
        python_callable= _make_tile,
        provide_context=True,
        dag=dag
    )


    # def make_tile(i, **context):
    #     file_path_list = context['task_instance'].xcom_pull(task_ids='get_file_path_list')
    #     container_directory = './tiles'
    #
    #     docker_task = DockerOperator(
    #         task_id=f'make_tile_from_tiff_{i}',
    #         image='osgeo/gdal',
    #         command=f'python /usr/bin/gdal2tiles.py -v --processes=4 -z 0-13 {container_directory}/{i} {container_directory}/output',
    #         api_version='auto',
    #         auto_remove=True,
    #         volumes=[f'/Users/ymcho/dev/doker/airflow/tiles:{container_directory}'],
    #         docker_url="unix://var/run/docker.sock",
    #         network_mode="bridge"
    #     )
    #
    #     docker_task.execute(context=context)
    #     tile_tasks = []
    #     for i in range(10):
    #         make_tile_task = PythonOperator(
    #             task_id=f'make_tile_{i}',
    #             python_callable=make_tile,
    #             op_kwargs={'i': i},
    #             provide_context=True,
    #             dag=dag
    #         )
    #         tile_tasks.append(make_tile_task)

    find_data_two_days_ago >> get_file_uld_sn >> get_file_path_list >> make_tile_task
