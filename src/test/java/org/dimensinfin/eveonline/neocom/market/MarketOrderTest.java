package org.dimensinfin.eveonline.neocom.market;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_REMAIN;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_TOTAL;

public class MarketOrderTest {
	@Test
	public void buildContract() {
		final GetMarketsRegionIdOrders200Ok orderData = Mockito.mock( GetMarketsRegionIdOrders200Ok.class );
		final Station station = Mockito.mock( Station.class );
		final MarketOrder marketOrder = new MarketOrder.Builder()
				.withOrderData( orderData )
				.withStation( station )
				.build();
		Assertions.assertNotNull( marketOrder );
	}

	@Test
	public void gettersContract() {
		// Given
		final GetMarketsRegionIdOrders200Ok orderData = new GetMarketsRegionIdOrders200Ok();
		orderData.setOrderId( TEST_MARKET_ORDER_ID );
		orderData.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		orderData.setIsBuyOrder( true );
		orderData.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		orderData.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID );
		orderData.setPrice( TEST_MARKET_ORDER_PRICE );
		orderData.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		orderData.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		final Station station = Mockito.mock( Station.class );
		// Test
		final MarketOrder marketOrder = new MarketOrder.Builder()
				.withOrderData( orderData )
				.withStation( station )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_ORDER_ID, marketOrder.getOrderId() );
		Assertions.assertEquals( true, marketOrder.isBuyOrder() );
		Assertions.assertEquals( TEST_MARKET_ORDER_PRICE, marketOrder.getPrice(), 0.01 );
		Assertions.assertNotNull( marketOrder.getStation() );
		Assertions.assertEquals( TEST_MARKET_ORDER_TYPE_ID, marketOrder.getTypeId() );
		Assertions.assertEquals( TEST_MARKET_ORDER_VOLUME_REMAIN, marketOrder.getVolumeRemain() );
		Assertions.assertEquals( TEST_MARKET_ORDER_VOLUME_TOTAL, marketOrder.getVolumeTotal() );
	}
}