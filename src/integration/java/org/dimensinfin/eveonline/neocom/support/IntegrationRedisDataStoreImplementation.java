package org.dimensinfin.eveonline.neocom.support;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.industry.domain.ProcessedBlueprint;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;
import org.dimensinfin.logging.LogWrapper;

import static org.dimensinfin.eveonline.neocom.utility.GlobalWideConstants.REDIS_SEPARATOR;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class IntegrationRedisDataStoreImplementation implements IDataStorePort {
	private static final ObjectMapper neocomObjectMapper = new ObjectMapper();
	private static final JsonJacksonCodec codec = new JsonJacksonCodec( neocomObjectMapper );
	private static final String LOWEST_SELL_ORDER_MAP = "LSO";
	private static final String ESITYPE_CACHE_NAME = "ESIT";
	private static final Integer LOWEST_SELL_ORDER_TTL = 300;
	// - Processed Blueprints
	private static final String COST_INDEX_BLUEPRINTS_CACHE_NAME = "BCI";
	private static final Integer COST_INDEX_BLUEPRINTS_TTL = 12;
	// - Locations
	private static final String SPACE_LOCATIONS_CACHE_NAME = "SPL";
	private static final Integer SPACE_LOCATIONS_CACHE_TTL = 12;

	static {
		neocomObjectMapper.registerModule( new JodaModule() );
	}

	protected final RedissonClient redisClient;

	// - C O N S T R U C T O R S
	@Inject
	public IntegrationRedisDataStoreImplementation( @NotNull @Named(DMServicesDependenciesModule.REDIS_DATABASE_URL) final String redisAddress ) {
		final Config config = new Config();
		config.useSingleServer().setAddress( redisAddress );
		this.redisClient = Redisson.create( config );
	}

	@Override
	public Optional<EsiType> accessEsiType4Id( final int typeId ) {
		return Optional.empty();
	}

//	@Override
//	public EsiType storeEsiType4Id( final EsiType target ) {
//		return target;
//	}

	@Override
	@Nullable
	public GetUniverseTypesTypeIdOk accessEsiUniverseItem4Id( final int typeId, final ESIDataService.EsiItemPassThrough esiItemPassThrough ) {
		final RBucket<GetUniverseTypesTypeIdOk> esiIBucket = this.redisClient
				.getBucket( this.generateEsiItemUniqueId( typeId ), new JsonJacksonCodec() );
		if ( null == esiIBucket.get() ) {
			final GetUniverseTypesTypeIdOk type;
			try {
				type = NeoObjects.requireNonNull( esiItemPassThrough.downloadEsiType( typeId ), "ESI Type detected null after downloading." );
				esiIBucket.set( type );
				return type;
			} catch (final NullPointerException exception) {
				LogWrapper.error( new NeoComRuntimeException(
						MessageFormat.format( "The downloader did not find the requested ESI type [{0}].", typeId )
				) );
				return null;
			}
		} else return esiIBucket.get();
	}

	/**
	 * Reads the LSO from the map cache corresponding to the selected region. The uses the type to locate the map record. If the LSO is not found then
	 * it will use the callback to get a fresh value. LSO entries are time lived and after a time they are removed from the Map so new callbacks
	 * should be issued.
	 *
	 * @param regionId                    the region where to find the orders.
	 * @param typeId                      the type identifier for the orders to search.
	 * @param lowestSellOrderReloadMethod the call back method to retrieve the order in case there is not found at the cache or expired.
	 * @return the LSO with the lowest sell price or NULL is there are no orders (this is possible for an empty market type slot).
	 */
	@Override
	@Nullable
	public MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId,
	                                          final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod ) {
		final String uniqueLSOKey = this.generateLowestSellOrderUniqueId( regionId );
		final RMapCache<String, MarketOrder> LSOMap = this.redisClient.getMapCache( uniqueLSOKey, codec );
		final MarketOrder entry = LSOMap.get( uniqueLSOKey + REDIS_SEPARATOR + typeId );
		if ( null == entry ) { // The data is not on the cache. Fetch it from the service and update the cache.
			final MarketOrder order;
			try {
				order = Objects.requireNonNull( lowestSellOrderReloadMethod.getLowestSellOrder( regionId, typeId ) );
				LSOMap.put( uniqueLSOKey + REDIS_SEPARATOR + typeId, order, LOWEST_SELL_ORDER_TTL, TimeUnit.SECONDS );
				return order;
			} catch (final NullPointerException exception) {
				LogWrapper.error( new NeoComRuntimeException(
						MessageFormat.format( "There is no Lowest Sell Order available on type [{0,number,#}] at region [{1,number,#}]",
								typeId, regionId )
				) );
				return null;
			}
		}
		return entry;
	}

	/**
	 * Return the list of Processed Blueprints that have the BOM along with the price and cost. If the list is empty that means that the pilot has no
	 * blueprints or that the processing task has not been run and there are no updated blueprint data. The store uses a single key per pilot to store
	 * the complete list of processed blueprints in JSON coded format. The blueprint data generator is a background process that will scan the list of
	 * blueprints and the update the store.
	 */
	@Override
	public List<ProcessedBlueprint> accessProcessedBlueprints( final Integer pilotId ) {
		final String uniqueLSOKey = this.generateBlueprintCostIndexUniqueId( pilotId );
		final RMapCache<String, ProcessedBlueprint> BCIMap = this.redisClient.getMapCache( uniqueLSOKey, codec );
		try {
			return new ArrayList<>( BCIMap.values() );
		} catch (final RuntimeException rte) {
			LogWrapper.error( rte );
			return new ArrayList<>();
		}
	}

	// TODO - Check the case when the blueprint is not found. Start to use Optionals.
	@Override
	public Optional<ProcessedBlueprint> accessProcessedBlueprintsByUID( Integer pilotId, String blueprintUID ) {
		final String uniqueLSOKey = this.generateBlueprintCostIndexUniqueId( pilotId );
		final RMapCache<String, ProcessedBlueprint> BCIMap = this.redisClient.getMapCache( uniqueLSOKey, codec );
		ProcessedBlueprint blueprint = BCIMap.get( blueprintUID );
		return Optional.ofNullable( blueprint );
	}

	/**
	 * Processed Blueprints have to be shared between different architectures (Java, Typescript), then the serialization should be compatible to allow
	 * the data to be used on all the platforms. The selected format is the same to be used on the REST services.
	 *
	 * This data is stored on the Redis database under the Data Map named COST_INDEX_BLUEPRINTS_CACHE_NAME and the pilot identifier. That will get a
	 * single Redis record with all the set of blueprints for this pilot, where we can locate a single blueprint by it unique identifier.
	 *
	 * @param pilotId   the pilot identifier that requires this data
	 * @param blueprint the blueprint to be stored/updated on the Redis repository.
	 */
	@Override
	public void updateProcessedBlueprint( final Integer pilotId, final ProcessedBlueprint blueprint ) {
		final String uniqueLSOKey = this.generateBlueprintCostIndexUniqueId( pilotId );
		final RMapCache<String, ProcessedBlueprint> BCIMap = this.redisClient.getMapCache( uniqueLSOKey, codec );
		try {
			BCIMap.put( uniqueLSOKey + REDIS_SEPARATOR + blueprint.getTypeId(), blueprint );
		} catch (final RuntimeException rte) {
			LogWrapper.error( rte );
		}
	}

	@Override
	public Optional<SpaceLocation> accessLocation( final String locationCacheId ) {
		final RMapCache<String, SpaceLocation> BCIMap = this.redisClient.getMapCache( SPACE_LOCATIONS_CACHE_NAME, codec );
		final SpaceLocation location = BCIMap.get( locationCacheId );
		if ( Objects.isNull( location ) ) return Optional.empty();
		else return Optional.of( location );
	}

	@Override
	public SpaceLocation updateLocation( final String locationCacheId, final SpaceLocation location ) {
		final RMapCache<String, SpaceLocation> BCIMap = this.redisClient.getMapCache( SPACE_LOCATIONS_CACHE_NAME, codec );
		try {
			BCIMap.put( SPACE_LOCATIONS_CACHE_NAME + REDIS_SEPARATOR + locationCacheId, location );
		} catch (final RuntimeException rte) {
			LogWrapper.error( rte );
		} finally {
			return location;
		}
	}

	@Override
	public Optional<EsiType> accessType4Id( final int typeId, final Function<Integer, EsiType> generatorEsiType ) {
		return Optional.empty();
	}

	@Override
	public Optional<SpaceLocation> accessLocation4Id( final Long locationId, final Credential credential, final Function<Long, SpaceLocation> generatorLocation ) {
		return Optional.empty();
	}

	@Override
	public String generateBlueprintCostIndexUniqueId( final Integer pilotId ) {
		return COST_INDEX_BLUEPRINTS_CACHE_NAME + REDIS_SEPARATOR + pilotId;
	}

	private String generateEsiItemUniqueId( final Integer typeId ) {
		return ESITYPE_CACHE_NAME + REDIS_SEPARATOR + typeId;
	}

	private String generateLowestSellOrderUniqueId( final Integer regionId ) {
		return "LSO:" + regionId;
	}
}