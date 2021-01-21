package org.dimensinfin.eveonline.neocom.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jetbrains.annotations.NonNls;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.auth.ESIStore;
import org.dimensinfin.eveonline.neocom.auth.HttpAuthenticatedClientFactory;
import org.dimensinfin.eveonline.neocom.auth.HttpBackendClientFactory;
import org.dimensinfin.eveonline.neocom.auth.HttpUniverseClientFactory;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.core.StorageUnits;
import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.dimensinfin.eveonline.neocom.auth.HttpBackendClientFactory.DEFAULT_NEOCOM_BACKEND_HOST;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_SIZE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_TIMEOUT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.BACKEND_RETROFIT_CACHE_FILE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.BACKEND_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_DIRECTORY_PATH;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_OAUTH_AUTHORIZATION_ACCESS_TOKEN;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_OAUTH_AUTHORIZATION_AUTHORIZE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CALLBACK;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_CACHE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_CACHE_SIZE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_CACHE_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_SERVER_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.UNIVERSE_RETROFIT_SERVER_TIMEOUT;

public class RetrofitService {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	@NonNls
	private static final ResourceBundle i18Bundle = ResourceBundle.getBundle( "i18Properties" );
	public static final String DEFAULT_CONTENT_TYPE = i18Bundle.getString( "application.content.type" );
	private static final String UNIVERSE_CONNECTOR_IDENTIFIER = i18Bundle.getString( "universe.connector.identifier" );
	private static final String BACKEND_CONNECTOR_IDENTIFIER = i18Bundle.getString( "backend.connector.identifier" );
	// - P R O D U C T I O N   D E F A U L T S
	private static final String DEFAULT_ESI_DATA_SERVER = "https://esi.evetech.net/latest/";
	private static final String DEFAULT_RETROFIT_AGENT = "Default agent";
	private static final Integer DEFAULT_CACHE_SIZE_1GB = 1;
	private static final String DEFAULT_ESI_OAUTH_LOGIN_SERVER = "https://login.eveonline.com/";
	private static final String DEFAULT_AUTHORIZATION_ACCESS_TOKEN = "oauth/token";
	private static final String DEFAULT_AUTHORIZATION_AUTHORIZE = "oauth/authorize";

	// - C O M P O N E N T S
	private final IConfigurationService configurationProvider;
	private final @NonNls IFileSystem fileSystemAdapter;
	private final Map<String, Retrofit> connectors = new HashMap<>();

	// - C O N S T R U C T O R S
	@Inject
	public RetrofitService( @NotNull @Named(DMServicesDependenciesModule.ICONFIGURATION_SERVICE) final IConfigurationService configurationProvider,
	                        @NotNull @Named(DMServicesDependenciesModule.IFILE_SYSTEM) final IFileSystem fileSystemAdapter ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
	}

