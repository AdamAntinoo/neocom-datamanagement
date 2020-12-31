package org.dimensinfin.eveonline.neocom.auth;

import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class TokenVerification {
	private String authCode;
	private String state;
	private String dataSource;
	private TokenTranslationResponse tokenTranslationResponse;
	private String peck;
	private VerifyCharacterResponse verifyCharacterResponse;

	public String getAuthCode() {
		return this.authCode;
	}

	public TokenVerification setAuthCode( final String authCode ) {
		this.authCode = authCode;
		return this;
	}

	public String getState() {
		return this.state;
	}

	public TokenVerification setState( final String state ) {
		this.state = state;
		return this;
	}

	public String getDataSource() {
		if (null == this.dataSource) return ESIDataProvider.DEFAULT_ESI_SERVER;
		return this.dataSource;
	}

	public TokenVerification setDataSource( final String dataSource ) {
		this.dataSource = dataSource;
		return this;
	}

	public int getAccountIdentifier() {
		if (null != this.verifyCharacterResponse)
			return Long.valueOf( this.verifyCharacterResponse.getCharacterID() ).intValue();
		else throw new NeoComRuntimeException( ErrorInfoCatalog.INVALID_CREDENTIAL_IDENTIFIER );
	}

	public TokenTranslationResponse getTokenTranslationResponse() {
		return this.tokenTranslationResponse;
	}

	public TokenVerification setTokenTranslationResponse( TokenTranslationResponse tokenTranslationResponse ) {
		this.tokenTranslationResponse = tokenTranslationResponse;
		return this;
	}

	public String getPeck() {
		return this.peck;
	}

	public TokenVerification setPeck( String peck ) {
		this.peck = peck;
		return this;
	}

	public VerifyCharacterResponse getVerifyCharacterResponse() {
		return this.verifyCharacterResponse;
	}

	public TokenVerification setVerifyCharacterResponse( VerifyCharacterResponse verifyCharacterResponse ) {
		this.verifyCharacterResponse = verifyCharacterResponse;
		return this;
	}

	public String getScopes() {
		if (null != this.verifyCharacterResponse)
			return this.verifyCharacterResponse.getScopes();
		else return "publicData";
	}

	public String getAccessToken() {
		return this.tokenTranslationResponse.getAccessToken();
	}

	public String getRefreshToken() {
		return this.tokenTranslationResponse.getRefreshToken();
	}
}
