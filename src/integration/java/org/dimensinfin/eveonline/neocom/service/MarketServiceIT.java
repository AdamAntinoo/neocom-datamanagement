package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;
import org.dimensinfin.eveonline.neocom.support.IntegrationRedisDataStoreImplementation;
import org.dimensinfin.eveonline.neocom.support.MarketServiceReconfigurer;
import org.dimensinfin.logging.LogWrapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketServiceIT {
	private IDataStorePort dataStore;
	private LocationCatalogService locationCatalogService;
	private ESIDataService esiDataService;

	public void beforeEach() {
		LogWrapper.enter();
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.dataStore = injector.getInstance( IntegrationRedisDataStoreImplementation.class );
		this.locationCatalogService = injector.getInstance( LocationCatalogService.class );
		this.esiDataService = injector.getInstance( ESIDataService.class );
		final MarketServiceReconfigurer marketServiceReconfigurer = injector.getInstance( MarketServiceReconfigurer.class );
		LogWrapper.exit();
	}

	//	@Test
//	@Disabled
	public void getLowestSellOrder() {
		// Prepare
		this.beforeEach();
		// Given
		final Integer regionId = 10000002;
		final Integer typeId = 597;
		// Test
		final MarketService marketService = new MarketService(
				this.dataStore,
				this.locationCatalogService,
				this.esiDataService
		);
		final MarketOrder obtained = marketService.getLowestSellOrder( regionId, typeId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1023000.0, obtained.getPrice(), 0.1 );
		Assertions.assertEquals( 60003760, obtained.getStation().getStationId() );
		Assertions.assertEquals( 8, obtained.getVolumeRemain() );
	}

	//	@Test
//	@Disabled
	public void getLowestSellPrice() {
		// Prepare
		this.beforeEach();
		// Given
		final Integer regionId = 10000002;
		final Integer typeId = 597;
		// Test
		final MarketService marketService = new MarketService(
				this.dataStore,
				this.locationCatalogService,
				this.esiDataService
		);
		final double obtained = marketService.getLowestSellPrice( regionId, typeId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1023000.0, obtained, 0.1 );
	}

	//	@Test
//	@Disabled
	public void getMarketConsolidatedByRegion4ItemId() {
		// Prepare
		this.beforeEach();
		// Given
		final Integer regionId = 10000002;
		final Integer typeId = 597;
		// Test
		final MarketService marketService = new MarketService(
				this.dataStore,
				this.locationCatalogService,
				this.esiDataService
		);
		final MarketData obtained = marketService.getMarketConsolidatedByRegion4ItemId( regionId, typeId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1023000.0, obtained.getBestSellPrice(), 0.1 );
		Assertions.assertEquals( 7.0, obtained.getBestBuyPrice(), 0.1 );
	}

	@Test
	public void getRegionMarketHub() {
		// Prepare
		this.beforeEach();
		// Test
		final MarketService marketService = new MarketService(
				this.dataStore,
				this.locationCatalogService,
				this.esiDataService
		);
		long expected = 60008494L;
		Station obtained = marketService.getRegionMarketHub( 10000020 );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( expected, obtained.getStationId() );

		// Test
		expected = 60004588L;
		obtained = marketService.getRegionMarketHub( 10000030 );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( expected, obtained.getStationId() );
	}
}
