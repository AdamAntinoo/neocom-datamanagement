package org.dimensinfin.eveonline.neocom.service;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOfferEntity;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.loyalty.service.LoyaltyService;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.support.IntegrationNeoComDatabaseService;
import org.dimensinfin.logging.LogWrapper;

public class LoyaltyServiceIT {
	private static final int TEST_LOYALTY_CORPORATION_ID = 1000179;
	private static final int TEST_LOYALTY_MARKET_HUB_REGION_ID = 10000043;
	private static final int TEST_LOYALTY_DAYS_IN_RANGE = 15;
	private static final int TEST_LOYALTY_RANGE_COVERAGE = 70;
	private static final int TEST_LOYALTY_MINIMUM_TRADE_VOLUME = 3;

	private LoyaltyOffersRepository loyaltyOffersRepository;
	private ESIDataService esiDataService;
	private ResourceFactory resourceFactory;
	private MarketService marketService;

	public void beforeEach() throws SQLException {
		new File( "neocom.db" ).delete();
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.loyaltyOffersRepository = injector.getInstance( LoyaltyOffersRepository.class );
		this.esiDataService = injector.getInstance( ESIDataService.class );
		this.resourceFactory = injector.getInstance( ResourceFactory.class );
		this.marketService = injector.getInstance( MarketService.class );
		final IntegrationNeoComDatabaseService neocomDatabaseService = injector.getInstance( IntegrationNeoComDatabaseService.class );
		neocomDatabaseService.onCreate( neocomDatabaseService.getConnectionSource() );
	}

	//	@Test
	public void checkLoyaltyOfferGeneration() throws SQLException {
		// Prepare
		this.beforeEach();
		// Process offers.
		final LoyaltyService loyaltyService = new LoyaltyService(
				this.esiDataService,
				this.loyaltyOffersRepository,
				this.resourceFactory,
				this.marketService )
				.setRegionId( 10000002 ) // Jita
				//								.setRegionId( 10000043 ) // Amarr
				.setDaysInRange( TEST_LOYALTY_DAYS_IN_RANGE )
				.setDateCoveragePct( TEST_LOYALTY_RANGE_COVERAGE )
				.setMinTradeVolume( TEST_LOYALTY_MINIMUM_TRADE_VOLUME );
		// Test
		//				final List<LoyaltyOfferEntity> offerList = loyaltyService.processOffers( 1000179 ); // 24th - Amarr
		final List<LoyaltyOfferEntity> offerList = loyaltyService.processOffers( 1000182 ); // Tribunal - Minmatar
		for (final LoyaltyOfferEntity offer : offerList.stream()
				.filter( offer -> offer.getLpValue() > loyaltyService.getProfitLevel() )
				.sorted( ( of1, of2 ) -> Long.compare( of2.getLpValue(), of1.getLpValue() ) )
				.collect( Collectors.toList() ))
			LogWrapper.info( offer.toString() );
	}

	@Test
	public void processOffers() throws SQLException {
		// Prepare
		this.beforeEach();
		// Check initial conditions
		Assertions.assertTrue( this.loyaltyOffersRepository.searchOffers4Corporation( TEST_LOYALTY_CORPORATION_ID ).size() == 0 );
		// Process offers.
		final LoyaltyService loyaltyService = new LoyaltyService( this.esiDataService, this.loyaltyOffersRepository, this.resourceFactory,
				this.marketService )
				.setRegionId( TEST_LOYALTY_MARKET_HUB_REGION_ID )
				.setDaysInRange( TEST_LOYALTY_DAYS_IN_RANGE )
				.setDateCoveragePct( TEST_LOYALTY_RANGE_COVERAGE )
				.setMinTradeVolume( TEST_LOYALTY_MINIMUM_TRADE_VOLUME );
		// Test
		loyaltyService.processOffers( TEST_LOYALTY_CORPORATION_ID );
		// Assertions
		Assertions.assertTrue( this.loyaltyOffersRepository.searchOffers4Corporation( TEST_LOYALTY_CORPORATION_ID ).size() > 0 );
	}
}