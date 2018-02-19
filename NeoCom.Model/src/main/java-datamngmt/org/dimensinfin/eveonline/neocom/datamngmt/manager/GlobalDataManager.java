//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.connectors.LoggingConnector;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.parser.eve.CharacterInfoParser;
import com.beimin.eveapi.parser.pilot.PilotAccountBalanceParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.joda.time.Instant;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.conf.GlobalConfigurationProvider;
import org.dimensinfin.eveonline.neocom.core.NeoComConnector;
import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.ColonyStorage;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.TimeStamp;
import org.dimensinfin.eveonline.neocom.enums.EMarketSide;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.manager.AbstractManager;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.manager.PlanetaryManager;
import org.dimensinfin.eveonline.neocom.market.MarketDataSet;
import org.dimensinfin.eveonline.neocom.model.ApiKey;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.PilotV1;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;

/**
 * This static class centralizes all the functionality to access data. It will provide a consistent api to the rest
 * of the application and will hide the internals of how that data is obtained, managed and stored.
 * All thet now are direct database access or cache access or even Model list accesses will be hidden under an api
 * that will decide at any point from where to get the information and if there are more jobs to do to keep
 * that information available and up to date.
 * <p>
 * The initial release will start transferring the ModelFactory functionality.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManager {
	// --- P U B L I C   E N U M E R A T O R S
	public enum EDataUpdateJobs {
		READY, CHARACTER_CORE, CHARACTER_FULL, ASSETDATA, BLUEPRINTDATA, INDUSTRYJOBS, MARKETORDERS, COLONYDATA, SKILL_DATA
	}

	public enum ECacheTimes {
		PLANETARY_INTERACTION_PLANETS, PLANETARY_INTERACTION_STRUCTURES, CORPORATION_CUSTOM_OFFICES, UNIVERSE_SCHEMATICS, MARKET_PRICES
	}

	// --- P R I V A T E   E N U M E R A T O R S
	private enum EModelVariants {
		PILOTV1, APIKEY
	}

	private enum EManagerCodes {
		PLANETARY_MANAGER, ASSETS_MANAGER
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManager");
	private static final String SERVER_DATASOURCE = "tranquility";

	// --- E X C E P T I O N   L O G G I N G   S E C T I O N
	private static final List<ExceptionRecord> exceptionsIntercepted=new ArrayList();
	public static void interceptException(final Exception exceptionIntercepted){
		exceptionsIntercepted.add(new ExceptionRecord(exceptionIntercepted));
	}

	// --- E V E A P I   X M L   S E C T I O N

	/** Initialize the beimin Eve Api connector to remove SSL certification. From this point on we can use the beimin
	 * XML api to access CCP data. */
	static {
		EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
		// Remove the secure XML access and configure the ApiConnector.
		ApiConnector.setSecureXmlProcessing(false);
	}

	/**
	 * GDM singleton to store all data, caches and references. The use of a singleton will allow to drop all data
	 * on a single operation and restart all data caches.
	 */

	// --- C O N F I G U R A T I O N   S E C T I O N
	private static IConfigurationProvider configurationManager = new GlobalConfigurationProvider(null);

	public static void connectConfigurationManager( final IConfigurationProvider newconfigurationProvider ) {
		configurationManager = newconfigurationProvider;
//		configurationManager.initialize();
	}

	public static String getResourceString( final String key ) {
		return configurationManager.getResourceString(key);
	}

	public static String getResourceString( final String key, final String defaultValue ) {
		return configurationManager.getResourceString(key, defaultValue);
	}

	public static int getResourceInt( final String key ) {
		try {
			return Integer.valueOf(configurationManager.getResourceString(key)).intValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static int getResourceInt( final String key, final String defaultValue ) {
		try {
			return Integer.valueOf(configurationManager.getResourceString(key, defaultValue)).intValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static long getResourceLong( final String key ) {
		try {
			return Long.valueOf(configurationManager.getResourceString(key)).longValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static long getResourceLong( final String key, final String defaultValue ) {
		try {
			return Long.valueOf(configurationManager.getResourceString(key, defaultValue)).longValue();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static boolean getResourceBoolean( final String key ) {
		return Boolean.valueOf(configurationManager.getResourceString(key)).booleanValue();
	}

	public static boolean getResourceBoolean( final String key, final boolean defaultValue ) {
		return Boolean.valueOf(configurationManager.getResourceString(key, Boolean.valueOf(defaultValue).toString())).booleanValue();
	}

	// --- C A C H E   S T O R A G E   S E C T I O N
	private static final Hashtable<Integer, EveItem> itemCache = new Hashtable<Integer, EveItem>();
	private static final Hashtable<Long, EveLocation> locationCache = new Hashtable<Long, EveLocation>();
	private static final Hashtable<Integer, ItemGroup> itemGroupCache = new Hashtable<Integer, ItemGroup>();
	private static final Hashtable<Integer, ItemCategory> itemCategoryCache = new Hashtable<Integer, ItemCategory>();
	private static final Hashtable<String, Long> ESICacheTimes = new Hashtable();

	static {
		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_PLANETS.name(), TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.PLANETARY_INTERACTION_STRUCTURES.name(), TimeUnit.SECONDS.toMillis(600));
		ESICacheTimes.put(ECacheTimes.MARKET_PRICES.name(), TimeUnit.SECONDS.toMillis(3600));
	}

	private static final ManagerOptimizedCache managerCache = new ManagerOptimizedCache();
	private static final ModelTimedCache modelCache = new ModelTimedCache();
	private static MarketDataServer marketDataService = null;
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(1000);

	public static void setMarketDataManager( final MarketDataServer manager ) {
		logger.info(">> [GlobalDataManager.setMarketDataManager]");
		marketDataService = manager;
		// At this point we should have been initialized.
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = ESINetworkManager.getMarketsPrices(SERVER_DATASOURCE);
		logger.info(">> [GlobalDataManager.setMarketDataManager]> Process all market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
		logger.info("<< [GlobalDataManager.setMarketDataManager]");
	}

	public static MarketDataSet searchMarketData( final int itemId, final EMarketSide side ) {
		if (null != marketDataService) return marketDataService.searchMarketData(itemId, side);
		else throw new RuntimeException("No MarketDataManager service connected.");
	}
	public static void activateMarketDataCache4Id( final int typeId ) {
		if (null != marketDataService)  marketDataService.activateMarketDataCache4Id(typeId);
		else throw new RuntimeException("No MarketDataManager service connected.");
	}

	/**
	 * Returns the default and average prices found on the ESI market price list for the specified item identifier.
	 *
	 * @param typeId
	 * @return
	 */
	public static GetMarketsPrices200Ok searchMarketPrice( final int typeId ) {
		final GetMarketsPrices200Ok hit = marketDefaultPrices.get(typeId);
		if (null == hit) return new GetMarketsPrices200Ok().typeId(typeId);
		else return hit;
	}

	public static void cleanEveItemCache() {
		itemCache.clear();
	}

	// --- M A P P E R S   &   T R A N S F O R M E R S   S E C T I O N
	/**
	 * Instance for the mapping of OK instances to the MVC compatible classes.
	 */
	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setMethodAccessLevel(Configuration.AccessLevel.PRIVATE);
	}

	/**
	 * Jackson mapper to use for object json serialization.
	 */
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	static {
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		jsonMapper.registerModule(new JodaModule());
		// Add our own serializers.
		SimpleModule neocomSerializerModule = new SimpleModule();
		neocomSerializerModule.addSerializer(Ship.class, new ShipSerializer());
		neocomSerializerModule.addSerializer(Credential.class, new CredentialSerializer());
		jsonMapper.registerModule(neocomSerializerModule);
	}

	// --- M U L T I T H R E A D I N G   S E C T I O N
	/**
	 * Background executor to use for long downloading jobs.
	 */
	private static final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
	private static final ExecutorService marketDataExecutor = Executors.newFixedThreadPool(2);
	private static final ExecutorService uiDataExecutor = Executors.newSingleThreadExecutor();

	public void shutdownExecutors() {
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown downloadExecutor");
			downloadExecutor.shutdown();
			downloadExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!downloadExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			downloadExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown marketDataExecutor");
			marketDataExecutor.shutdown();
			marketDataExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!marketDataExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			marketDataExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
		try {
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Attempt to shutdown uiDataExecutor");
			uiDataExecutor.shutdown();
			uiDataExecutor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (final InterruptedException iee) {
			logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
		} finally {
			if (!uiDataExecutor.isTerminated()) {
				logger.info("W- [GlobalDataManager.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
			}
			uiDataExecutor.shutdownNow();
			logger.info("-- [GlobalDataManager.shutdownExecutor]> Shutdown completed.");
		}
	}

	public static void suspendThread( final long millis ) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
		}
	}

	public static Future<?> submitJob2Download( final Runnable task ) {
		return downloadExecutor.submit(task);
	}

	public static Future<?> submitJob2Generic( final Runnable task ) {
		return downloadExecutor.submit(task);
	}

	// --- D A T A B A S E   F I E L D S
	// --- N E O C O M   P R I V A T E   D A T A B A S E   S E C T I O N
	/**
	 * Reference to the NeoCom persistece database Dao provider. This filed should be injected on startup.
	 */
	private static INeoComDBHelper neocomDBHelper = null;

	public static INeoComDBHelper getNeocomDBHelper() {
		if (null == neocomDBHelper)
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
		return neocomDBHelper;
	}

	public static void connectNeoComDBConnector( final INeoComDBHelper newhelper ) {
		if (null != newhelper) neocomDBHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
	}

	/**
	 * Reads all the keys stored at the database and classifies them into a set of Login names.
	 */
	@Deprecated
	public static List<ApiKey> accessAllApiKeys() {
		logger.info(">> [GlobalDataManager.accessAllLogins]");
		// Get access to all ApiKey registers
		List<ApiKey> keyList = new Vector<ApiKey>();
		try {
			keyList = getNeocomDBHelper().getApiKeysDao().queryForAll();
			// Extend the keys with some of the XML api information to get access to characters and credentials.
			for (ApiKey key : keyList) {
				key = GlobalDataManager.extendApiKey(key);
			}
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessAllLogins]> Exception reading all Logins. " + sqle.getMessage());
		} finally {
			logger.info("<< [GlobalDataManager.accessAllLogins]");
		}
		return keyList;
	}

	private static ApiKey extendApiKey( ApiKey basekey ) {
		// Check if this request is already available on the cache.
		try {
			// Get the ApiKey Information block.
			ApiAuthorization authorization = new ApiAuthorization(basekey.getKeynumber(), basekey.getValidationcode());
			ApiKeyInfoParser infoparser = new ApiKeyInfoParser();
			ApiKeyInfoResponse inforesponse = infoparser.getResponse(authorization);
			if (null != inforesponse) {
				basekey.setAuthorization(authorization)
						.setDelegated(inforesponse.getApiKeyInfo());
				//				.setCachedUntil(inforesponse.getCachedUntil());
				return basekey;
			}
		} catch (ApiException apie) {
			apie.printStackTrace();
		}
		return basekey;
	}

	/**
	 * Reads all the list of credentials stored at the Database and returns them. Activation depends on the
	 * interpretation used by the application.
	 */
	public static List<Credential> accessAllCredentials() {
		List<Credential> credentialList = new ArrayList<>();
		try {
			final Dao<Credential, String> credentialDao = GlobalDataManager.getNeocomDBHelper().getCredentialDao();
			final PreparedQuery<Credential> preparedQuery = credentialDao.queryBuilder().prepare();
			credentialList = credentialDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;
	}

	/**
	 * Reads the list of Colonies for the identified Credential from the persistence database. Inthe case there are no
	 * records the method checks the TimeStamp to verify that the Downloader is working on this demand and if
	 * the timer has elapsed or there is no TS, forces a first download directly throught the Network.
	 *
	 * @param credential
	 * @return
	 */
	public static List<Colony> accessColonies4Credential( final Credential credential ) {
		logger.info(">> [GlobalDataManager.accessColonies4Credential]> Credential: {}", credential.getAccountName());
		List<Colony> colonyList = new ArrayList<>();
		try {
			// SELECT * FROM COLONY WHERE OWNERID = <identifier>
			Dao<Colony, String> colonyDao = getNeocomDBHelper().getColonyDao();
			QueryBuilder<Colony, String> queryBuilder = colonyDao.queryBuilder();
			Where<Colony, String> where = queryBuilder.where();
			where.eq("ownerID", credential.getAccountId());
			PreparedQuery<Colony> preparedQuery = queryBuilder.prepare();
			colonyList = colonyDao.query(preparedQuery);

			// Check the number of registers. If they are zero then we have to perform additional checks.
			if (colonyList.size() < 1) {
				// Check if there is a valid TS.
				final String reference = constructJobReference(EDataUpdateJobs.COLONYDATA, credential.getAccountId());
				final TimeStamp ts = GlobalDataManager.getNeocomDBHelper().getTimeStampDao().queryForId(reference);
				if (null == ts) {
					// No time stamp so force a request for this data now.
					return GlobalDataManager.downloadColonies4Credential(credential);
				} else {
					// Check time stamp if elapsed.
					if (ts.getTimeStamp() < Instant.now().getMillis())
						return GlobalDataManager.downloadColonies4Credential(credential);
				}
			}

			// Add pending downloaded information.
			for (Colony col : colonyList) {
				final List<ColonyStructure> struc = accessColonyStructures4Planet(credential.getAccountId(), col.getPlanetId());
				// Check that the structures have been stored at the database. If this fails go to download them.
				if (struc.size() < 1)
					col.setStructures(downloadStructures4Colony(credential.getAccountId(), col.getPlanetId()));
				else col.setStructures(struc);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessColonies4Credential]> Exception reading Colonies. " + sqle.getMessage());
		} finally {
			logger.info("<< [GlobalDataManager.accessColonies4Credential]");
		}
		return colonyList;
	}

	public static List<ColonyStructure> accessColonyStructures4Planet( final int identifier, final int planet ) {
		logger.info(">> [GlobalDataManager.accessColonyStructures4Planet]");
		List<ColonyStructure> structureList = new ArrayList<>();
		try {
			try {
				// Compose the unique key reference.
				final String ref = constructPlanetStorageIdentifier(identifier, planet);
				logger.info(">> [GlobalDataManager.accessColonyStructures4Planet]> Structure reference: {}", ref);
				// SELECT * FROM ColonyStorage WHERE planetIdentifier = <identifier>
				final List<ColonyStorage> structureData = GlobalDataManager.getNeocomDBHelper()
						.getColonyStorageDao().queryForEq("planetIdentifier", ref);
				if (null != structureData) {
					for (ColonyStorage storage : structureData) {
						// Reconstruct the structure from the serialized data.
						final ColonyStructure structure = jsonMapper.readValue(storage.getColonySerialization(), ColonyStructure.class);
						structureList.add(structure);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManager.accessColonyStructures4Planet]> Exception reading Colonies. " + sqle.getMessage());
		} finally {
			logger.info("<< [GlobalDataManager.accessColonyStructures4Planet]");
		}
		return structureList;
	}

	public static ArrayList<NeoComAsset> accessAssetsContainedAt( final long characterID, final long containerId ) {
		return accessAssetsContainedAt(containerId);
	}

	public static ArrayList<NeoComAsset> accessAssetsContainedAt( final long containerId ) {
		// Select assets for the owner and with an specific type id.
		List<NeoComAsset> assetList = new ArrayList<NeoComAsset>();
		try {
			// TODO Another and more simple way to execute the command.
			assetList = getNeocomDBHelper().getAssetDao().queryForEq("parentAssetID", Long.valueOf(containerId).toString());
			//			Dao<NeoComAsset, String> assetDao = getNeocomDBHelper().getAssetDao();
			//			QueryBuilder<NeoComAsset, String> queryBuilder = assetDao.queryBuilder();
			//			Where<NeoComAsset, String> where = queryBuilder.where();
			//			// TODO Check if this gives the same results because a container only can belong to an owner. If we have the
			//			// identifier then we should be the owner or being using that credential.
			////			where.eq("ownerID", characterID);
			////			where.and();
			//			where.eq("parentAssetID", containerId);
			//			PreparedQuery<NeoComAsset> preparedQuery = queryBuilder.prepare();
			//			assetList = assetDao.query(preparedQuery);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<NeoComAsset>) assetList;
	}

	// --- S D E   D A T A B A S E   S E C T I O N
	/**
	 * Reference to the SDE database managers to access the Eve Online downloaded database.
	 */
	private static ISDEDBHelper neocomSDEHelper = null;

	private static ISDEDBHelper getSDEDBHelper() {
		if (null == neocomSDEHelper)
			throw new RuntimeException("[NeoComDatabase]> SDE Eve database neocomSDEHelper not defined. No access to platform library to get SDE data.");
		return neocomSDEHelper;
	}

	public static void connectSDEDBConnector( final ISDEDBHelper newhelper ) {
		if (null != newhelper) neocomSDEHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> SDE Eve database neocomSDEHelper not defined. No access to platform library to get SDE data.");
	}

	public static EveItem searchItem4Id( final int typeId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (itemCache.containsKey(typeId)) return itemCache.get(typeId);
		else {
			final EveItem hit = GlobalDataManager.getSDEDBHelper().searchItem4Id(typeId);
			// Add the hit to the cache.
			itemCache.put(typeId, hit);
			return hit;
		}
	}

	public static EveLocation searchLocation4Id( final long locationId ) {
		// Check if this item already on the cache. The only values that can change upon time are the Market prices.
		if (locationCache.containsKey(locationId)) {
			// Account for a hit on the cache.
			int access = GlobalDataManager.getSDEDBHelper().locationsCacheStatistics.accountAccess(true);
			int hits = GlobalDataManager.getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location " + locationId + " found at cache.");
			return locationCache.get(locationId);
		} else {
			final EveLocation hit = GlobalDataManager.getSDEDBHelper().searchLocation4Id(locationId);
			// Add the hit to the cache.
			locationCache.put(locationId, hit);
			// Account for a miss on the cache.
			int access = GlobalDataManager.getSDEDBHelper().locationsCacheStatistics.accountAccess(false);
			int hits = GlobalDataManager.getSDEDBHelper().locationsCacheStatistics.getHits();
			logger.info(">< [GlobalDataManager.searchLocation4Id]> [HIT-" + hits + "/" + access + "] Location {}" + locationId + " " +
					"found at database.");
			return hit;
		}
	}

	public static EveLocation searchLocationBySystem( final String name ) {
		return GlobalDataManager.getSDEDBHelper().searchLocationBySystem(name);
	}

	public static ItemGroup searchItemGroup4Id( final int targetGroupId ) {
		if (itemGroupCache.containsKey(targetGroupId)) return itemGroupCache.get(targetGroupId);
		else {
			final ItemGroup hit = GlobalDataManager.getSDEDBHelper().searchItemGroup4Id(targetGroupId);
			// Add the hit to the cache.
			itemGroupCache.put(targetGroupId, hit);
			return hit;
		}
	}

	public static ItemCategory searchItemCategory4Id( final int targetCategoryId ) {
		if (itemCategoryCache.containsKey(targetCategoryId)) return itemCategoryCache.get(targetCategoryId);
		else {
			final ItemCategory hit = GlobalDataManager.getSDEDBHelper().searchItemCategory4Id(targetCategoryId);
			// Add the hit to the cache.
			itemCategoryCache.put(targetCategoryId, hit);
			return hit;
		}
	}

	public static int searchStationType( final long typeId ) {
		return GlobalDataManager.getSDEDBHelper().searchStationType(typeId);
	}

	// --- P R I M A R Y    K E Y   C O N S T R U C T O R S
	public static String constructModelStoreReference( final GlobalDataManager.EDataUpdateJobs type, final long
			identifier ) {
		return new StringBuffer("TS/")
				.append(type.name())
				.append("/")
				.append(identifier)
				.toString();
	}

	public static String constructJobReference( final EDataUpdateJobs type, final long identifier ) {
		return new StringBuffer("JOB:")
				.append(type.name())
				.append("/")
				.append(identifier)
				.toString();
	}

	public static String constructPlanetStorageIdentifier( final int characterIdentifier, final int planetIdentifier ) {
		return new StringBuffer("CS:")
				.append(Integer.valueOf(characterIdentifier).toString())
				.append(":")
				.append(Integer.valueOf(planetIdentifier).toString())
				.toString();
	}

	// --- M A N A G E R - S T O R E   I N T E R F A C E
	public static AssetsManager getAssetsManager( final Credential credential ) {
		return GlobalDataManager.getAssetsManager(credential, false);
	}

	public static AssetsManager getAssetsManager( final Credential credential, final boolean forceNew ) {
		// Check if this request is already available on the cache.
		final AssetsManager hit = (AssetsManager) managerCache.access(EManagerCodes.ASSETS_MANAGER, credential.getAccountId());
		if ((null == hit) || (forceNew)) {
			final AssetsManager manager = new AssetsManager(DataManagementModelStore.getCredential4Id(credential.getAccountId()));
			managerCache.store(EManagerCodes.ASSETS_MANAGER, manager, credential.getAccountId());
			return manager;
		} else return hit;
	}

	public static AssetsManager dropAssetsManager( final long identifier ) {
		return (AssetsManager) managerCache.delete(EManagerCodes.ASSETS_MANAGER, identifier);
	}

	public static PlanetaryManager getPlanetaryManager( final Credential credential ) {
		return getPlanetaryManager(credential, false);
	}

	public static PlanetaryManager getPlanetaryManager( final Credential credential, final boolean forceNew ) {
		// Check if this request is already available on the cache.
		final PlanetaryManager hit = (PlanetaryManager) managerCache.access(EManagerCodes.PLANETARY_MANAGER, credential.getAccountId());
		if ((null == hit) || (forceNew)) {
			// TODO This line depends on the architecture of the data loading when it should not.
			final PlanetaryManager manager = new PlanetaryManager(credential);
			managerCache.store(EManagerCodes.PLANETARY_MANAGER, manager, credential.getAccountId());
			return manager;
		} else return hit;
	}

	public static PlanetaryManager dropPlanetaryManager( final long identifier ) {
		return (PlanetaryManager) managerCache.delete(EManagerCodes.PLANETARY_MANAGER, identifier);
	}

	// --- M O D E L - S T O R E   I N T E R F A C E

	/**
	 * Construct a minimal implementation of a Pilot from the XML api. This will get deprecated soon but during
	 * some time It will be compatible and I will have a better view of what variants are being used.
	 *
	 * @param identifier character identifier from the valid Credential.
	 * @return an instance of a PilotV1 class that has some of the required information to be shown on the ui at this
	 * point.
	 */
	public static PilotV1 getPilotV1( final int identifier ) {
		logger.info(">> [GlobalDataManager.getPilotV1]> Identifier: {}",identifier);
		try {
			// Check if this request is already available on the cache.
			final ICollaboration hit = modelCache.access(EModelVariants.PILOTV1, identifier);
			if (null == hit) {
				logger.info("-- [GlobalDataManager.getPilotV1]> Instance not found at cache. Downloading pilot <{}> info.", identifier);
				final PilotV1 newchar = new PilotV1();
				// Get the credential from the Store and check if this identifier has access to the XML api.
				final Credential credential = DataManagementModelStore.getCredential4Id(identifier);
				if (null != credential) {
					logger.info("-- [GlobalDataManager.getPilotV1]> Processing data with Credential <{}>.", credential.getAccountName());
					// Check the Credential type.
					CharacterInfoResponse inforesponse = null;
					if (credential.isXMLCompatible()) {
						try {
							logger.info(">> [GlobalDataManager.getPilotV1]> XML Compatible. Complete data with ApiKey information.");
							// Copy the authorization and add to it the characterID
							final ApiAuthorization authcopy = new ApiAuthorization(credential.getKeyCode(), identifier,
									credential.getValidationCode());
							// TODO It seems this is not required on this version of the object.
							//		newchar.setAuthorization(authcopy);
							// Copy the id to a non volatile field.
							newchar.setCharacterId(identifier);
							newchar.setName(credential.getAccountName());
							// Access the delegated Character using the ApiKey XML old api.
							final List<ApiKey> apikeyList = GlobalDataManager.getNeocomDBHelper().getApiKeysDao().queryForEq("keynumber",
									credential.getKeyCode());
							if (null != apikeyList) {
								final ApiKey apikey = extendApiKey(apikeyList.get(0));
								Collection<Character> coreList = apikey.getEveCharacters();
								for (Character character : coreList) {
									if (character.getCharacterID() == identifier)
										newchar.setDelegatedCharacter(character);
								}
							}
//							logger.info(">> [GlobalDataManager.getPilotV1]> XML Compatible. Get balance information.");
							// Balance information
							final PilotAccountBalanceParser balanceparser = new PilotAccountBalanceParser();
							final AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
							if (null != balanceresponse) {
								final Set<EveAccountBalance> balance = balanceresponse.getAll();
								if (balance.size() > 0) {
									newchar.setAccountBalance(balance.iterator().next().getBalance());
								}
							}
							logger.info("-- [GlobalDataManager.getPilotV1]> XML Compatible. Get balance {}.", newchar.getAccountBalance());

							// Character information
							final CharacterInfoParser infoparser = new CharacterInfoParser();
							inforesponse = infoparser.getResponse(authcopy);
							if (null != inforesponse) {
								newchar.setCharacterInfo(inforesponse);
							}
							logger.info("-- [GlobalDataManager.getPilotV1]> XML Compatible. Get CharacterInfo.");
						} catch (ApiException apie) {
							apie.printStackTrace();
						} catch (SQLException sqle) {
							sqle.printStackTrace();
						}
					}
					if (credential.isESICompatible()) {
						logger.info("-- [GlobalDataManager.getPilotV1]> ESI Compatible. Download clone information.");
						// Clone data
						final GetCharactersCharacterIdClonesOk cloneInformation = ESINetworkManager.getCharactersCharacterIdClones(Long.valueOf(identifier).intValue(), credential.getRefreshToken(), "tranquility");
						if (null != cloneInformation) newchar.setHomeLocation(cloneInformation.getHomeLocation());
					}
					if (null != inforesponse) {
						try {
							// Store the result on the cache with the timing indicator to where this entry is valid.
							final Instant expirationTime = new Instant(inforesponse.getCachedUntil()).plus(TimeUnit.HOURS.toMillis(2));
							modelCache.store(EModelVariants.PILOTV1, newchar, expirationTime, identifier);

							// Store this same information on the database to record the TimeStamp.
							final String reference = GlobalDataManager.constructModelStoreReference(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE, credential.getAccountId());
							TimeStamp timestamp = getNeocomDBHelper().getTimeStampDao().queryForId(reference);
							if (null == timestamp) timestamp = new TimeStamp(reference, expirationTime);
							logger.info("-- [GlobalDataManager.getPilotV1]> Updating character TimeStamp {}.", reference);
							timestamp.setTimeStamp(expirationTime)
									.setCredentialId(credential.getAccountId())
									.store();
						} catch (SQLException sqle) {
							sqle.printStackTrace();
						}
					}
				}
				return newchar;
			} else {
				logger.info("-- [GlobalDataManager.getPilotV1]> Pilot <{}> found at cache.", identifier);
				return (PilotV1) hit;
			}
		} finally {
			logger.info("<< [GlobalDataManager.getPilotV1]");
		}
	}

	public static boolean checkPilotV1( final int identifier ) {
		final ICollaboration hit = modelCache.access(EModelVariants.PILOTV1, identifier);
		if (null == hit) return false;
		else return true;
	}

	/**
	 * Deletes the current entry if found and forces a new download.
	 *
	 * @param identifier the pilot identifier to load.
	 * @return
	 */
	public static PilotV1 udpatePilotV1( final int identifier ) {
		logger.info(">> [GlobalDataManager.udpatePilotV1]");
		try {
			final ICollaboration hit = modelCache.access(EModelVariants.PILOTV1, identifier);
			if (null != hit) modelCache.delete(EModelVariants.PILOTV1, identifier);
			return getPilotV1(identifier);
		} finally {
			logger.info("<< [GlobalDataManager.udpatePilotV1]");
		}
	}

	// --- N E T W O R K    D O W N L O A D   I N T E R F A C E
	static{
		ESINetworkManager.initialize();
	}
	public static List<Colony> downloadColonies4Credential( final Credential credential ) {
		// Optimize the access to the Colony data.
		//		if(colonies.size()<1) {
		final Chrono accessFullTime = new Chrono();
		List<Colony> colonies = new ArrayList<>();
		try {
			// Create a request to the ESI api downloader to get the list of Planets of the current Character.
			final int identifier = credential.getAccountId();
			final List<GetCharactersCharacterIdPlanets200Ok> colonyInstances = ESINetworkManager.getCharactersCharacterIdPlanets(identifier, credential.getRefreshToken(), SERVER_DATASOURCE);
			// Transform the received OK instance into a NeoCom compatible model instance.
			for (GetCharactersCharacterIdPlanets200Ok colonyOK : colonyInstances) {
				try {
					Colony col = modelMapper.map(colonyOK, Colony.class);
					// Block to add additional data not downloaded on this call.
					// To set more information about this particular planet we should call the Universe database.
					final GetUniversePlanetsPlanetIdOk planetData = ESINetworkManager.getUniversePlanetsPlanetId(col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
					if (null != planetData) col.setPlanetData(planetData);

					try {
						// During this first phase download all the rest of the information.
						// Get to the Network and download the data from the ESI api.
						final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
						if (null != colonyStructures) {
							// Add the original data to the colony if we need some more information later.
							col.setStructuresData(colonyStructures);
							List<ColonyStructure> results = new ArrayList<>();

							// Process the structures converting the pin to the Colony structures compatible with MVC.
							final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
							for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
								ColonyStructure newstruct = modelMapper.map(structureOK, ColonyStructure.class);
								// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
								try {
									final String serialized = jsonMapper.writeValueAsString(newstruct);
									final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), col.getPlanetId());
									final ColonyStorage storage = new ColonyStorage(newstruct.getPinId())
											.setPlanetIdentifier(storageIdentifier)
											.setColonySerialization(serialized)
											.store();
								} catch (JsonProcessingException jpe) {
									jpe.printStackTrace();
								}
								// missing code
								results.add(newstruct);
							}
							col.setStructures(results);
						}
					} catch (RuntimeException rtex) {
						rtex.printStackTrace();
					}
					col.store();
					colonies.add(col);
				} catch (RuntimeException rtex) {
					rtex.printStackTrace();
				}
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return colonies;
	}

	public static List<ColonyStructure> downloadStructures4Colony( final int characterid, final int planetid ) {
		logger.info(">> [GlobalDataManager.accessStructures4Colony]");
		List<ColonyStructure> results = new ArrayList<>();
		// Get the Credential that matched the received identifier.
		Credential credential = DataManagementModelStore.getCredential4Id(characterid);
		if (null != credential) {
			// Get to the Network and download the data from the ESI api.
			final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), planetid, credential.getRefreshToken(), SERVER_DATASOURCE);
			if (null != colonyStructures) {
				// Process the structures converting the pin to the Colony structures compatible with MVC.
				final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
				for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
					ColonyStructure newstruct = modelMapper.map(structureOK, ColonyStructure.class);
					// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
					try {
						final String serialized = jsonMapper.writeValueAsString(newstruct);
						final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), planetid);
						final ColonyStorage storage = new ColonyStorage(newstruct.getPinId())
								.setPlanetIdentifier(storageIdentifier)
								.setColonySerialization(serialized)
								.store();
					} catch (JsonProcessingException jpe) {
						jpe.printStackTrace();
					}
					results.add(newstruct);
				}
			}
		} else {
			// TODO. It will not return null. The miss searching for a credential will generate an exception.
			// Possible that because the application has been previously removed from memory that data is not reloaded.
			// Call the reloading mechanism and have a second opportunity.
			DataManagementModelStore.accessCredentialList();
			credential = DataManagementModelStore.getCredential4Id(characterid);
			if (null == credential) return new ArrayList<>();
			else return GlobalDataManager.downloadStructures4Colony(characterid, planetid);
		}
		return results;
	}

	public static List<Fitting> downloadFitting4Credential( final int characterid ) {
		logger.info(">> [GlobalDataManager.downloadFitting4Credential]");
		List<Fitting> results = new ArrayList<>();
		try {
			Credential credential = DataManagementModelStore.getCredential4Id(characterid);
//		if (null != credential) {
			// Get to the Network and download the data from the ESI api.
			final List<GetCharactersCharacterIdFittings200Ok> fittings = ESINetworkManager.getCharactersCharacterIdFittings(characterid, credential.getRefreshToken(), SERVER_DATASOURCE);
			if (null != fittings) {
				// Process the fittings processing them and converting the data to structures compatible with MVC.

////				final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
				for (GetCharactersCharacterIdFittings200Ok fit : fittings) {
					final Fitting newfitting = modelMapper.map(fit, Fitting.class);
//					// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
//					try {
//						final String serialized = jsonMapper.writeValueAsString(newstruct);
//						final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), planetid);
//						final ColonyStorage storage = new ColonyStorage(newstruct.getPinId())
//								.setPlanetIdentifier(storageIdentifier)
//								.setColonySerialization(serialized)
//								.store();
//					} catch (JsonProcessingException jpe) {
//						jpe.printStackTrace();
//					}
					results.add(newfitting);
//				}
				}
			}
			return results;
		} catch (NeocomRuntimeException nrex) {
			logger.info("EX [GlobalDataManager.downloadFitting4Credential]> Credential not found in the list. Exception: {}", nrex
					.getMessage());
			return new ArrayList<>();
		} catch (RuntimeException ntex) {
			logger.info("EX [GlobalDataManager.downloadFitting4Credential]> Mapping error - {}", ntex
					.getMessage());
			return new ArrayList<>();
		} finally {
			logger.info("<< [GlobalDataManager.downloadFitting4Credential]");
		}
	}
	// --- S E R I A L I Z A T I O N   I N T E R F A C E
//	public static String serializeCredentialList( final List<Credential> credentials ) {
//		// Use my own serialization control to return the data to generate exactly what I want.
//		String contentsSerialized = "[jsonClass: \"Exception\"," +
//				"message: \"Unprocessed data. Possible JsonProcessingException exception.\"]";
//		try {
//			contentsSerialized = jsonMapper.writeValueAsString(credentials);
//		} catch (JsonProcessingException jpe) {
//			jpe.printStackTrace();
//		}
//		return contentsSerialized;
//	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("GlobalDataManager [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	// - CLASS IMPLEMENTATION .................................................................................
	public static class ManagerOptimizedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, AbstractManager> _managerCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size() {
			return _managerCacheStore.size();
		}

		public String constructManagerIdentifier( final String type, final long identifier ) {
			return new StringBuffer(type).append("/").append(identifier).toString();
		}

		public AbstractManager access( final EManagerCodes variant, long longIdentifier ) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			final AbstractManager hit = _managerCacheStore.get(locator);
			return hit;
		}

		public boolean store( final EManagerCodes variant, final AbstractManager instance, final long longIdentifier ) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			_managerCacheStore.put(locator, instance);
			return true;
		}

		public AbstractManager delete( final EManagerCodes variant, final long longIdentifier ) {
			final String locator = constructManagerIdentifier(variant.name(), longIdentifier);
			final AbstractManager hit = _managerCacheStore.get(locator);
			_managerCacheStore.remove(locator);
			return hit;
		}
	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ModelTimedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, ICollaboration> _instanceCacheStore = new Hashtable();
		private Hashtable<String, Instant> _timeCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size() {
			return _instanceCacheStore.size();
		}

		public ICollaboration access( final EModelVariants variant, long longIdentifier ) {
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				if (null != hit) {
					final Instant expitationTime = _timeCacheStore.get(locator);
					if (expitationTime.isBefore(Instant.now())) return null;
					else return hit;
				}
			}
			return null;
		}

		public ICollaboration delete( final EModelVariants variant, long longIdentifier ) {
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				_instanceCacheStore.put(locator, null);
				return hit;
			}
			return null;
		}

		public boolean store( final EModelVariants variant, final ICollaboration instance, final Instant expirationTime, final long longIdentifier ) {
			// Store command for PILOTV1 instances.
			if (variant == EModelVariants.PILOTV1) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				_instanceCacheStore.put(locator, instance);
				_timeCacheStore.put(locator, expirationTime);
				return true;
			}
			// Store command for APIKEY instances.
			if (variant == EModelVariants.APIKEY) {
				final String locator = EModelVariants.APIKEY.name() + "/" + Long.valueOf(longIdentifier).toString();
				_instanceCacheStore.put(locator, instance);
				_timeCacheStore.put(locator, expirationTime);
				return true;
			}
			return false;
		}
	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ShipSerializer extends JsonSerializer<Ship> {
		// - F I E L D - S E C T I O N ............................................................................

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public void serialize( final Ship value, final JsonGenerator jgen, final SerializerProvider provider )
				throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeStringField("jsonClass", value.getJsonClass());
			jgen.writeNumberField("assetId", value.getAssetId());
			jgen.writeNumberField("typeId", value.getTypeId());
			jgen.writeNumberField("ownerId", value.getOwnerID());
			jgen.writeStringField("name", value.getItemName());
			jgen.writeStringField("category", value.getCategory());
			jgen.writeStringField("groupName", value.getGroupName());
			jgen.writeStringField("tech", value.getTech());
			jgen.writeStringField("userLabel", value.getUserLabel());
			jgen.writeNumberField("price", value.getItem().getPrice());
			jgen.writeNumberField("highesBuyerPrice", value.getItem().getHighestBuyerPrice().getPrice());
			jgen.writeNumberField("lowerSellerPrice", value.getItem().getLowestSellerPrice().getPrice());
			jgen.writeObjectField("item", value.getItem());
			jgen.writeEndObject();
		}
	}
	// ........................................................................................................

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class CredentialSerializer extends JsonSerializer<Credential> {
		// - F I E L D - S E C T I O N ............................................................................

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public void serialize( final Credential value, final JsonGenerator jgen, final SerializerProvider provider )
				throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeStringField("jsonClass", value.getJsonClass());
			jgen.writeNumberField("accountId", value.getAccountId());
			jgen.writeStringField("accountName", value.getAccountName());
			jgen.writeStringField("tokenType", value.getTokenType());
			jgen.writeBooleanField("isActive", value.isActive());
			jgen.writeBooleanField("isXML", value.isXMLCompatible());
			jgen.writeBooleanField("isESI", value.isESICompatible());
//			jgen.writeObjectField("pilot", GlobalDataManager.getPilotV1(value.getAccountId()));
			jgen.writeEndObject();
		}
	}
	// ........................................................................................................
	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ExceptionRecord {
		// - F I E L D - S E C T I O N ............................................................................
private long timeStamp =0;
private Exception exceptionRegistered=null;
		// - C O N S T R U C T O R - S E C T I O N ................................................................
public ExceptionRecord(final Exception newexception){
	this.exceptionRegistered=newexception;
	this.timeStamp= Instant.now().getMillis();
}

		// - M E T H O D - S E C T I O N ..........................................................................
		public void setTimeStamp( final long timeStamp ) {
			this.timeStamp = timeStamp;
		}
		public void setTimeStamp( final Instant timeStamp ) {
			this.timeStamp = timeStamp.getMillis();
		}
	}
	// ........................................................................................................
}
// - UNUSED CODE ............................................................................................
//[01]
