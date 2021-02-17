package org.dimensinfin.eveonline.neocom.auth;

/**
 * This class is usded to store the information that should be added to the Token conversion endpoint. It now does not get serialized on the body
 * because the change to use URL encoded messages.
 *
 * @author Adam Antinoo
 */
public class TokenRequestBody {
	public String grant_type = "authorization_code";
	public String code;

	// - C O N S T R U C T O R S
	public TokenRequestBody() {
		super();
	}

	// - G E T T E R S   &   S E T T E R S
	public String getCode() {
		return code;
	}

	public TokenRequestBody setCode( final String code ) {
		this.code = code;
		return this;
	}

	public String getGrant_type() {
		return this.grant_type;
	}
}
