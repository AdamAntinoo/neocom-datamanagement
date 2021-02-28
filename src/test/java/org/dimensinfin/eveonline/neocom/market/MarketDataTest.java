package org.dimensinfin.eveonline.neocom.market;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

public class MarketDataTest {
	private static final Integer TEST_MARKET_DATA_TYPE_ID = 134;

	@Test
	public void buildContract() {
		final MarketOrder bestBuy = Mockito.mock( MarketOrder.class );
		final MarketOrder bestSell = Mockito.mock( MarketOrder.class );
		final MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( bestBuy )
				.withBestSellOrder( bestSell )
				.withSellOrders( new ArrayList<>() )
				.build();
		Assertions.assertNotNull( marketData );
	}

	@Test
	public void buildFailure() {
		final MarketOrder bestBuy = Mockito.mock( MarketOrder.class );
		final MarketOrder bestSell = Mockito.mock( MarketOrder.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new MarketData.Builder()
					.withTypeId( TEST_MARKET_DATA_TYPE_ID )
					.withBestBuyOrder( bestBuy )
					.withBestSellOrder( bestSell )
					.withSellOrders( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new MarketData.Builder()
					.withBestBuyOrder( bestBuy )
					.withBestSellOrder( bestSell )
					.withSellOrders( new ArrayList<>() )
					.build();
		} );
	}

	@Test
	public void calculateMarketWidth() {
		// Given
		final MarketOrder bestBuy = Mockito.mock( MarketOrder.class );
		final MarketOrder bestSell = Mockito.mock( MarketOrder.class );
		// Test
		MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( null )
				.withBestSellOrder( bestSell )
				.withSellOrders( new ArrayList<>() )
				.build();
		// Assertions
		Assertions.assertEquals( -1, marketData.getMarketWidth() );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( bestBuy )
				.withBestSellOrder( null )
				.withSellOrders( new ArrayList<>() )
				.build();
		// Assertions
		Assertions.assertEquals( -1, marketData.getMarketWidth() );
		// When
		Mockito.when( bestBuy.getPrice() ).thenReturn( 100.0 );
		Mockito.when( bestSell.getPrice() ).thenReturn( 200.0 );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( bestBuy )
				.withBestSellOrder( bestSell )
				.withSellOrders( new ArrayList<>() )
				.build();
		// Assertions
		Assertions.assertEquals( 100.0, marketData.getMarketWidth(), 0.1 );
	}

	@Test
	public void getterContract() {
		// Given
		final MarketOrder bestBuy = Mockito.mock( MarketOrder.class );
		final MarketOrder bestSell = Mockito.mock( MarketOrder.class );
		// Test
		final MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( bestBuy )
				.withBestSellOrder( bestSell )
				.withSellOrders( new ArrayList<>() )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_DATA_TYPE_ID, marketData.getTypeId() );
		Assertions.assertNotNull( marketData.getBestBuyOrder() );
		Assertions.assertNotNull( marketData.getBestSellOrder() );
		Assertions.assertNotNull( marketData.getSellOrders() );
		// Given
		final GetMarketsRegionIdOrders200Ok sellOrder1 = Mockito.mock( GetMarketsRegionIdOrders200Ok.class );
		final GetMarketsRegionIdOrders200Ok sellOrder2 = Mockito.mock( GetMarketsRegionIdOrders200Ok.class );
		final List<GetMarketsRegionIdOrders200Ok> sellOrders = new ArrayList<>();
		sellOrders.add( sellOrder1 );
		sellOrders.add( sellOrder2 );
		// When
		Mockito.when( sellOrder1.getPrice() ).thenReturn( 100.0 );
		Mockito.when( sellOrder2.getPrice() ).thenReturn( 200.0 );
		Mockito.when( sellOrder1.getVolumeRemain() ).thenReturn( 10 );
		Mockito.when( sellOrder2.getVolumeRemain() ).thenReturn( 10 );
		// Test
		final MarketData marketDataSellAverage = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withBestBuyOrder( bestBuy )
				.withBestSellOrder( bestSell )
				.withSellOrders( sellOrders )
				.build();
		// Assertions
		Assertions.assertEquals( 150.0, marketDataSellAverage.getSellAverage(), 0.1 );
	}
}