package org.dimensinfin.eveonline.neocom.backend.rest.v1;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialStoreResponse {
	private String jwtToken;

	private CredentialStoreResponse() {}

	public String getJwtToken() {
		return jwtToken;
	}

	// - B U I L D E R
	public static class Builder {
		private CredentialStoreResponse onConstruction;

		public Builder() {
			this.onConstruction = new CredentialStoreResponse();
		}

		public CredentialStoreResponse build() {
			return this.onConstruction;
		}

		public CredentialStoreResponse.Builder withJwtToken( final String jwtToken ) {
			Objects.requireNonNull( jwtToken );
			this.onConstruction.jwtToken = jwtToken;
			return this;
		}
	}
}
