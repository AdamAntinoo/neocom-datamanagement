#!/bin/bash
figlet CreateSDE
echo "> Database creation started."
cp ./downloads/*.csv .

echo ">> Remove current sde database..."
rm -rf sde.db
echo ">> Create new sde database..."
/usr/bin/sqlite3 sde.db < db-ddl-0.20.0.ddl
echo "> Database creation completed."
rm -rf *.csv

