package org.dimensinfin.eveonline.neocom.auth;

import java.util.Objects;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class NeoComAuthApi20 extends DefaultApi20 {
	private String accessServer;
	private String accessTokenEndpoint;
	private String authorizationBaseUrl;

	@Override
	public String getAccessTokenEndpoint() {
		return this.accessServer + this.accessTokenEndpoint;
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return this.accessServer + authorizationBaseUrl;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComAuthApi20 onConstruction;

		public Builder() {
			this.onConstruction = new NeoComAuthApi20();
		}

		public Builder withAccessServer( final String accessServer ) {
			this.onConstruction.accessServer = accessServer;
			return this;
		}

		public Builder withAccessTokenEndpoint( final String accessTokenEndpoint ) {
			this.onConstruction.accessTokenEndpoint = accessTokenEndpoint;
			return this;
		}

		public Builder withAuthorizationBaseUrl( final String authorizationBaseUrl ) {
			this.onConstruction.authorizationBaseUrl = authorizationBaseUrl;
			return this;
		}

		public NeoComAuthApi20 build() {
			Objects.requireNonNull(this.onConstruction.accessServer);
			return this.onConstruction;
		}
	}
}
