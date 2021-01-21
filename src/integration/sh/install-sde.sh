#!/bin/bash
figlet InstallSDE

cp downloads/*.csv .
./create-sde.sh

echo "> Moving database to destinations."
mv sde.db ../resources/
rm -rf *.csv
echo "> SDE db installation completed."
