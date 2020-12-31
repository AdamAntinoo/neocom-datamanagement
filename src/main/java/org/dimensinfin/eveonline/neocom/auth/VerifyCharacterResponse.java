package org.dimensinfin.eveonline.neocom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VerifyCharacterResponse {
	@JsonProperty("CharacterID")
	private long characterID;
	@JsonProperty("CharacterName")
	private String characterName;
	@JsonProperty("ExpiresOn")
	private String expiresOn;
	private long expiresMillis;
	@JsonProperty("Scopes")
	private String scopes;
	@JsonProperty("TokenType")
	private String tokenType;
	@JsonProperty("CharacterOwnerHash")
	private String characterOwnerHash;
	@JsonProperty("IntellectualProperty")
	private String intellectualProperty;

	// - C O N S T R U C T O R S
	public VerifyCharacterResponse() {
		super();
	}

	public long getCharacterID() {
		return this.characterID;
	}

	public String getCharacterName() {
		return this.characterName;
	}

	public String getScopes() {
		return this.scopes;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this , ToStringStyle.JSON_STYLE)
				.append( "characterID", characterID )
				.append( "characterName", characterName )
				.append( "expiresOn", expiresOn )
				.append( "expiresMillis", expiresMillis )
				.append( "scopes", scopes )
				.append( "tokenType", tokenType )
				.append( "characterOwnerHash", characterOwnerHash )
				.append( "intellectualProperty", intellectualProperty )
				.toString();
	}
}
