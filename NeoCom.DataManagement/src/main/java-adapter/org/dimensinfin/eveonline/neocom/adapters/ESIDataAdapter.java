package org.dimensinfin.eveonline.neocom.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.domain.EsiItemV2;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CharacterApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nytimes.android.external.cache3.Futures;
import retrofit2.Response;

/**
 * This class will be the base to access most of the non authenticated SDE data available though the ESI data service.
 *
 * The new data service allows to access many endpoints with data that do not require pilot authentication. With this endpoints I will try to remove
 * the required SDE database and remove the need to add that heavy resource to the application download when implemented in Android.
 *
 * This class will also use other components to be able to store downloaded SDE data into caches, be them temporal in memory or persisted on disk.
 */
public class ESIDataAdapter {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";
	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	private static ESIDataAdapter singleton;
	// - C A C H E S
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(100);
	private static Map<Integer, GetUniverseRaces200Ok> racesCache = new HashMap<>();
	private static Map<Integer, GetUniverseAncestries200Ok> ancestriesCache = new HashMap<>();
	private static Map<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new HashMap<>();

	//	private static File storeDataFile;
	//	private static Gson gson = new Gson();
	//	private static Retrofit neocomRetrofitNoAuth;
	//	private static Store<EsiItemV2, Integer> esiItemStore;
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private NeoComRetrofitFactory retrofitFactory;
	private StoreCacheManager cacheManager;

	// - C O N S T R U C T O R S
	private ESIDataAdapter( final IConfigurationProvider newConfigurationProvider
			, final IFileSystem newFileSystemAdapter ) {
		configurationProvider = newConfigurationProvider;
		fileSystemAdapter = newFileSystemAdapter;
//		cacheManager = newCacheManager;
	}

	// - D O W N L O A D   S T A R T E R S
	public void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getMarketsPrices();
		logger.info(">> [ESIDataAdapter.downloadItemPrices]> Download market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
	}

