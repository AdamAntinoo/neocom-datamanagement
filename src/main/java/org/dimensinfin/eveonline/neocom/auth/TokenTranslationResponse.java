//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class TokenTranslationResponse {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("token_type")
	public String tokenType;
	@JsonProperty("expires_in")
	public long expires;
	@JsonProperty("refresh_token")
	public String refreshToken;
	private final long created = System.currentTimeMillis();
	private String scope;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TokenTranslationResponse () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getAccessToken () {
		return accessToken;
	}

	public String getTokenType () {
		return tokenType;
	}

	public long getExpires () {
		return expires;
	}

	public long getExpiresOn () {
		return created + (expires * 1000);
	}

	public String getRefreshToken () {
		return refreshToken;
	}

	public String getScope () {
		return scope;
	}

	public TokenTranslationResponse setAccessToken (final String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public TokenTranslationResponse setTokenType (final String tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	public TokenTranslationResponse setExpires (final long expires) {
		this.expires = expires;
		return this;
	}

	public TokenTranslationResponse setRefreshToken (final String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public TokenTranslationResponse setScope (final String scope) {
		this.scope = scope;
		return this;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
