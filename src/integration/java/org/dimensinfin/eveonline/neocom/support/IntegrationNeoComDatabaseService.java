package org.dimensinfin.eveonline.neocom.support;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.DatabaseVersion;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.entities.PilotPreferencesEntity;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOfferEntity;
import org.dimensinfin.logging.LogWrapper;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class IntegrationNeoComDatabaseService implements NeoComDatabaseService {
	private final String databaseConnectionDescriptor;

	private boolean isOpen = false;
	private JdbcPooledConnectionSource connectionSource;
	private Dao<DatabaseVersion, String> versionDao;
	private Dao<Credential, String> credentialDao;
	private Dao<NeoAsset, UUID> assetDao;
	private Dao<MiningExtractionEntity, String> miningExtractionDao;
	private Dao<PilotPreferencesEntity, UUID> pilotPreferencesDao;
	private Dao<JobEntity, String> industryJobDao;
	private Dao<LoyaltyOfferEntity, Integer> loyaltyOfferDao;

	// - C O N S T R U C T O R S
	@Inject
	public IntegrationNeoComDatabaseService( @NotNull @Named("NeoComDatabasePath") final String databaseConnectionDescriptor ) {
		this.databaseConnectionDescriptor = databaseConnectionDescriptor;
	}

	// - G E T T E R S   &   S E T T E R S
	@Override
	public Dao<NeoAsset, UUID> getAssetDao() throws SQLException {
		if (null == this.assetDao) {
			this.assetDao = DaoManager.createDao( this.getConnectionSource(), NeoAsset.class );
		}
		return this.assetDao;
	}

	@Override
	public Dao<Credential, String> getCredentialDao() throws SQLException {
		if (null == this.credentialDao) {
			this.credentialDao = DaoManager.createDao( this.getConnectionSource(), Credential.class );
		}
		return this.credentialDao;
	}

	@Override
	public Dao<JobEntity, String> getIndustryJobDao() throws SQLException {
		if (null == this.industryJobDao) {
			this.industryJobDao = DaoManager.createDao( this.getConnectionSource(), JobEntity.class );
		}
		return this.industryJobDao;
	}

	@Override
	public Dao<MiningExtractionEntity, String> getMiningExtractionDao() throws SQLException {
		if (null == this.miningExtractionDao) {
			this.miningExtractionDao = DaoManager.createDao( this.getConnectionSource(), MiningExtractionEntity.class );
		}
		return this.miningExtractionDao;
	}

	@Override
	public Dao<PilotPreferencesEntity, UUID> getPilotPreferencesDao() throws SQLException {
		if (null == this.pilotPreferencesDao) {
			this.pilotPreferencesDao = DaoManager.createDao( this.getConnectionSource(), PilotPreferencesEntity.class );
		}
		return this.pilotPreferencesDao;
	}

	@Override
	public Dao<DatabaseVersion, String> getVersionDao() throws SQLException {
		if (null == this.versionDao) {
			this.versionDao = DaoManager.createDao( this.getConnectionSource(), DatabaseVersion.class );
		}
		return this.versionDao;
	}

	@Override
	public Dao<LoyaltyOfferEntity, Integer> getLoyaltyOfferDao() throws SQLException {
		if (null == this.loyaltyOfferDao) {
			this.loyaltyOfferDao = DaoManager.createDao( this.getConnectionSource(), LoyaltyOfferEntity.class );
		}
		return this.loyaltyOfferDao;
	}

	public ConnectionSource getConnectionSource() throws SQLException {
		if (null == this.connectionSource) this.openNeoComDB();
		return this.connectionSource;
	}

	public void onCreate( final ConnectionSource connectionSource ) {
		if (null != connectionSource) {
			try {
				TableUtils.createTableIfNotExists( connectionSource, Credential.class );
				TableUtils.createTableIfNotExists( connectionSource, MiningExtractionEntity.class );
				TableUtils.createTableIfNotExists( connectionSource, LoyaltyOfferEntity.class );
			} catch (final SQLException sqle) {
				LogWrapper.error( sqle );
			}
		}
	}

	private boolean createConnectionSource() throws SQLException {
		this.connectionSource = new JdbcPooledConnectionSource( this.databaseConnectionDescriptor );
		// Configure the new connection pool.
		this.connectionSource.setMaxConnectionAgeMillis(
				TimeUnit.MINUTES.toMillis( 5 ) ); // only keep the connections open for 5 minutes
		this.connectionSource.setCheckConnectionsEveryMillis(
				TimeUnit.SECONDS.toMillis( 60 ) ); // change the check-every milliseconds from 30 seconds to 60
		this.connectionSource.setTestBeforeGet( true );
		return true;
	}

	private void openNeoComDB() throws SQLException {
		LogWrapper.enter();
		if (!this.isOpen) {
			this.isOpen = this.createConnectionSource();
			LogWrapper.info( MessageFormat.format( "Opened database {0} successfully with version {1}.",
					this.databaseConnectionDescriptor, "production" ) );
		}
		LogWrapper.exit( MessageFormat.format( "Current database state: {0}", this.isOpen ) );
	}
}