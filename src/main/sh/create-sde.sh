#!/bin/bash
echo ">> Remove current sde database..."
rm sde.db
echo ">> Create new sde database..."
/usr/bin/sqlite3 sde.db < db-ddl-0.20.0.ddl
echo ">> Database creation completed."
