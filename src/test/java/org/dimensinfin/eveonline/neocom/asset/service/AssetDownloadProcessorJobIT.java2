package org.dimensinfin.eveonline.neocom.asset.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.integration.support.GroupCount;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

/**
 * This test unit should do an integration testing because asset management requires ESI universe data to download most of the asset information
 * that is on the eve items and also the NeoCom database because the real work is done storing snd retrieving the asset information from their
 * final repository.
 * And most of the mock information should be provided by recorded ESI authenticated responses so there is a api simulator service to mock the
 * authenticated data.
 *
 * All this setup creates an integration environment fully operative and that will store and retrieve some real data while using other mock data as
 * driving force to complete the tests.
 *
 * The operative environment initialization is delegated to a superclass.
 */
public class AssetDownloadProcessorJobIT extends IntegrationEnvironmentDefinitionTCLocal {
//	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final int TEST_CORPORATION_ID = 98384726;

	@BeforeAll
	private static void beforeAll() {
		credential4Test = Mockito.mock( Credential.class );
		Mockito.when( credential4Test.getAccountId() ).thenReturn( 92223647 );
		Mockito.when( credential4Test.getDataSource() ).thenReturn( "tranquility" );
		Mockito.when( credential4Test.setMiningResourcesEstimatedValue( Mockito.anyDouble() ) ).thenReturn( credential4Test );
	}

//	private AssetDownloadProcessorJobIT() {}

