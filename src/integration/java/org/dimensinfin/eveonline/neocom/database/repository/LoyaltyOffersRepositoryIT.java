package org.dimensinfin.eveonline.neocom.database.repository;

import java.io.File;
import java.sql.SQLException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOfferEntity;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationNeoComDatabaseService;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_ITEM_SELL_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_MARKET_HUB_REGION;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_CORPORATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_ISK_COST;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_LP_COST;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_QUANTITY;

public class LoyaltyOffersRepositoryIT {
	private static final int TEST_LOYALTY_OFFER_ID = 4104;
	private static final int TEST_LOYALTY_TYPE_ID = 9957;
	private static final int TEST_LOYALTY_CORPORATION_ID = 1000179;
	private static final int TEST_LOYALTY_MARKET_HUB_REGION_ID = 10000030;
	private ESIDataService esiDataService;
	private ResourceFactory resourceFactory;
	private IntegrationNeoComDatabaseService neocomDatabaseService;

	public void beforeEach() throws SQLException {
		new File( "neocom.db" ).delete();
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.esiDataService = injector.getInstance( ESIDataService.class );
		this.resourceFactory = injector.getInstance( ResourceFactory.class );
		this.neocomDatabaseService = injector.getInstance( IntegrationNeoComDatabaseService.class );
		this.neocomDatabaseService.onCreate( this.neocomDatabaseService.getConnectionSource() );
	}

	@Test
	public void persist() throws SQLException {
		// Prepare
		this.beforeEach();
		// Given
		final String TEST_LOYALTY_CORPORATION_NAME = this.esiDataService.getCorporationsCorporationId( TEST_LOYALTY_CORPORATION_ID ).getName();
		final LoyaltyOfferEntity loyaltyEntity = new LoyaltyOfferEntity.Builder()
				.withId( TEST_LOYALTY_OFFER_ID )
				.withLoyaltyCorporation( TEST_LOYALTY_OFFER_CORPORATION_ID, TEST_LOYALTY_OFFER_CORPORATION_NAME )
				.withType( this.resourceFactory.generateType4Id( TEST_LOYALTY_TYPE_ID ) )
				.withLpCost( TEST_LOYALTY_OFFER_LP_COST )
				.withIskCost( TEST_LOYALTY_OFFER_ISK_COST )
				.withQuantity( TEST_LOYALTY_OFFER_QUANTITY )
				.withMarketRegionId( TEST_LOYALTY_MARKET_HUB_REGION )
				.withPrice( TEST_LOYALTY_ITEM_SELL_PRICE )
				.build();
		// Test
		final LoyaltyOffersRepository loyaltyOffersRepository = new LoyaltyOffersRepository( this.neocomDatabaseService );
		loyaltyOffersRepository.persist( loyaltyEntity );
		// Assertions
		Assertions.assertTrue( this.neocomDatabaseService.getLoyaltyOfferDao().countOf() > 0 );
		// Remove files
		this.tearDown();
	}

	public void tearDown() {
		new File( "neocom.db" ).delete();
	}
}