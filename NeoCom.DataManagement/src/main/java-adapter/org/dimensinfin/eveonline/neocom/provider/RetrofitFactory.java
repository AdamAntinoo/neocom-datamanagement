package org.dimensinfin.eveonline.neocom.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
//							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
//							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );
	private static final String UNIVERSE_CONNECTOR_IDENTIFIER = "-UNIVERSE-CONNECTOR-";
	private static final String BACKEND_CONNECTOR_IDENTIFIER = "-BACKEND-CONNECTOR-";
	// - P R O D U C T I O N   D E F A U L T S
	private static final String DEFAULT_RETROFIT_AGENT = "Default agent";
	private static final String DEFAULT_ESI_OAUTH_LOGIN_SERVER = "https://login.eveonline.com/";
	private static final String DEFAULT_ESI_DATA_SERVER = "https://esi.evetech.net/latest/";
	private static final String DEFAULT_AUTHORIZATION_ACCESS_TOKEN = "oauth/token";
	private static final String DEFAULT_AUTHORIZATION_AUTHORIZE = "oauth/authorize";

	// - C O M P O N E N T S
	protected IConfigurationService configurationProvider;
	protected IFileSystem fileSystemAdapter;
	private Map<String, Retrofit> connectors = new HashMap<>();

	// - C O N S T R U C T O R S
	protected RetrofitFactory() {}
