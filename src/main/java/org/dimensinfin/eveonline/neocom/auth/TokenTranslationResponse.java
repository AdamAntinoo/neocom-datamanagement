package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Adam Antinoo
 */
public class TokenTranslationResponse {
	private final long created = System.currentTimeMillis();
	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("token_type")
	public String tokenType;
	@JsonProperty("expires_in")
	public long expires;
	@JsonProperty("refresh_token")
	public String refreshToken;
	private String scope;

	// - C O N S T R U C T O R S
	public TokenTranslationResponse() {
		super();
	}

	// - G E T T E R S   &   S E T T E R S
	public String getAccessToken() {
		return accessToken;
	}

	public TokenTranslationResponse setAccessToken( final String accessToken ) {
		this.accessToken = accessToken;
		return this;
	}

	public long getExpires() {
		return expires;
	}

	public TokenTranslationResponse setExpires( final long expires ) {
		this.expires = expires;
		return this;
	}

	public long getExpiresOn() {
		return created + (expires * 1000);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public TokenTranslationResponse setRefreshToken( final String refreshToken ) {
		this.refreshToken = refreshToken;
		return this;
	}

	public String getScope() {
		return scope;
	}

	public TokenTranslationResponse setScope( final String scope ) {
		this.scope = scope;
		return this;
	}

	public String getTokenType() {
		return tokenType;
	}

	public TokenTranslationResponse setTokenType( final String tokenType ) {
		this.tokenType = tokenType;
		return this;
	}
}
