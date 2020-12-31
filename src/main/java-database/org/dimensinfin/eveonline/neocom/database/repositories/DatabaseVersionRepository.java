package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.DatabaseVersion;

public class DatabaseVersionRepository {
	protected static Logger logger = LoggerFactory.getLogger( DatabaseVersionRepository.class );
	protected Dao<DatabaseVersion, Integer> databaseVersionDao;

	protected DatabaseVersionRepository() {}

	public DatabaseVersion accessVersion() {
		try {
			final List<DatabaseVersion> versionData = this.databaseVersionDao.queryForAll();
			if (null != versionData)
				if (versionData.size() > 0)
					return versionData.get( 0 );
			return new DatabaseVersion( 0 );
		} catch (final SQLException sqle) {
			return new DatabaseVersion( 0 );
		}
	}

	public void persist( final DatabaseVersion record ) throws SQLException {
		if (null != record) {
			final Dao.CreateOrUpdateStatus value = this.databaseVersionDao.createOrUpdate( record );
		}
	}

	// - B U I L D E R
	public static class Builder {
		private DatabaseVersionRepository onConstruction;

		public Builder() {
			this.onConstruction = new DatabaseVersionRepository();
		}

		public DatabaseVersionRepository.Builder withDatabaseVersionDao( final Dao<DatabaseVersion, Integer> databaseVersionDao ) {
			Objects.requireNonNull( databaseVersionDao );
			this.onConstruction.databaseVersionDao = databaseVersionDao;
			return this;
		}

		public DatabaseVersionRepository build() {
			Objects.requireNonNull( this.onConstruction.databaseVersionDao );
			return this.onConstruction;
		}
	}
}
