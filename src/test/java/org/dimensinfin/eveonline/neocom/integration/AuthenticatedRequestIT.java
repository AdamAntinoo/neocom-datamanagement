package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth2Flow;
import org.dimensinfin.eveonline.neocom.auth.TokenVerification;
import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_OAUTH_AUTHORIZATION_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CALLBACK;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL;

public class AuthenticatedRequestIT {
	private static final String CURRENT_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0" +
			".eyJqdGkiOiJlNDg4YTUyYi1iZWEzLTRiNzUtYTg3OC1jYTk4OWUzODA3YWQiLCJraWQiOiJKV1QtU2lnbmF0dXJlLUtleSIsInN1YiI6IkNIQVJBQ1RFUjpFVkU6MjExMzE5NzQ3MCIsImF6cCI6Ijk4ZWI4ZDMxYzVkMjQ2NDliYTRmN2ViMDE1NTk2ZmJkIiwibmFtZSI6IlRpcCBUb3BoYW5lIiwib3duZXIiOiJYK1JkU0ZMa2VXK3dhRGtyWHNWdEZXUXZSWlk9IiwiZXhwIjoxNTczMTYyMjU2LCJpc3MiOiJsb2dpbi5ldmVvbmxpbmUuY29tIn0.TfjObEi-fmMpcc_1Umzpvay_vx15EHJX1ketkM0fANsJcUHMWMp0cKp7XFaQE99BEuq-jwBZakF983JR-niDSamSrubTv9xVeDLFkkYh8QS1nMiWhBXN0SxYJFfVpWIxVcW9RVgHDSgumhpoQKFq4pz7lnkFeZypNeLD-Pot0G_byL1yCPm3D8aRNi8ERtbO5bAspCsMVNq861Ugzyg9fih4gF9yaEPxyv9rfPXIyw4Gmk5PUrwOytYxegxV8YAKfhIyJwYNh-jr_FgnY8k_U3j8ffgIAgdo4Tck8JTEm8GC97LZsQRumJxk_ny35mNWmaIdYDiic2YmEgJGXWYrFQ";
	private static final String SCOPES = "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";
	private static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );

	private IConfigurationService configurationProvider;
	private NeoComOAuth2Flow flow;
	private String STATE;

	private List<String> constructScopes( final String data ) {
		final List<String> resultScopes = new ArrayList<>();
//		resultScopes.add( "publicData" );
		final String[] scopes = data.split( " " );
		for (int i = 0; i < scopes.length; i++)
			resultScopes.add( scopes[i] );
		return resultScopes;
	}

	private void setupAuthentication( final String code ) {
		STATE = this.configurationProvider.getResourceString( ESI_OAUTH_AUTHORIZATION_STATE );
		final String dataSource = "Tranquility".toLowerCase();
		this.flow = new NeoComOAuth2Flow.Builder().withConfigurationService( this.configurationProvider ).build();
		this.flow.onStartFlow( code, STATE, dataSource );
	}

	private void setupEnvironment() throws IOException {
//		this.configurationProvider = new TestConfigurationService.Builder()
//				.optionalPropertiesDirectory( "/src/test/resources/properties.unittest" ).build();
	}

	//	@Test
	void createAuthenticatedClient() throws IOException {
		this.setupEnvironment();
		this.setupAuthentication( "0Cw8DQXo_0C3OgPYQPMnHg" );
		final TokenVerification tokenStore = this.flow.onTranslationStep();
		final Long structureId = 1031243921503L;
		final String esiDataServerLocation = "https://esi.evetech.net/latest/";
		final String CLIENT_ID = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		final String SECRET_KEY = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY );
		final String CALLBACK = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CALLBACK );
		final String AGENT = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_AGENT,
				"Default agent" );
		final NeoComOAuth20 neoComOAuth20 = new NeoComOAuth20.Builder()
				.withClientId( CLIENT_ID )
				.withClientKey( SECRET_KEY )
				.withCallback( CALLBACK )
				.withAgent( AGENT )
				.withStore( ESIStore.DEFAULT )
				.withScopes( tokenStore.getScopes() )
				.withState( STATE )
				.withBaseUrl( this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL
						, "https://login.eveonline.com/" ) )
				.withAccessTokenEndpoint( "oauth/token")
				.withAuthorizationBaseUrl( "oauth/authorize" )
				.build();


		final OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "accept", "application/json" )
							.addHeader( "User-Agent", AGENT );
					return chain.proceed( builder.build() );
				} )
				.addInterceptor( chain -> {
					okhttp3.Response r = chain.proceed( chain.request() );
					if (r.isSuccessful()) {
						return r;
					}
					if (r.body().string().contains( "invalid_token" )) {
						neoComOAuth20.fromRefresh( tokenStore.getRefreshToken() );
						r = chain.proceed( chain.request() );
					}
					return r;
				} )
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl( esiDataServerLocation )
				.addConverterFactory( GSON_CONVERTER_FACTORY )
				.client( httpClient )
				.build();
		// Get the data from a public structure.
		final Response<GetUniverseStructuresStructureIdOk> dataResponse = retrofit.create( UniverseApi.class )
				.getUniverseStructuresStructureId( structureId, tokenStore.getDataSource(), null,
						tokenStore.getAccessToken() )
				.execute();
		if (dataResponse.isSuccessful()) {
			NeoComLogger.info( "character: " + tokenStore.getVerifyCharacterResponse().toString() );
			NeoComLogger.info( "token: " + tokenStore.getTokenTranslationResponse().toString() );
			NeoComLogger.info( "data: " + dataResponse.body().toString() );
		} else
			NeoComLogger.info( "Exception: " + dataResponse.toString() );
	}
}