	/**
	 * Creates a unique retrofit connector for each unique credential found on the request. Connectors are cached indefinitely and are Retrofit
	 * instances that have their own http connector and its own configuration for the authenticated ESI backend services.
	 * Each one of the connectors has a cache that it is unique and specific for each credential so no cross requests should occur at the cache
	 * level for identical ESI backend requests.
	 *
	 * @param credential the credential that should be used on the authenticated ESI backend service.
	 * @return the configured and ready to use retrofit instance. If it wes already created and found on the cache it can be reused.
	 */
	@LogEnterExit
	public Retrofit accessAuthenticatedConnector( final Credential credential ) {
		Retrofit hitConnector = this.connectors.get( credential.getUniqueCredential() );
		if (null == hitConnector) { // Create a new connector for this credential.
			final String esiDataServerLocation = this.configurationProvider.getResourceString(
					AUTHENTICATED_RETROFIT_SERVER_LOCATION,
					DEFAULT_ESI_DATA_SERVER );
			final String agent = this.configurationProvider.getResourceString(
					AUTHENTICATED_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
			final int timeout = (int) TimeUnit.SECONDS
					.toSeconds( this.configurationProvider.getResourceInteger( AUTHENTICATED_RETROFIT_SERVER_TIMEOUT ) );
			if (this.cacheActive( AUTHENTICATED_RETROFIT_CACHE_STATE )) {
				final Integer cacheSize = this.configurationProvider.getResourceInteger(
						AUTHENTICATED_RETROFIT_CACHE_SIZE, DEFAULT_CACHE_SIZE_1GB );
				try {
					final File cacheDataFile = this.testValidCacheFile(
							new File( this.fileSystemAdapter.accessResource4Path(
									this.credentialUniqueCacheFilePath( credential )
							) )
					);
					hitConnector = new Retrofit.Builder()
							.baseUrl( esiDataServerLocation )
							.addConverterFactory( GSON_CONVERTER_FACTORY )
							.client( new HttpAuthenticatedClientFactory.Builder()
									.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
									.withConfigurationProvider( this.configurationProvider )
									.withCredential( credential )
									.withAgent( agent )
									.withTimeout( timeout )
									.withCacheFile( cacheDataFile )
									.withCacheSize( cacheSize, StorageUnits.GIGABYTES )
									.generate() )
							.build();
				} catch (final IOException ioe) {
					hitConnector = new Retrofit.Builder()
							.baseUrl( esiDataServerLocation )
							.addConverterFactory( GSON_CONVERTER_FACTORY )
							.client( new HttpAuthenticatedClientFactory.Builder()
									.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
									.withConfigurationProvider( this.configurationProvider )
									.withCredential( credential )
									.withAgent( agent )
									.withTimeout( timeout )
									.generate() )
							.build();
				}
			} else
				hitConnector = new Retrofit.Builder()
						.baseUrl( esiDataServerLocation )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpAuthenticatedClientFactory.Builder()
								.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
								.withConfigurationProvider( this.configurationProvider )
								.withCredential( credential )
								.withAgent( agent )
								.withTimeout( timeout )
								.generate() )
						.build();
			this.connectors.put( credential.getUniqueCredential(), hitConnector );
		}
		return hitConnector;
	}

