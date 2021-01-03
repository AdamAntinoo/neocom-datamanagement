#!/bin/bash
figlet InstallSDE
./create-sde.sh

echo "> Moving database to destinations."
mv sde.db ../resources/
rm -rf *.csv
echo "> SDE db installation completed."