	@Test
	public void buildComplete() {
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
				.withAssetRepository( this.itAssetRepository )
				.withCredential( credential4Test )
				.withCredentialRepository( this.itCredentialRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withEsiDataProvider( this.esiDataProvider )
				.build();

		Assertions.assertNotNull( assetDownloadProcessorJob );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
					.withAssetRepository( this.itAssetRepository )
					.withCredential( credential4Test )
					.withCredentialRepository( this.itCredentialRepository )
					.withEsiDataProvider( this.esiDataProvider )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
					.withAssetRepository( this.itAssetRepository )
					.withCredential( credential4Test )
					.withCredentialRepository( this.itCredentialRepository )
					.withLocationCatalogService( null )
					.withEsiDataProvider( this.esiDataProvider )
					.build();
		} );
	}

	@Test
	public void call() throws Exception {
		final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
				.withAssetRepository( this.itAssetRepository )
				.withCredential( credential4Test )
				.withCredentialRepository( this.itCredentialRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withEsiDataProvider( this.esiDataProvider )
				.build();
		Assertions.assertTrue( assetDownloadProcessorJob.call() );
	}

//	@Test
	public void downloadCorporationAssets() {
		final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
				.withAssetRepository( this.itAssetRepository )
				.withCredential( credential4Test )
				.withCredentialRepository( this.itCredentialRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withEsiDataProvider( this.esiDataProvider )
				.build();

		Assertions.assertNotNull( assetDownloadProcessorJob );
		final List<NeoAsset> assetList = assetDownloadProcessorJob.downloadCorporationAssets( TEST_CORPORATION_ID );
		Assertions.assertNotNull( assetList );
		Assertions.assertEquals( 26, assetList.size() );
		NeoAsset checkItem = null;
		for (NeoAsset asset : assetList) {
			if (asset.getAssetId() == 1030723122045L) checkItem = asset;
		}
		Assertions.assertNotNull( checkItem );
		final SpaceLocation checkLocation = this.itLocationCatalogService.searchStructure4Id( checkItem.getLocationId().getSpaceIdentifier(),
				credential4Test);
		Assertions.assertNotNull( checkLocation );
	}

//	@Test
	public void downloadPilotAssetsESI() throws SQLException, IOException {
		final AssetDownloadProcessorJob assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
				.withAssetRepository( this.itAssetRepository )
				.withCredential( credential4Test )
				.withCredentialRepository( this.itCredentialRepository )
				.withLocationCatalogService( this.itLocationCatalogService )
				.withEsiDataProvider( this.esiDataProvider )
				.build();

		Assertions.assertNotNull( assetDownloadProcessorJob.downloadPilotAssets() );
		final List<NeoAsset> assets = this.itAssetRepository
				.findAllByOwnerId( credential4Test.getAccountId() );
		Assertions.assertEquals( 26, assets.size() );
		final List<GroupCount> groupCounts = this.readGroupCounts();
		int propulsionCount = 0;
		int miningLasertCount = 0;
		for (NeoAsset asset : assets) {
			if (asset.getGroupName().equalsIgnoreCase( "Mining Laser" )) miningLasertCount++;
			if (asset.getGroupName().equalsIgnoreCase( "Propulsion Module" )) propulsionCount++;
		}
		for (GroupCount count : groupCounts) {
			if (count.getGroup().equalsIgnoreCase( "Mining Laser" ))
				Assertions.assertEquals( miningLasertCount, count.getCount() );
			if (count.getGroup().equalsIgnoreCase( "Propulsion Module" ))
				Assertions.assertEquals( propulsionCount, count.getCount() );
		}
	}

	@BeforeEach
	public void setUp() throws IOException, SQLException {
		this.setupEnvironment();
	}

	private List<GroupCount> readGroupCounts() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final File groupCountsFile = new File( this.itFileSystemAdapter.accessResource4Path( "/TestData/groupsCounts.json" ) );
		return mapper.readValue( groupCountsFile,
				mapper.getTypeFactory().constructCollectionType( List.class, GroupCount.class ) );
	}

//
//	public static void main( String[] args ) {
//		NeoComLogger.enter();
//		final AssetDownloadProcessorJobIT application = new AssetDownloadProcessorJobIT();
//		try {
////			application.setupEnvironment();
//			application.registerJobOnScheduler();
//			JobScheduler.getJobScheduler().runSchedule();
//			application.waitSchedulerCompletion();
//			application.checkAssertions();
//		} catch (IOException ioe) {
//			NeoComLogger.info( "Application interrupted: ", ioe );
//		} catch (SQLException sqle) {
//			NeoComLogger.info( "Application interrupted: ", sqle );
//		}
//		NeoComLogger.exit();
//	}


//	private AssetDownloadProcessorJob assetProcessorJob;


//	private List<GroupCount> groupCounts;

//	private void checkAssertions() throws IOException, SQLException {
//		Assertions.assertNotNull( JobScheduler.getJobScheduler() );
//		Assertions.assertNotNull( this.assetProcessorJob );
//
//		final List<NeoAsset> assets = this.itAssetRepository
//				.findAllByOwnerId( SupportIntegrationCredential.itCredential.getAccountId() );
//		Assertions.assertEquals( 36, assets.size() );
//
//		this.readGroupCounts();
//		int propulsionCount = 0;
//		int miningLasertCount = 0;
//		for (NeoAsset asset : assets) {
//			if (asset.getGroupName().equalsIgnoreCase( "Mining Laser" )) miningLasertCount++;
//			if (asset.getGroupName().equalsIgnoreCase( "Propulsion Module" )) propulsionCount++;
//		}
//		for (GroupCount count : this.groupCounts) {
//			if (count.getGroup().equalsIgnoreCase( "Mining Laser" ))
//				Assertions.assertEquals( miningLasertCount, count.getCount() );
//			if (count.getGroup().equalsIgnoreCase( "Propulsion Module" ))
//				Assertions.assertEquals( propulsionCount, count.getCount() );
//		}
//		Assertions.assertTrue(
//				this.itCredentialRepository.findCredentialById( SupportIntegrationCredential.itCredential.getUniqueId() )
//						.getMiningResourcesEstimatedValue() > 0.0
//		);
//	}
//	private void registerJobOnScheduler() {
//		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
//		this.assetProcessorJob = new AssetDownloadProcessorJob.Builder()
//				.withCredential( SupportIntegrationCredential.itCredential )
//				.withEsiDataProvider( this.esiDataProvider )
//				.withLocationCatalogService( locationCatalogService )
//				.withAssetRepository( this.itAssetRepository )
//				.withCredentialRepository( this.itCredentialRepository )
//				.addCronSchedule( "* - *" )
//				.build();
//		JobScheduler.getJobScheduler().registerJob( this.assetProcessorJob );
//	}

//	private void waitSchedulerCompletion() {
//		JobScheduler.getJobScheduler().wait4Completion();
//	}
//	@BeforeEach
//	void beforeEach() throws IOException, SQLException {
//		this.configurationProvider = new TestConfigurationService.Builder()
//				.withPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
//		this.configurationProvider.setProperty( "P.authenticated.retrofit.server.location",
//				"http://" +
//						esisimulator.getContainerIpAddress() +
//						":" + esisimulator.getMappedPort( ESI_UNITTESTING_PORT ) +
//						"/latest/" );
//		this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
//				.optionalApplicationDirectory( "./src/test/NeoCom.UnitTest/" )
//				.build();
//		this.retrofitFactory = new RetrofitFactory.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.build();
//		this.storeCacheManager = new StoreCacheManager.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.withRetrofitFactory( this.retrofitFactory )
//				.build();
//		this.esiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.withStoreCacheManager( this.storeCacheManager )
//				.withRetrofitFactory( this.retrofitFactory )
//				.build();
//		this.locationCatalogService = new LocationCatalogService.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.withESIUniverseDataProvider( this.esiUniverseDataProvider )
//				.withRetrofitFactory( this.retrofitFactory )
//				.build();
//		this.assetRepository = Mockito.mock( AssetRepository.class );
//		Mockito.doAnswer((pilotId) -> {
//			return null;
//		}).when(this.assetRepository).clearInvalidRecords( Mockito.anyLong());
//		Mockito.doAnswer((asset) -> {
//			return null;
//		}).when(this.assetRepository).persist( Mockito.any(NeoAsset.class));
//		Mockito.doAnswer((pilotId) -> {
//			return null;
//		}).when(this.assetRepository).replaceAssets( Mockito.anyLong());
//		this.credentialRepository = Mockito.mock( CredentialRepository.class );
//		Mockito.doAnswer((credential) -> {
//			return null;
//		}).when(this.credentialRepository).persist( Mockito.any(Credential.class));
//		this.esiDataProvider = new ESIDataProvider.Builder()
//				.withConfigurationProvider( this.configurationProvider )
//				.withFileSystemAdapter( this.fileSystemAdapter )
//				.withLocationCatalogService( this.locationCatalogService )
//				.withStoreCacheManager( this.storeCacheManager )
//				.withRetrofitFactory( this.retrofitFactory )
//				.build();
//		NeoItem.injectEsiUniverseDataAdapter( this.esiDataProvider );
//		this.assetDownloadProcessorJob = new AssetDownloadProcessorJob.Builder()
//				.withAssetRepository( this.assetRepository )
//				.withCredential( credential4Test )
//				.withCredentialRepository( this.credentialRepository )
//				.withLocationCatalogService( this.locationCatalogService )
//				.withEsiDataProvider( this.esiDataProvider )
//				.build();
//	}

//	@Test
//	void runAssetProcessorIT() {
//		AssetDownloadProcessorJobIT.main( null );
//	}
//	private List<GetCharactersCharacterIdAssets200Ok> loadAssetTestData() throws IOException {
//		SimpleModule testModule = new SimpleModule( "NoeComIntegrationModule",
//				Version.unknownVersion() );
//		testModule.addDeserializer( GetCharactersCharacterIdAssets200Ok.class,
//				new GetCharactersCharacterIdAssets200OkDeserializer( GetCharactersCharacterIdAssets200Ok.class ) );
//		mapper.registerModule( testModule );
//
//		final GetCharactersCharacterIdAssets200Ok[] data = this.mapper.readValue( FileUtils.readFileToString(
//				new File( this.itFileSystemAdapter.accessResource4Path( "TestData/assetTestList.json" ) ),
//				"utf-8" ), GetCharactersCharacterIdAssets200Ok[].class );
//		return new ArrayList<>( Arrays.asList( data ) );
//	}

}
