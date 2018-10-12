//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import org.dimensinfin.eveonline.neocom.entities.Colony;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.entities.FittingRequest;
import org.dimensinfin.eveonline.neocom.entities.Job;
import org.dimensinfin.eveonline.neocom.entities.MarketOrder;
import org.dimensinfin.eveonline.neocom.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.entities.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.entities.Property;
import org.dimensinfin.eveonline.neocom.entities.RefiningData;
import org.dimensinfin.eveonline.neocom.entities.TimeStamp;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

import java.sql.SQLException;

/**
 * This interface defines the methods that should be implemented at the final Helper implementation so all
 * platforms share a compatible api. This helper is associated to the NeoCom private database only. That database
 * is used through Dao elements and the api exports the same functionality that are found on Android database
 * helpers.
 * @author Adam Antinoo
 */
public interface INeoComDBHelper {
	public int getDatabaseVersion();

	public int getStoredVersion() throws SQLException;

	public boolean isDatabaseValid();

	public boolean isOpen();

	public void onCreate( final ConnectionSource databaseConnection );

	public void onUpgrade( final ConnectionSource databaseConnection, final int oldVersion, final int newVersion );

	public void loadSeedData();

	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException;

	public Dao<TimeStamp, String> getTimeStampDao() throws SQLException;

	public Dao<Credential, String> getCredentialDao() throws SQLException;

	public Dao<EveLocation, String> getLocationDao() throws SQLException;

	public Dao<Property, String> getPropertyDao() throws SQLException;

	public Dao<NeoComAsset, String> getAssetDao() throws SQLException;

	public Dao<NeoComBlueprint, String> getBlueprintDao() throws SQLException;

	public Dao<Job, String> getJobDao() throws SQLException;

	public Dao<MarketOrder, String> getMarketOrderDao() throws SQLException;

	public Dao<FittingRequest, String> getFittingRequestDao() throws SQLException;

	public Dao<MiningExtraction, String> getMiningExtractionDao() throws SQLException;

	public Dao<RefiningData, String> getRefiningDataDao() throws SQLException;

	public void clearInvalidRecords( final long pilotid );

	public void replaceAssets( final long pilotid );

	public void replaceBlueprints( final long pilotid );

	public Dao<Colony, String> getColonyDao() throws SQLException;

	//	@Deprecated
	//	public Dao<ColonyStorage, String> getColonyStorageDao() throws SQLException;
	//
	//	@Deprecated
	//	public Dao<ColonySerialized, String> getColonySerializedDao() throws SQLException;

}