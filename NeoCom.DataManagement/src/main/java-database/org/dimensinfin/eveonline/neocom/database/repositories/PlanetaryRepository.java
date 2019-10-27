package org.dimensinfin.eveonline.neocom.database.repositories;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.RawStatement;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * This class will interface with the local repository for SDE data (first option) and possibly on the future for the local persistence repository.
 * The functionality exposed is related to the Planetary Interaction area covering from the schematics decomposition to planet data
 * storage to process optimizations.
 */
public class PlanetaryRepository implements Serializable {
	private static final long serialVersionUID = 7406787994714386613L;
	private static Logger logger = LoggerFactory.getLogger(PlanetaryRepository.class);

	// - C O M P O N E N T S
	private ISDEDatabaseAdapter sdeDatabaseAdapter;

	// - S C H E M A T I C S 4 O U T P U T
	private static int SCHEMATICS4OUTPUT_TYPEID_COLINDEX = 1;
	private static int SCHEMATICS4OUTPUT_NAME_COLINDEX = 2;
	private static int SCHEMATICS4OUTPUT_CYCLETIME_COLINDEX = 3;
	private static int SCHEMATICS4OUTPUT_RESOURCETYPEID_COLINDEX = 4;
	private static int SCHEMATICS4OUTPUT_QUANTITY_COLINDEX = 5;
	private static int SCHEMATICS4OUTPUT_ISINPUT_COLINDEX = 6;
	private static final String SELECT_SCHEMATICS4OUTPUT = "SELECT ps.schematicID, ps.schematicName, ps.cycleTime, pstms.typeId, pstms.quantity, pstms.isInput"
			                                                       + " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms, planetSchematics ps"
			                                                       + " WHERE  pstmt.typeId = ?"
			                                                       + " AND    pstmt.isInput = 0"
			                                                       + " AND    pstms.schematicID = pstmt.schematicID"
			                                                       + " AND    ps.schematicID=pstmt.schematicID";

	public List<Schematics> searchSchematics4Output( final int targetId ) {
		logger.info(">< [PlanetaryRepository.searchSchematics4Output]> typeId: {}", targetId);
		List<Schematics> scheList = new Vector<Schematics>();
		try {
			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement(SELECT_SCHEMATICS4OUTPUT, new String[]{Integer.valueOf(targetId).toString()});
			while (cursor.moveToNext()) {
				scheList.add(new Schematics.Builder()
						             .withSchematicId(cursor.getInt(SCHEMATICS4OUTPUT_TYPEID_COLINDEX))
						             .withSchematicName(cursor.getString(SCHEMATICS4OUTPUT_NAME_COLINDEX))
						             .withCycleTime(cursor.getInt(SCHEMATICS4OUTPUT_CYCLETIME_COLINDEX))
						             .withResourceTypeId(cursor.getInt(SCHEMATICS4OUTPUT_RESOURCETYPEID_COLINDEX))
						             .withQuantity(cursor.getInt(SCHEMATICS4OUTPUT_QUANTITY_COLINDEX))
						             .withDirection((cursor.getInt(SCHEMATICS4OUTPUT_ISINPUT_COLINDEX) == 1) ? true : false)
						             .build());
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [PlanetaryRepository.searchSchematics4Output]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [PlanetaryRepository.searchSchematics4Output]");
			return scheList;
		}
	}

	// - S C H E M A T I C S 4 I D
	private static int SCHEMATICS4ID_NAME_COLINDEX = 1;
	private static int SCHEMATICS4ID_CYCLETIME_COLINDEX = 2;
	private static int SCHEMATICS4ID_RESOURCETYPEID_COLINDEX = 3;
	private static int SCHEMATICS4ID_QUANTITY_COLINDEX = 4;
	private static int SCHEMATICS4ID_ISINPUT_COLINDEX = 5;
	private static final String SELECT_SCHEMATICS4ID = "SELECT ps.schematicname, ps.cycleTime, psm.typeID, psm.quantity, psm.isInput"
			                                                   + " FROM  planetSchematics ps, planetSchematicsTypeMap psm"
			                                                   + " WHERE ps.schematicID = ?"
			                                                   + " AND   psm.schematicID = ps.schematicID";

	public List<Schematics> searchSchematics4Id( final Integer schematicId ) {
		logger.info(">< [PlanetaryRepository.searchSchematics4Id]> typeId: {}", schematicId);
		List<Schematics> scheList = new ArrayList<>();
		try {
			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement(SELECT_SCHEMATICS4ID, new String[]{schematicId.toString()});
			while (cursor.moveToNext()) {
				scheList.add(new Schematics.Builder()
						             .withSchematicId(schematicId)
						             .withSchematicName(cursor.getString(SCHEMATICS4ID_NAME_COLINDEX))
						             .withCycleTime(cursor.getInt(SCHEMATICS4ID_CYCLETIME_COLINDEX))
						             .withResourceTypeId(cursor.getInt(SCHEMATICS4ID_RESOURCETYPEID_COLINDEX))
						             .withQuantity(cursor.getInt(SCHEMATICS4ID_QUANTITY_COLINDEX))
						             .withDirection((cursor.getInt(SCHEMATICS4ID_ISINPUT_COLINDEX) == 1) ? true : false)
						             .build());
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [PlanetaryRepository.searchSchematics4Id]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [PlanetaryRepository.searchSchematics4Id]");
			return scheList;
		}
	}

	// - B U I L D E R
	public static class Builder {
		private PlanetaryRepository onConstruction;

		public Builder() {
			this.onConstruction = new PlanetaryRepository();
		}

		public PlanetaryRepository.Builder withSDEDatabaseAdapter( final ISDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public PlanetaryRepository build() {
			Objects.requireNonNull(this.onConstruction.sdeDatabaseAdapter);
			return this.onConstruction;
		}
	}
}
