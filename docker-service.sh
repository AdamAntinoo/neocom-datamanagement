#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1
ENVIRONMENT=$2
if [ -z "$ENVIRONMENT" ]
then
    ENVIRONMENT="integration"
fi
figlet "Docker Service"
echo
echo "Using environment: ${ENVIRONMENT}"
#APPLICATION_JAR_NAME="neocom-infinity-backend-acceptance"
WORKING_DIRECTORY="$(dirname "$0")"
DOCKER_DIRECTORY="${WORKING_DIRECTORY}/src/integration/resources/docker"
DOCKER_COMPOSER_COMMAND="docker-compose --file ${DOCKER_DIRECTORY}/docker-compose.yml"

# - G E N E R A T E   C O N T A I N E R
#generateContainer() {
#  cd "${WORKING_DIRECTORY}" || exit 1;
#  rm -v "${DOCKER_DIRECTORY}"/*.jar
#  ./gradlew clean bootJar
#  cp ./build/libs/*.jar "$DOCKER_DIRECTORY"
#  cp ./build/resources/main/app-banner.txt "$DOCKER_DIRECTORY"
#  cp ./build/resources/main/app-banner.txt ./build/libs/app-banner.txt
#  # Copy all properties sets into the container. Configuration will select the right set.
##  cp -r "./build/resources/main/properties" "${DOCKER_DIRECTORY}/properties"
#  cp -r "./build/resources/main/properties-acceptance" "${DOCKER_DIRECTORY}/properties-acceptance"
#  cd "$DOCKER_DIRECTORY" || exit 1;
#  mv -v NeoCom.Infinity.Backend*.jar ${APPLICATION_JAR_NAME}".jar"
#  echo "${DOCKER_DIRECTORY}/Dockerfile"
#  docker build -t neocom/neocom-infinity.backend .
#}
# - S T A R T / S T O P
start() {
  cd "${WORKING_DIRECTORY}" || exit 1;
  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
  $RUN_COMMAND up &
}
stop() {
  cd "${WORKING_DIRECTORY}" || exit 1;
  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
  $RUN_COMMAND down
}
#recycle() {
#  # - STOP
#  cd "${WORKING_DIRECTORY}" || exit 1;
#  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
#  $RUN_COMMAND down
#  generateContainer
#  cd "${WORKING_DIRECTORY}" || exit 1;
#  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
#  $RUN_COMMAND up &
#}

case $COMMAND in
#'generate')
#  generateContainer
#  ;;
'start')
  start
  ;;
'stop')
  stop
  ;;
#'recycle')
#  recycle
#  ;;
*)
  echo "Usage: $0 { start | stop }"
  echo
  exit 1
  ;;
esac
exit 0
