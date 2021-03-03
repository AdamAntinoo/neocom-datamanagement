package org.dimensinfin.eveonline.neocom.market;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketDataConstants.TEST_MARKET_DATA_BEST_BUY_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketDataConstants.TEST_MARKET_DATA_BEST_SELL_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketServiceConstants.TEST_MARKET_SERVICE_REGION_ID;

public class MarketDataTest {
	private static final Integer TEST_MARKET_DATA_TYPE_ID = 134;
	private MarketOrder bestBuy;
	private Station regionHub;
	private MarketOrder bestSell;

	@BeforeEach
	public void beforeEach() {
		this.regionHub = Mockito.mock( Station.class );
		this.bestBuy = Mockito.mock( MarketOrder.class );
		this.bestSell = Mockito.mock( MarketOrder.class );
	}

	@Test
	public void buildContract() {
		final MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		Assertions.assertNotNull( marketData );
	}

	@Test
	public void buildFailureMissingWith() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new MarketData.Builder()
					.withTypeId( TEST_MARKET_DATA_TYPE_ID )
					.withSellOrders( new ArrayList<>() )
					.withBestBuyOrder( this.bestBuy )
					.withBestSellOrder( this.bestSell )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new MarketData.Builder()
					.withTypeId( TEST_MARKET_DATA_TYPE_ID )
					.withRegionHub( null )
					.withSellOrders( new ArrayList<>() )
					.withBestBuyOrder( this.bestBuy )
					.withBestSellOrder( this.bestSell )
					.build();
		} );
	}

	@Test
	public void getBestBuyPrice() {
		// When
		Mockito.when( this.bestBuy.getPrice() ).thenReturn( TEST_MARKET_DATA_BEST_BUY_PRICE );
		// Test
		MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_DATA_BEST_BUY_PRICE, marketData.getBestBuyPrice(), 0.01 );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( null )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( 0.0, marketData.getBestBuyPrice(), 0.01 );
	}

	@Test
	public void getBestSellPrice() {
		// When
		Mockito.when( this.bestSell.getPrice() ).thenReturn( TEST_MARKET_DATA_BEST_SELL_PRICE );
		// Test
		MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_DATA_BEST_SELL_PRICE, marketData.getBestSellPrice(), 0.01 );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( null )
				.build();
		// Assertions
		Assertions.assertEquals( 0.0, marketData.getBestSellPrice(), 0.01 );
	}

	@Test
	public void getMarketWidth() {
		// When
		Mockito.when( this.bestBuy.getPrice() ).thenReturn( 100.0 );
		Mockito.when( this.bestSell.getPrice() ).thenReturn( 200.0 );
		// Test
		MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( 200.0 - 100.0, marketData.getMarketWidth(), 0.1 );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( null )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( -1.0, marketData.getMarketWidth(), 0.1 );
		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( null )
				.build();
		// Assertions
		Assertions.assertEquals( -1.0, marketData.getMarketWidth(), 0.1 );
	}

	@Test
	public void getSellAverage() {
		// Given
		final GetMarketsRegionIdOrders200Ok sellOrder1 = new GetMarketsRegionIdOrders200Ok();
		final GetMarketsRegionIdOrders200Ok sellOrder2 = new GetMarketsRegionIdOrders200Ok();
		final GetMarketsRegionIdOrders200Ok sellOrder3 = new GetMarketsRegionIdOrders200Ok();
		final List<GetMarketsRegionIdOrders200Ok> sellOrders = new ArrayList<>();
		sellOrder1.setPrice( 100.0 );
		sellOrder1.setVolumeRemain( 10 );
		sellOrders.add( sellOrder1 );
		sellOrder2.setPrice( 200.0 );
		sellOrder2.setVolumeRemain( 15 );
		sellOrders.add( sellOrder2 );
		sellOrder3.setPrice( 130.0 );
		sellOrder3.setVolumeRemain( 20 );
		sellOrders.add( sellOrder3 );
		// Test
		final MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( sellOrders )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( 146.667, marketData.getSellAverage(), 0.01 );
	}

	@Test
	public void getSellDeep() {
		// Given
		final GetMarketsRegionIdOrders200Ok sellOrder1 = new GetMarketsRegionIdOrders200Ok();
		final GetMarketsRegionIdOrders200Ok sellOrder2 = new GetMarketsRegionIdOrders200Ok();
		final GetMarketsRegionIdOrders200Ok sellOrder3 = new GetMarketsRegionIdOrders200Ok();
		final List<GetMarketsRegionIdOrders200Ok> sellOrders = new ArrayList<>();
		sellOrder1.setPrice( 100.0 );
		sellOrder1.setVolumeRemain( 10 );
		sellOrders.add( sellOrder1 );
		sellOrder2.setPrice( 200.0 );
		sellOrder2.setVolumeRemain( 15 );
		sellOrders.add( sellOrder2 );
		sellOrder3.setPrice( 103.0 );
		sellOrder3.setVolumeRemain( 20 );
		sellOrders.add( sellOrder3 );
		// Test
		final MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( sellOrders )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( 30, marketData.getSellDeep() );
	}

	//	@Test
	//	public void getSellRegionId() {
	//		// Given
	//		final Station regionHub = Mockito.mock( Station.class );
	//		final MarketOrder bestBuy = Mockito.mock( MarketOrder.class );
	//		final MarketOrder bestSell = Mockito.mock( MarketOrder.class );
	//		// When
	//		Mockito.when( regionHub.getRegionId() ).thenReturn( TEST_MARKET_SERVICE_REGION_ID );
	//		// Test
	//		final MarketData marketDataDefault = new MarketData.Builder()
	//				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
	//				.withBestBuyOrder( bestBuy )
	//				.withBestSellOrder( bestSell )
	//				.withSellOrders( new ArrayList<>() )
	//				.build();
	//		// Assertions
	//		Assertions.assertEquals( MarketService.PREDEFINED_MARKET_REGION_ID, marketDataDefault.getSellRegionId() );
	//		// Test
	//		final MarketData marketData = new MarketData.Builder()
	//				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
	//				.withRegionHub( regionHub )
	//				.withBestBuyOrder( bestBuy )
	//				.withBestSellOrder( bestSell )
	//				.withSellOrders( new ArrayList<>() )
	//				.build();
	//		// Assertions
	//		Assertions.assertEquals( TEST_MARKET_SERVICE_REGION_ID, marketData.getSellRegionId() );
	//	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.regionHub.getRegionId() ).thenReturn( TEST_MARKET_SERVICE_REGION_ID );
		// Test
		MarketData marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( new ArrayList<>() )
				.withBestBuyOrder( this.bestBuy )
				.withBestSellOrder( this.bestSell )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_DATA_TYPE_ID, marketData.getTypeId() );
		Assertions.assertNotNull( marketData.getBestBuyOrder() );
		Assertions.assertNotNull( marketData.getBestSellOrder() );
		Assertions.assertNotNull( marketData.getSellOrders() );
		Assertions.assertEquals( TEST_MARKET_SERVICE_REGION_ID, marketData.getSellRegionId() );

		// Test
		marketData = new MarketData.Builder()
				.withTypeId( TEST_MARKET_DATA_TYPE_ID )
				.withRegionHub( this.regionHub )
				.withSellOrders( null )
				.withBestBuyOrder( null )
				.withBestSellOrder( null )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_MARKET_DATA_TYPE_ID, marketData.getTypeId() );
		Assertions.assertNull( marketData.getBestBuyOrder() );
		Assertions.assertNull( marketData.getBestSellOrder() );
		Assertions.assertTrue( marketData.getSellOrders().isEmpty() );
	}
}