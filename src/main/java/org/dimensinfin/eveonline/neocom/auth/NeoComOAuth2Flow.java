package org.dimensinfin.eveonline.neocom.auth;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.httpclient.RetrofitConfiguration;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.utility.Base64;
import org.dimensinfin.logging.LogWrapper;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import static org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog.AUTHENTICATION_FAILURE_ESI_SSO;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_LOGIN_HOST;

public class NeoComOAuth2Flow {
	private static final String ACCESS_TOKEN_HOST_HEADER = "login.eveonline.com";
	protected TokenVerification tokenVerificationStore;
	// - C O M P O N E N T S
	private RetrofitConfiguration configuration;

	// - C O N S T R U C T O R S
	private NeoComOAuth2Flow() {}

	public String generateLoginUrl() {
		final String state = Base64.encodeBytes( this.configuration.getState().getBytes() );
		final String clientId = this.configuration.getClientId();
		return "https://" +
				this.configuration.getServerLoginBase() +
				"/" +
				this.configuration.getAuthorizationAuthorizePath() +
				"?response_type=code" +
				"&redirect_uri=eveauth-neocom%3A%2F%2Fesiauthentication" +
				"&scope=publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 " +
				"esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search" +
				".search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1" +
				"&client_id=" + clientId +
				"&state=" + state;
	}

	public void onStartFlow( final String code, final String state, final String dataSource ) {
		this.tokenVerificationStore = new TokenVerification()
				.setAuthCode( code )
				.setState( state )
				.setDataSource( dataSource.toLowerCase() );
	}

	public TokenVerification onTranslationStep() {
		this.tokenVerificationStore
				.setTokenTranslationResponse( this.getTokenTranslationResponse( this.tokenVerificationStore ) );
		tokenVerificationStore.setVerifyCharacterResponse( this.getVerifyCharacterResponse( this.tokenVerificationStore ) );
		return this.tokenVerificationStore;
	}

	/**
	 * Validates the encoded values of the state received to verify that the calling application is on the NeoCom set. There is a fixed value and in
	 * future implementations a variable value to improve security.
	 *
	 * @param encodedState the state data received encoded in Base64. Needs to match the state generated locally.
	 */
	public boolean verifyState( final String encodedState ) {
		final String checkState = Base64.encodeBytes(
				this.configuration.getState().getBytes()
		).replaceAll( "\n", "" );
		return encodedState.equals( checkState );
	}

	public TokenTranslationResponse updateAccessToken( final String refreshToken, final String scope ) {
		final String authorizationServer = this.configuration.getServerLoginBase();
		final String authorizationContentType = this.configuration.getAuthorizationContentType();
		final String authorizationClientid = this.configuration.getClientId();
		final String authorizationSecret = this.configuration.getSecretKey();
		final String authorizationBasic = Base64.encodeBytes( String.join( ":", authorizationClientid, authorizationSecret ).getBytes() );
		final PostRefreshAccessToken serviceRefreshAccessToken = new Retrofit.Builder()
				.baseUrl( authorizationServer ) // This should be the URL with protocol configured on the tranquility server
				.addConverterFactory( JacksonConverterFactory.create() )
				.build()
				.create( PostRefreshAccessToken.class );
		final String grantType = "refresh_token";
		final Call<TokenTranslationResponse> request = serviceRefreshAccessToken.getNewAccessToken(
				authorizationContentType,
//				"Basic "+authorizationBasic,
				authorizationClientid,
				refreshToken
//				new TokenRefreshBody().setRefreshToken( refreshToken )
		);
		try {
			final Response<TokenTranslationResponse> response = request.execute();
			if ( response.isSuccessful() ) {
				LogWrapper.info( "Response is 200 OK." );
				return response.body();
			} else {
				LogWrapper.info( MessageFormat.format( "Response is {0} - {1}.",
						response.code(),
						response.errorBody().string() ) );
				throw new NeoComRuntimeException( AUTHENTICATION_FAILURE_ESI_SSO.getErrorMessage( response.errorBody().string() ) );
			}
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
			throw new NeoComRuntimeException( ioe );
		}
	}

