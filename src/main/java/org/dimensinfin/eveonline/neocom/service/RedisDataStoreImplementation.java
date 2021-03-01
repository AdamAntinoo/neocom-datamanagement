package org.dimensinfin.eveonline.neocom.service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class RedisDataStoreImplementation implements IDataStore {
	private static final String REDIS_SEPARATOR = ":";
	private static final String LOWEST_SELL_ORDER_MAP = "LSO";
	private static final Integer LOWEST_SELL_ORDER_TTL = 300;
	private final RedissonClient redisClient;

	// - C O N S T R U C T O R S
	@Inject
	public RedisDataStoreImplementation() {
		final Config config = new Config();
		config.useSingleServer().setAddress( "redis://127.0.0.1:6379" );
		this.redisClient = Redisson.create( config );
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
				throw new NeoComRuntimeException( "There is no Lowest Sell Order available" );
			}
		}
		return entry;
	}

	private String generateLowestSellOrderUniqueId( final Integer regionId, final Integer typeId ) {
		return "LSO:" + regionId + REDIS_SEPARATOR + typeId;
	}
}