package org.dimensinfin.eveonline.neocom.service;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.utility.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.utility.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

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
	}

	@Test
	public void cleanLocationsCache() {
		// Given
		final GetUniverseRegionsRegionIdOk testLocationRegion =new GetUniverseRegionsRegionIdOk();
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
		Mockito.when( this.retrofitService.accessUniverseConnector(  ) )
				.thenReturn( this.getNewUniverseConnector( httpClient ) );
		// Test
		final LocationCatalogService locationCatalogService = new LocationCatalogService( this.retrofitService );
		int count = locationCatalogService.cleanLocationsCache();
		// Assertions
		Assertions.assertEquals( 0, count );
		locationCatalogService.searchLocation4Id( TEST_LOCATION_ID );
		count = locationCatalogService.cleanLocationsCache();
		Assertions.assertEquals( 1, count );
	}

	@Test
	public void constructorContract() {
		final LocationCatalogService locationCatalogService = new LocationCatalogService( this.retrofitService );
		Assertions.assertNotNull( locationCatalogService );
	}

	private Retrofit getNewUniverseConnector( final OkHttpClient client ) {
		return new Retrofit.Builder()
				.baseUrl( TEST_RETROFIT_BASE_URL )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( client )
				.build();
	}
}