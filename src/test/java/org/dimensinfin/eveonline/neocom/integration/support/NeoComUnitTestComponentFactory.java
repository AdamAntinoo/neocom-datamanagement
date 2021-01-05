package org.dimensinfin.eveonline.neocom.integration.support;

import java.sql.SQLException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

/**
 * This is a singleton with global access that will contain application component references so they can be injected to other
 * components. The creation
 * of those components will be down internally on demand and with the knowledge of what components depend on other components.
 *
 * @author Adam Antinoo
 */
public class NeoComUnitTestComponentFactory {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";
	//	protected static Logger logger = LoggerFactory.getLogger( NeoComUnitTestComponentFactory.class );
	private static NeoComUnitTestComponentFactory singleton;
	private TestConfigurationService configurationProvider;
	private IFileSystem fileSystemAdapter;
	private ESIDataProvider esiDataAdapter;
	private RetrofitFactory retrofitFactory;
	private LocationCatalogService locationCatalogService;
	private StoreCacheManager storeCacheManager;
	private ESIUniverseDataProvider esiUniverseDataProvider;
	private AssetRepository assetRepository;
	private IntegrationNeoComDBAdapter neoComIntegrationDBAdapter;
//	private SBNeoComDBAdapter neocomDBAdapter;
//	private CredentialRepository credentialRepository;

	public static NeoComUnitTestComponentFactory getSingleton() {
		if (null == singleton) singleton = new NeoComUnitTestComponentFactory();
		return singleton;
	}

