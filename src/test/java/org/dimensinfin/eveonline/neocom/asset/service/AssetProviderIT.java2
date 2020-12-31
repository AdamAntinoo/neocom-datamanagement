package org.dimensinfin.eveonline.neocom.asset.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.table.TableUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.domain.AssetContainerType;
import org.dimensinfin.eveonline.neocom.asset.domain.INeoAsset;
import org.dimensinfin.eveonline.neocom.asset.domain.LocationAssetContainer;
import org.dimensinfin.eveonline.neocom.asset.domain.NeoAssetAssetContainer;
import org.dimensinfin.eveonline.neocom.asset.domain.Ship;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.container.FacetedExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.StationImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinition;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class AssetProviderIT extends IntegrationEnvironmentDefinitionTCLocal {
	private static final Integer TEST_MINIMAL_CORPORATION_IDENTIFIER = 98300000;
	private static final Integer TEST_DEEP_CORPORATION_IDENTIFIER = 98310000;

	@Test
	public void buildComplete() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();
		Assertions.assertNotNull( provider );
	}

	@Test
	public void buildCompleteWithOptional() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final NetworkAssetSource networkAssetSource = Mockito.mock( NetworkAssetSource.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.optionalAssetSource( networkAssetSource )
				.build();
		Assertions.assertNotNull( provider );
	}

	@Test
	public void buildFailure() {
		final Credential credential = Mockito.mock( Credential.class );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( null )
						.withAssetRepository( assetRepository )
						.withLocationCatalogService( locationService )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( credential )
						.withAssetRepository( null )
						.withLocationCatalogService( locationService )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class,
				() -> new AssetProvider.Builder()
						.withCredential( credential )
						.withAssetRepository( assetRepository )
						.withLocationCatalogService( null )
						.build(),
				"Expected AssetProvider.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void classifyAssetsByLocationNoAssets() throws SQLException {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		this.onCreate();
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withAssetRepository( this.itAssetRepository )
				.build();

		Assertions.assertEquals( 0, provider.classifyAssetsByLocation() );
	}

	@Test
	public void classifyCorporationAssetsByLocationDeepList() {
		final Integer corporationId = TEST_DEEP_CORPORATION_IDENTIFIER;
		final AssetProvider assetProvider = new AssetProvider.Builder()
				.withCredential( credential4Test )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.optionalAssetSource( new NetworkAssetSource.Builder()
						.withCredential( credential4Test )
						.withAssetRepository( this.itAssetRepository )
						.withCredentialRepository( this.itCredentialRepository )
						.withEsiDataProvider( this.esiDataProvider )
						.withLocationCatalogService( this.itLocationCatalogService )
						.build() )
				.build();
		Assertions.assertNotNull( assetProvider );

		final List<LocationAssetContainer> locations = assetProvider.classifyCorporationAssetsByLocation( corporationId );
		Assertions.assertNotNull( locations );
		Assertions.assertEquals( 2, locations.size() );

		final LocationAssetContainer firstLocation = locations.get( 0 );
		Assertions.assertNotNull( firstLocation );
		Assertions.assertEquals( AssetContainerType.SPACE, firstLocation.getType() );
		Assertions.assertNotNull( firstLocation.getContents() );
		Assertions.assertEquals( 1, firstLocation.getContents().size() );
		final SpaceLocation spaceLocation1 = firstLocation.getSpaceLocation();
		Assertions.assertNotNull( spaceLocation1 );
		Assertions.assertTrue( spaceLocation1 instanceof SpaceSystem );
		Assertions.assertEquals( "Domain", ((SpaceSystem) spaceLocation1).getRegionName() );
		Assertions.assertEquals( "Liela", ((SpaceSystem) spaceLocation1).getConstellationName() );
		Assertions.assertEquals( "Avair", ((SpaceSystem) spaceLocation1).getSolarSystemName() );

		final LocationAssetContainer secondLocation = locations.get( 1 );
		Assertions.assertNotNull( secondLocation );
		Assertions.assertEquals( AssetContainerType.SPACE, secondLocation.getType() );
		Assertions.assertNotNull( secondLocation.getContents() );
		Assertions.assertEquals( 1, secondLocation.getContents().size() );
		final SpaceLocation spaceLocation2 = secondLocation.getSpaceLocation();
		Assertions.assertNotNull( spaceLocation2 );
		Assertions.assertTrue( spaceLocation2 instanceof StationImplementation );
		Assertions.assertEquals( "Devoid", ((SpaceSystem) spaceLocation2).getRegionName() );
		Assertions.assertEquals( "Ryra", ((SpaceSystem) spaceLocation2).getConstellationName() );
		Assertions.assertEquals( "Esescama", ((SpaceSystem) spaceLocation2).getSolarSystemName() );
		Assertions.assertEquals( "Esescama VIII - Moon 3 - Imperial Armaments Warehouse",
				((StationImplementation) spaceLocation2).getStationName() );
		final INeoAsset office = secondLocation.getContents().get( 0 );
		Assertions.assertTrue( office instanceof NeoAssetAssetContainer );
		Assertions.assertTrue( ((NeoAssetAssetContainer) office).getContainerFace().isOffice() );
		Assertions.assertEquals( 1, ((NeoAssetAssetContainer) office).getContents().size() );
		final INeoAsset ship = ((NeoAssetAssetContainer) office).getContents().get( 0 );
		Assertions.assertTrue( ship instanceof Ship );
		Assertions.assertEquals( 2, ((Ship) ship).getContents().size() );
	}

	@Test
	public void classifyCorporationAssetsByLocationMinimalList() {
		final Integer corporationId = TEST_MINIMAL_CORPORATION_IDENTIFIER;
		final AssetProvider assetProvider = new AssetProvider.Builder()
				.withCredential( credential4Test )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.optionalAssetSource( new NetworkAssetSource.Builder()
						.withCredential( credential4Test )
						.withAssetRepository( this.itAssetRepository )
						.withCredentialRepository( this.itCredentialRepository )
						.withEsiDataProvider( this.esiDataProvider )
						.withLocationCatalogService( this.itLocationCatalogService )
						.build() )
				.build();
		Assertions.assertNotNull( assetProvider );

		final List<LocationAssetContainer> locations = assetProvider.classifyCorporationAssetsByLocation( corporationId );
		Assertions.assertNotNull( locations );
		Assertions.assertEquals( 1, locations.size() );

		final LocationAssetContainer firstLocation = locations.get( 0 );
		Assertions.assertNotNull( firstLocation );
		Assertions.assertEquals( AssetContainerType.SPACE, firstLocation.getType() );
		Assertions.assertNotNull( firstLocation.getContents() );
		Assertions.assertEquals( 1, firstLocation.getContents().size() );
		final SpaceLocation spaceLocation1 = firstLocation.getSpaceLocation();
		Assertions.assertNotNull( spaceLocation1 );
		Assertions.assertTrue( spaceLocation1 instanceof StationImplementation );
		Assertions.assertEquals( "Domain", ((SpaceSystem) spaceLocation1).getRegionName() );
		Assertions.assertEquals( "Liela", ((SpaceSystem) spaceLocation1).getConstellationName() );
		Assertions.assertEquals( "Avair", ((SpaceSystem) spaceLocation1).getSolarSystemName() );
		Assertions.assertEquals( "Avair VII - Moon 25 - Theology Council Tribunal", ((StationImplementation) spaceLocation1).getStationName() );
	}

	public void verifyTimeStamp() {
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 321 );
		final LocationIdentifier locationIdentifier = Mockito.mock( LocationIdentifier.class );
		Mockito.when( locationIdentifier.getType() ).thenReturn( LocationIdentifierType.SPACE );
		Mockito.when( locationIdentifier.getSpaceIdentifier() ).thenReturn( 3100000L );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Mockito.when( asset.getAssetId() ).thenReturn( 987654L );
		Mockito.when( asset.getLocationId() ).thenReturn( locationIdentifier );
		final List<NeoAsset> assetList = new ArrayList<>();
		assetList.add( asset );
		final AssetRepository assetRepository = Mockito.mock( AssetRepository.class );
		Mockito.when( assetRepository.findAllByOwnerId( Mockito.anyInt() ) ).thenReturn( assetList );
		final GetUniverseRegionsRegionIdOk regionData = new GetUniverseRegionsRegionIdOk();
		regionData.setRegionId( 1100000 );
		regionData.setName( "-TEST-REGION-NAME-" );
		final SpaceLocationImplementation spaceLocation = Mockito.mock( SpaceLocationImplementation.class );
		Mockito.when( spaceLocation.getRegion() ).thenReturn( regionData );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( assetRepository )
				.withLocationCatalogService( locationService )
				.build();

		provider.classifyAssetsByLocation(); // The first time the timestamp is not set.
		provider.classifyAssetsByLocation(); // The second time I run the rest of the code
	}

	private void onCreate() throws SQLException {
		TableUtils.dropTable( connectionSource, NeoAsset.class, true );
		TableUtils.createTableIfNotExists( connectionSource, NeoAsset.class );
	}

	//	@Test
