package org.dimensinfin.eveonline.neocom.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticatedRequestIT {
	private static final String CURRENT_TOKEN = "1|CfDJ8O+5Z0aH+aBNj61BXVSPWfiuSOW/ZVEyRif/g8GTH5L5eRfFyX4dJjkZm79N0/MJtEEfuuc5jG5MMPJwqZk82qSS/kxYIzGp+FcDwrlnBXvOVt+aEyZVAv/B68xPFaNLAm+GB9F92Nj4jv0bmJKF3htPtmCJIN9AMMVA6Xo+hrt9";
	private static final String SCOPES = "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";
	private IConfigurationProvider configurationProvider;

	@Test
	void createAuthenticatedClient() throws IOException {
		final Integer characterId = 2113197470;
		final String dataSource = "Tranquility".toLowerCase();
		final Long structureId = 1031243921503L;
		final String accessToken = CURRENT_TOKEN;
		final String refreshToken = "9CZ7fZpk8xBqS3knXMJVmurr06IL-oq9S5rM-EIUF0b8sAeZOyD6NvbFkojAXzsi0rqySj8UX-Vx96ro432KnwW6FTjx-ovJEUDbojKzyZyzutMHAvMYWIPwBfAqp92jg8Fv4hTxXegBoeiqoUy-4veuEMyERES0xrsB7gJux8ZQXqOAP0fVdEZvXQyEhj5X";
		final String esiDataServerLocation = "https://esi.evetech.net/latest/";
		final Converter.Factory GSON_CONVERTER_FACTORY =
				GsonConverterFactory.create(
						new GsonBuilder()
								.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
								.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
								.create() );
		this.configurationProvider = new SBConfigurationProvider.Builder()
				.withPropertiesDirectory( "/src/test/resources/properties.it" ).build();
		final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.clientid" );
		final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.secretkey" );
		final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.callback" );
		final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
				"Default agent" );
		final String STATE = this.configurationProvider.getResourceString( "P.esi.authorization.state" );
		final NeoComOAuth20 neoComOAuth20 = new NeoComOAuth20.Builder()
				.withClientId( CLIENT_ID )
				.withClientKey( SECRET_KEY )
				.withCallback( CALLBACK )
				.withAgent( AGENT )
				.withStore( ESIStore.DEFAULT )
				.withScopes( this.constructScopes( SCOPES ) )
				.withState( STATE )
				.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.server"
						, "https://login.eveonline.com/" ) )
				.withAccessTokenEndpoint(
						this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.accesstoken.url"
								, "oauth/token" ) )
				.withAuthorizationBaseUrl(
						this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.authorize.url"
								, "oauth/authorize" ) )
				.build();


		final OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor( chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", AGENT );
					return chain.proceed( builder.build() );
				} )
				.addInterceptor( chain -> {
					okhttp3.Response r = chain.proceed( chain.request() );
					if (r.isSuccessful()) {
						return r;
					} else {
//					if (r.body().string().contains( "invalid_token" )) {
						neoComOAuth20.fromRefresh( refreshToken );
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
				.getUniverseStructuresStructureId( structureId, dataSource, null, accessToken )
				.execute();
		if (dataResponse.isSuccessful())
			NeoComLogger.info( "data: " + dataResponse.body().toString() );
		else
			NeoComLogger.info( "Exception: " + dataResponse.toString() );
	}

	private List<String> constructScopes( final String data ) {
		final List<String> resultScopes = new ArrayList<>();
//		resultScopes.add( "publicData" );
		final String[] scopes = data.split( " " );
		for (int i = 0; i < scopes.length; i++)
			resultScopes.add( scopes[i] );
		return resultScopes;
	}

//	private void createClient() {
//		OkHttpClient.Builder retrofitClient =
//				new OkHttpClient.Builder()
//						.addInterceptor( chain -> {
//							Request.Builder builder = chain.request().newBuilder()
//									.addHeader( "User-Agent", this.agent );
//							return chain.proceed( builder.build() );
//						} )
//						.addInterceptor( chain -> {
//							if (StringUtils.isBlank( getRefreshToken() )) {
//								return chain.proceed( chain.request() );
//							}
//
//							Request.Builder builder = chain.request().newBuilder();
//							final TokenTranslationResponse token = this.neoComOAuth20.fromRefresh( getRefreshToken() );
//							if (null != token) {
//								builder.addHeader( "Authorization", "Bearer " + token.getAccessToken() );
//							}
//							return chain.proceed( builder.build() );
//						} )
//						.addInterceptor( chain -> {
//							if (StringUtils.isBlank( getRefreshToken() )) {
//								return chain.proceed( chain.request() );
//							}
//
//							okhttp3.Response r = chain.proceed( chain.request() );
//							if (r.isSuccessful()) {
//								return r;
//							}
//							if (r.body().string().contains( "invalid_token" )) {
//								this.neoComOAuth20.fromRefresh( getRefreshToken() );
//								r = chain.proceed( chain.request() );
//							}
//							return r;
//						} );
//
//		if (this.timeout != -1) {
//			retrofitClient.readTimeout( this.timeout, TimeUnit.MILLISECONDS );
//		}
//
//		if (null != this.cacheDataFile) {
//			retrofitClient.cache( new Cache( this.cacheDataFile, this.cacheSize ) );
//		}
//
//		OkHttpClient httpClient = retrofitClient
//				.certificatePinner(
//						new CertificatePinner.Builder()
//								.add( "login.eveonline.com", "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
//								.add( "login.eveonline.com", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
//								.add( "login.eveonline.com", "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
//								.build() )
//				.build();
//		return new Retrofit.Builder()
//				.baseUrl( this.esiDataServerLocation )
//				.addConverterFactory( GSON_CONVERTER_FACTORY )
//				.client( httpClient )
//				.build();
//
//	}
//
//	protected NeoComOAuth20 getConfiguredOAuth( final String selector ) {
//		Objects.requireNonNull( selector );
//		final List<String> scopes = this.constructScopes( this.configurationProvider.getResourceString( "P.esi."
//				+ selector.toLowerCase() + ".authorization.scopes.filename" ) );
//		NeoComOAuth20 auth = null;
//		if ("TRANQUILITY".equalsIgnoreCase( selector )) {
//			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.clientid" );
//			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.secretkey" );
//			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.callback" );
//			final String AGENT = this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.agent",
//					"Default agent" );
//			// Verify that the constants have values. Otherwise launch exception.
//			if (CLIENT_ID.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (SECRET_KEY.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (CALLBACK.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			auth = new NeoComOAuth20.Builder()
//					.withClientId( CLIENT_ID )
//					.withClientKey( SECRET_KEY )
//					.withCallback( CALLBACK )
//					.withAgent( AGENT )
//					.withStore( ESIStore.DEFAULT )
//					.withScopes( scopes )
//					.withState( "NEOCOM-VERIFICATION-STATE" )
//					.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.tranquility.authorization.server"
//							, "https://login.eveonline.com/" ) )
//					.withAccessTokenEndpoint( this.configurationProvider.getResourceString( "P.esi.authorization.accesstoken.url"
//							, "oauth/token" ) )
//					.withAuthorizationBaseUrl( this.configurationProvider.getResourceString( "P.esi.authorization.authorize.url"
//							, "oauth/authorize" ) )
//					.build();
//			// TODO - When new refactoring isolates scopes remove this.
//			SCOPESTRING = this.transformScopes( scopes );
//		}
//		if ("SINGULARITY".equalsIgnoreCase( selector )) {
//			final String CLIENT_ID = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.clientid" );
//			final String SECRET_KEY = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.secretkey" );
//			final String CALLBACK = this.configurationProvider.getResourceString( "P.esi.singularity.authorization.callback" );
//			final String AGENT = this.configurationProvider.getResourceString( "P.esi.authorization.agent", "Default agent" );
//			// Verify that the constants have values. Otherwise launch exception.
//			if (CLIENT_ID.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (SECRET_KEY.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			if (CALLBACK.isEmpty())
//				throw new NeoComRuntimeException(
//						"RT [NeoComRetrofitFactory.getConfiguredOAuth]> ESI configuration property is empty." );
//			auth = new NeoComOAuth20.Builder()
//					.withClientId( CLIENT_ID )
//					.withClientKey( SECRET_KEY )
//					.withCallback( CALLBACK )
//					.withAgent( AGENT )
//					.withStore( ESIStore.DEFAULT )
//					.withScopes( scopes )
//					.withState( this.configurationProvider.getResourceString( "P.esi.authorization.state"
//							, "NEOCOM-VERIFICATION-STATE" ) )
//					.withBaseUrl( this.configurationProvider.getResourceString( "P.esi.singularity.authorization.server"
//							, "https://sisilogin.testeveonline.com/" ) )
//					.withAccessTokenEndpoint( this.configurationProvider.getResourceString( "P.esi.authorization.accesstoken.url"
//							, "oauth/token" ) )
//					.withAuthorizationBaseUrl( this.configurationProvider.getResourceString( "P.esi.authorization.authorize.url"
//							, "oauth/authorize" ) )
//					.build();
//		}
//		Objects.requireNonNull( auth );
//		return auth;
//	}

}