import pandas as pd
from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.utils.dates import days_ago
from datetime import datetime, timedelta
import decimal


default_args = {
    'owner': 'airflow',
    'start_date': datetime(2023, 6, 20),
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

def query_postgres(**kwargs):
    pg_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
    sql = "SELECT * FROM tb_sat_img;"
    df = pg_hook.get_pandas_df(sql)
    df = df.applymap(
        lambda x: x.isoformat() if isinstance(x, pd.Timestamp)
        else int(x) if isinstance(x, (decimal.Decimal, float))
        else x
    )

    result = df.to_dict('records')

    kwargs['task_instance'].xcom_push(key='query_result', value=result)


with DAG(
        'pandas_xcom_test',
        default_args=default_args,
        description='A simple tutorial DAG',
        schedule_interval=None,
        start_date=days_ago(2),
        tags=['example'],
) as dag:
    query_postgres_operator = PythonOperator(
        task_id='pandas_xcom_test',
        python_callable=query_postgres,
        provide_context=True,
    )
