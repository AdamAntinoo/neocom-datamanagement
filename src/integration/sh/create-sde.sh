#!/bin/bash
figlet CreateSDE
echo "> Database creation started."
mv ./downloads/*.csv .

echo ">> Remove current sde database..."
rm sde.db
echo ">> Create new sde database..."
/usr/bin/sqlite3 sde.db < db-ddl-0.20.0.ddl
cp sde.db ../resources/
echo "> Database creation completed."
rm -rf *.csv
