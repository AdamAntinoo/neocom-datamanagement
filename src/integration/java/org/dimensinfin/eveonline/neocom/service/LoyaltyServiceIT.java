package org.dimensinfin.eveonline.neocom.service;

import java.io.File;
import java.sql.SQLException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.loyalty.service.LoyaltyService;
import org.dimensinfin.eveonline.neocom.support.IntegrationNeoComDatabaseService;

public class LoyaltyServiceIT {
	private static final int TEST_LOYALTY_CORPORATION_ID = 1000179;
	private static final int TEST_LOYALTY_MARKET_HUB_REGION_ID = 10000043;
	private static final int TEST_LOYALTY_DAYS_IN_RANGE = 15;
	private static final int TEST_LOYALTY_RANGE_COVERAGE = 70;
	private static final int TEST_LOYALTY_MINIMUM_TRADE_VOLUME = 3;

	private LoyaltyOffersRepository loyaltyOffersRepository;
	private ESIDataService esiDataService;
	private ResourceFactory resourceFactory;

	public void beforeEach() throws SQLException {
		new File( "neocom.db" ).delete();
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.loyaltyOffersRepository = injector.getInstance( LoyaltyOffersRepository.class );
		this.esiDataService = injector.getInstance( ESIDataService.class );
		this.resourceFactory = injector.getInstance( ResourceFactory.class );
		final IntegrationNeoComDatabaseService neocomDatabaseService = injector.getInstance( IntegrationNeoComDatabaseService.class );
		neocomDatabaseService.onCreate( neocomDatabaseService.getConnectionSource() );
	}

	@Test
	public void processOffers() throws SQLException {
		// Prepare
		this.beforeEach();
		// Check initial conditions
		Assertions.assertTrue( this.loyaltyOffersRepository.searchOffers4Corporation( TEST_LOYALTY_CORPORATION_ID ).size() == 0 );
		// Process offers.
		final LoyaltyService loyaltyService = new LoyaltyService( this.esiDataService, this.loyaltyOffersRepository, this.resourceFactory )
				.setRegionId(  TEST_LOYALTY_MARKET_HUB_REGION_ID)
				.setDaysInRange(TEST_LOYALTY_DAYS_IN_RANGE  )
				.setDateCoveragePct( TEST_LOYALTY_RANGE_COVERAGE )
				.setMinTradeVolume( TEST_LOYALTY_MINIMUM_TRADE_VOLUME );
		// Test
		loyaltyService.processOffers( TEST_LOYALTY_CORPORATION_ID );
		// Assertions
		Assertions.assertTrue( this.loyaltyOffersRepository.searchOffers4Corporation( TEST_LOYALTY_CORPORATION_ID ).size() > 0 );
	}
}