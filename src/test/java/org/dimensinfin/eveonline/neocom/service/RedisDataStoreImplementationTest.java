package org.dimensinfin.eveonline.neocom.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_REMAIN;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_TOTAL;

public class RedisDataStoreImplementationTest {
	private static final String LOWEST_SELL_ORDER_MAP = "LSO";
	private final Integer regionId = 10000002;
	private final Integer typeId = 34;
	private GetUniverseRegionsRegionIdOk region;
	private Station station;
	private GetMarketsRegionIdOrders200Ok orderData;

	@Test
	public void accessLowestSellOrderCached() {
		this.accessLowestSellOrderNotCached();
		// Given
		this.orderData.setOrderId( -45L );
		final MarketOrder marketOrder = new MarketOrder.Builder()
				.withStation( this.station )
				.withOrderData( this.orderData )
				.build();
		// Test
		final RedisDataStoreImplementation redisDataStore = new RedisDataStoreImplementation();
		final MarketOrder obtained = redisDataStore.accessLowestSellOrder( regionId, typeId,
				( r, t ) -> marketOrder
		);
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof MarketOrder );
		Assertions.assertEquals( TEST_MARKET_ORDER_ID, obtained.getOrderId() );
	}

	@Test
	public void accessLowestSellOrderNotCached() {
		// Given
		final MarketOrder marketOrder = new MarketOrder.Builder()
				.withStation( this.station )
				.withOrderData( this.orderData )
				.build();
		// Test
		this.clearLSOCache();
		final RedisDataStoreImplementation redisDataStore = new RedisDataStoreImplementation();
		final MarketOrder obtained = redisDataStore.accessLowestSellOrder( this.regionId, this.typeId,
				( r, t ) -> marketOrder
		);
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained instanceof MarketOrder );
		Assertions.assertEquals( TEST_MARKET_ORDER_ID, obtained.getOrderId() );
	}

	@BeforeEach
	public void beforeEach() {
		this.region = new GetUniverseRegionsRegionIdOk();
		this.region.setRegionId( this.regionId );
		this.region.setName( "-TEST-REGION-NAME-" );
		this.station = new SpaceLocationImplementation.Builder()
				.withRegion( this.region )
				.build();
		this.orderData = new GetMarketsRegionIdOrders200Ok();
		this.orderData.setOrderId( TEST_MARKET_ORDER_ID );
		this.orderData.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		this.orderData.setPrice( TEST_MARKET_ORDER_PRICE );
		this.orderData.setIsBuyOrder( false );
		this.orderData.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		this.orderData.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
	}

	@Test
	public void constructorContract() {
		final RedisDataStoreImplementation redisDataStore = new RedisDataStoreImplementation();
		Assertions.assertNotNull( redisDataStore );
	}

	private void clearLSOCache() {
		// Clear the Redis LSO map.
		final Config config = new Config();
		config.useSingleServer().setAddress( "redis://127.0.0.1:6379" );
		RedissonClient redisClient = Redisson.create( config );
		final RMapCache<String, MarketOrder> cache = redisClient.getMapCache( LOWEST_SELL_ORDER_MAP );
		cache.clear();
	}
}