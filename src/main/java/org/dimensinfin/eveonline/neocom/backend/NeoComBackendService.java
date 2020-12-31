package org.dimensinfin.eveonline.neocom.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.annotation.LogEnterExit;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.backend.rest.v1.CredentialStoreResponse;
import org.dimensinfin.eveonline.neocom.backend.rest.v1.NeoComApiv1;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.RetrofitFactory.DEFAULT_CONTENT_TYPE;

public class NeoComBackendService {
	// - C O M P O N E N T S
	protected RetrofitFactory retrofitFactory;

	// - C O N S T R U C T O R S
	protected NeoComBackendService() {}

	@TimeElapsed
	@LogEnterExit
	public List<MiningExtractionEntity> accessTodayMiningExtractions4Pilot( final Credential credential ) {
		NeoComLogger.enter( "Credential: {}", credential.toString() );
		try {
			final Response<List<MiningExtractionEntity>> backendApiResponse = this.retrofitFactory
					.accessBackendConnector()
					.create( NeoComApiv1.class )
					.accessTodayMiningExtractions4Pilot( DEFAULT_CONTENT_TYPE,
							"Bearer " + credential.getJwtToken(),
							credential.getAccountId() )
					.execute();
			if (backendApiResponse.isSuccessful()) return backendApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	@LogEnterExit
	public CredentialStoreResponse storeCredential( final Credential credential ) {
		NeoComLogger.enter( "Credential: {}", credential.toString() );
		try {
			final Response<CredentialStoreResponse> backendApiResponse = this.retrofitFactory
					.accessBackendConnector()
					.create( NeoComApiv1.class )
					.storeCredential( DEFAULT_CONTENT_TYPE, credential.getAccountId(), credential )
					.execute();
			if (backendApiResponse.isSuccessful()) return backendApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComBackendService onConstruction;

		public Builder() {
			this.onConstruction = new NeoComBackendService();
		}

		public NeoComBackendService build() {
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			return this.onConstruction;
		}

		public NeoComBackendService.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}
	}
}
