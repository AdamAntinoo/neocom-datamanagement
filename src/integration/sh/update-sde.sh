#!/bin/bash
WORKING_DIRECTORY=$(pwd)

downloadFiles() {
  cd "${WORKING_DIRECTORY}/downloads" || exit 1
  allFiles=("mapRegions" "mapConstellations" "mapSolarSystems" "staStationTypes" "planetSchematicsPinMap" "planetSchematicsTypeMap" "invTypes"
  "industryActivityMaterials" "industryActivitySkills")

  for file in ${allFiles[@]}; do
    curl -L -o "$file.csv.bz2" "https://www.fuzzwork.co.uk/dump/latest/$file.csv.bz2"
  done
}
decompress() {
  cd "${WORKING_DIRECTORY}/downloads" || exit 1
  bzip2 -d *.bz2
  mv *.csv ../
}

figlet UpdateSDE
echo "> Database source files update started."
echo ">> Download latest set of files..."
rm -rf *.csv
downloadFiles
echo ">> Decompressing downloaded files..."
decompress
echo "> Database source files update completed."
