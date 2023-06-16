FROM apache/airflow:slim-2.6.1rc3-python3.10

USER root
RUN apt-get update && apt-get install -y libgdal-dev && rm -rf /var/lib/apt/lists/*

USER airflow
RUN pip install psycopg2-binary
RUN pip install apache-airflow-providers-postgres
RUN pip install 'apache-airflow-providers-docker==2.1.0'
CMD ["airflow", "standalone"]
