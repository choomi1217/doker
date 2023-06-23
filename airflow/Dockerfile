FROM apache/airflow:slim-2.6.1rc3-python3.10

USER root
#RUN apt-get update && apt-get install -y libgdal-dev \ wget \ bzip2 && rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y libgdal-dev  wget  bzip2 && rm -rf /var/lib/apt/lists/*

USER airflow
RUN pip install psycopg2-binary
RUN pip install apache-airflow-providers-postgres
RUN pip install 'apache-airflow-providers-docker==2.1.0'
RUN pip install pandas
#RUN pip install rasterio > 실패
#RUN pip install GDAL > 실패

# Conda를 설치합니다.
# RUN wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O ~/miniconda.sh && \
#    bash ~/miniconda.sh -b -p $HOME/miniconda && \
#    rm ~/miniconda.sh

# Conda를 PATH에 추가합니다.
# ENV PATH /root/miniconda/bin:$PATH

# 추가적으로 필요한 Conda 패키지를 설치합니다.
# RUN conda install -y your-package=your-version


CMD ["airflow", "standalone"]

