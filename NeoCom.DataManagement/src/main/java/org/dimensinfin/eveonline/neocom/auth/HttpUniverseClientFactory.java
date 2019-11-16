package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.core.StorageUnits;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUniverseClientFactory {
	private static final String ESI_HOST = "login.eveonline.com";

	private String agent = "NeoCom Data Management Library Agent.";
	private Integer timeoutSeconds = 60;
	private File cacheStoreFile;
	private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes( 2 );
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;

	private HttpUniverseClientFactory() {}

	private OkHttpClient clientBuilder() {
		final OkHttpClient.Builder universeClientBuilder =
				new OkHttpClient.Builder()
						.addInterceptor( chain -> {
							Request.Builder builder = chain.request().newBuilder()
									.addHeader( "User-Agent", this.agent );
							return chain.proceed( builder.build() );
						} )
						.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS )
						.certificatePinner(
								new CertificatePinner.Builder()
										.add( "login.eveonline.com", "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
										.add( "login.eveonline.com", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
										.add( "login.eveonline.com", "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
										.build() );
		// Additional characteristics
		if (null != this.cacheStoreFile)
			universeClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return universeClientBuilder.build();
	}

	// - B U I L D E R
	public static class Builder {
		private HttpUniverseClientFactory onConstruction;

		public Builder() {
			this.onConstruction = new HttpUniverseClientFactory();
		}

		public HttpUniverseClientFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public HttpUniverseClientFactory.Builder withAgent( final String agent ) {
			Objects.requireNonNull( agent );
			this.onConstruction.agent = agent;
			return this;
		}

		public HttpUniverseClientFactory.Builder withCacheFile( final File cacheLocation ) {
			Objects.requireNonNull( cacheLocation );
			this.onConstruction.cacheStoreFile = cacheLocation;
			return this;
		}

		public HttpUniverseClientFactory.Builder withCacheSize( final Integer size, final StorageUnits unit ) {
			Objects.requireNonNull( size );
			this.onConstruction.cacheSizeBytes = unit.toBytes( size );
			return this;
		}

		public HttpUniverseClientFactory.Builder withTimeout( final Integer seconds ) {
			if (null != seconds)
				this.onConstruction.timeoutSeconds = seconds;
			return this;
		}

		public OkHttpClient generate() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
//			Objects.requireNonNull( this.onConstruction.agent );
			return this.onConstruction.clientBuilder();
		}
	}
}
