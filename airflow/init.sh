#!/bin/bash
airflow db init
airflow users create --username admin --password 1234 --firstname Jerry --lastname Park --role Admin --email jerry@innopam.com
airflow connections add 'test_api' --conn-type 'http' --conn-host 'http://192.168.10.206' -conn-port '30001'
