package org.dimensinfin.eveonline.neocom.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.logging.LogWrapper;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_LOGIN_HOST;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_OAUTH_AUTHENTICATION_TYPE;

public class NeoComOAuth20 {
	private static Logger logger = LoggerFactory.getLogger( NeoComOAuth20.class );
	// - F I E L D - S E C T I O N
	private String clientId;
	private String clientKey;
	private String callback;
	private String agent;
	private ESIStore store;
	private String scopes;
	private String state;
	private String baseUrl;
	private String accessTokenEndpoint;
	private String authorizationBaseUrl;

	private OAuth20Service oAuth20Service;
	private VerifyCharacter verify;

	// - C O N S T R U C T O R S
	private NeoComOAuth20() {
		super();
	}

	public String getAuthorizationUrl() {
		return this.oAuth20Service.getAuthorizationUrl();
	}

	public TokenTranslationResponse fromRefresh( final String refresh ) {
		LogWrapper.enter();
		try {
			TokenTranslationResponse existing = this.store.get( refresh );
			if ((null == existing) || (existing.getExpiresOn() < (System.currentTimeMillis() - 5 * 1000))) {
				logger.info( "-- [NeoComOAuth20.fromRefresh]> Refresh of access token requested." );
				final OAuth2AccessToken token = this.oAuth20Service.refreshAccessToken( refresh );
				logger.info( "<< [NeoComOAuth20.fromRefresh]> Saving new token." );
				return save( token );
			}
			logger.info( "<< [NeoComOAuth20.fromRefresh]> Return valid token." );
			return existing;
		} catch (OAuthException | IOException | InterruptedException | ExecutionException ee) {
			LogWrapper.error( ee );
			return null;
		} finally {
			LogWrapper.exit();
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer( "NeoComOAuth20 [" );
		buffer.append( "name: " ).append( 0 );
		buffer.append( "]" );
		buffer.append( "->" ).append( super.toString() );
		return buffer.toString();
	}

	public VerifyCharacterResponse verify( final String refresh ) {
		final TokenTranslationResponse stored = this.store.get( refresh );
		try {
			final Response<VerifyCharacterResponse> r =
					this.verify.getVerification( ESI_OAUTH_AUTHENTICATION_TYPE + stored.getAccessToken() ).execute();
			if (r.isSuccessful()) {
				return r.body();
			}
			return null;
		} catch (IOException e) {
			logger.error( e.getMessage(), e );
			return null;
		}
	}

	private void build() {
		this.store = store;
		ServiceBuilder builder = new ServiceBuilder( this.clientId )
				.apiKey( this.clientId )
				.apiSecret( this.clientKey )
				.state( this.state );
		if (StringUtils.isNotBlank( this.callback )) builder.callback( this.callback );
		if (!scopes.isEmpty()) builder.scope( this.scopes );

		this.oAuth20Service = builder.build( new NeoComAuthApi20.Builder()
				.withAccessServer( this.baseUrl )
				.withAccessTokenEndpoint( this.accessTokenEndpoint )
				.withAuthorizationBaseUrl( this.authorizationBaseUrl )
				.build() );
		OkHttpClient.Builder verifyClient =
				new OkHttpClient.Builder()
						.protocols( Arrays.asList( Protocol.HTTP_1_1 ) )
						.certificatePinner(
								new CertificatePinner.Builder()
										.add( ESI_LOGIN_HOST, "sha256/5UeWOuDyX7IUmcKnsVdx+vLMkxEGAtzfaOUQT/caUBE=" )
										.add( ESI_LOGIN_HOST, "sha256/980Ionqp3wkYtN9SZVgMzuWQzJta1nfxNPwTem1X0uc=" )
										.add( ESI_LOGIN_HOST, "sha256/du6FkDdMcVQ3u8prumAo6t3i3G27uMP2EOhR8R0at/U=" )
										.build() )
						.addInterceptor( chain -> chain.proceed(
								chain.request()
										.newBuilder()
										.addHeader( "User-Agent", this.agent )
										.build() ) );
		this.verify = new Retrofit.Builder()
				.baseUrl( this.baseUrl )
				.addConverterFactory( JacksonConverterFactory.create() )
				.client( verifyClient.build() )
				.build()
				.create( VerifyCharacter.class );
	}

	private TokenTranslationResponse save( final OAuth2AccessToken token ) {
		TokenTranslationResponse returned =
				new TokenTranslationResponse()
						.setAccessToken( token.getAccessToken() )
						.setRefreshToken( token.getRefreshToken() )
						.setTokenType( token.getTokenType() )
						.setScope( token.getScope() )
						.setExpires( token.getExpiresIn() );
		this.store.save( returned );
		return returned;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComOAuth20 onConstruction;

		public Builder() {
			this.onConstruction = new NeoComOAuth20();
		}

		public NeoComOAuth20 build() {
			this.onConstruction.build();
			return this.onConstruction;
		}

		public Builder withAccessTokenEndpoint( final String accessTokenEndpoint ) {
			this.onConstruction.accessTokenEndpoint = accessTokenEndpoint;
			return this;
		}

		public Builder withAgent( final String agent ) {
			this.onConstruction.agent = agent;
			return this;
		}

		public Builder withAuthorizationBaseUrl( final String authorizationBaseUrl ) {
			this.onConstruction.authorizationBaseUrl = authorizationBaseUrl;
			return this;
		}

		public Builder withBaseUrl( final String baseUrl ) {
			this.onConstruction.baseUrl = baseUrl;
			return this;
		}

		public Builder withCallback( final String callback ) {
			Objects.requireNonNull( callback );
			this.onConstruction.callback = callback;
			return this;
		}

		public Builder withClientId( final String clientId ) {
			Objects.requireNonNull( clientId );
			this.onConstruction.clientId = clientId;
			return this;
		}

		public Builder withClientKey( final String clientKey ) {
			Objects.requireNonNull( clientKey );
			this.onConstruction.clientKey = clientKey;
			return this;
		}

		public Builder withScopes( final String scopes ) {
			if (null == scopes) this.onConstruction.scopes = "publicData";
			else this.onConstruction.scopes = scopes;
			return this;
		}

		public Builder withState( final String state ) {
			this.onConstruction.state = state;
			return this;
		}

		public Builder withStore( final ESIStore store ) {
			this.onConstruction.store = store;
			return this;
		}
	}
}