	private TokenTranslationResponse getTokenTranslationResponse( final TokenVerification store ) {
		// Preload configuration variables.
		final String authorizationServer = this.configuration.getServerLoginBase();
		final String authorizationClientid = this.configuration.getClientId();
		final String authorizationSecretKey = this.configuration.getSecretKey();
		final String authorizationContentType = this.configuration.getAuthorizationContentType();
		final boolean tlsAuthentication = false;
		// Get the request.
		GetAccessToken serviceGetAccessToken;
		try {
			if ( tlsAuthentication )
				serviceGetAccessToken = new Retrofit.Builder()
						.baseUrl( authorizationServer ) // This should be the URL with protocol configured on the tranquility server
						.addConverterFactory( JacksonConverterFactory.create() )
						.client( new OkHttpClient.Builder()
								.connectionSpecs( Arrays.asList( ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS ) )
								.build() )
						.build()
						.create( GetAccessToken.class );
			else
				serviceGetAccessToken = new Retrofit.Builder()
						.baseUrl( authorizationServer ) // This should be the URL with protocol configured on the tranquility server
						.addConverterFactory( JacksonConverterFactory.create() )
						.build()
						.create( GetAccessToken.class );
		} catch (final RuntimeException rte) {
			// Url can miss the protocol name so silently the system fails.
			LogWrapper.error( rte );
			return null;
		}
		final TokenRequestBody tokenRequestBody = new TokenRequestBody().setCode( store.getAuthCode() );
		LogWrapper.info( "Creating request call." );
		final String peckString = authorizationClientid + ":" + authorizationSecretKey;
		String peck = Base64.encodeBytes( peckString.getBytes() ).replaceAll( "\n", "" );
		store.setPeck( peck );
		final Call<TokenTranslationResponse> request = serviceGetAccessToken.getAccessToken(
				authorizationContentType,
				ACCESS_TOKEN_HOST_HEADER, // This is the esi login server for /oauth/token call. WARNING do not add the protocol.
				"Basic " + peck,
				tokenRequestBody.getGrant_type(),
				tokenRequestBody.getCode()
		);
		// Getting the request response to be stored if valid.
		try {
			final Response<TokenTranslationResponse> response = request.execute();
			if ( response.isSuccessful() ) {
				LogWrapper.info( "Response is 200 OK." );
				return response.body();
			} else {
				LogWrapper.info( MessageFormat.format( "Response is {0} - {1}.",
						response.code(),
						response.errorBody().string() ) );
				throw new NeoComRuntimeException( AUTHENTICATION_FAILURE_ESI_SSO.getErrorMessage( response.errorBody().string() ) );
			}
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
			throw new NeoComRuntimeException( ioe );
		}
	}

	private VerifyCharacterResponse getVerifyCharacterResponse( final TokenVerification store ) {
		OkHttpClient.Builder verifyClient =
				new OkHttpClient.Builder()
						.certificatePinner(
								new CertificatePinner.Builder()
										.add( ESI_LOGIN_HOST, "sha256/5UeWOuDyX7IUmcKnsVdx+vLMkxEGAtzfaOUQT/caUBE=" )
										.add( ESI_LOGIN_HOST, "sha256/980Ionqp3wkYtN9SZVgMzuWQzJta1nfxNPwTem1X0uc=" )
										.add( ESI_LOGIN_HOST, "sha256/du6FkDdMcVQ3u8prumAo6t3i3G27uMP2EOhR8R0at/U=" )
										.build() )
						.addInterceptor( chain -> chain.proceed(
								chain.request()
										.newBuilder()
										.addHeader( "User-Agent", "org.dimensinfin" )
										.build() ) );
		// Verify the character authenticated.
		NeoComLogger.info( "Creating character verification." );
		final String authorizationServer = this.configuration.getServerLoginBase();
		final VerifyCharacter verificationService = new Retrofit.Builder()
				.baseUrl( authorizationServer ) // This should be the URL with protocol configured on the tranquility server
				.addConverterFactory( JacksonConverterFactory.create() )
				.client( verifyClient.build() )
				.build()
				.create( VerifyCharacter.class );
		final String accessToken = store.getTokenTranslationResponse().getAccessToken();
		try {
			final Response<VerifyCharacterResponse> verificationResponse = verificationService
					.getVerification( "Bearer " + accessToken )
					.execute();
			if ( verificationResponse.isSuccessful() ) {
				NeoComLogger.info( "Character verification OK." );
				return verificationResponse.body();
			} else {
				NeoComLogger.info( MessageFormat.format( "Response is {0} - {1}.",
						verificationResponse.code() + "",
						verificationResponse.message() ) );
			}
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComOAuth2Flow onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new NeoComOAuth2Flow();
		}

		public NeoComOAuth2Flow build() {
			Objects.requireNonNull( this.onConstruction.configuration );
			return this.onConstruction;
		}

		public NeoComOAuth2Flow.Builder withConfigurationService( final RetrofitConfiguration configuration ) {
			this.onConstruction.configuration = Objects.requireNonNull( configuration );
			return this;
		}
	}
}