	public void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getRaces(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadPilotFamilyData]> Download race: {} items", racesList.size());
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put(race.getRaceId(), race);
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getAncestries(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadPilotFamilyData]> Download ancestries: {} items", racesList.size());
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put(ancestry.getId(), ancestry);
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getBloodlines(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadPilotFamilyData]> Download blood lines: {} items", racesList.size());
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put(bloodLine.getBloodlineId(), bloodLine);
		}
	}

	// - S D E   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		if (marketDefaultPrices.containsKey(typeId)) return marketDefaultPrices.get(typeId).getAdjustedPrice();
		else return -1.0;
	}

	public EsiItemV2 searchEsiItem4Id( final int itemId ) {
		return this.cacheManager.accessItem(itemId).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		logger.info("-- [ESIDataAdapter.searchItemGroup4Id]> targetGroupId: {}", groupId);
		return this.cacheManager.accessGroup(groupId).blockingGet();
	}

	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		logger.info("-- [ESIDataAdapter.searchItemCategory4Id]> targetGroupId: {}", categoryId);
		return this.cacheManager.accessCategory(categoryId).blockingGet();
	}

	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
		return this.racesCache.get(identifier);
	}

	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
		return this.ancestriesCache.get(identifier);
	}

	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
		return this.bloodLinesCache.get(identifier);
	}

	public Future<MarketDataSet> searchMarketData( final int itemId, final EMarketSide side ) {
		return Futures.immediateFuture(new MarketDataSet(itemId, side));
	}
	// - U N I V E R S E

	/**
	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * To access the public data it will use the current unauthorized retrofit connection.
	 */
	public List<GetMarketsPrices200Ok> getMarketsPrices() {
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = retrofitFactory.accessNoAuthRetrofit().create(MarketApi.class)
					                                                                .getMarketsPrices(DEFAULT_ESI_SERVER.toLowerCase(), null)
					                                                                .execute();
			if (!marketApiResponse.isSuccessful()) {
				return new ArrayList<>();
			} else return marketApiResponse.body();
		} catch (IOException ioe) {
			return new ArrayList<>();
		}
	}

	public GetUniverseGroupsGroupIdOk getUniverseGroupById( final Integer groupId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseGroupsGroupIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                           .getUniverseGroupsGroupId(groupId
							                                                           , DEFAULT_ACCEPT_LANGUAGE
							                                                           , DEFAULT_ESI_SERVER, null, null)
					                                                           .execute();
			if (!groupResponse.isSuccessful()) {
				return null;
			} else return groupResponse.body();
		} catch (IOException ioe) {
			return null;
		}
	}

	public GetUniverseCategoriesCategoryIdOk getUniverseCategoryById( final Integer categoryId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseCategoriesCategoryIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                                  .getUniverseCategoriesCategoryId(categoryId
							                                                                  , DEFAULT_ACCEPT_LANGUAGE
							                                                                  , DEFAULT_ESI_SERVER, null, null)
					                                                                  .execute();
			if (!groupResponse.isSuccessful()) {
				return null;
			} else return groupResponse.body();
		} catch (IOException ioe) {
			return null;
		}
	}

	private List<GetUniverseRaces200Ok> getRaces( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getRaces]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                        .getUniverseRaces(DEFAULT_ACCEPT_LANGUAGE, datasource, null, "en-us")
					                                                        .execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getRaces]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private List<GetUniverseAncestries200Ok> getAncestries( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getAncestries]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                                  .getUniverseAncestries(DEFAULT_ACCEPT_LANGUAGE, datasource, null, "en-us")
					                                                                  .execute();
			if (ancestriesList.isSuccessful()) return ancestriesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getAncestries]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private List<GetUniverseBloodlines200Ok> getBloodlines( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getBloodlines]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                                  .getUniverseBloodlines(DEFAULT_ACCEPT_LANGUAGE, datasource, null, "en-us")
					                                                                  .execute();
			if (bloodLinesList.isSuccessful()) return bloodLinesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getBloodlines]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	// - C H A R A C T E R   P U B L I C   I N F O R M A T I O N
	public GetCharactersCharacterIdOk getCharactersCharacterId( final int identifier, final String refreshToken, final String server ) {
		logger.info("-- [ESIDataAdapter.getCharactersCharacterId]> Charcter identifier: {}", identifier);
		//		final Chrono accessFullTime = new Chrono();
		// Store the response at the cache or if there is a network failure return the last access if available
		//		final String reference = constructCachePointerReference(GlobalDataManagerCache.ECacheTimes.CHARACTER_PUBLIC, identifier);
		// Check if network is available and we have configured allowed access to download data.
		//		if (allowDownloadPass()) {
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			//				final NeoComOAuth20 auth = new NeoComOAuth20(CLIENT_ID, SECRET_KEY, CALLBACK, AGENT, STORE, SCOPES);
			//				final Retrofit retro = NeoComRetrofitHTTP.build(auth, AGENT, cacheDataFile, cacheSize, timeout);
			final Response<GetCharactersCharacterIdOk> characterResponse = this.retrofitFactory.accessESIAuthRetrofit()
					                                                               .create(CharacterApi.class)
					                                                               .getCharactersCharacterId(identifier
							                                                               , datasource, null).execute();
			// TODO - Replace by a new request not authenticated and direct.
			//				final Response<GetCharactersCharacterIdOk> characterResponse = processCharacterNotAuthenticated(datasource, identifier);
			if (characterResponse.isSuccessful()) {
				// Store results on the cache.
				//					okResponseCache.put(reference, characterResponse);
				return characterResponse.body();
			} else return null;
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
			// Return cached response if available
			//				if (null != okResponseCache.get(reference))
			//					return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
			//				else
			return null;
		} catch (RuntimeException rte) {
			logger.error("EX [ESINetworkManager.getCharactersCharacterId]> [EXCEPTION]: {}", rte.getMessage());
			rte.printStackTrace();
			// Return cached response if available
			//				if (null != okResponseCache.get(reference))
			//					return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
			//				else
			return null;
			//			} finally {
			//				logger.info("<< [ESINetworkManager.getCharactersCharacterId]> [TIMING] Full elapsed: {}"
			//						, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		//		} else if (null != okResponseCache.get(reference))
		//			return (GetCharactersCharacterIdOk) okResponseCache.get(reference).body();
		//		else return null;
	}

	// - B U I L D E R
	public static class Builder {
		private ESIDataAdapter onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as possible.
		 */
		public Builder( final IConfigurationProvider configurationProvider
				, final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull(configurationProvider);
			Objects.requireNonNull(fileSystemAdapter);
			//			Objects.requireNonNull(cacheManager);
			this.onConstruction = new ESIDataAdapter(configurationProvider, fileSystemAdapter);
		}

		public ESIDataAdapter build() {
			this.onConstruction.cacheManager = new StoreCacheManager.Builder().withEsiDataAdapter(this.onConstruction).build();
			//			retrofitFactory = new NeoComRetrofitFactory.Builder(configurationProvider, fileSystemAdapter).build();
			singleton = this.onConstruction;
			//			this.onConstruction.createStore(); // Run the initialisation code.
			return this.onConstruction;
		}
	}

	/**
	 * Search for the item on the current downloaded items cache. If not found then go for it to the network.
	 */
	public GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId ) {
		final GetUniverseTypesTypeIdOk item = this.getUniverseTypeById(typeId, "tranquility");
		//		return getUniverseTypeById("tranquility", typeId);
		return item;
	}

	@Deprecated
	private GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId, final String server ) {
		//		logger.info(">> [ESINetworkManagerMock.getUniverseTypeById]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseTypesTypeIdOk> itemListResponse = retrofitFactory.accessNoAuthRetrofit()
					                                                            .create(UniverseApi.class)
					                                                            .getUniverseTypesTypeId(typeId
							                                                            , "en-us"
							                                                            , server
							                                                            , null
							                                                            , null)
					                                                            .execute();
			if (!itemListResponse.isSuccessful()) {
				return null;
			} else {
				logger.info("-- [ESINetworkManager.getUniverseTypeById]> Downloading: {}-{}"
						, itemListResponse.body().getTypeId()
						, itemListResponse.body().getName());
				return itemListResponse.body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			//			logger.info("<< [ESINetworkManager.getUniverseTypeById]> [TIMING] Full elapsed: {}"
			//					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}
}
