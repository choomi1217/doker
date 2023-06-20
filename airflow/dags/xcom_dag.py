from airflow import DAG
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.operators.python import PythonOperator
from airflow.utils.dates import days_ago
from decimal import Decimal


def fetch_data_from_postgres(**context):
    postgres_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
    sql = "SELECT * FROM tb_sat_img"
    conn = postgres_hook.get_conn()
    cursor = conn.cursor()
    cursor.execute(sql)
    results = cursor.fetchall()
    column_names = [desc[0] for desc in cursor.description]

    dict_results = []
    for row in results:
        row_dict = {}
    for desc, value in zip(column_names, row):
        if isinstance(value, Decimal):
            value = float(value)
            row_dict[desc] = value
            dict_results.append(row_dict)

    context['task_instance'].xcom_push('my_data', dict_results)


def use_data_in_next_task(**context):
    data = context['task_instance'].xcom_pull(task_ids='fetch_data_from_postgres', key='my_data')
    print(data)

default_args = {
    'owner': 'airflow',
}

with DAG(
        'postgres_to_xcom_dag',
        default_args=default_args,
        description='A simple tutorial DAG',
        schedule_interval=None,
        start_date=days_ago(2),
        tags=['example'],
) as dag:
    fetch_data_from_postgres = PythonOperator(
        task_id='fetch_data_from_postgres',
        python_callable=fetch_data_from_postgres,
        provide_context=True,
    )

    use_data_in_next_task = PythonOperator(
        task_id='use_data_in_next_task',
        python_callable=use_data_in_next_task,
        provide_context=True,
    )

    fetch_data_from_postgres >> use_data_in_next_task