	@Deprecated
	@LogEnterExit
	public Retrofit accessBackendConnector() {
		Retrofit hitConnector = this.connectors.get( BACKEND_CONNECTOR_IDENTIFIER );
		try {
			if (null == hitConnector) { // Create a new connector for the backend and cache it.
				final String serverBaseUrl = this.configurationProvider.getResourceString(
						BACKEND_RETROFIT_SERVER_LOCATION,
						DEFAULT_NEOCOM_BACKEND_HOST );
				final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH )
						+ this.configurationProvider.getResourceString( BACKEND_RETROFIT_CACHE_FILE_NAME );
				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
				this.testValidCacheFile( cacheDataFile );
				hitConnector = new Retrofit.Builder()
						.baseUrl( serverBaseUrl )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpBackendClientFactory.Builder()
								.optionalCacheFile( cacheDataFile )
								.generate() )
						.build();
				this.connectors.put( BACKEND_CONNECTOR_IDENTIFIER, hitConnector );
			}
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
		}
		return hitConnector;
	}

	@LogEnterExit
	public Retrofit accessUniverseConnector() {
		Retrofit hitConnector = this.connectors.get( UNIVERSE_CONNECTOR_IDENTIFIER );
		if (null == hitConnector) { // Create a new connector for non authenticated universe access.
			final String esiDataServerLocation = this.configurationProvider.getResourceString(
					UNIVERSE_RETROFIT_SERVER_LOCATION,
					DEFAULT_ESI_DATA_SERVER );
			final String agent = this.configurationProvider.getResourceString( UNIVERSE_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
			final int timeout = (int) TimeUnit.SECONDS
					.toSeconds( this.configurationProvider.getResourceInteger( UNIVERSE_RETROFIT_SERVER_TIMEOUT ) );
			if (this.cacheActive( UNIVERSE_RETROFIT_CACHE_STATE )) {
				final Integer cacheSize = this.configurationProvider.getResourceInteger(
						UNIVERSE_RETROFIT_CACHE_SIZE, DEFAULT_CACHE_SIZE_1GB );
				try {
					final File cacheDataFile = this.testValidCacheFile(
							new File( this.fileSystemAdapter.accessResource4Path(
									this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH ) +
											this.configurationProvider.getResourceString( UNIVERSE_RETROFIT_CACHE_NAME )
							) )
					);
					hitConnector = new Retrofit.Builder()
							.baseUrl( esiDataServerLocation )
							.addConverterFactory( GSON_CONVERTER_FACTORY )
							.client( new HttpUniverseClientFactory.Builder()
									.optionalAgent( agent )
									.optionalTimeout( timeout )
									.optionalCacheFile( cacheDataFile )
									.optionalCacheSize( cacheSize, StorageUnits.GIGABYTES )
									.generate() )
							.build();
				} catch (final IOException ioe) {
					hitConnector = new Retrofit.Builder()
							.baseUrl( esiDataServerLocation )
							.addConverterFactory( GSON_CONVERTER_FACTORY )
							.client( new HttpUniverseClientFactory.Builder()
									.optionalAgent( agent )
									.optionalTimeout( timeout )
									.generate() )
							.build();
				}
			} else
				hitConnector = new Retrofit.Builder()
						.baseUrl( esiDataServerLocation )
						.addConverterFactory( GSON_CONVERTER_FACTORY )
						.client( new HttpUniverseClientFactory.Builder()
								.optionalAgent( agent )
								.optionalTimeout( timeout )
								.generate() )
						.build();
			this.connectors.put( UNIVERSE_CONNECTOR_IDENTIFIER, hitConnector );
		}
		return hitConnector;
	}

	protected NeoComOAuth20 getConfiguredOAuth( final Credential credential ) {
		final String scopes = Objects.requireNonNull( credential.getScope() );
		final String SERVER_LOGIN_BASE = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL,
				DEFAULT_ESI_OAUTH_LOGIN_SERVER );
		final String CLIENT_ID = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		final String SECRET_KEY = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY );
		final String CALLBACK = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CALLBACK );
		final String AGENT = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_AGENT, DEFAULT_RETROFIT_AGENT );
		final String STATE = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_STATE );
		// Verify that the constants have values. Otherwise launch exception.
		if (null == CLIENT_ID || CLIENT_ID.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if (SECRET_KEY.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if (CALLBACK.isEmpty())
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		return Objects.requireNonNull( new NeoComOAuth20.Builder()
				.withClientId( CLIENT_ID )
				.withClientKey( SECRET_KEY )
				.withCallback( CALLBACK )
				.withAgent( AGENT )
				.withStore( ESIStore.DEFAULT )
				.withScopes( scopes )
				.withState( STATE )
				.withBaseUrl( SERVER_LOGIN_BASE )
				.withAccessTokenEndpoint( this.configurationProvider.getResourceString(
						ESI_OAUTH_AUTHORIZATION_ACCESS_TOKEN,
						DEFAULT_AUTHORIZATION_ACCESS_TOKEN ) )
				.withAuthorizationBaseUrl( this.configurationProvider.getResourceString(
						ESI_OAUTH_AUTHORIZATION_AUTHORIZE,
						DEFAULT_AUTHORIZATION_AUTHORIZE ) )
				.build()
		);
	}

	/**
	 * Checks if the cache is activated on the configuration for a service. If so then checks that file system is writable.
	 */
	private boolean cacheActive( final String configurationKey ) {
		if (this.configurationProvider.getResourceBoolean( configurationKey )) {
			final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH );
			return this.fileSystemAdapter.checkWritable( cacheFilePath );
		}
		return false;
	}

	private @NonNls String credentialUniqueCacheFilePath( final Credential credential ) {
		return this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH ) +
				this.configurationProvider.getResourceString( AUTHENTICATED_RETROFIT_CACHE_NAME ) +
				credential.getUniqueCredential();
	}

	private File testValidCacheFile( final File cacheDataFile ) throws IOException {
		if (cacheDataFile.createNewFile())
			return cacheDataFile;
		else throw new IOException( "Cache file system is not operative. Removing cached from Retrofit services." );
	}
}
