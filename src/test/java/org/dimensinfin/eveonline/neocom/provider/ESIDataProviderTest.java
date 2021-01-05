package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;

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
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiUniverseTestDataConstants.TEST_ESI_CHARACTER_DATA;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiUniverseTestDataConstants.TEST_ESI_CHARACTER_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_AGENT;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_BASE_URL;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_TIMEOUT;

public class ESIDataProviderTest {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private final Gson gson = new Gson();
	private LocationCatalogService locationCatalogService;
	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private StoreCacheManager storeCacheManager;
	private RetrofitFactory retrofitFactory;
	private OkHttpClient universeClient;
	private OkHttpClient.Builder universeClientBuilder;
	private MockInterceptor interceptor;
	private Retrofit universeConnector;

	@BeforeEach
	public void beforeEach() throws JsonProcessingException {
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.configurationService = Mockito.mock( IConfigurationService.class );
		this.fileSystem = Mockito.mock( IFileSystem.class );
		this.storeCacheManager = Mockito.mock( StoreCacheManager.class );
		this.retrofitFactory = Mockito.mock( RetrofitFactory.class );

		this.universeClientBuilder = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", TEST_RETROFIT_AGENT );
					return chain.proceed( builder.build() );
				} )
				.readTimeout( TEST_RETROFIT_TIMEOUT, TimeUnit.SECONDS );

		final GetCharactersCharacterIdAssets200Ok asset1 = new GetCharactersCharacterIdAssets200Ok();
		final GetCharactersCharacterIdAssets200Ok asset2 = new GetCharactersCharacterIdAssets200Ok();
		final List<GetCharactersCharacterIdAssets200Ok> testCharacterAssets = new ArrayList<>();
		testCharacterAssets.add( asset1 );
		testCharacterAssets.add( asset2 );
		final String serializedGetCharactersCharacter = this.gson.toJson( TEST_ESI_CHARACTER_DATA );
		final String serializedGetCharactersCharacterIdAssets = this.gson.toJson( testCharacterAssets );

		this.interceptor = new MockInterceptor();
		this.interceptor.addRule()
				.pathMatches( Pattern.compile( ".*/characters/.*" ) )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacter ) )
				);
		this.interceptor.addRule()
				.pathMatches( Pattern.compile( ".*/characters/.*/assets/.*" ) )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetCharactersCharacterIdAssets ) )
				);
		this.universeClient = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", TEST_RETROFIT_AGENT );
					return chain.proceed( builder.build() );
				} )
				.addInterceptor( interceptor )
				.readTimeout( TEST_RETROFIT_TIMEOUT, TimeUnit.SECONDS )
				.build();
		this.universeConnector = new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( this.universeClient )
				.build();
	}

	@Test
	public void getCharactersCharacterId() {
		// Given
		final Integer characterId = TEST_CHARACTER_IDENTIFIER;
		// When
		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenReturn( this.universeConnector );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		final GetCharactersCharacterIdOk obtained = esiDataProvider.getCharactersCharacterId( characterId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof GetCharactersCharacterIdOk );
		Assertions.assertEquals( TEST_ESI_CHARACTER_NAME, obtained.getName() );
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
				.url( "http://localhost/characters/92223647/assets/?datasource=tranquility&page=1" )
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
		Mockito.when( this.retrofitFactory.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		final List<GetCharactersCharacterIdAssets200Ok> obtained = esiDataProvider.getCharactersCharacterIdAssets( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 2, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdAssetsException() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		// When
		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		final List<GetCharactersCharacterIdAssets200Ok> obtained = esiDataProvider.getCharactersCharacterIdAssets( credential );
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
				.url( "http://localhost/characters/92223647/blueprints/?datasource=tranquility&page=1" )
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
		Mockito.when( this.retrofitFactory.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ACCOUNT_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		final List<GetCharactersCharacterIdBlueprints200Ok> obtained = esiDataProvider.getCharactersCharacterIdBlueprints( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 2, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdBlueprintsException() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		// When
		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		final List<GetCharactersCharacterIdBlueprints200Ok> obtained = esiDataProvider.getCharactersCharacterIdBlueprints( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	@Test
	public void getCharactersCharacterIdException() {
		// Given
		final Integer characterId = TEST_CHARACTER_IDENTIFIER;
		// When
		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenThrow( RuntimeException.class );
		// Test
		final ESIDataProvider esiDataProvider = new ESIDataProvider.Builder()
				.withLocationCatalogService( this.locationCatalogService )
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withStoreCacheManager( this.storeCacheManager )
				.withRetrofitFactory( this.retrofitFactory )
				.build();
		Assertions.assertNull( esiDataProvider.getCharactersCharacterId( characterId ) );
	}

	private Retrofit getNewUniverseConnector( final OkHttpClient client ) {
		return new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( client )
				.build();
	}
}
