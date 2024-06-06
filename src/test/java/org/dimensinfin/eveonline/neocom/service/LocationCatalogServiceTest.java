package org.dimensinfin.eveonline.neocom.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.Structure;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.support.InstanceGenerator;
import org.dimensinfin.eveonline.neocom.utility.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.utility.GSONLocalDateDeserializer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_AGENT;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_BASE_URL;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_TIMEOUT;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_ACCESIBLE_STRUCTURE_LOCATION_ID;

public class LocationCatalogServiceTest {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final Long TEST_LOCATION_ID = 10000003L;
	private final Gson gson = new Gson();
	private RetrofitService retrofitService;
	private OkHttpClient.Builder universeClientBuilder;
	private IDataStore dataStore;

	@BeforeEach
	public void beforeEach() {
		this.retrofitService = Mockito.mock( RetrofitService.class );
		// - HTTP access mock
		this.universeClientBuilder = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", TEST_RETROFIT_AGENT );
					return chain.proceed( builder.build() );
				} )
				.readTimeout( TEST_RETROFIT_TIMEOUT, TimeUnit.SECONDS );
		this.dataStore = Mockito.mock( IDataStore.class );
	}

	@Test
	public void cleanLocationsCache() {
		// Given
		final GetUniverseRegionsRegionIdOk testLocationRegion = new GetUniverseRegionsRegionIdOk();
		testLocationRegion.setRegionId( TEST_LOCATION_ID.intValue() );
		final String serializedGetUniverseRegionsRegionId = this.gson.toJson( testLocationRegion );
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( "http://localhost/universe/regions/" + TEST_LOCATION_ID + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseRegionsRegionId ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		// When
		Mockito.when( this.retrofitService.accessUniverseConnector() )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		// Test
		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		locationCatalogService.cleanLocationsCache();
		int count = locationCatalogService.cleanLocationsCache();
		// Assertions
		Assertions.assertEquals( 0, count );
		locationCatalogService.searchLocation4Id( TEST_LOCATION_ID );
		count = locationCatalogService.cleanLocationsCache();
		Assertions.assertEquals( 1, count );
	}

	@Test
	public void constructorContract() {
		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		Assertions.assertNotNull( locationCatalogService );
	}

	@Test
	void lookupLocation4Id_for_nonCached_structure_accesible() {
		// Given
		final Long locationIdentifier = TEST_ACCESIBLE_STRUCTURE_LOCATION_ID;
		final Credential credential = new InstanceGenerator().getCredential();
		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		final GetUniverseStructuresStructureIdOk accessibleStructure = new GetUniverseStructuresStructureIdOk()
				.name( "-STRUCTURE-NAME-" )
				.solarSystemId( 64000001 );
		final String serializedGetUniverseStructuresStructureIdOk = this.gson.toJson( accessibleStructure );
		final GetUniverseSystemsSystemIdOk solarSystem = new GetUniverseSystemsSystemIdOk()
				.systemId( 64000001 )
				.name( "-SYSTEM_NAME-" )
				.constellationId( 40008494 );
		final String serializedGetUniverseSystemsSystemIdOk = this.gson.toJson( solarSystem );
		final GetUniverseConstellationsConstellationIdOk constellation = new GetUniverseConstellationsConstellationIdOk()
				.constellationId( 40008494 )
				.name( "-CONSTELLATION-NAME-" )
				.regionId( 10008494 );
		final String serializedGetUniverseConstellationsConstellationIdOk = this.gson.toJson( constellation );
		final GetUniverseRegionsRegionIdOk region = new GetUniverseRegionsRegionIdOk()
				.regionId( 10008494 )
				.name( "-REGION-" );
		final String serializedGetUniverseRegionsRegionIdOk = this.gson.toJson( region );
		// When
		final MockInterceptor interceptor = new MockInterceptor();
		interceptor.addRule()
				.get()
				.url( TEST_RETROFIT_BASE_URL + "/universe/structures/" + locationIdentifier + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseStructuresStructureIdOk ) )
				);
		interceptor.addRule()
				.get()
				.url( TEST_RETROFIT_BASE_URL + "/universe/systems/" + 64000001 + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseSystemsSystemIdOk ) )
				);
		interceptor.addRule()
				.get()
				.url( TEST_RETROFIT_BASE_URL + "/universe/constellations/" + 40008494 + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseConstellationsConstellationIdOk ) )
				);
		interceptor.addRule()
				.get()
				.url( TEST_RETROFIT_BASE_URL + "/universe/regions/" + 10008494 + "/?datasource=tranquility" )
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseRegionsRegionIdOk ) )
				);
		final OkHttpClient httpClient = this.universeClientBuilder.addInterceptor( interceptor ).build();
		Mockito.when( this.retrofitService.accessAuthenticatedConnector( Mockito.any( Credential.class ) ) )
				.thenReturn( this.getNewAuthenticatedConnector( httpClient ) );
		Mockito.when( this.retrofitService.accessUniverseConnector() )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( "-STRUCTURE-NAME-", ((Structure) sut.get()).getStructureName() );
		Assertions.assertEquals( 64000001, ((Structure) sut.get()).getStructureId() );
	}

	private Retrofit getNewUniverseConnector( final OkHttpClient client ) {
		return new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( client )
				.build();
	}

	private Retrofit getNewAuthenticatedConnector( final OkHttpClient client ) {
		return new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( client )
				.build();
	}

	private LocationCatalogService getLocationCatalogService() {
		return new LocationCatalogService( this.retrofitService, this.dataStore );
	}
}