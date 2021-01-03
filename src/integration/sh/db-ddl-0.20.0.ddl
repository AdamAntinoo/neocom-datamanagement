.print ">>> Create Version data..."
.read createVersionTable.sql
.read setVersion.sql
.print ">>> Create Regions data..."
.mode csv
.import mapRegions.csv mapRegions
.print ">>> Create Constellations data..."
.mode csv
.import mapConstellations.csv mapConstellations
.print ">>> Create Solar Systems data..."
.mode csv
.import mapSolarSystems.csv mapSolarSystems
.print ">>> Create Station Types data..."
.mode csv
.import staStationTypes.csv staStationTypes
.print ">>> Create Planetary Interaction Schematics Pin maps data..."
.mode csv
.import planetSchematicsPinMap.csv planetSchematicsPinMap
.print ">>> Create Planetary Interaction Schematics Type maps data..."
.mode csv
.import planetSchematicsTypeMap.csv planetSchematicsTypeMap
.print ">>> Create Inventory types data..."
.mode csv
.import invTypes.csv invTypes
.print ">>> Create Industry Materials Production List..."
.mode csv
.import industryActivityMaterials.csv industryActivityMaterials
.print ">>> Create Industry Materials Skills List..."
.mode csv
.import industryActivitySkills.csv industryActivitySkills
.exit
