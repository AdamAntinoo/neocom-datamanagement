package org.dimensinfin.eveonline.neocom.adapter.httpclient;

import java.util.Objects;
import java.util.function.UnaryOperator;

import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.HttpUniverseClientFactory;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.utility.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.utility.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.utility.StorageUnits;

import lombok.Builder;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Builder(setterPrefix = "with")
//@NoArgsConstructor
public class UpgradedRetrofitService /*extends RetrofitService*/ {
	private static final String DEFAULT_AUTHORIZATION_ACCESS_TOKEN = "oauth/token";
	private static final String DEFAULT_AUTHORIZATION_AUTHORIZE = "oauth/authorize";
	protected static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );

	private RetrofitConfiguration configuration;
	private UnaryOperator<Credential> credentialPersistenceFunction;

	@LogEnterExit
	public Retrofit accessUniverseConnector() {
		if ( this.configuration.isUsesCache() ) {
			return new Retrofit.Builder()
					.baseUrl( this.configuration.getEsiDataServerLocation() )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( new HttpUniverseClientFactory.Builder()
							.optionalAgent( this.configuration.getAgent() )
							.optionalTimeout( this.configuration.getTimeout() )
							.optionalCacheFile( this.configuration.getCacheDataFile() )
							.optionalCacheSize( this.configuration.getCacheSize(), StorageUnits.GIGABYTES )
							.generate() )
					.build();
		} else {
			return new Retrofit.Builder()
					.baseUrl( this.configuration.getEsiDataServerLocation() )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( new HttpUniverseClientFactory.Builder()
							.optionalAgent( this.configuration.getAgent() )
							.optionalTimeout( this.configuration.getTimeout() )
							.generate() )
					.build();
		}
	}

	/**
	 * Creates a unique retrofit connector for each unique credential found on the request. Connectors are cached indefinitely and are Retrofit
	 * instances that have their own http connector and its own configuration for the authenticated ESI backend services. Each one of the connectors
	 * has a cache that it is unique and specific for each credential so no cross requests should occur at the cache level for identical ESI backend
	 * requests.
	 *
	 * The request for a new Retrofit connector received their particular <code>Credential</code> and uses a external configuration service to
	 * configure the Http Client. It will also use an external <code>Function</code> to persist the updated authentication token when the refresh
	 * feature is activated to any expiration for an authentication token will update the token for the Credential and persist their values into the
	 * Credential Persistence service.
	 *
	 * @param credential the credential that should be used on the authenticated ESI backend service.
	 * @return the configured and ready to use retrofit instance. If it was already created and found on the cache it can be reused.
	 */
	@LogEnterExit
	public Retrofit accessAuthenticatedConnector( final Credential credential ) {
		if ( this.configuration.isUsesCache() ) {
			return new Retrofit.Builder()
					.baseUrl( this.configuration.getEsiDataServerLocation() )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( UpgradedHttpAuthenticatedClientFactory.builder()
							.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
							.withConfiguration( this.configuration )
							.withCredential( credential )

							//							.withConfigurationProvider( this.configurationProvider )
							//							.withAgent( this.configuration.getAgent() )
							//							.withTimeout( this.configuration.getTimeout() )
							//							.withCacheFile( this.configuration.getCacheDataFile() )
							//							.withCacheSize( this.configuration.getCacheSize(), StorageUnits.GIGABYTES )
							.build()
							.generate() )
					.build();
		} else
			return new Retrofit.Builder()
					.baseUrl( this.configuration.getEsiDataServerLocation() )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( UpgradedHttpAuthenticatedClientFactory.builder()
							.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
							//							.withConfigurationProvider( this.configurationProvider )
							//							.withCredential( credential )
							//							.withAgent( this.configuration.getAgent() )
							//							.withTimeout( this.configuration.getTimeout() )
							.build()
							.generate() )
					.build();
	}

	protected NeoComOAuth20 getConfiguredOAuth( final Credential credential ) {
		final String scopes = Objects.requireNonNull( credential.getScope() );
		return Objects.requireNonNull( new NeoComOAuth20.Builder()
				.withClientId( this.configuration.getClientId() )
				.withClientKey( this.configuration.getSecretKey() )
				.withCallback( this.configuration.getCallback() )
				.withAgent( this.configuration.getAgent() )
				.withStore( ESIStore.DEFAULT )
				.withScopes( scopes )
				.withState( this.configuration.getState() )
				.withBaseUrl( this.configuration.getServerLoginBase() )
				.withAccessTokenEndpoint( DEFAULT_AUTHORIZATION_ACCESS_TOKEN )
				.withAuthorizationBaseUrl( DEFAULT_AUTHORIZATION_AUTHORIZE )
				.build()
		);

	}
}
