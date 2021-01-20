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
import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AllianceApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApiV2;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.service.IStoreCache;
import org.dimensinfin.logging.LogWrapper;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class ESIUniverseDataProvider {
	public static final String EN_US = "en-us";
	protected static final Logger logger = LoggerFactory.getLogger( ESIUniverseDataProvider.class );
	// - I N T E R N A L   C A C H E S
	private static final Map<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap<>( 1200 );
	private static final Map<Integer, GetUniverseRaces200Ok> racesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseAncestries200Ok> ancestriesCache = new HashMap<>();
	private static final Map<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new HashMap<>();
	// - C O M P O N E N T S
	protected IConfigurationService configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected IStoreCache storeCacheManager;
	protected RetrofitFactory retrofitService;

	// - C O N S T R U C T O R S
	protected ESIUniverseDataProvider() {}

	// - G E T T E R S   &   S E T T E R S
	// - A L L I A N C E   P U B L I C   I N F O R M A T I O N
	public GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier ) {
		try {
			final Response<GetAlliancesAllianceIdOk> allianceResponse = this.retrofitService
					.accessUniverseConnector()
					.create( AllianceApi.class )
					.getAlliancesAllianceId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

	public GetAlliancesAllianceIdIconsOk getAlliancesAllianceIdIcons( final int identifier ) {
		try {
			final Response<GetAlliancesAllianceIdIconsOk> allianceResponse = this.retrofitService
					.accessUniverseConnector()
					.create( AllianceApi.class )
					.getAlliancesAllianceIdIcons( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			LogWrapper.error( ioe );
		}
		return null;
	}

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

	/**
	 * On new implementation if there are no more pages of data the response is a 404 instead of an empty list. Detect also this case.
	 */
	@TimeElapsed
	public List<GetMarketsRegionIdOrders200Ok> getUniverseMarketOrdersForId( final Integer regionId, final Integer typeId ) {
		LogWrapper.enter( MessageFormat.format( "regionId: {0} - typeId: {1}", regionId, typeId ) );
		final List<GetMarketsRegionIdOrders200Ok> returnMarketOrderList = new ArrayList<>( 1000 );
		try {
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while (morePages) {
				final Response<List<GetMarketsRegionIdOrders200Ok>> marketOrdersResponse = this.retrofitService
						.accessUniverseConnector()
						.create( MarketApiV2.class )
						.getMarketsRegionIdOrders( regionId, "all", DEFAULT_ESI_SERVER, pageCounter, typeId, null )
						.execute();
				if (marketOrdersResponse.isSuccessful()) {
					// Check for out of page running.
					if (Objects.requireNonNull( marketOrdersResponse.body() ).isEmpty()) morePages = false;
					else {
						// Copy the assets to the result list.
						returnMarketOrderList.addAll( Objects.requireNonNull( marketOrdersResponse.body() ) );
						pageCounter++;
					}
				} else morePages = false;
			}
		} catch (final IOException | RuntimeException rtex) {
			LogWrapper.error( rtex );
		} finally {
			LogWrapper.exit();
		}
		return returnMarketOrderList;
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

	// - A N C E S T R Y   D A T A
	@RequiresNetwork
	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
		if (ancestriesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return ancestriesCache.get( identifier );
	}

	@RequiresNetwork
	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
		if (bloodLinesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return bloodLinesCache.get( identifier );
	}

	// - S D E   I N T E R N A L   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		LogWrapper.info( MessageFormat.format( "Price for: {0}", typeId ) );
		if (marketDefaultPrices.isEmpty()) // First download the family data.
			this.downloadItemPrices();
		if (marketDefaultPrices.containsKey( typeId )) return marketDefaultPrices.get( typeId ).getAdjustedPrice();
		else return -1.0;
	}

	@RequiresNetwork
	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
		if (bloodLinesCache.isEmpty()) // First download the family data.
			this.downloadPilotFamilyData();
		return racesCache.get( identifier );
	}

	//	@Deprecated
	//	@TimeElapsed
	//	public GetUniverseSystemsSystemIdOk searchSolarSystem4Id( final int solarSystemId ) {
	//		//		LogWrapper.info( "SolarSystem: {}", solarSystemId + "" );
	//		//		return this.storeCacheManager.accessSolarSystem( solarSystemId ).blockingGet();
	//		return null;
	//	}

	private void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getUniverseMarketsPrices();
		logger.info( ">> [ESIDataProvider.downloadItemPrices]> Download market prices: {} items", marketPrices.size() );
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put( price.getTypeId(), price );
		}
	}

	private synchronized void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getUniverseRaces( DEFAULT_ESI_SERVER );
		LogWrapper.info( MessageFormat.format( "Download race: {0} items", racesList.size() ) );
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put( race.getRaceId(), race );
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getUniverseAncestries( DEFAULT_ESI_SERVER );
		LogWrapper.info( MessageFormat.format( "Download ancestries: {0} items", ancestriesList.size() ) );
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put( ancestry.getId(), ancestry );
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getUniverseBloodlines( DEFAULT_ESI_SERVER );
		LogWrapper.info( MessageFormat.format( "Download blood lines: {0} items", bloodLineList.size() ) );
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put( bloodLine.getBloodlineId(), bloodLine );
		}
	}

	@TimeElapsed
	private List<GetUniverseAncestries200Ok> getUniverseAncestries( final String datasource ) {
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseAncestries(
							DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, EN_US )
					.execute();
			if (ancestriesList.isSuccessful()) return ancestriesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return new ArrayList<>();
	}

	@TimeElapsed
	private List<GetUniverseBloodlines200Ok> getUniverseBloodlines( final String datasource ) {
		try {
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = this.retrofitService
					.accessUniverseConnector()
					.create(
							UniverseApi.class )
					.getUniverseBloodlines(
							DEFAULT_ACCEPT_LANGUAGE,
							datasource, null, EN_US )
					.execute();
			if (bloodLinesList.isSuccessful()) return bloodLinesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return new ArrayList<>();
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

	@TimeElapsed
	private List<GetUniverseRaces200Ok> getUniverseRaces( final String datasource ) {
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseRaces( DEFAULT_ACCEPT_LANGUAGE, datasource, null, EN_US )
					.execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
		}
		return new ArrayList<>();
	}

	// - B U I L D E R
	public static class Builder {
		private ESIUniverseDataProvider onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new ESIUniverseDataProvider();
		}

		public ESIUniverseDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.retrofitService );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			NeoItem.injectEsiUniverseDataAdapter( this.onConstruction );
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

		public ESIUniverseDataProvider.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitService = retrofitFactory;
			return this;
		}

		public ESIUniverseDataProvider.Builder withStoreCacheManager( final IStoreCache storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}
	}
}
