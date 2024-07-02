package org.dimensinfin.eveonline.neocom.adapter.httpclient;

import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth20;
import org.dimensinfin.eveonline.neocom.auth.NeoComOAuth2Flow;
import org.dimensinfin.eveonline.neocom.auth.TokenTranslationResponse;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import lombok.Builder;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_LOGIN_HOST;

@Builder(setterPrefix = "with")
public class UpgradedHttpAuthenticatedClientFactory {
	private static final long EXPIRE_LIMIT = 30L;
	private NeoComOAuth20 neoComOAuth20;
	private RetrofitConfiguration configuration;
	private Credential credential;
	private UnaryOperator<Credential> credentialPersistenceFunction;

	public OkHttpClient generate() {
		final HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
		//		final String CLIENT_ID = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		logInterceptor.level( HttpLoggingInterceptor.Level.BASIC );
		final OkHttpClient.Builder authenticatedClientBuilder = new OkHttpClient.Builder()
				.addInterceptor( chain -> { // Add headers
					final Request.Builder builder = chain.request().newBuilder()
							.addHeader( "User-Agent", this.configuration.getAgent() )
							.addHeader( "Content/Type", "application/json" );
					return chain.proceed( builder.build() );
				} )
				.addInterceptor( logInterceptor )
				.addInterceptor( chain -> { // Check that access token is not expired
					final String token = this.credential.getAccessToken();
					final long timeToExpire = this.timeToExpire( token );
					// - If the time to expire is less than 30 seconds then refresh the token.
					if ( timeToExpire < EXPIRE_LIMIT ) {
						final TokenTranslationResponse tokenResponse = this.refreshAccessToken();
						this.updateCredentialToken( tokenResponse );
					}
					//					if ( StringUtils.isBlank( this.credential.getRefreshToken() ) )
					//						return chain.proceed( chain.request() );
					final Request.Builder builder = chain.request().newBuilder();
					//					final TokenTranslationResponse token = this.neoComOAuth20.fromRefresh( this.credential.getRefreshToken() );
					//					if ( null != token )
					builder.addHeader( "Authorization", "Bearer " + this.credential.getAccessToken() );
					return chain.proceed( builder.build() );
				} )
				.addInterceptor( chain -> {
					if ( StringUtils.isBlank( this.credential.getRefreshToken() ) )
						return chain.proceed( chain.request() );
					Response r = chain.proceed( chain.request() );
					if ( r.isSuccessful() )
						return r;
					if ( r.body().string().contains( "invalid_token" ) ) {
						this.neoComOAuth20.fromRefresh( this.credential.getRefreshToken() );
						r = chain.proceed( chain.request() );
					}
					return r;
				} )
				.readTimeout( this.configuration.getTimeout(), TimeUnit.SECONDS )
				.certificatePinner(
						new CertificatePinner.Builder()
								.add( ESI_LOGIN_HOST, "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=" )
								.add( ESI_LOGIN_HOST, "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=" )
								.add( ESI_LOGIN_HOST, "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=" )
								.build() );
		// - Additional characteristics
		//		if ( null != this.cacheStoreFile )
		//			authenticatedClientBuilder.cache( new Cache( this.cacheStoreFile, this.cacheSizeBytes ) );
		return authenticatedClientBuilder.build();
	}

	private void updateCredentialToken( final TokenTranslationResponse token ) {
		this.credentialPersistenceFunction.apply( this.credential.setAccessToken( token.getAccessToken() )
				.setRefreshToken( token.getRefreshToken() )
		);
	}

	private TokenTranslationResponse refreshAccessToken() {
		// TODO - Create a new special flow that will not use the Configuration until this is solved.
		final NeoComOAuth2Flow flow = new NeoComOAuth2Flow.Builder().build();
		final TokenTranslationResponse token = flow.updateAccessToken( this.credential.getRefreshToken(), this.credential.getScope() );
		return token;
	}

	private long timeToExpire( final String token ) {
		final String[] chunks = token.split( "\\." );
		final Base64.Decoder decoder = Base64.getUrlDecoder();
		final String payload = new String( decoder.decode( chunks[1] ) );
		// - Extract the expiration time.
		JSONObject jsonObject = new JSONObject( payload );
		final long expValue = jsonObject.getLong( "exp" );
		long currentTimeUTC = Instant.now().getEpochSecond();
		long differenceInSeconds = expValue - currentTimeUTC;
		return differenceInSeconds;
	}
}