	// - A C C E S S O R S
	public IConfigurationService getConfigurationProvider() {
		if (null == this.configurationProvider)
			try {
				this.configurationProvider =
						new TestConfigurationService.Builder()
								.optionalPropertiesDirectory( "/src/test/resources/properties.unittest" )
								.build();
				this.configurationProvider.readAllProperties();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
		return this.configurationProvider;
	}

	public IFileSystem getFileSystemAdapter() {
		if (null == this.fileSystemAdapter)
			this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
					.optionalApplicationDirectory( "./out/test/NeoCom.UnitTest/" )
					.build();
		return this.fileSystemAdapter;
	}

	public RetrofitFactory getRetrofitFactory() {
		if (null == this.retrofitFactory)
			this.retrofitFactory = new RetrofitFactory.Builder()
					.withConfigurationProvider( this.getConfigurationProvider() )
					.withFileSystemAdapter( this.getFileSystemAdapter() )
					.build();
		return this.retrofitFactory;
	}

	public StoreCacheManager getStoreCacheManager() {
		if (null == this.storeCacheManager)
			this.storeCacheManager = new StoreCacheManager.Builder()
					.withConfigurationProvider( this.getConfigurationProvider() )
					.withFileSystemAdapter( this.getFileSystemAdapter() )
					.withRetrofitFactory( this.getRetrofitFactory() )
					.build();
		return this.storeCacheManager;
	}

	public ESIUniverseDataProvider getESIUniverseDataProvider() {
		if (null == this.esiUniverseDataProvider)
			this.esiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
					.withConfigurationProvider( this.getConfigurationProvider() )
					.withFileSystemAdapter( this.getFileSystemAdapter() )
					.withRetrofitFactory( this.getRetrofitFactory() )
					.withStoreCacheManager( this.getStoreCacheManager() )
					.build();
		return this.esiUniverseDataProvider;
	}

	public LocationCatalogService getLocationCatalogService() {
		if (null == this.locationCatalogService)
			this.locationCatalogService = new LocationCatalogService.Builder()
					.withConfigurationProvider( this.getConfigurationProvider() )
					.withFileSystemAdapter( this.getFileSystemAdapter() )
					.withRetrofitFactory( this.getRetrofitFactory() )
					.withESIUniverseDataProvider( this.getESIUniverseDataProvider() )
					.build();
		return this.locationCatalogService;
	}

	public AssetRepository getAssetRepository() {
		if (null == this.assetRepository)
			try {
				this.assetRepository = new AssetRepository.Builder()
						.withAssetDao( this.getNeoComDBHelper().getAssetDao() )
						.withConnection4Transaction( this.getNeoComDBHelper().getConnectionSource() )
						.build();
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		return this.assetRepository;
	}

	public IntegrationNeoComDBAdapter getNeoComDBHelper() {
		if (null == this.neoComIntegrationDBAdapter) {
			final String databaseHostName = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasehost" );
			final String databasePath = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasepath" );
			final String databaseUser = this.getConfigurationProvider().getResourceString( "P.database.neocom.databaseuser" );
			final String databasePassword = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasepassword" );
			final String neocomDatabaseURL = databaseHostName +
					"/" + databasePath +
					"?user=" + databaseUser +
					"&password=" + databasePassword;
//			connectionUrl = "jdbc:postgresql://"
//					+ postgres.getContainerIpAddress()
//					+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
//					+ "/" + "postgres" +
//					"?user=" + "neocom" +
//					"&password=" + "01.Alpha";
			try {
				this.neoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
						.withDatabaseURLConnection( neocomDatabaseURL )
						.build();
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return this.neoComIntegrationDBAdapter;
	}

	public void setNeoComDBAdapter( final IntegrationNeoComDBAdapter neocomDBAdapter ) {
		Objects.requireNonNull( neocomDBAdapter );
		this.neoComIntegrationDBAdapter = neocomDBAdapter;
	}

//	public CredentialRepository getCredentialRepository() {
//		if (null == this.credentialRepository) {
//			try {
//				credentialRepository = new CredentialRepository.Builder()
//						.withCredentialDao( this.getNeoComDBAdapter().getCredentialDao() )
//						.build();
//			} catch (SQLException sqle) {
//				credentialRepository = null;
//				Objects.requireNonNull( credentialRepository );
//			}
//		}
//		return this.credentialRepository;
//	}
//
//	public SBNeoComDBAdapter getNeoComDBAdapter() {
//		if (null == this.neocomDBAdapter) {
//			try {
//				// Create the neocom databasea adapter from configuration properties.
//				final String databaseHost = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasehost" );
//				final String databaseName = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasename" );
////				final String user = this.getConfigurationProvider().getResourceString( "P.database.neocom.databaseuser" );
////				final String password = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasepassword" );
//				final String locator = databaseHost+databaseName;
//				this.neocomDBAdapter = new SBNeoComDBAdapter.Builder()
//						.optionalDatabaseUrl( locator )
//						.build();
//
//
//				//			try {
////			final String databaseType = this.getConfigurationProvider()
////					.getResourceString( "P.database.neocom.databasetype", "sqlite" );
////			if (databaseType.equalsIgnoreCase( "postgres" )) {
////				// Postgres means Heroku and then configuration for connection from environment
////				final String localConnectionDescriptor = System.getenv( "JDBC_DATABASE_URL" );
////				neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
////						.withDatabaseConnection( localConnectionDescriptor )
////						.build();
////			}
////			if (databaseType.equalsIgnoreCase( "sqlite" )) {
////				// Postgres means Heroku and then configuration for connection from environment
////				final String localConnectionDescriptor = System.getenv( "JDBC_DATABASE_URL" );
////				neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
////						.withDatabaseConnection( localConnectionDescriptor )
////						.build();
////			}
//			} catch (SQLException sqle) {
//				neocomDBAdapter = null;
//				Objects.requireNonNull( neocomDBAdapter );
//			}
//		}
//		return this.neocomDBAdapter;
//	}

	//	public static NeoComUnitTestComponentFactory initialiseSingleton( final Application newApplication ) {
	//		application = newApplication;
	//		if (null == singleton) singleton = new NeoComUnitTestComponentFactory();
	//		return singleton;
	//	}


	//	public Application getApplication() {
	//		if (null == application)
	//			throw new NeoComRuntimeException("NeoCom global singleton is not instantiated. Please complete initialisation.");
	//		return application;
	//	}


	//	@Deprecated
	//	public IGlobalPreferencesManager getPreferencesProvider() {
	//		if (null == this.preferencesProvider) {
	//			preferencesProvider = new GlobalAndroidPreferencesProvider.Builder()
	//					                      .withApplication(this.getApplication())
	//					                      .build();
	//		}
	//		return this.preferencesProvider;
	//	}
	//
	//
	//	@Deprecated
	//	public ESIGlobalAdapter getEsiAdapter() {
	//		if (null == this.esiAdapter) {
	//			esiAdapter = new ESIGlobalAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
	//					             .build();
	//		}
	//		return this.esiAdapter;
	//	}

	//	public ESIDataAdapter getEsiDataAdapter() {
	//		if (null == this.esiDataAdapter)
	//			esiDataAdapter = new ESIDataAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
	//					                 .build();
	//		NeoItem.injectEsiDataAdapter(this.esiDataAdapter);
	//		return this.esiDataAdapter;
	//	}
	//
	//	public DataDownloaderService getDownloaderService() {
	//		if (null == this.downloaderService) {
	//			downloaderService = new DataDownloaderService.Builder(this.getEsiDataAdapter())
	//					                    .build();
	//		}
	//		return this.downloaderService;
	//	}


	//	public MiningRepository getMiningRepository() {
	//		if (null == this.miningRepository) {
	//			try {
	//				miningRepository = new MiningRepository.Builder()
	//						                   .withMiningExtractionDao(this.getNeoComDBHelper().getMiningExtractionDao())
	//						                   .build();
	//			} catch (SQLException sqle) {
	//				miningRepository = null;
	//				Objects.requireNonNull(miningRepository);
	//			}
	//		}
	//		return this.miningRepository;
	//	}


	//	public ISDEDatabaseAdapter getSDEDatabaseAdapter() {
	//		if (null == this.sdeDatabaseAdapter) {
	//			this.sdeDatabaseAdapter = new SDEAndroidDBHelper.Builder()
	//					                          .withFileSystemAdapter(this.getFileSystemAdapter())
	//					                          .build();
	//		}
	//		return this.sdeDatabaseAdapter;
	//	}
	//
	//	public PlanetaryRepository getPlanetaryRepository() {
	//		if (null == this.planetaryRepository) {
	//			this.planetaryRepository = new PlanetaryRepository.Builder()
	//					                           .withSDEDatabaseAdapter(this.getSDEDatabaseAdapter())
	//					                           .build();
	//		}
	//		return this.planetaryRepository;
	//	}
}
