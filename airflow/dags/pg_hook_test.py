
default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2023, 5, 1, tzinfo=pendulum.timezone("Asia/Seoul")),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 0,
    'retry_delay': timedelta(minutes=1)
}

def select():
    two_days_ago = datetime.now() - timedelta(days=2)
    pg_hook = PostgresHook(postgres_conn_id='my_postgres_connection')
    conn = pg_hook.get_conn()
    cursor = conn.cursor()
    cursor.execute(
        f'''SELECT sat_img_sn
                    , sat_img_nm
                    , sat_cd
                    , sat_img_lvl
                    , sat_type
                    , sat_obsrvn_bgng_dt
                    , sa_obsrvn_end_dt
                    , sat_obsrvn_rgn
                    , sat_img_proj
                    , sat_obsrvn_cycl
                    , sat_img_res
                    , sat_latlon
                    , sat_img_geom_info
                    , snsr
                    , frmt
                    , algo
                    , fire_sz
                    , file_uld_sn
                    , creatr_id
                    , crt_dt
                    , mdfr_id
                    , mdfcn_dt
                    , use_yn 
                 FROM tb_sat_img 
                WHERE crt_dt > '{two_days_ago}'
                '''
    )
    return cursor.fetchall()

with DAG(
        'tile',
        default_args=default_args,
        description='tile',
        schedule_interval='*/10 * * * *',  # 10분마다
        catchup=False
) as dag:

