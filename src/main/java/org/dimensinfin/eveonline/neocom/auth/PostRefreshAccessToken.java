package org.dimensinfin.eveonline.neocom.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostRefreshAccessToken {
	@Headers({
			"Host: login.eveonline.com"
	})
	@POST("/v2/oauth/token?grant_type=refresh_token")
	Call<TokenTranslationResponse> getNewAccessToken(
			@Header("Content-Type") final String contentType,
//			@Header("Authorization") final String basicAuthorization,
//			@Query("refresh_token") final String refreshToken,
			@Body final TokenRefreshBody body
	);
}
