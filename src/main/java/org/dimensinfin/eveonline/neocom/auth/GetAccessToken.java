package org.dimensinfin.eveonline.neocom.auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetAccessToken {
	@FormUrlEncoded
	@POST("/v2/oauth/token")
	Call<TokenTranslationResponse> getAccessToken( @Header("Content-Type") final String contentType,
	                                               @Header("Host") final String host,
	                                               @Header("Authorization") final String token,
	                                               @Field("grant_type") final String grantType,
	                                               @Field("code") final String code );
}
