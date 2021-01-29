package org.dimensinfin.eveonline.neocom.loyalty.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.EsiType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_ITEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_ITEM_ID_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_ITEM_SELL_PRICE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_MARKET_HUB_REGION;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_CORPORATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_ISK_COST;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_LP_COST;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.LoyaltyOfferEntityConstants.TEST_LOYALTY_OFFER_QUANTITY;

public class LoyaltyOfferEntityTest {
	@Test
	public void buildContract() {
		final EsiType esiLoyaltyType = Mockito.mock( EsiType.class );
		Mockito.when( esiLoyaltyType.getTypeId() ).thenReturn( TEST_LOYALTY_ITEM_ID );
		Mockito.when( esiLoyaltyType.getName() ).thenReturn( TEST_LOYALTY_ITEM_ID_NAME );
		final LoyaltyOfferEntity entity = new LoyaltyOfferEntity.Builder()
				.withId( TEST_LOYALTY_OFFER_ID )
				.withLoyaltyCorporation( TEST_LOYALTY_OFFER_CORPORATION_ID, TEST_LOYALTY_OFFER_CORPORATION_NAME )
				.withType( esiLoyaltyType )
				.withLpCost( TEST_LOYALTY_OFFER_LP_COST )
				.withIskCost( TEST_LOYALTY_OFFER_ISK_COST )
				.withQuantity( TEST_LOYALTY_OFFER_QUANTITY )
				.withMarketRegionId( TEST_LOYALTY_MARKET_HUB_REGION )
				.withPrice( TEST_LOYALTY_ITEM_SELL_PRICE )
				.build();
		Assertions.assertNotNull( entity );
	}
}