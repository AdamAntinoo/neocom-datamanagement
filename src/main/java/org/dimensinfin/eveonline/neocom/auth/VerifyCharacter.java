package org.dimensinfin.eveonline.neocom.auth;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface VerifyCharacter {
	@GET("/oauth/verify")
	Call<VerifyCharacterResponse> getVerification( @Header("Authorization") String token );
}
