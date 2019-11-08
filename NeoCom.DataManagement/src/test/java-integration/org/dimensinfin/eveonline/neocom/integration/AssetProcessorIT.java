package org.dimensinfin.eveonline.neocom.integration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import org.dimensinfin.eveonline.neocom.adapter.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapter.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.adapter.RetrofitUniverseConnector;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.processor.AssetDownloadProcessor;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.LocationRepository;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.integration.support.GetCharactersCharacterIdAssets200OkDeserializer;
import org.dimensinfin.eveonline.neocom.integration.support.IntegrationNeoComDBAdapter;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.HourlyCronScheduleGenerator;
import org.dimensinfin.eveonline.neocom.service.scheduler.JobScheduler;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class AssetProcessorIT {
	private GenericContainer postgres;

	private final ObjectMapper mapper = new ObjectMapper();
	private Credential itCredential;
	private IConfigurationProvider itConfigurationProvider;
	private IFileSystem itFileSystemAdapter;
	private JobScheduler itJobScheduler;
	private AssetRepository itAssetRepository;
	private IntegrationNeoComDBAdapter itNeoComIntegrationDBAdapter;
	private RetrofitUniverseConnector itRetrofitUniverseConnector;
	private ESIUniverseDataProvider itEsiUniverseDataProvider;
	private NeoComRetrofitFactory itRetrofitFactory;
	private LocationCatalogService itLocationService;
	private StoreCacheManager itStoreCacheManager;
	private ESIDataAdapter itEsiDataProvider;


	private AssetProcessorIT() {}

	public static void main( String[] args ) {
		NeoComLogger.enter();
		final AssetProcessorIT application = new AssetProcessorIT();
		try {
//			application.startContainers();
			application.setUpEnvironment();
			application.registerJobOnScheduler();
			application.itJobScheduler.runSchedule();
//			application.stopContainers();
			application.waitSchedulerCompletion();
		} catch (IOException ioe) {
			NeoComLogger.info( "Application interrupted: ", ioe );
		} catch (SQLException sqle) {
			NeoComLogger.info( "Application interrupted: ", sqle );
		}
		NeoComLogger.exit();
	}

	private void startContainers() {
//		this.postgres = new GenericContainer<>( "postgres:11.2" )
//				.withExposedPorts( 5432 )
//				.withEnv( "POSTGRES_DB", "postgres" )
//				.withEnv( "POSTGRES_USER", "neocom" )
//				.withEnv( "POSTGRES_PASSWORD", "01.Alpha" );

		this.postgres = new PostgreSQLContainer( "postgres:9.6-alpine" )
				.withDatabaseName( "postgres" )
				.withPassword( "01.Alpha" )
				.withUsername( "neocom" )
				.withExposedPorts( 5432 )
				.withLogConsumer( new Slf4jLogConsumer( LoggerFactory.getLogger( "🐳 " + "postgres" ) ) );
//				.withNetwork(network);

		this.postgres.start();
	}

	private void stopContainers() {
		this.postgres.stop();
	}

	private void waitSchedulerCompletion() {
		this.itJobScheduler.wait4Completion();
	}

	private void setUpEnvironment() throws IOException, SQLException {
		this.itCredential = new Credential.Builder(2113197470)
				.withAccountId(2113197470)
				.withAccountName("Tip Tophane")
				.withAccessToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0.eyJzY3AiOlsicHVibGljRGF0YSIsImVzaS1sb2NhdGlvbi5yZWFkX2xvY2F0aW9uLnYxIiwiZXNpLWxvY2F0aW9uLnJlYWRfc2hpcF90eXBlLnYxIiwiZXNpLW1haWwucmVhZF9tYWlsLnYxIiwiZXNpLXNraWxscy5yZWFkX3NraWxscy52MSIsImVzaS1za2lsbHMucmVhZF9za2lsbHF1ZXVlLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NoYXJhY3Rlcl93YWxsZXQudjEiLCJlc2ktd2FsbGV0LnJlYWRfY29ycG9yYXRpb25fd2FsbGV0LnYxIiwiZXNpLXNlYXJjaC5zZWFyY2hfc3RydWN0dXJlcy52MSIsImVzaS1jbG9uZXMucmVhZF9jbG9uZXMudjEiLCJlc2ktdW5pdmVyc2UucmVhZF9zdHJ1Y3R1cmVzLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2Fzc2V0cy52MSIsImVzaS1wbGFuZXRzLm1hbmFnZV9wbGFuZXRzLnYxIiwiZXNpLWZpdHRpbmdzLnJlYWRfZml0dGluZ3MudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jaGFyYWN0ZXJfam9icy52MSIsImVzaS1tYXJrZXRzLnJlYWRfY2hhcmFjdGVyX29yZGVycy52MSIsImVzaS1jaGFyYWN0ZXJzLnJlYWRfYmx1ZXByaW50cy52MSIsImVzaS1jb250cmFjdHMucmVhZF9jaGFyYWN0ZXJfY29udHJhY3RzLnYxIiwiZXNpLWNsb25lcy5yZWFkX2ltcGxhbnRzLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NvcnBvcmF0aW9uX3dhbGxldHMudjEiLCJlc2ktY2hhcmFjdGVycy5yZWFkX25vdGlmaWNhdGlvbnMudjEiLCJlc2ktY29ycG9yYXRpb25zLnJlYWRfZGl2aXNpb25zLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2NvcnBvcmF0aW9uX2Fzc2V0cy52MSIsImVzaS1jb3Jwb3JhdGlvbnMucmVhZF9ibHVlcHJpbnRzLnYxIiwiZXNpLWNvbnRyYWN0cy5yZWFkX2NvcnBvcmF0aW9uX2NvbnRyYWN0cy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NvcnBvcmF0aW9uX2pvYnMudjEiLCJlc2ktbWFya2V0cy5yZWFkX2NvcnBvcmF0aW9uX29yZGVycy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NoYXJhY3Rlcl9taW5pbmcudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jb3Jwb3JhdGlvbl9taW5pbmcudjEiXSwianRpIjoiY2UzNjg2NTQtZjZkNS00NTVlLWExZDQtZGVlNTExMjY2ZmZkIiwia2lkIjoiSldULVNpZ25hdHVyZS1LZXkiLCJzdWIiOiJDSEFSQUNURVI6RVZFOjIxMTMxOTc0NzAiLCJhenAiOiI5OGViOGQzMWM1ZDI0NjQ5YmE0ZjdlYjAxNTU5NmZiZCIsIm5hbWUiOiJUaXAgVG9waGFuZSIsIm93bmVyIjoiWCtSZFNGTGtlVyt3YURrclhzVnRGV1F2UlpZPSIsImV4cCI6MTU3MzE3NTc0MSwiaXNzIjoibG9naW4uZXZlb25saW5lLmNvbSJ9.BO08hYferxicVqut5ta1cwMrNpd89v-HlTQLu2wbrt71XJsVYMatdj6A3GwHyJy5vhdbSA5UX5V4IKYa9s8XIg0QsVwZ-GxYqNzZxyJT-PxXQPQvgYiepuUY9WsFqfvfvWZib50UwFlGBtjgkAcAlU3oec9WQ0jUaMWPAnEI2L_gVjIqLD6Tz_tz2jov17N9mcbmzmrLGgiUKS_B7O9RuI_Th-SmVHKCL_fEQWlt8es_FSDj9v145aRvffRte0rhH822Y8JOsU2eIOhcFrQpDIHzOkxEtsDnvqIu0DFZMJ6t1zuEN8wf7oXTU3PF3o4qfe_BtESBW0zPNHXRKAj1RQ")
				.withRefreshToken("QIqzZ6M3sEeyar2gKfdi6A==")
				.withDataSource("tranquility")
				.withScope("publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1")
				.withAssetsCount(6119)
				.withWalletBalance(2.27058387661E9)
				.withRaceName("Minmatar")
				.build();
		this.itConfigurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		this.itFileSystemAdapter = new SBFileSystemAdapter.Builder()
				.optionalApplicationDirectory( "./src/test/NeoCom.IntegrationTest/" )
				.build();
		this.itJobScheduler = new JobScheduler.Builder()
				.withCronScheduleGenerator( new HourlyCronScheduleGenerator() ).build();
		// Database setup
		final String databaseHostName = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasehost" );
//		final String containerHost = this.postgres.getContainerIpAddress();
//		final Integer containerPort = this.postgres.getFirstMappedPort();
		final String databasePath = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepath" );
		final String databaseUser = this.itConfigurationProvider.getResourceString( "P.database.neocom.databaseuser" );
		final String databasePassword = this.itConfigurationProvider.getResourceString( "P.database.neocom.databasepassword" );
		final String neocomDatabaseURL = databaseHostName +
				"/" + databasePath +
				"?user=" + databaseUser +
				"&password=" + databasePassword;
//		final String postgresTestContainerUrl = this.postgres.get
		this.itNeoComIntegrationDBAdapter = new IntegrationNeoComDBAdapter.Builder()
				.withDatabaseURLConnection( neocomDatabaseURL )
				.build();
		this.itAssetRepository = new AssetRepository.Builder()
				.withAssetDao( this.itNeoComIntegrationDBAdapter.getAssetDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.itRetrofitUniverseConnector = new RetrofitUniverseConnector.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itStoreCacheManager = new StoreCacheManager.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystem( this.itFileSystemAdapter )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		this.itEsiUniverseDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withStoreCacheManager( this.itStoreCacheManager )
				.withRetrofitUniverseConnector( this.itRetrofitUniverseConnector )
				.build();
		final LocationRepository locationRepository = Mockito.mock( LocationRepository.class );
		this.itRetrofitFactory = new NeoComRetrofitFactory.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.build();
		this.itLocationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withLocationRepository( locationRepository )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final List<GetCharactersCharacterIdAssets200Ok> testAssetList = this.loadAssetTestData();
//		final Credential credential = Mockito.mock( Credential.class );
		this.itEsiDataProvider = Mockito.mock( ESIDataAdapter.class );
		Mockito.when( this.itEsiDataProvider.getCharactersCharacterIdAssets( Mockito.any( Credential.class ) ) )
				.thenReturn( testAssetList );
		this.itRetrofitFactory.add2MockList( "getCharactersCharacterIdAssets" );
	}

	private List<GetCharactersCharacterIdAssets200Ok> loadAssetTestData() throws IOException {
//		this.mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//		ObjectMapper mapper = new ObjectMapper();
		SimpleModule testModule = new SimpleModule( "NoeComIntegrationModule",
				Version.unknownVersion() );
		testModule.addDeserializer( GetCharactersCharacterIdAssets200Ok.class,
				new GetCharactersCharacterIdAssets200OkDeserializer( GetCharactersCharacterIdAssets200Ok.class ) );
		mapper.registerModule( testModule );

		final GetCharactersCharacterIdAssets200Ok[] data = this.mapper.readValue( FileUtils.readFileToString(
				new File( this.itFileSystemAdapter.accessResource4Path( "TestData/assetTestList.json" ) ),
				"utf-8" ), GetCharactersCharacterIdAssets200Ok[].class );
		return new ArrayList<>( Arrays.asList( data ) );
	}

	private void registerJobOnScheduler() {
//		final Credential credential = Mockito.mock( Credential.class );
//		Mockito.when( credential.getAccountId() ).thenReturn( 122345 );
//		Mockito.when( credential.getAccountName() ).thenReturn( "-TEST-" );
//		Mockito.when( credential.getAccessToken() ).thenReturn( "-ACCESS-TOKEN-" );
//		Mockito.when( credential.getRefreshToken() ).thenReturn( "-REFRESH-TOKEN-" );
//		Mockito.when( credential.getDataSource() ).thenReturn( ESIDataAdapter.DEFAULT_ESI_SERVER );
//		Mockito.when( credential.getScope() ).thenReturn( "publicData" );
//		Mockito.when( credential.getUniqueId() ).thenReturn( "tranquility/12345" );

		final Job assetProcessorJob = new AssetDownloadProcessor.Builder()
				.withCredential( this.itCredential )
				.withEsiDataAdapter( this.itEsiDataProvider )
				.withLocationCatalogService( this.itLocationService )
				.withAssetRepository( this.itAssetRepository )
				.withNeoAssetConverter( new GetCharactersCharacterIdAsset2NeoAssetConverter() )
				.addCronSchedule( "* - *" )
				.build();
		this.itJobScheduler.registerJob( assetProcessorJob );
	}

//	@Test
//	void downloadAssets() {
//		this.registerJobOnScheduler();
//		this.itJobScheduler.runSchedule();
//	}
}