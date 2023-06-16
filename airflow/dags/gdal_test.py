from osgeo import gdal

def my_gdal_task(**kwargs):
    # GDAL 코드를 여기에 작성합니다.
    pass

with DAG('my_dag', default_args=default_args, schedule_interval='@daily') as dag:
    gdal_task = PythonOperator(
        task_id='my_gdal_task',
        python_callable=my_gdal_task,
        provide_context=True,
    )