//
//	@LogEnterExit
//	public Retrofit accessAuthenticatedConnector( final Credential credential ) throws IOException {
//		Retrofit hitConnector = this.connectors.get( credential.getUniqueCredential() );
//		try {
//			if (null == hitConnector) { // Create a new connector for this credential.
//				final String esiDataServerLocation = this.configurationProvider.getResourceString(
//						AUTHENTICATED_RETROFIT_SERVER_LOCATION,
//						DEFAULT_ESI_DATA_SERVER );
//				final String agent = this.configurationProvider
//						.getResourceString( AUTHENTICATED_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
//				final int timeout = (int) TimeUnit.SECONDS
//						.toSeconds( this.configurationProvider.getResourceInteger( AUTHENTICATED_RETROFIT_SERVER_TIMEOUT ) );
//				final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH )
//						+ this.configurationProvider.getResourceString( AUTHENTICATED_RETROFIT_CACHE_NAME );
//				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
//				this.testValidCacheFile( cacheDataFile );
//				final Integer cacheSize = this.configurationProvider.getResourceInteger( AUTHENTICATED_RETROFIT_CACHE_SIZE );
//				hitConnector = new Retrofit.Builder()
//						.baseUrl( esiDataServerLocation )
//						.addConverterFactory( GSON_CONVERTER_FACTORY )
//						.client( new HttpAuthenticatedClientFactory.Builder()
//								.withNeoComOAuth20( this.getConfiguredOAuth( credential ) )
//								.withConfigurationProvider( this.configurationProvider )
//								.withCredential( credential )
//								.withAgent( agent )
//								.withTimeout( timeout )
//								// TODO - Removed until the effect of same cache in multiple retrofits is confirmed.
////							.withCacheFile( cacheDataFile )
////							.withCacheSize( cacheSize, StorageUnits.GIGABYTES )
//								.generate() )
//						.build();
//				this.connectors.put( credential.getUniqueCredential(), hitConnector );
//			}
//		} catch (final IOException ioe) {
//			NeoComLogger.error( ioe );
//			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
//		}
//		return hitConnector;
//	}
//
//	@LogEnterExit
//	public Retrofit accessBackendConnector() {
//		Retrofit hitConnector = this.connectors.get( BACKEND_CONNECTOR_IDENTIFIER );
//		try {
//			if (null == hitConnector) { // Create a new connector for the backend and cache it.
//				final String serverBaseUrl = this.configurationProvider.getResourceString( BACKEND_RETROFIT_SERVER_LOCATION,
//						HttpBackendClientFactory.DEFAULT_NEOCOM_BACKEND_HOST );
//				final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH )
//						+ this.configurationProvider.getResourceString( BACKEND_RETROFIT_CACHE_FILE_NAME );
//				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
//				this.testValidCacheFile( cacheDataFile );
//				hitConnector = new Retrofit.Builder()
//						.baseUrl( serverBaseUrl )
//						.addConverterFactory( GSON_CONVERTER_FACTORY )
//						.client( new HttpBackendClientFactory.Builder()
//								.optionalCacheFile( cacheDataFile )
//								.generate() )
//						.build();
//				this.connectors.put( BACKEND_CONNECTOR_IDENTIFIER, hitConnector );
//			}
//		} catch (final IOException ioe) {
//			NeoComLogger.error( ioe );
//			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
//		}
//		return hitConnector;
//	}
//
//	@LogEnterExit
//	public Retrofit accessUniverseConnector() {
//		Retrofit hitConnector = this.connectors.get( UNIVERSE_CONNECTOR_IDENTIFIER );
//		try {
//			if (null == hitConnector) { // Create a new connector for this credential.
//				final String esiDataServerLocation = this.configurationProvider.getResourceString(
//						UNIVERSE_RETROFIT_SERVER_LOCATION,
//						DEFAULT_ESI_DATA_SERVER );
//				final String agent = this.configurationProvider.getResourceString( UNIVERSE_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
//				final int timeout = (int) TimeUnit.SECONDS
//						.toSeconds( this.configurationProvider.getResourceInteger( UNIVERSE_RETROFIT_SERVER_TIMEOUT ) );
//				final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH )
//						+ this.configurationProvider.getResourceString( UNIVERSE_RETROFIT_CACHE_NAME );
//				final File cacheDataFile = new File( this.fileSystemAdapter.accessResource4Path( cacheFilePath ) );
//				this.testValidCacheFile( cacheDataFile );
//				final Integer cacheSize = this.configurationProvider.getResourceInteger( UNIVERSE_RETROFIT_CACHE_SIZE, 1 );
//				hitConnector = new Retrofit.Builder()
//						.baseUrl( esiDataServerLocation )
//						.addConverterFactory( GSON_CONVERTER_FACTORY )
//						.client( new HttpUniverseClientFactory.Builder()
//								.optionalAgent( agent )
//								.optionalTimeout( timeout )
//								.optionalCacheFile( cacheDataFile )
//								.optionalCacheSize( cacheSize, StorageUnits.GIGABYTES )
//								.generate() )
//						.build();
//				this.connectors.put( UNIVERSE_CONNECTOR_IDENTIFIER, hitConnector );
//			}
//		} catch (final IOException ioe) {
//			NeoComLogger.error( ioe );
//			throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
//		}
//		return hitConnector;
//	}
//
//	protected NeoComOAuth20 getConfiguredOAuth( final Credential credential ) {
//		Objects.requireNonNull( credential );
//		final String scopes = credential.getScope();
//		final String SERVER_LOGIN_BASE = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL,
//				DEFAULT_ESI_OAUTH_LOGIN_SERVER );
//		final String CLIENT_ID = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
//		final String SECRET_KEY = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY );
//		final String CALLBACK = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CALLBACK );
//		final String AGENT = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_AGENT, DEFAULT_RETROFIT_AGENT );
//		final String STATE = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_STATE );
//		// Verify that the constants have values. Otherwise launch exception.
//		if (CLIENT_ID.isEmpty())
//			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
//		if (SECRET_KEY.isEmpty())
//			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
//		if (CALLBACK.isEmpty())
//			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
//		final NeoComOAuth20 auth = new NeoComOAuth20.Builder()
//				.withClientId( CLIENT_ID )
//				.withClientKey( SECRET_KEY )
//				.withCallback( CALLBACK )
//				.withAgent( AGENT )
//				.withStore( ESIStore.DEFAULT )
//				.withScopes( scopes )
//				.withState( STATE )
//				.withBaseUrl( SERVER_LOGIN_BASE )
//				.withAccessTokenEndpoint( this.configurationProvider.getResourceString(
//						ESI_OAUTH_AUTHORIZATION_ACCESS_TOKEN,
//						DEFAULT_AUTHORIZATION_ACCESS_TOKEN ) )
//				.withAuthorizationBaseUrl( this.configurationProvider.getResourceString(
//						ESI_OAUTH_AUTHORIZATION_AUTHORIZE,
//						DEFAULT_AUTHORIZATION_AUTHORIZE ) )
//				.build();
//		Objects.requireNonNull( auth );
//		return auth;
//	}
//
//	private void testValidCacheFile( final File cacheDataFile ) throws IOException {
//		cacheDataFile.createNewFile();
//	}
//
	// - B U I L D E R
	public static class Builder {
		private RetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new RetrofitFactory();
		}

		public RetrofitFactory build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			return this.onConstruction;
		}

		public RetrofitFactory.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public RetrofitFactory.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}
	}
}
