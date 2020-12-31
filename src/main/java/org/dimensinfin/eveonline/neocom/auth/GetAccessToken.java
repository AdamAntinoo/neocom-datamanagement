package org.dimensinfin.eveonline.neocom.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetAccessToken {
	@POST("/oauth/token")
	Call<TokenTranslationResponse> getAccessToken( @Header("Content-Type") String contentType,
	                                               @Header("Host") String host,
	                                               @Header("Authorization") String token,
	                                               @Body TokenRequestBody body );
}
