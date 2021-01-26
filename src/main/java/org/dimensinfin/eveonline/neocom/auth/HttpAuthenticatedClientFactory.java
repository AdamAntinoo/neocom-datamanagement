package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.dimensinfin.eveonline.neocom.utility.StorageUnits;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_LOGIN_HOST;

public class HttpAuthenticatedClientFactory {

	private String agent = "NeoCom Data Management Library Agent.";
	private Integer timeoutSeconds = 60;
	private File cacheStoreFile;
	private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes( 2 );
//	private Retrofit refreshRetrofit;
	// - C O M P O N E N T S
	private IConfigurationService configurationProvider;
	private Credential credential;
	private NeoComOAuth20 neoComOAuth20;

	private HttpAuthenticatedClientFactory() {}

	private OkHttpClient clientBuilder() {
		final OkHttpClient.Builder authenticatedClientBuilder = new OkHttpClient.Builder()
				.addInterceptor(chain -> {
					Request.Builder builder = chain.request().newBuilder()
							.addHeader("User-Agent", this.agent);
					return chain.proceed(builder.build());
				})
				.addInterceptor(chain -> {
					if (StringUtils.isBlank(this.getRefreshToken()))
						return chain.proceed(chain.request());
					Request.Builder builder = chain.request().newBuilder();
					final TokenTranslationResponse token = this.neoComOAuth20.fromRefresh(this.getRefreshToken());
					if (null != token)
						builder.addHeader("Authorization", "Bearer " + token.getAccessToken());
					return chain.proceed(builder.build());
				})
				.addInterceptor(chain -> {
					if (StringUtils.isBlank(this.getRefreshToken()))
						return chain.proceed(chain.request());
					Response r = chain.proceed(chain.request());
					if (r.isSuccessful())
						return r;
					if (r.body().string().contains("invalid_token")) {
						this.neoComOAuth20.fromRefresh(getRefreshToken());
						r = chain.proceed(chain.request());
					}
					return r;
				})
				.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS )
				.certificatePinner(
						new CertificatePinner.Builder()
								.add( ESI_LOGIN_HOST, "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
								.add( ESI_LOGIN_HOST, "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
								.add( ESI_LOGIN_HOST, "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
								.build() );
		// Additional characteristics
		if (null != this.cacheStoreFile)
			authenticatedClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return authenticatedClientBuilder.build();
	}
	protected  String getRefreshToken() {
		return this.credential.getRefreshToken();
	}

	// - B U I L D E R
	public static class Builder {
		private HttpAuthenticatedClientFactory onConstruction;

		public Builder() {
			this.onConstruction = new HttpAuthenticatedClientFactory();
		}

		public HttpAuthenticatedClientFactory.Builder withNeoComOAuth20( final NeoComOAuth20 neoComOAuth20 ) {
			Objects.requireNonNull( neoComOAuth20 );
			this.onConstruction.neoComOAuth20 = neoComOAuth20;
			return this;
		}
		public HttpAuthenticatedClientFactory.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withAgent( final String agent ) {
			Objects.requireNonNull( agent );
			this.onConstruction.agent = agent;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCacheFile( final File cacheLocation ) {
			Objects.requireNonNull( cacheLocation );
			this.onConstruction.cacheStoreFile = cacheLocation;
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withCacheSize( final Integer size, final StorageUnits unit ) {
			Objects.requireNonNull( size );
			this.onConstruction.cacheSizeBytes = unit.toBytes( size );
			return this;
		}

		public HttpAuthenticatedClientFactory.Builder withTimeout( final Integer seconds ) {
			if (null != seconds)
				this.onConstruction.timeoutSeconds = seconds;
			return this;
		}

		public OkHttpClient generate() {
			Objects.requireNonNull( this.onConstruction.neoComOAuth20 );
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction.clientBuilder();
		}
	}
}
