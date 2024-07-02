package org.dimensinfin.eveonline.neocom.adapter.httpclient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import org.dimensinfin.eveonline.neocom.backend.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

import lombok.Getter;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_NAME;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_CACHE_SIZE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_LOCATION;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.AUTHENTICATED_RETROFIT_SERVER_TIMEOUT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.CACHE_DIRECTORY_PATH;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_AGENT;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CALLBACK;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_STATE;

@Getter
public class RetrofitConfiguration {
	// - P R O D U C T I O N   D E F A U L T S
	private static final String DEFAULT_ESI_DATA_SERVER = "https://esi.evetech.net/latest/";
	private static final String DEFAULT_RETROFIT_AGENT = "Default agent";
	private static final Integer DEFAULT_CACHE_SIZE_1GB = 1;
	private static final String DEFAULT_ESI_OAUTH_LOGIN_SERVER = "https://login.eveonline.com/";
	private static final String DEFAULT_AUTHORIZATION_ACCESS_TOKEN = "oauth/token";
	private static final String DEFAULT_AUTHORIZATION_AUTHORIZE = "oauth/authorize";

	private final IConfigurationService configurationProvider;
	private final IFileSystem fileSystemAdapter;

	private String cacheConfigurationPropertyName;
	private String esiDataServerLocation;
	private String agent;
	private int timeout;
	private boolean usesCache = false;
	private Integer cacheSize;
	private File cacheDataFile;
	private String serverLoginBase;
	private String clientId;
	private String secretKey;
	private String callback;
	private String state;

	public RetrofitConfiguration( final @NotNull IConfigurationService configurationProvider,
	                              final @NotNull IFileSystem fileSystemAdapter,
	                              final @NotNull String cacheConfigurationPropertyName ) {
		this.configurationProvider = configurationProvider;
		this.fileSystemAdapter = fileSystemAdapter;
		this.cacheConfigurationPropertyName = cacheConfigurationPropertyName;
		this.initialize();
	}

	public void initialize() {
		this.esiDataServerLocation = this.configurationProvider.getResourceString(
				AUTHENTICATED_RETROFIT_SERVER_LOCATION,
				DEFAULT_ESI_DATA_SERVER );
		this.agent = this.configurationProvider.getResourceString(
				AUTHENTICATED_RETROFIT_SERVER_AGENT, DEFAULT_RETROFIT_AGENT );
		this.timeout = (int) TimeUnit.SECONDS
				.toSeconds( this.configurationProvider.getResourceInteger( AUTHENTICATED_RETROFIT_SERVER_TIMEOUT ) );
		this.usesCache = this.cacheActive( this.cacheConfigurationPropertyName );
		if ( this.usesCache ) {
			this.cacheSize = this.configurationProvider.getResourceInteger(
					AUTHENTICATED_RETROFIT_CACHE_SIZE, DEFAULT_CACHE_SIZE_1GB );
			try {
				this.cacheDataFile = this.testValidCacheFile(
						new File( this.fileSystemAdapter.accessResource4Path(
								this.credentialUniqueCacheFilePath( "credential.getUniqueCredential" )
						) )
				);
			} catch (final IOException ioe) {
				NeoComLogger.error( ioe );
			}
		}
		//		final String scopes = Objects.requireNonNull( credential.getScope() );
		this.serverLoginBase = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER_URL,
				DEFAULT_ESI_OAUTH_LOGIN_SERVER );
		this.clientId = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		this.secretKey = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY );
		this.callback = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CALLBACK );
		this.agent = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_AGENT, DEFAULT_RETROFIT_AGENT );
		this.state = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_STATE );
		// Verify that the constants have values. Otherwise, launch exception.
		if ( null == clientId || clientId.isEmpty() )
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if ( secretKey.isEmpty() )
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
		if ( callback.isEmpty() )
			throw new NeoComRuntimeException( ErrorInfoCatalog.MANDATORY_CONFIGURATION_PROPERTY_EMPTY.getErrorMessage() );
	}

	/**
	 * Checks if the cache is activated on the configuration for a service. If so then checks that file system is writable.
	 */
	private boolean cacheActive( final String configurationKey ) {
		if ( this.configurationProvider.getResourceBoolean( configurationKey ) ) {
			final String cacheFilePath = this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH );
			return this.fileSystemAdapter.checkWritable( cacheFilePath );
		}
		return false;
	}

	private File testValidCacheFile( final File cacheDataFile ) throws IOException {
		if ( cacheDataFile.exists() ) return cacheDataFile;
		if ( cacheDataFile.createNewFile() )
			return cacheDataFile;
		else throw new IOException( "Cache file system is not operative. Removing cached from Retrofit services." );
	}

	private @NonNls String credentialUniqueCacheFilePath( final String uniqueIdentifier ) {
		return this.configurationProvider.getResourceString( CACHE_DIRECTORY_PATH ) +
				this.configurationProvider.getResourceString( AUTHENTICATED_RETROFIT_CACHE_NAME ) +
				uniqueIdentifier;
	}

}
