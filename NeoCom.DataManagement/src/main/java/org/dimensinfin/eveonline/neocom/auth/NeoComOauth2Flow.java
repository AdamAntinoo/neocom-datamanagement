package org.dimensinfin.eveonline.neocom.auth;

import java.util.Base64;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NeoComOauth2Flow {
	private TokenVerification tokenVerificationStore;
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;

	private NeoComOauth2Flow() {}

	public void onStartFlow( final String code, final String state, final String dataSource ) {
		final TokenVerification tokenVerificationStore = new TokenVerification()
				.setAuthCode( code )
				.setState( state )
				.setDataSource( dataSource.toLowerCase() );
	}

	public void onTranslationStep() {
		this.tokenVerificationStore
				.setTokenTranslationResponse( this.getTokenTranslationResponse( this.tokenVerificationStore ) );
	}

	/**
	 * Validates the encoded values of the state received to verify that the calling application is on the NeoCom set. There is
	 * a fixed value and in future implementations a variable value to improve security.
	 *
	 * @param state the state data received. Needs to mah the state generated locally.
	 */
	private boolean validateStateMatch( final String state ) {
		final String testState = Base64.getEncoder().encodeToString(
				this.configurationProvider.getResourceString( "P.esi.authorization.state" ).getBytes()
		).replaceAll( "\n", "" );
		return state.equals( testState );
	}

	private TokenTranslationResponse getTokenTranslationResponse( final TokenVerification store ) {
		// Preload configuration variables.
		final String esiServer = store.getDataSource();
		final String authorizationServer = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.server" );
		final String authorizationClientid = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.clientid" );
		final String authorizationSecretKey = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.secretkey" );
		final String authorizationContentType = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.content.type" );
		final String esiServerLoginUrl = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.server.url" );
		// Get the request.
		final GetAccessToken serviceGetAccessToken = new Retrofit.Builder()
				.baseUrl( authorizationServer )
				.addConverterFactory( JacksonConverterFactory.create() )
				.build()
				.create( GetAccessToken.class );
		final TokenRequestBody tokenRequestBody = new TokenRequestBody().setCode( store.getAuthCode() );
		NeoComLogger.info( "Creating request call." );
		final String peckString = authorizationClientid + ":" + authorizationSecretKey;
		String peck = Base64.getEncoder().encodeToString( peckString.getBytes() ).replaceAll( "\n", "" );
		store.setPeck( peck );
		final Call<TokenTranslationResponse> request = serviceGetAccessToken.getAccessToken(
				"Basic " + peck, authorizationContentType,
				esiServerLoginUrl,
				tokenRequestBody
		);
		// Getting the request response to be stored if valid.
		// TODO - This depends on the exception management implementation on the library.
//		try {
//			final Response<TokenTranslationResponse> response = request.execute();
//			if (response.isSuccessful()) {
//				NeoComLogger.info( "Response is 200 OK." );
//				final TokenTranslationResponse token = response.body();
//				return token;
//			} else {
//				NeoComLogger.info( "Response is {} - {}.", HttpStatus.BAD_REQUEST, response.message() );
//				throw new NeoComSBException( ErrorInfo.AUTHORIZATION_TRANSLATION );
//			}
//		} catch (IOException ioe) {
//			NeoComLogger.info( "Response is {} - {}.", HttpStatus.BAD_REQUEST, ioe.getMessage() );
//			throw new NeoComSBException( ErrorInfo.AUTHORIZATION_TRANSLATION, ioe );
//		}
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComOauth2Flow onConstruction;

		public Builder() {
			this.onConstruction = new NeoComOauth2Flow();
		}

		public NeoComOauth2Flow.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public NeoComOauth2Flow build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction;
		}
	}
}