package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRefreshBody {
	@JsonProperty("grant_type")
	public String grant_type = "refresh_token";
	@JsonProperty("code")
	public String refresh_token;

	// - C O N S T R U C T O R S
	public TokenRefreshBody() {
		super();
	}

	public TokenRefreshBody setRefreshToken( final String refresh_token ) {
		this.refresh_token = refresh_token;
		return this;
	}
}
