package org.dimensinfin.eveonline.neocom.service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.logging.LogWrapper;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class RedisDataStoreImplementation implements IDataStore {
	private static final String REDIS_SEPARATOR = ":";
	private static final String LOWEST_SELL_ORDER_MAP = "LSO";
	private static final String ESITYPE_CACHE_NAME = "ESIT";
	private static final Integer LOWEST_SELL_ORDER_TTL = 300;
	private final RedissonClient redisClient;

	// - C O N S T R U C T O R S
	@Inject
	public RedisDataStoreImplementation() {
		final Config config = new Config();
		config.useSingleServer().setAddress( "redis://localhost:6379" );
		this.redisClient = Redisson.create( config );
	}

	@Override
	public GetUniverseTypesTypeIdOk accessEsiItem4Id( final int typeId, final ESIDataService.EsiItemPassThrough esiItemPassThrough ) {
		final RBucket<GetUniverseTypesTypeIdOk> esiIBucket = this.redisClient
				.getBucket( this.generateEsiItemUniqueId( typeId ), new JsonJacksonCodec() );
		if (null == esiIBucket.get()) {
			final GetUniverseTypesTypeIdOk type;
			try {
				type = Objects.requireNonNull( esiItemPassThrough.downloadEsiType( typeId ) );
				esiIBucket.set( type );
				return type;
			} catch (final NullPointerException exception) {
				throw new NeoComRuntimeException(
						MessageFormat.format( "The downloader did not find the requested ESI type [{0}].", typeId )
				);
			}
		}
		return esiIBucket.get();
	}

	@Override
	public MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId, final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod ) {
		final String uniqueLSOKey = this.generateLowestSellOrderUniqueId( regionId, typeId );
		final RMapCache<String, MarketOrder> LSOMap = this.redisClient.getMapCache( LOWEST_SELL_ORDER_MAP );
		final MarketOrder entry = LSOMap.get( uniqueLSOKey );
		if (null == entry) { // The data is not on the cache. Fetch it from the service and update the cache.
			final MarketOrder order;
			try {
				order = Objects.requireNonNull( lowestSellOrderReloadMethod.getLowestSellOrder( regionId, typeId ) );
				LSOMap.put( uniqueLSOKey, order, LOWEST_SELL_ORDER_TTL, TimeUnit.SECONDS );
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

	private String generateEsiItemUniqueId( final Integer typeId ) {
		return ESITYPE_CACHE_NAME + REDIS_SEPARATOR + typeId;
	}

	private String generateLowestSellOrderUniqueId( final Integer regionId, final Integer typeId ) {
		return "LSO:" + regionId + REDIS_SEPARATOR + typeId;
	}
}