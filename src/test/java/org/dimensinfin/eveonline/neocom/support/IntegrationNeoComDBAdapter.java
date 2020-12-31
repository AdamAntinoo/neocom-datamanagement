package org.dimensinfin.eveonline.neocom.support;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class IntegrationNeoComDBAdapter {
	private String localConnectionDescriptor;
	private boolean databaseValid = false;
	private boolean isOpen = false;
	private JdbcPooledConnectionSource connectionSource;

	private Dao<NeoAsset, UUID> assetDao;
	private Dao<Credential, String> credentialDao;
	private Dao<MiningExtractionEntity, String> miningExtractionEntityDao;

	private IntegrationNeoComDBAdapter() {}

	// - D A O - A C C E S S
	public ConnectionSource getConnectionSource() throws SQLException {
		if (null == this.connectionSource) this.openNeoComDB();
		return this.connectionSource;
	}

	public Dao<NeoAsset, UUID> getAssetDao() throws SQLException {
		if (null == this.assetDao) {
			this.assetDao = DaoManager.createDao( this.getConnectionSource(), NeoAsset.class );
		}
		return this.assetDao;
	}

	public Dao<Credential, String> getCredentialDao() throws SQLException {
		if (null == this.credentialDao) {
			this.credentialDao = DaoManager.createDao( this.getConnectionSource(), Credential.class );
		}
		return this.credentialDao;
	}

	public Dao<MiningExtractionEntity, String> getMiningExtractionDao() throws SQLException {
		if (null == this.credentialDao) {
			this.miningExtractionEntityDao = DaoManager.createDao( this.getConnectionSource(), MiningExtractionEntity.class );
		}
		return this.miningExtractionEntityDao;
	}

	public void onCreate( final ConnectionSource connectionSource ) {
		try {
			TableUtils.createTableIfNotExists( connectionSource, NeoAsset.class );
			TableUtils.createTableIfNotExists( connectionSource, Credential.class );
			TableUtils.createTableIfNotExists( connectionSource, MiningExtractionEntity.class );
		} catch (SQLException sqle) {
			NeoComLogger.error( "SQL NeoComDatabase: ", sqle );
		}
	}

	private boolean createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource( this.localConnectionDescriptor );
		// Configure the new connection pool.
		connectionSource.setMaxConnectionAgeMillis(
				TimeUnit.MINUTES.toMillis( 5 ) ); // Only keep the connections open for 5 minutes
		connectionSource.setCheckConnectionsEveryMillis(
				TimeUnit.SECONDS.toMillis( 60 ) ); // Change the check-every milliseconds from 30 seconds to 60
		connectionSource.setTestBeforeGet( true );
		return true;
	}

	private boolean openNeoComDB() throws SQLException {
		NeoComLogger.info( ">> [SBNeoComDBHelper.openNeoComDB]" );
		if (!this.isOpen) if (null == this.connectionSource) {
			this.isOpen = this.createConnectionSource();
		}
		NeoComLogger.info( "<< [SBNeoComDBHelper.openNeoComDB]" );
		return isOpen;
	}

	// - B U I L D E R
	public static class Builder {
		private IntegrationNeoComDBAdapter onConstruction;

		public Builder() {
			this.onConstruction = new IntegrationNeoComDBAdapter();
		}

		public IntegrationNeoComDBAdapter build() throws SQLException {
			Objects.requireNonNull( this.onConstruction.localConnectionDescriptor );
			this.onConstruction.onCreate( this.onConstruction.getConnectionSource() );
			return this.onConstruction;
		}

		public IntegrationNeoComDBAdapter.Builder withDatabaseURLConnection( final String databaseURLConnection ) {
			Objects.requireNonNull( databaseURLConnection );
			this.onConstruction.localConnectionDescriptor = databaseURLConnection;
			return this;
		}
	}
}
