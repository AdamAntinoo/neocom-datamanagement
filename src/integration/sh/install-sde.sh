#!/bin/bash
figlet InstallSDE
./update-sde.sh
./create-sde.sh

echo "> Moving database to destinations."
mv sde.db ../resources/
echo "> SDE db installation completed."
