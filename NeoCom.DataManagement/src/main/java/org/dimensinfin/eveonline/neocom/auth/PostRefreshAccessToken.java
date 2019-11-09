package org.dimensinfin.eveonline.neocom.auth;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostRefreshAccessToken {
	@POST("/v2/oauth/token?grant_type=refresh_token")
	Call<TokenTranslationResponse> getNewAccessToken( @Header("Content-Type") final String contentType,
	                                                  @Header("Host") final String esiHost,
	                                                  @Header("Authorization") final String peck,
	                                                  @Query("refresh_token") final String refreshToken );
}
