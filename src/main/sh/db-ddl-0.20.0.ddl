.print ">>> Create Regions data..."
.read mapRegions.sql
.print ">>> Create Contellations data..."
.read mapConstellations.sql
.print ">>> Create Solar Systems data..."
.read mapSolarSystems.sql
.print ">>> Create Station Types data..."
.read staStationTypes.sql
.print ">>> Create Planetary Interaction Schematics data..."
.read planetSchematics.sql
.print ">>> Create Planetary Interaction Schematics Type maps data..."
.read planetSchematicsTypeMap.sql
.print ">>> Create Version data..."
.read createVersionTable.sql
.read setVersion.sql
.print ">>> Create Inventory types data..."
.read invTypes.sql
.exit
