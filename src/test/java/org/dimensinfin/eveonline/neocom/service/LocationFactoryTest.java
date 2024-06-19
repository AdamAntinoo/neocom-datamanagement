package org.dimensinfin.eveonline.neocom.service;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;
import org.dimensinfin.eveonline.neocom.support.InstanceGenerator;
import org.dimensinfin.eveonline.neocom.utility.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.utility.GSONLocalDateDeserializer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_AGENT;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.RetrofitFactoryConstants.TEST_RETROFIT_TIMEOUT;
import static org.junit.jupiter.api.Assertions.*;

class LocationFactoryTest {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final Long TEST_LOCATION_ID = 10000003L;
	private final Gson gson = new Gson();
	private RetrofitService retrofitService;
	private ILocationFactoryPort locationFactory;
	private OkHttpClient.Builder universeClientBuilder;
	private IDataStore dataStore;

	private OkHttpClient httpClient;

	@BeforeEach
	public void beforeEach() {
		this.retrofitService = Mockito.mock( RetrofitService.class );
		this.locationFactory = Mockito.mock( ILocationFactoryPort.class );

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
	void buildUpLocation4LocationId() {
	}
	private MockInterceptor addStructureRule( final MockInterceptor interceptor ) {
		final GetUniverseStructuresStructureIdOk accessibleStructure = new GetUniverseStructuresStructureIdOk()
				.name( "-STRUCTURE-NAME-" )
				.solarSystemId( 64000001 );
		final String serializedGetUniverseStructuresStructureIdOk = this.gson.toJson( accessibleStructure );
		interceptor.addRule()
				.pathStarts( "/universe/structures/" )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseStructuresStructureIdOk ) )
				);
		return interceptor;
	}

	private MockInterceptor addStationRule( final MockInterceptor interceptor ) {
		final GetUniverseStationsStationIdOk station = new InstanceGenerator().getGetUniverseStationsStationIdOk();
		final String serializedGetUniverseStationsStationIdOk = this.gson.toJson( station );
		interceptor.addRule()
				.get()
				.pathStarts( "/universe/stations/" )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseStationsStationIdOk ) )
				);
		return interceptor;
	}

	private MockInterceptor addSystemRule( final MockInterceptor interceptor ) {
		final GetUniverseSystemsSystemIdOk solarSystem = new GetUniverseSystemsSystemIdOk()
				.systemId( 64000001 )
				.name( "-SYSTEM_NAME-" )
				.constellationId( 40008494 );
		final String serializedGetUniverseSystemsSystemIdOk = this.gson.toJson( solarSystem );
		interceptor.addRule()
				.get()
				.pathStarts( "/universe/systems/" )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseSystemsSystemIdOk ) )
				);
		return interceptor;
	}

	private MockInterceptor addConstellationRule( final MockInterceptor interceptor ) {
		final GetUniverseConstellationsConstellationIdOk constellation = new GetUniverseConstellationsConstellationIdOk()
				.constellationId( 40008494 )
				.name( "-CONSTELLATION-NAME-" )
				.regionId( 10008494 );
		final String serializedGetUniverseConstellationsConstellationIdOk = this.gson.toJson( constellation );
		interceptor.addRule()
				.get()
				.pathStarts( "/universe/constellations/" )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseConstellationsConstellationIdOk ) )
				);
		return interceptor;
	}

	private MockInterceptor addRegionRule( final MockInterceptor interceptor ) {
		final GetUniverseRegionsRegionIdOk region = new GetUniverseRegionsRegionIdOk()
				.regionId( 10008494 )
				.name( "-REGION-" );
		final String serializedGetUniverseRegionsRegionIdOk = this.gson.toJson( region );
		interceptor.addRule()
				.get()
				.pathStarts( "/universe/regions/" )
				.anyTimes()
				.answer( request -> new Response.Builder()
						.code( 200 )
						.body( ResponseBody.create( MEDIATYPE_JSON, serializedGetUniverseRegionsRegionIdOk ) )
				);
		return interceptor;
	}

}