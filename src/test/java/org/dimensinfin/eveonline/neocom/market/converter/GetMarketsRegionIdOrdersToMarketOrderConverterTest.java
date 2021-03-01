package org.dimensinfin.eveonline.neocom.market.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocationImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_ID;
import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_INSTALLER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_REMAIN;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.MarketOrderConstants.TEST_MARKET_ORDER_VOLUME_TOTAL;

public class GetMarketsRegionIdOrdersToMarketOrderConverterTest {

	private LocationCatalogService locationCatalogService;

	@BeforeEach
	public void beforeEach() {
		this.locationCatalogService = Mockito.mock(LocationCatalogService.class);
	}

	@Test
	public void constructorContract() {
		final GetMarketsRegionIdOrdersToMarketOrderConverter converter=new GetMarketsRegionIdOrdersToMarketOrderConverter(this.locationCatalogService);
		Assertions.assertNotNull(converter);
	}

	@Test
	public void convert() {
		// Given
		final GetMarketsRegionIdOrders200Ok esiOrder = new GetMarketsRegionIdOrders200Ok();
		esiOrder.setOrderId( TEST_MARKET_ORDER_ID );
		esiOrder.setTypeId( TEST_MARKET_ORDER_TYPE_ID );
		esiOrder.setIsBuyOrder( true );
		esiOrder.setLocationId( (long) TEST_MARKET_ORDER_SYSTEM_ID );
		esiOrder.setSystemId( TEST_MARKET_ORDER_SYSTEM_ID );
		esiOrder.setPrice( TEST_MARKET_ORDER_PRICE );
		esiOrder.setVolumeRemain( TEST_MARKET_ORDER_VOLUME_REMAIN );
		esiOrder.setVolumeTotal( TEST_MARKET_ORDER_VOLUME_TOTAL );
		final SpaceLocationImplementation marketHub=Mockito.mock(SpaceLocationImplementation.class);
		// When
		Mockito.when( this.locationCatalogService.searchLocation4Id(Mockito.anyLong()) ).thenReturn( marketHub );
		Mockito.when( marketHub.getStationId() ).thenReturn( (long)TEST_MARKET_ORDER_SYSTEM_ID );
		// Test
		final GetMarketsRegionIdOrdersToMarketOrderConverter converter=new GetMarketsRegionIdOrdersToMarketOrderConverter(this.locationCatalogService);
		final MarketOrder obtained = converter.convert( esiOrder );
		// Assertions
		Assertions.assertEquals( TEST_MARKET_ORDER_ID, obtained.getOrderId() );
		Assertions.assertEquals( (long)TEST_MARKET_ORDER_SYSTEM_ID, obtained.getStation().getStationId() );
		Assertions.assertEquals( TEST_MARKET_ORDER_PRICE, obtained.getPrice(), 0.01 );
	}
}