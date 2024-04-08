package org.dimensinfin.eveonline.neocom.auth;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiClientBuilder;
import net.troja.eve.esi.auth.OAuth;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.utility.StorageUnits;

import java.io.File;
import java.util.Objects;

import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;

public class HttpAuthenticatedClientFactory {

    private String agent = "NeoCom Data Management Library Agent.";
    private Integer timeoutSeconds = 60;
    private File cacheStoreFile;
    private Long cacheSizeBytes = StorageUnits.GIGABYTES.toBytes(2);
    // - C O M P O N E N T S
    private IConfigurationService configurationProvider;
    private Credential credential;
    private OAuth neoComOAuth20;

    // - C O N S T R U C T O R S
    private HttpAuthenticatedClientFactory() {
    }

    protected String getRefreshToken() {
        return this.credential.getRefreshToken();
    }

//    @Depreacted
    private OkHttpClient clientBuilder() {
        final HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        final String CLIENT_ID = this.configurationProvider.getResourceString(ESI_TRANQUILITY_AUTHORIZATION_CLIENTID);
        logInterceptor.level(HttpLoggingInterceptor.Level.BASIC);
        final ApiClient client = new ApiClientBuilder().clientID(CLIENT_ID).build();
        this.neoComOAuth20 = (OAuth) client.getAuthentication("evesso");
        return client.getHttpClient();
//		final OkHttpClient.Builder authenticatedClientBuilder = new OkHttpClient.Builder()
//				.addInterceptor( chain -> {
//					final Request.Builder builder = chain.request().newBuilder()
//							.addHeader( "User-Agent", this.agent );
//					return chain.proceed( builder.build() );
//				} )
//				//				.addInterceptor( logInterceptor )
//				.addInterceptor( chain -> {
//					if (StringUtils.isBlank( this.getRefreshToken() ))
//						return chain.proceed( chain.request() );
//					final Request.Builder builder = chain.request().newBuilder();
//					final TokenTranslationResponse token = this.neoComOAuth20.fromRefresh( this.getRefreshToken() );
//					if (null != token)
//						builder.addHeader( "Authorization", "Bearer " + token.getAccessToken() );
//					return chain.proceed( builder.build() );
//				} )
//				.addInterceptor( chain -> {
//					if (StringUtils.isBlank( this.getRefreshToken() ))
//						return chain.proceed( chain.request() );
//					Response r = chain.proceed( chain.request() );
//					if (r.isSuccessful())
//						return r;
//					if (r.body().string().contains( "invalid_token" )) {
//						this.neoComOAuth20.fromRefresh( this.getRefreshToken() );
//						r = chain.proceed( chain.request() );
//					}
//					return r;
//				} )
//				.readTimeout( this.timeoutSeconds, TimeUnit.SECONDS )
//				.certificatePinner(
//						new CertificatePinner.Builder()
//								.add( ESI_LOGIN_HOST, "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
//								.add( ESI_LOGIN_HOST, "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
//								.add( ESI_LOGIN_HOST, "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
//								.build() );
        // Additional characteristics
//		if (null != this.cacheStoreFile)
//			authenticatedClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
//		return authenticatedClientBuilder.build();
    }

    // - B U I L D E R
    public static class Builder {
        private final HttpAuthenticatedClientFactory onConstruction;

        // - C O N S T R U C T O R S
        public Builder() {
            this.onConstruction = new HttpAuthenticatedClientFactory();
        }

        public OkHttpClient generate() {
            Objects.requireNonNull(this.onConstruction.neoComOAuth20);
            Objects.requireNonNull(this.onConstruction.credential);
            Objects.requireNonNull(this.onConstruction.configurationProvider);
            return this.onConstruction.clientBuilder();
        }

        public HttpAuthenticatedClientFactory.Builder withAgent(final String agent) {
            Objects.requireNonNull(agent);
            this.onConstruction.agent = agent;
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withCacheFile(final File cacheLocation) {
            Objects.requireNonNull(cacheLocation);
            this.onConstruction.cacheStoreFile = cacheLocation;
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withCacheSize(final Integer size, final StorageUnits unit) {
            Objects.requireNonNull(size);
            this.onConstruction.cacheSizeBytes = unit.toBytes(size);
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withConfigurationProvider(final IConfigurationService configurationProvider) {
            Objects.requireNonNull(configurationProvider);
            this.onConstruction.configurationProvider = configurationProvider;
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withCredential(final Credential credential) {
            Objects.requireNonNull(credential);
            this.onConstruction.credential = credential;
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withNeoComOAuth20(final OAuth neoComOAuth20) {
            Objects.requireNonNull(neoComOAuth20);
            this.onConstruction.neoComOAuth20 = neoComOAuth20;
            return this;
        }

        public HttpAuthenticatedClientFactory.Builder withTimeout(final Integer seconds) {
            if (null != seconds)
                this.onConstruction.timeoutSeconds = seconds;
            return this;
        }
    }
}
