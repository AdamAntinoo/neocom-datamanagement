package org.dimensinfin.eveonline.neocom.auth;

import java.util.HashMap;
import java.util.Map;

public interface ESIStore {
	ESIStore DEFAULT = new ESIStore() {
		private Map<String, TokenTranslationResponse> map = new HashMap<>();

		@Override
		public void save( TokenTranslationResponse token ) {
			this.map.put(token.getRefreshToken(), token);
		}

		@Override
		public void delete( String refresh ) {
			this.map.remove(refresh);
		}

		@Override
		public TokenTranslationResponse get( String refresh ) {
			return this.map.get(refresh);
		}
	};


	void save( final TokenTranslationResponse token );

	void delete( final String refresh );

	TokenTranslationResponse get( final String refresh );
}
