from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
from datetime import datetime, timedelta
from airflow.utils.dates import days_ago
from decimal import Decimal

default_args = {
    'owner': 'airflow',
    'start_date': datetime(2023, 6, 20),
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

def query_postgres(**kwargs):
    pg_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
    sql = "SELECT * FROM tb_sat_img;"
    connection = pg_hook.get_conn()
    cursor = connection.cursor()

    cursor.execute(sql)
    sources = cursor.fetchall()
    keys = cursor.description
    result = []
    for source in sources:
        row = {}
        for i, value in enumerate(source):
            if isinstance(value, Decimal):
                value = int(value)
            elif isinstance(value, datetime):
                value = value.isoformat()
            row[keys[i][0]] = value
        result.append(row)

    kwargs['task_instance'].xcom_push(key='query_result', value=result)


with DAG(
        'xcom_test2',
        default_args=default_args,
        description='A simple tutorial DAG',
        schedule_interval=None,
        start_date=days_ago(2),
        tags=['example'],
) as dag:
    query_postgres_operator = PythonOperator(
        task_id='query_postgres',
        python_callable=query_postgres,
        provide_context=True,
    )
