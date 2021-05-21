#!/bin/bash
BASE_DIR="templates"
WORK_DIR="generated-configs"

service_name=labhunter

replicas=1

datasource_jdbcUrl="\"jdbc:postgresql:\/\/172.17.0.6:5432\/labhunter\""
datasource_username="postgresadmin"
hh_api_url="https:\/\/api.hh.ru"
jwt_header="Authorization"
jwt_secret="RusskiyeVpered"
jwt_expiration=604800

yamls=(labhunter-ingress.yaml labhunter-config-map.yaml labhunter-deployment.yaml labhunter-db-passwords.yaml)

mkdir -p "${WORK_DIR}"

for yaml in "${yamls[@]}"; do
    cp "${BASE_DIR}"/"${yaml}" "${WORK_DIR}"/
    ### add service name to configs ###
    sed -i '' -e "s/{{service.name}}/${service_name}/g" "${WORK_DIR}"/"${yaml}"
    ### autonum-deployment scripts ###
    sed -i '' -e "s/{{replicas}}/${replicas}/g" "${WORK_DIR}"/"${yaml}"
    ### db scripts ###
    sed -i '' -e "s/{{datasource.jdbcUrl}}/${datasource_jdbcUrl}/g" \
    -e "s/{{datasource.username}}/${datasource_username}/g" "${WORK_DIR}"/"${yaml}"
    ### sberflake-сonfig-map scripts ###
    sed -i '' -e "s/{{hh.api.url}}/${hh_api_url}/g" \
    -e "s/{{jwt.header}}/${jwt_header}/g" \
    -e "s/{{jwt.secret}}/${jwt_secret}/g" \
    -e "s/{{jwt.expiration}}/${jwt_expiration}/g" "${WORK_DIR}"/"${yaml}"
  done