//	void runAssetProviderIT() throws SQLException, IOException {
//		this.setupEnvironment();
//		final AssetProvider provider = new AssetProvider.Builder()
//				.withCredential( SupportIntegrationCredential.itCredential )
//				.withAssetRepository( this.itAssetRepository )
//				.withLocationCatalogService( this.itLocationService )
//				.build();
//		Assertions.assertNotNull( provider );
//
//		provider.classifyAssetsByLocation();
//	}

	//	@AfterEach
//	void tearDown() {
//		this.postgres.stop();
//	}
//	@BeforeEach
//	void setUp() throws SQLException {
////		this.configurationProvider = new TestConfigurationService.Builder()
////				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
//		this.configurationProvider.setProperty( "P.authenticated.retrofit.server.location",
//				"http://" +
//						esisimulator.getContainerIpAddress() +
//						":" + esisimulator.getMappedPort( ESI_UNITTESTING_PORT ) +
//						"/latest/" );
//		final IntegrationNeoComDBAdapter neocomDBAdapter = new IntegrationNeoComDBAdapter.Builder()
//				.withDatabaseURLConnection( connectionUrl )
//				.build();
//		NeoComUnitTestComponentFactory.getSingleton().setNeoComDBAdapter( neocomDBAdapter );
//		this.assetRepository= NeoComUnitTestComponentFactory.getSingleton().getAssetRepository();
////		private Dao<NeoAsset, UUID> assetDao;
////		connectionUrl = "jdbc:postgresql://"
////				+ postgres.getContainerIpAddress()
////				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
////				+ "/" + "postgres" +
////				"?user=" + "neocom" +
////				"&password=" + "01.Alpha";
////		NeoComLogger.info( "Postgres SQL URL: {}", connectionUrl );
//
////		this.connectionSource = new JdbcConnectionSource( this.connectionUrl, new PostgresDatabaseType() );
////		this.onCreate();
////		this.miningDao = DaoManager.createDao( connectionSource, MiningExtraction.class );
//
//		//		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
////				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest/" )
////				.build();
////		this.itLocationService = new LocationCatalogService.Builder()
////				.withConfigurationProvider( this.itConfigurationProvider )
////				.withFileSystemAdapter( this.itFileSystemAdapter )
////				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
////				.withRetrofitFactory( this.itRetrofitFactory )
////				.build();
//
//
////		this.retrofitFactory = new RetrofitFactory.Builder()
////				.withConfigurationProvider( this.configurationProvider )
////				.withFileSystemAdapter( this.fileSystemAdapter )
////				.build();
////		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
////		final StoreCacheManager storeCacheManager = Mockito.mock( StoreCacheManager.class );
////		this.esiDataProvider = new ESIDataProvider.Builder()
////				.withConfigurationProvider( this.configurationProvider )
////				.withFileSystemAdapter( this.fileSystemAdapter )
////				.withLocationCatalogService( locationCatalogService )
////				.withStoreCacheManager( storeCacheManager )
////				.withRetrofitFactory( this.retrofitFactory )
////				.build();
//	}

	//	@Test
	void getRegionList() throws IOException, SQLException {
//		this.setupEnvironment();
		// Configure the database to use the docker test container.
//		this.postgres.start();
//		this.connectionUrl = "jdbc:postgresql://"
//				+ postgres.getContainerIpAddress()
//				+ ":" + postgres.getMappedPort( PostgreSQLContainer.POSTGRESQL_PORT )
//				+ "/" + "postgres" +
//				"?user=" + "neocom" +
//				"&password=" + "01.Alpha";
//		NeoComLogger.info( "Postgres SQL URL: {}", connectionUrl );
//		this.connectionSource = new JdbcConnectionSource( this.connectionUrl, new PostgresDatabaseType() );
		this.onCreate();
//		this.assetDao = DaoManager.createDao( connectionSource, NeoAsset.class );
//		this.itNeoComIntegrationDBAdapter = Mockito.mock( IntegrationNeoComDBAdapter.class );
//		Mockito.when( this.itNeoComIntegrationDBAdapter.getAssetDao() ).thenReturn( this.assetDao );
//		// Configure the authenticated esi access to use the apisimulator mock service.
//		this.itConfigurationProvider = new TestConfigurationService.Builder()
//				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
//		this.itRetrofitFactory = new RetrofitFactory.Builder()
//				.withConfigurationProvider( this.itConfigurationProvider )
//				.withFileSystemAdapter( this.itFileSystemAdapter )
//				.build();
//		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
//		this.itLocationService = new LocationCatalogService.Builder()
//				.withConfigurationProvider( this.itConfigurationProvider )
//				.withFileSystemAdapter( this.itFileSystemAdapter )
//				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
//				.withRetrofitFactory( this.itRetrofitFactory )
//				.build();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		final AssetProvider provider = new AssetProvider.Builder()
				.withCredential( credential )
				.withAssetRepository( this.itAssetRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build();
		Assertions.assertNotNull( provider );
		provider.classifyAssetsByLocation();

		final List<FacetedExpandableContainer> regions = provider.getRegionList();
		Assertions.assertNotNull( regions );
		Assertions.assertEquals( 1, regions.size() );
		Assertions.assertEquals( 2, regions.get( 0 ).getContents().size() );
	}
}
