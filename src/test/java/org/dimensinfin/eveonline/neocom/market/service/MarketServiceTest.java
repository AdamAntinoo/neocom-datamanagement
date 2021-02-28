package org.dimensinfin.eveonline.neocom.market.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.IDataStore;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

public class MarketServiceTest {
	private LocationCatalogService locationCatalogService;
	private ESIDataService esiDataService;
	private IDataStore dataStore;

	@BeforeEach
	public void beforeEach() {
		this.dataStore = Mockito.mock(IDataStore.class);
		this.locationCatalogService = Mockito.mock(LocationCatalogService.class);
		this.esiDataService = Mockito.mock( ESIDataService.class );
	}

	@Test
	public void constructorContract() {
		final MarketService marketService = new MarketService( this.dataStore, this.locationCatalogService, this.esiDataService );
		Assertions.assertNotNull( marketService );
	}

//	@Test
	public void getLowestSellPrice() {
		final Integer TARGET_SYSTEM_ID = 321543;
		// Given
		final List<GetMarketsRegionIdOrders200Ok> orders = new ArrayList<>();
		final GetMarketsRegionIdOrders200Ok order1 = new GetMarketsRegionIdOrders200Ok();
		order1.setIsBuyOrder( true );
		order1.setSystemId( TARGET_SYSTEM_ID );
		order1.setPrice( 100.0 );
		orders.add( order1 );
		final GetMarketsRegionIdOrders200Ok order2 = new GetMarketsRegionIdOrders200Ok();
		order2.setIsBuyOrder( true );
		order2.setSystemId( TARGET_SYSTEM_ID );
		order2.setPrice( 200.0 );
		orders.add( order2 );
		final GetMarketsRegionIdOrders200Ok order3 = new GetMarketsRegionIdOrders200Ok();
		order3.setIsBuyOrder( false );
		order3.setSystemId( TARGET_SYSTEM_ID );
		order3.setPrice( 200.0 );
		orders.add( order3 );
		final GetMarketsRegionIdOrders200Ok order4 = new GetMarketsRegionIdOrders200Ok();
		order4.setIsBuyOrder( true );
		order4.setSystemId( TARGET_SYSTEM_ID + 1 );
		order4.setPrice( 200.0 );
		orders.add( order4 );
		// Test
		final MarketService marketService = new MarketService( dataStore, this.locationCatalogService, this.esiDataService );
//		final double obtained = marketService.getLowestSellPrice( orders, TARGET_SYSTEM_ID );
		// Assertions
		final double expected = 200.0;
//		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void getMarketHubSellOrders4Id() {
		// Given
		final Integer TARGET_SYSTEM_ID = 321543;
		final Integer TARGET_REGION_ID = 1000002;
		final Long TEST_ORDER_ID = 123456L;
		final Integer ITEM_ID = 123;
		final Station regionHub = Mockito.mock( Station.class );
		final List<GetMarketsRegionIdOrders200Ok> orders = new ArrayList<>();
		final GetMarketsRegionIdOrders200Ok order1 = new GetMarketsRegionIdOrders200Ok();
		order1.setIsBuyOrder( true );
		order1.setSystemId( TARGET_SYSTEM_ID );
		order1.setPrice( 100.0 );
		orders.add( order1 );
		final GetMarketsRegionIdOrders200Ok order2 = new GetMarketsRegionIdOrders200Ok();
		order2.setIsBuyOrder( true );
		order2.setSystemId( TARGET_SYSTEM_ID );
		order2.setPrice( 200.0 );
		orders.add( order2 );
		final GetMarketsRegionIdOrders200Ok order3 = new GetMarketsRegionIdOrders200Ok();
		order3.setOrderId( TEST_ORDER_ID );
		order3.setIsBuyOrder( false );
		order3.setSystemId( TARGET_SYSTEM_ID );
		order3.setPrice( 200.0 );
		orders.add( order3 );
		final GetMarketsRegionIdOrders200Ok order4 = new GetMarketsRegionIdOrders200Ok();
		order4.setIsBuyOrder( true );
		order4.setSystemId( TARGET_SYSTEM_ID + 1 );
		order4.setPrice( 200.0 );
		orders.add( order4 );
		// When
//		Mockito.when( this.ma.getRegionMarketHub( TARGET_REGION_ID ) ).thenReturn( regionHub );
		Mockito.when( this.esiDataService.getUniverseMarketOrdersForId( Mockito.anyInt(), Mockito.anyInt() ) )
				.thenReturn( orders );
		Mockito.when( regionHub.getSolarSystemId() ).thenReturn( TARGET_SYSTEM_ID );
		// Test
		final MarketService marketService = new MarketService( dataStore, this.locationCatalogService, this.esiDataService );
		final List<GetMarketsRegionIdOrders200Ok> obtained = marketService.getMarketHubSellOrders4Id( TARGET_REGION_ID, ITEM_ID );
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1, obtained.size() );
		Assertions.assertEquals( 200.0, obtained.get( 0 ).getPrice(), 0.1 );
		Assertions.assertEquals( TEST_ORDER_ID, obtained.get( 0 ).getOrderId() );
	}
}