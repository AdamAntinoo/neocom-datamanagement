package org.dimensinfin.eveonline.neocom.market.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.converter.GetMarketsRegionIdOrdersToMarketOrderConverter;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_REMAIN;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_TOTAL;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketServiceConstants.TEST_MARKET_SERVICE_REGION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketServiceConstants.TEST_MARKET_SERVICE_TYPE_ID;

public class MarketServiceTest {
	private final List<GetMarketsRegionIdOrders200Ok> orderList = new ArrayList<>();
	private LocationCatalogService locationCatalogService;
	private ESIDataService esiDataService;
	private IDataStorePort dataStore;

	@BeforeEach
	public void beforeEach() {
		this.dataStore = Mockito.mock( IDataStorePort.class );
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.esiDataService = Mockito.mock( ESIDataService.class );
		// Prepare the list of orders for the tests
		final GetMarketsRegionIdOrders200Ok order1 = new GetMarketsRegionIdOrders200Ok();
		order1.setOrderId( TEST_MARKET_ORDER_ID );
		order1.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		order1.setIsBuyOrder( true );
		order1.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		order1.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID );
		order1.setPrice( 100.0 );
		order1.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		order1.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		this.orderList.add( order1 );
		final GetMarketsRegionIdOrders200Ok order2 = new GetMarketsRegionIdOrders200Ok();
		order2.setOrderId( TEST_MARKET_ORDER_ID + 1 );
		order2.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		order2.setIsBuyOrder( true );
		order2.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		order2.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID );
		order2.setPrice( 200.0 );
		order2.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		order2.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		this.orderList.add( order2 );
		final GetMarketsRegionIdOrders200Ok order3 = new GetMarketsRegionIdOrders200Ok();
		order3.setOrderId( TEST_MARKET_ORDER_ID + 2 );
		order3.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		order3.setIsBuyOrder( false );
		order3.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		order3.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID );
		order3.setPrice( 200.0 );
		order3.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		order3.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		this.orderList.add( order3 );
		final GetMarketsRegionIdOrders200Ok order4 = new GetMarketsRegionIdOrders200Ok();
		order4.setOrderId( TEST_MARKET_ORDER_ID + 3 );
		order4.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		order4.setIsBuyOrder( false );
		order4.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		order4.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID + 1 );
		order4.setPrice( 200.0 );
		order4.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		order4.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		this.orderList.add( order4 );
	}

	@Test
	public void constructorContract() {
		final MarketService marketService = new MarketService( this.dataStore, this.locationCatalogService, this.esiDataService );
		Assertions.assertNotNull( marketService );
	}

	@Test
	public void getLowestSellOrder() {
		// Given
		final Integer regionId = TEST_MARKET_SERVICE_REGION_ID;
		final Integer typeId = TEST_MARKET_SERVICE_TYPE_ID;
		final SpaceLocationImplementation marketLocation = Mockito.mock( SpaceLocationImplementation.class );
		// When
		Mockito.when( this.esiDataService.getUniverseMarketOrdersForId( Mockito.anyInt(), Mockito.anyInt() ) )
				.thenReturn( this.orderList );
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( marketLocation );
		// Test
		final MarketService marketService = new MarketService( this.dataStore, this.locationCatalogService, this.esiDataService );
		final MarketOrder obtained = marketService.getLowestSellOrder( regionId, typeId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_MARKET_ORDER_ID + 2, obtained.getOrderId() );
		Assertions.assertEquals( 200.0, obtained.getPrice(), 0.1 );
	}

	@Test
	public void getLowestSellPrice() {
		// Given
		final SpaceLocationImplementation marketLocation = Mockito.mock( SpaceLocationImplementation.class );
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( marketLocation );
		final MarketOrder order = new GetMarketsRegionIdOrdersToMarketOrderConverter( this.locationCatalogService )
				.convert( this.orderList.get( 2 ) );
		// When
		Mockito.when( this.dataStore.accessLowestSellOrder( Mockito.anyInt(), Mockito.anyInt(),
				Mockito.any( MarketService.LowestSellOrderPassThrough.class ) ) ).thenReturn( order );
		// Test
		final MarketService marketService = new MarketService( this.dataStore, this.locationCatalogService, this.esiDataService );
		final double obtained = marketService.getLowestSellPrice( TEST_MARKET_SERVICE_REGION_ID, TEST_MARKET_SERVICE_TYPE_ID );
		// Assertions
		final double expected = 200.0;
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( expected, obtained );
	}

}