#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1
ENVIRONMENT=$2
if [ -z "$ENVIRONMENT" ]; then
  ENVIRONMENT="integration"
fi
figlet "Docker Service"
echo
echo "Using environment: ${ENVIRONMENT}"
WORKING_DIRECTORY="$(dirname "$0")"
DOCKER_COMPOSER_COMMAND="docker-compose --file src/integration/scripts/dm-docker-${ENVIRONMENT}/docker-compose.yml"
# - S T A R T / S T O P
start() {
  cd "${WORKING_DIRECTORY}" || exit 1
  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
  $RUN_COMMAND up &
}
stop() {
  cd "${WORKING_DIRECTORY}" || exit 1
  RUN_COMMAND="${DOCKER_COMPOSER_COMMAND}"
  $RUN_COMMAND down
}

case $COMMAND in
'start')
  start
  ;;
'stop')
  stop
  ;;
*)
  echo "Usage: $0 { start | stop }"
  echo
  exit 1
  ;;
esac
exit 0
