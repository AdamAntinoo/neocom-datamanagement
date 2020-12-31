#!/bin/bash
WORKING_DIRECTORY=$(pwd)

downloadFiles() {
  cd "${WORKING_DIRECTORY}/downloads" || exit 1
  allFiles=("mapRegions" "mapConstellations" "mapSolarSystems" "staStationTypes" "planetSchematics" "planetSchematicsTypeMap" "invTypes" "industryActivityMaterials")

  for file in ${allFiles[@]}; do
    curl -L -o "$file.csv.bz2" "https://www.fuzzwork.co.uk/dump/latest/$file.csv.bz2"
  done
}
decompress() {
  cd "${WORKING_DIRECTORY}/downloads" || exit 1
  bzip2 -d *
}

figlet CreateSDE
echo ">>> Download latest set of files..."
downloadFiles
echo ">>> Decompressing downloaded files..."
decompress

cd "${WORKING_DIRECTORY}"
echo ">> Remove current sde database..."
rm sde.db
echo ">> Create new sde database..."
/usr/bin/sqlite3 sde.db < db-ddl-0.20.0.ddl
echo ">> Database creation completed."
