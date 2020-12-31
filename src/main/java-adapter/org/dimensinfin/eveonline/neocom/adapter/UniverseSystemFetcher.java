package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;

import com.nytimes.android.external.store3.base.Fetcher;

import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class UniverseSystemFetcher implements Fetcher<GetUniverseSystemsSystemIdOk, Integer> {
	private Retrofit neocomRetrofitNoAuth; // HTTP client to be used on not authenticated endpoints.

	public UniverseSystemFetcher( final Retrofit neocomRetrofitNoAuth ) {
		this.neocomRetrofitNoAuth = neocomRetrofitNoAuth;
	}

	@Override
	public Single<GetUniverseSystemsSystemIdOk> fetch( final Integer systemId ) {
		try {
			return Single.just( this.getUniverseSystemById( systemId ) );
		} catch (IOException e) {
			return Single.just( null );
		}
	}

	public GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) throws IOException {
		final Response<GetUniverseSystemsSystemIdOk> systemResponse = this.neocomRetrofitNoAuth
				.create( UniverseApi.class )
				.getUniverseSystemsSystemId( systemId
						, DEFAULT_ACCEPT_LANGUAGE
						, DEFAULT_ESI_SERVER.toLowerCase(), null, null )
				.execute();
		if (systemResponse.isSuccessful()) return systemResponse.body();
		return null;
	}
}
