#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1

SIMULATION_NAME=$2
SERVICE_PORT=$3
ADMIN_PORT=$(( $SERVICE_PORT + 100 ))

WORKING_DIRECTORY=`pwd`
WORKING_DIRECTORY="${WORKING_DIRECTORY}/src/integration/resources"
APISIMULATOR_COMMAND="${WORKING_DIRECTORY}/apisimulator-http-1.6.1/bin/apisimulator"
APISUMULATOR_ADMIN_PORT=" -admin_port ${ADMIN_PORT} "
APISIMULATOR_OPTIONS=" -p ${SERVICE_PORT} "
APISIMULATOR_SIMULATION="${WORKING_DIRECTORY}/$SIMULATION_NAME"

export APISIMULATOR_JAVA='/usr/lib/jvm/java-1.11.0-openjdk-amd64'

# - S T A R T
start() {
  cd ${WORKING_DIRECTORY}
  echo ">> Starting api simulator with: $APISIMULATOR_SIMULATION"
  echo ">>> Service port: $SERVICE_PORT"
  echo ">>> Administration port: $ADMIN_PORT"
  echo ">>> Simulation: $SIMULATION_NAME"
  ${APISIMULATOR_COMMAND} start $APISIMULATOR_SIMULATION $APISIMULATOR_OPTIONS $APISUMULATOR_ADMIN_PORT &
}
stop() {
  cd ${WORKING_DIRECTORY}
  echo "Stopping api simulator..."
  $APISIMULATOR_COMMAND stop $APISIMULATOR_SIMULATION $APISUMULATOR_ADMIN_PORT
}

case $COMMAND in
'start')
  start
  ;;
'stop')
  stop
  ;;
'restart')
  stop
  wait 1;
  start
  ;;
*)
  echo "Usage: $0 { start | stop | restart }"
  echo
  exit 1
  ;;
esac
exit 0
