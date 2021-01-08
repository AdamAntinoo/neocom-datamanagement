package org.dimensinfin.eveonline.neocom.database;

import java.sql.SQLException;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.entities.PilotPreferencesEntity;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;

public interface NeoComDatabaseService {
	Dao<NeoAsset, UUID> getAssetDao() throws SQLException;

	Dao<Credential, String> getCredentialDao() throws SQLException;

	Dao<JobEntity, String> getIndustryJobDao() throws SQLException;

	Dao<MiningExtractionEntity, String> getMiningExtractionDao() throws SQLException;

	Dao<PilotPreferencesEntity, UUID> getPilotPreferencesDao() throws SQLException;

	Dao<DatabaseVersion, String> getVersionDao() throws SQLException;
}