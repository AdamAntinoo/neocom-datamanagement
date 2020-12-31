#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1

WORKING_DIRECTORY='/home/adam/Development/NeoCom/neocom-datamanagement/NeoCom.DataManagement'
DOCKER_COMMAND="docker-compose --file src/test/resources/docker/docker-compose.yml "

# - S T A R T
start() {
  cd ${WORKING_DIRECTORY}
  $DOCKER_COMMAND up &
}
stop() {
  cd ${WORKING_DIRECTORY}
  $DOCKER_COMMAND down &
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
