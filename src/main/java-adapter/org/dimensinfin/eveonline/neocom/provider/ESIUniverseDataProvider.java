package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.service.IStoreCache;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class ESIUniverseDataProvider {
	public static final String EN_US = "en-us";
	protected static final Logger logger = LoggerFactory.getLogger( ESIUniverseDataProvider.class );
	// - I N T E R N A L   C A C H E S
	@Deprecated
	private static final Map<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap<>( 1200 );
	// - C O M P O N E N T S
	protected IConfigurationService configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected IStoreCache storeCacheManager;
	protected RetrofitService retrofitService;

	// - C O N S T R U C T O R S
	protected ESIUniverseDataProvider() {}

	// - C O R P O R A T I O N   P U B L I C   I N F O R M A T I O N
	public GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier ) {
		try {
			final Response<GetCorporationsCorporationIdOk> corporationResponse = this.retrofitService
					.accessUniverseConnector()
					.create( CorporationApi.class )
					.getCorporationsCorporationId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	public GetCorporationsCorporationIdIconsOk getCorporationsCorporationIdIcons( final int identifier ) {
		try {
			final Response<GetCorporationsCorporationIdIconsOk> corporationResponse = this.retrofitService
					.accessUniverseConnector()
					.create( CorporationApi.class )
					.getCorporationsCorporationIdIcons( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	// - P R O V I D E R   A P I
	// - C A C H E D   A P I
	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessType( itemId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}


	// - S D E   I N T E R N A L   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		LogWrapper.info( MessageFormat.format( "Price for: {0}", typeId ) );
		if (marketDefaultPrices.isEmpty()) // First download the family data.
			this.downloadItemPrices();
		if (marketDefaultPrices.containsKey( typeId )) return marketDefaultPrices.get( typeId ).getAdjustedPrice();
		else return -1.0;
	}

	private void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getUniverseMarketsPrices();
		logger.info( ">> [ESIDataProvider.downloadItemPrices]> Download market prices: {} items", marketPrices.size() );
		for (final GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put( price.getTypeId(), price );
		}
	}


	/**
	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * To access the public data it will use the current unauthorized retrofit connection.
	 */
	@TimeElapsed
	private List<GetMarketsPrices200Ok> getUniverseMarketsPrices() {
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = this.retrofitService
					.accessUniverseConnector()
					.create( MarketApi.class )
					.getMarketsPrices( DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (marketApiResponse.isSuccessful())
				return marketApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		}
		return new ArrayList<>();
	}

	// - B U I L D E R
	public static class Builder {
		private final ESIUniverseDataProvider onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new ESIUniverseDataProvider();
		}

		public ESIUniverseDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.retrofitService );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			return this.onConstruction;
		}

		public ESIUniverseDataProvider.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public ESIUniverseDataProvider.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public ESIUniverseDataProvider.Builder withRetrofitFactory( final RetrofitService retrofitService ) {
			Objects.requireNonNull( retrofitService );
			this.onConstruction.retrofitService = retrofitService;
			return this;
		}

		public ESIUniverseDataProvider.Builder withStoreCacheManager( final IStoreCache storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}
	}
}
