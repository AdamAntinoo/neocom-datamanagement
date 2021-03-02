#!/bin/bash
# - PARAMETERS & CONSTANTS
ENVIRONMENT=$1

WORKING_DIRECTORY="$(dirname "$0")"

echo "./docker-service.sh stop ${ENVIRONMENT}"
./docker-service.sh stop ${ENVIRONMENT}
echo "./docker-service.sh start ${ENVIRONMENT}"
./docker-service.sh start ${ENVIRONMENT}
