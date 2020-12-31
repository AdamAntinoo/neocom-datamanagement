package org.dimensinfin.eveonline.neocom.backend.rest.v1;

import java.util.List;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NeoComApiv1 {
	@GET("/api/v1/neocom/miningextractions/pilot/{pilotId}/today")
	Call<List<MiningExtractionEntity>> accessTodayMiningExtractions4Pilot( @Header("Content-Type") String contentType,
	                                                                       @Header("Authorization") String jwtToken,
	                                                                       @Path("pilotId") Integer pilotId );

	@PUT("/api/v1/neocom/credentials/{credentialId}")
	Call<CredentialStoreResponse> storeCredential( @Header("Content-Type") String contentType,
	                                               @Path("credentialId") Integer credentialId,
	                                               @Body Credential credential );
}
