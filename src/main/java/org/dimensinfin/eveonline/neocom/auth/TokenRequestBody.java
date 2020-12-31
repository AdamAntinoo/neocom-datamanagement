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
public class TokenRequestBody {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	@JsonProperty("grant_type")
	public String grant_type = "authorization_code";
	@JsonProperty("code")
	public String code;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TokenRequestBody () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getGrant_type () {
		return "authorization_code";
	}
	public String getCode () {
		return code;
	}

	public TokenRequestBody setCode (final String code) {
		this.code = code;
		return this;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
