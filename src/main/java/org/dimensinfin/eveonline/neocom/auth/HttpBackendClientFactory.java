package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.core.StorageUnits;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpBackendClientFactory {
	public static final String DEFAULT_NEOCOM_BACKEND_HOST = "http://localhost:9500/";

	private String agent = "NeoCom Backend Agent. - Access from DataManagement library 0.20.0";
	private Integer timeoutSeconds = 60;
	private File cacheStoreFile;
	private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes( 1 );

	private HttpBackendClientFactory() {}

	private OkHttpClient clientBuilder() {
		final OkHttpClient.Builder backendClientBuilder =
				new OkHttpClient.Builder()
						.addInterceptor( chain -> {
							Request.Builder builder = chain.request().newBuilder()
									.addHeader( "User-Agent", this.agent );
							return chain.proceed( builder.build() );
						} )
						.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS );
		// Additional characteristics
		if (null != this.cacheStoreFile) // If the cache file is not set then deactivate the cache
			backendClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return backendClientBuilder.build();
	}

	// - B U I L D E R
	public static class Builder {
		private HttpBackendClientFactory onConstruction;

		public Builder() {
			this.onConstruction = new HttpBackendClientFactory();
		}

		public OkHttpClient generate() {
			return this.onConstruction.clientBuilder();
		}

		public HttpBackendClientFactory.Builder optionalAgent( final String agent ) {
			Objects.requireNonNull( agent );
			this.onConstruction.agent = agent;
			return this;
		}

		public HttpBackendClientFactory.Builder optionalCacheFile( final File cacheLocation ) {
			Objects.requireNonNull( cacheLocation );
			this.onConstruction.cacheStoreFile = cacheLocation;
			return this;
		}

		public HttpBackendClientFactory.Builder optionalCacheSize( final Integer size, final StorageUnits unit ) {
			Objects.requireNonNull( size );
			this.onConstruction.cacheSizeBytes = unit.toBytes( size );
			return this;
		}

		public HttpBackendClientFactory.Builder optionalTimeout( final Integer seconds ) {
			if (null != seconds)
				this.onConstruction.timeoutSeconds = seconds;
			return this;
		}
	}
}
