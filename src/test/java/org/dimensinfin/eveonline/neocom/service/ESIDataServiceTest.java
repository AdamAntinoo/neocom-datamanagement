package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CredentialConstants.TEST_CREDENTIAL_ACCOUNT_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ESIDataServiceConstants.TEST_CHARACTER_IDENTIFIER;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_AGENT;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_BASE_URL;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_TIMEOUT;

public class ESIDataServiceTest {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private final Gson gson = new Gson();
	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private IStoreCache storeCacheManager;
	private RetrofitService retrofitService;
	private LocationCatalogService locationCatalogService;

	private OkHttpClient.Builder universeClientBuilder;

	@BeforeEach
	public void beforeEach() {
		// - Build the mock instances to be received
		//		this.buildMocks();
		this.configurationService = Mockito.mock( IConfigurationService.class );
		this.fileSystem = Mockito.mock( IFileSystem.class );
		this.storeCacheManager = Mockito.mock( IStoreCache.class );
		this.retrofitService = Mockito.mock( RetrofitService.class );
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		// - HTTP access mock
		this.universeClientBuilder = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", TEST_RETROFIT_AGENT );
					return chain.proceed( builder.build() );
				} )
				.readTimeout( TEST_RETROFIT_TIMEOUT, TimeUnit.SECONDS );
	}

	@Test
	public void getCharactersCharacterId() throws IOException {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdOk testCharacterId = GetCharactersMockConstants.TEST_ESI_CHARACTER_DATA;
		final String serializedGetCharactersCharacterId = this.gson.toJson( testCharacterId );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterId ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetCharactersCharacterIdOk obtained = esiDataService.getCharactersCharacterId( TEST_CHARACTER_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof GetCharactersCharacterIdOk );
		Assertions.assertEquals( GetCharactersMockConstants.TEST_ESI_CHARACTER_NAME, obtained.getName() );
	}

	@Test
	public void getCharactersCharacterIdAssets() throws IOException {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdAssets200Ok asset1 = new GetCharactersCharacterIdAssets200Ok();
		final GetCharactersCharacterIdAssets200Ok asset2 = new GetCharactersCharacterIdAssets200Ok();
		final List<GetCharactersCharacterIdAssets200Ok> testCharacterAssets = new ArrayList<>();
		testCharacterAssets.add( asset1 );
		testCharacterAssets.add( asset2 );
		final String serializedGetCharactersCharacterIdAssets = this.gson.toJson( testCharacterAssets );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/assets/?datasource=tranquility&page=1" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdAssets ) )
				);
		interceptor.addRule()
				.pathMatches( Pattern.compile( ".*/characters/.*/assets.*" ) )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, "[]" ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdAssets200Ok> obtained = esiDataService.getCharactersCharacterIdAssets( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 2, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdAssetsException() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdAssets200Ok> obtained = esiDataService.getCharactersCharacterIdAssets( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdBlueprints() throws IOException {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdBlueprints200Ok blueprint1 = new GetCharactersCharacterIdBlueprints200Ok();
		final GetCharactersCharacterIdBlueprints200Ok blueprint2 = new GetCharactersCharacterIdBlueprints200Ok();
		final List<GetCharactersCharacterIdBlueprints200Ok> testCharacterBlueprints = new ArrayList<>();
		testCharacterBlueprints.add( blueprint1 );
		testCharacterBlueprints.add( blueprint2 );
		final String serializedGetCharactersCharacterIdBlueprints = this.gson.toJson( testCharacterBlueprints );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/blueprints/?datasource=tranquility&page=1" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdBlueprints ) )
				);
		interceptor.addRule()
				.pathMatches( Pattern.compile( ".*/characters/.*/blueprints.*" ) )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, "[]" ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdBlueprints200Ok> obtained = esiDataService.getCharactersCharacterIdBlueprints( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 2, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdBlueprintsException() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdBlueprints200Ok> obtained = esiDataService.getCharactersCharacterIdBlueprints( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdException() {
		// Given
		final Integer characterId = TEST_CHARACTER_IDENTIFIER;
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		Assertions.assertNull( esiDataService.getCharactersCharacterId( characterId ) );
	}

	@Test
	public void getCharactersCharacterIdIndustryJobs() throws IOException {
		// Given
		final Integer TEST_ESI_CHARACTER_INDUSTRY_JOB_ID = 32154365;
		final Integer TEST_ESI_CHARACTER_BLUEPRINT_ID = 32154365;
		final Credential credential = Mockito.mock( Credential.class );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> testCharacterIndustryJobs = new ArrayList();
		final GetCharactersCharacterIdIndustryJobs200Ok industryJob = new GetCharactersCharacterIdIndustryJobs200Ok();
		industryJob.setJobId( TEST_ESI_CHARACTER_INDUSTRY_JOB_ID );
		industryJob.setBlueprintTypeId( TEST_ESI_CHARACTER_BLUEPRINT_ID );
		testCharacterIndustryJobs.add( industryJob );
		final String serializedGetCharactersCharacterIdIndustryJobs = this.gson.toJson( testCharacterIndustryJobs );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/industry/jobs/?datasource=tranquility&include_completed=false" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdIndustryJobs ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> obtained = esiDataService.getCharactersCharacterIdIndustryJobs( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1, obtained.size() );
		Assertions.assertEquals( TEST_ESI_CHARACTER_INDUSTRY_JOB_ID, obtained.get( 0 ).getJobId() );
		Assertions.assertEquals( TEST_ESI_CHARACTER_BLUEPRINT_ID, obtained.get( 0 ).getBlueprintTypeId() );
	}

	@Test
	public void getCharactersCharacterIdLocation() throws IOException {
		final Integer TEST_ESI_CHARACTER_LOCATION_SYSTEM_ID = 30002516;
		final Integer TEST_ESI_CHARACTER_LOCATION_STATION_ID = 60010408;
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdLocationOk testCharacterLocation = new GetCharactersCharacterIdLocationOk();
		testCharacterLocation.setStationId( TEST_ESI_CHARACTER_LOCATION_STATION_ID );
		testCharacterLocation.setSolarSystemId( TEST_ESI_CHARACTER_LOCATION_SYSTEM_ID );
		final String serializedGetCharactersCharacterIdLocation = this.gson.toJson( testCharacterLocation );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/location/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdLocation ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetCharactersCharacterIdLocationOk obtained = esiDataService.getCharactersCharacterIdLocation( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_ESI_CHARACTER_LOCATION_SYSTEM_ID, obtained.getSolarSystemId() );
		Assertions.assertEquals( TEST_ESI_CHARACTER_LOCATION_STATION_ID, obtained.getStationId() );
	}

	@Test
	public void getCharactersCharacterIdOrders() throws IOException {
		// Given
		final Long TEST_ESI_CHARACTER_LOCATION_ID = 30002516L;
		final Credential credential = Mockito.mock( Credential.class );
		final List<GetCharactersCharacterIdOrders200Ok> testCharacterOrders = new ArrayList<>();
		final GetCharactersCharacterIdOrders200Ok order = new GetCharactersCharacterIdOrders200Ok();
		order.setIsBuyOrder( true );
		order.setLocationId( (long) TEST_ESI_CHARACTER_LOCATION_ID );
		testCharacterOrders.add( order );
		final String serializedGetCharactersCharacterIdOrders = this.gson.toJson( testCharacterOrders );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/orders/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdOrders ) )
				);
		interceptor.addRule()
				.pathMatches( Pattern.compile( ".*/characters/.*/assets.*" ) )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, "[]" ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final List<GetCharactersCharacterIdOrders200Ok> obtained = esiDataService.getCharactersCharacterIdOrders( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1, obtained.size() );
		Assertions.assertTrue( obtained.get( 0 ).getIsBuyOrder() );
		Assertions.assertEquals( TEST_ESI_CHARACTER_LOCATION_ID, obtained.get( 0 ).getLocationId() );
	}

	@Test
	public void getCharactersCharacterIdShip() throws IOException {
		final String TEST_ESI_CHARACTER_SHIP_NAME = "-TEST-SHIP-NAME-";
		final Integer TEST_ESI_CHARACTER_SHIP_TYPE_ID = 5567;
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdShipOk testCharacterShip = new GetCharactersCharacterIdShipOk();
		testCharacterShip.setShipName( TEST_ESI_CHARACTER_SHIP_NAME );
		testCharacterShip.setShipTypeId( TEST_ESI_CHARACTER_SHIP_TYPE_ID );
		final String serializedGetCharactersCharacterIdShip = this.gson.toJson( testCharacterShip );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/ship/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdShip ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetCharactersCharacterIdShipOk obtained = esiDataService.getCharactersCharacterIdShip( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_ESI_CHARACTER_SHIP_NAME, obtained.getShipName() );
		Assertions.assertEquals( TEST_ESI_CHARACTER_SHIP_TYPE_ID, obtained.getShipTypeId() );
	}

	@Test
	public void getCharactersCharacterIdSkills() throws IOException {
		final Long TEST_ESI_CHARACTER_TOTAL_SP = 432765L;
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdSkillsOk testCharacterSkills = new GetCharactersCharacterIdSkillsOk();
		testCharacterSkills.setTotalSp( TEST_ESI_CHARACTER_TOTAL_SP );
		final String serializedGetCharactersCharacterIdSkills = this.gson.toJson( testCharacterSkills );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/skills/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdSkills ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetCharactersCharacterIdSkillsOk obtained = esiDataService.getCharactersCharacterIdSkills( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_ESI_CHARACTER_TOTAL_SP, obtained.getTotalSp() );
		//		Assertions.assertEquals( TEST_ESI_CHARACTER_SHIP_TYPE_ID, obtained.getShipTypeId() );
	}

	@Test
	public void getCharactersCharacterIdWallet() throws IOException {
		final Double TEST_ESI_CHARACTER_WALLET_AMOUNT = 543987.78;
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final Double testCharacterWallet = TEST_ESI_CHARACTER_WALLET_AMOUNT;
		final String serializedGetCharactersCharacterIdWallet = this.gson.toJson( testCharacterWallet );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/characters/" + TEST_CREDENTIAL_ACCOUNT_ID + "/wallet/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdWallet ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final Double obtained = esiDataService.getCharactersCharacterIdWallet( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_ESI_CHARACTER_WALLET_AMOUNT, obtained, 0.01 );
	}

	@Test
	public void getRegionMarketHub() {
		// Given
		final Integer ESI_DATA_JITA_REGION_ID = 10000002;
		final String ESI_DATA_JITA_REGION_NAME = "The Forge";
		final Long ESI_DATA_JITA_STATION_ID = 10000002L;
		final String ESI_DATA_JITA_STATION_NAME = "The Forge";
		final Station jitaLocation = Mockito.mock( Station.class );
		// When
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( jitaLocation );
		Mockito.when( jitaLocation.getLocationType() ).thenReturn( LocationIdentifierType.STATION );
		Mockito.when( jitaLocation.getRegionId() ).thenReturn( ESI_DATA_JITA_REGION_ID );
		Mockito.when( jitaLocation.getRegionName() ).thenReturn( ESI_DATA_JITA_REGION_NAME );
		Mockito.when( jitaLocation.getStationId() ).thenReturn( ESI_DATA_JITA_STATION_ID );
		Mockito.when( jitaLocation.getStationName() ).thenReturn( ESI_DATA_JITA_STATION_NAME );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final Station obtained = esiDataService.getRegionMarketHub( 10000015 );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( LocationIdentifierType.STATION, obtained.getLocationType() );
		Assertions.assertEquals( ESI_DATA_JITA_REGION_ID, obtained.getRegionId() );
		Assertions.assertEquals( ESI_DATA_JITA_REGION_NAME, obtained.getRegionName() );
		Assertions.assertEquals( ESI_DATA_JITA_STATION_ID, obtained.getStationId() );
		Assertions.assertEquals( ESI_DATA_JITA_STATION_NAME, obtained.getStationName() );
	}

	@Test
	public void getUniverseConstellationById() {
		// Given
		final GetUniverseConstellationsConstellationIdOk constellation = Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		// When
		Mockito.when( this.locationCatalogService.getUniverseConstellationById( Mockito.anyInt() ) ).thenReturn( constellation );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetUniverseConstellationsConstellationIdOk obtained = esiDataService.getUniverseConstellationById( 10000015 );
		// Assertions
		Assertions.assertNotNull( obtained );
	}

	@Test
	public void getUniverseRegionById() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		// When
		Mockito.when( this.locationCatalogService.getUniverseRegionById( Mockito.anyInt() ) ).thenReturn( region );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetUniverseRegionsRegionIdOk obtained = esiDataService.getUniverseRegionById( 10000015 );
		// Assertions
		Assertions.assertNotNull( obtained );
	}

	@Test
	public void getUniverseStationById() {
		// Given
		final GetUniverseStationsStationIdOk station = Mockito.mock( GetUniverseStationsStationIdOk.class );
		// When
		Mockito.when( this.locationCatalogService.getUniverseStationById( Mockito.anyInt() ) ).thenReturn( station );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetUniverseStationsStationIdOk obtained = esiDataService.getUniverseStationById( 10000015 );
		// Assertions
		Assertions.assertNotNull( obtained );
	}

	@Test
	public void getUniverseSystemById() {
		// Given
		final GetUniverseSystemsSystemIdOk system = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		// When
		Mockito.when( this.locationCatalogService.getUniverseSystemById( Mockito.anyInt() ) ).thenReturn( system );
		// Test
		final ESIDataService esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.storeCacheManager,
				this.retrofitService,
				this.locationCatalogService );
		final GetUniverseSystemsSystemIdOk obtained = esiDataService.getUniverseSystemById( 10000015 );
		// Assertions
		Assertions.assertNotNull( obtained );
	}

	private Retrofit getNewUniverseConnector( final OkHttpClient client ) {
		return new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( client )
				.build();
	}

	public static class GetCharactersMockConstants {
		public static final GetCharactersCharacterIdOk TEST_ESI_CHARACTER_DATA = new GetCharactersCharacterIdOk();
		public static final String TEST_ESI_CHARACTER_NAME = "-TEST_ESI_CHARACTER_NAME-";
		public static final String TEST_ESI_CHARACTER_DESCRIPTION = "-TEST_ESI_CHARACTER_DESCRIPTION-";
		public static final Integer TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER = 98384726;
		public static final Integer TEST_ESI_CHARACTER_RACE_IDENTIFIER = 100;
		public static final Integer TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER = 200;
		public static final Integer TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER = 300;
		public static final Float TEST_ESI_CHARACTER_SECURITY_STATUS = 0.5F;

		public static final Integer TEST_ESI_UNIVERSE_JOB_ID = 98384726;
		public static final Integer TEST_ESI_UNIVERSE_JOB_INSTALLER_ID = 98384726;

		static {
			TEST_ESI_CHARACTER_DATA.setName( TEST_ESI_CHARACTER_NAME );
			TEST_ESI_CHARACTER_DATA.setCorporationId( TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER );
			//			TEST_ESI_CHARACTER_DATA.setBirthday( new DateTime() );
			TEST_ESI_CHARACTER_DATA.setRaceId( TEST_ESI_CHARACTER_RACE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setAncestryId( TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setBloodlineId( TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setDescription( TEST_ESI_CHARACTER_DESCRIPTION );
			TEST_ESI_CHARACTER_DATA.setGender( GetCharactersCharacterIdOk.GenderEnum.MALE );
			TEST_ESI_CHARACTER_DATA.setSecurityStatus( TEST_ESI_CHARACTER_SECURITY_STATUS );
		}
	}
}