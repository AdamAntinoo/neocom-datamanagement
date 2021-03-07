package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.utility.StorageUnits;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUniverseClientFactory {
	private static final String DEFAULT_ESI_LOGIN_BACKEND_HOST = "login.eveonline.com";

	private String agent = "NeoCom Data Management Library Agent.";
	private Integer timeoutSeconds = 60;
	private File cacheStoreFile;
	private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes( 1 );
	private boolean logging = false;

	// - C O N S T R U C T O R S
	private HttpUniverseClientFactory() {}

	private OkHttpClient clientBuilder() {
		final OkHttpClient.Builder universeClientBuilder =
				new OkHttpClient.Builder()
						.addInterceptor( chain -> {
							final Request.Builder builder = chain.request().newBuilder()
									.addHeader( "User-Agent", this.agent );
							return chain.proceed( builder.build() );
						} )
						.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS )
						.certificatePinner(
								new CertificatePinner.Builder()
										.add( DEFAULT_ESI_LOGIN_BACKEND_HOST, "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
										.add( DEFAULT_ESI_LOGIN_BACKEND_HOST, "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
										.add( DEFAULT_ESI_LOGIN_BACKEND_HOST, "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
										.build() );
		if (this.logging) {
			final HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
			logInterceptor.level( HttpLoggingInterceptor.Level.BASIC );
			universeClientBuilder.addInterceptor( logInterceptor );
		}
		// Additional characteristics
		if (null != this.cacheStoreFile) // If the cache file is not set then deactivate the cache
			universeClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return universeClientBuilder.build();
	}

	// - B U I L D E R
	public static class Builder {
		private final HttpUniverseClientFactory onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new HttpUniverseClientFactory();
		}

		public OkHttpClient generate() {
			return this.onConstruction.clientBuilder();
		}

		public HttpUniverseClientFactory.Builder optionalAgent( final String agent ) {
			Objects.requireNonNull( agent );
			this.onConstruction.agent = agent;
			return this;
		}

		public HttpUniverseClientFactory.Builder optionalCacheFile( final File cacheLocation ) {
			Objects.requireNonNull( cacheLocation );
			this.onConstruction.cacheStoreFile = cacheLocation;
			return this;
		}

		public HttpUniverseClientFactory.Builder optionalCacheSize( final Integer size, final StorageUnits unit ) {
			Objects.requireNonNull( size );
			this.onConstruction.cacheSizeBytes = unit.toBytes( size );
			return this;
		}

		public HttpUniverseClientFactory.Builder optionalLogging( final boolean logging ) {
			this.onConstruction.logging = logging;
			return this;
		}

		public HttpUniverseClientFactory.Builder optionalTimeout( final Integer seconds ) {
			if (null != seconds)
				this.onConstruction.timeoutSeconds = seconds;
			return this;
		}
	}
}
