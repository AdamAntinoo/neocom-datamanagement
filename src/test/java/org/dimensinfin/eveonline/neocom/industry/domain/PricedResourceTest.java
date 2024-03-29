package org.dimensinfin.eveonline.neocom.industry.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.market.MarketData;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PricedResourceConstants.TEST_RESOURCE_PRICE;

public class PricedResourceTest {

	@Test
	public void buildContract() {
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final MarketData marketData = Mockito.mock( MarketData.class );
		final PricedResource resource = new PricedResource.Builder()
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.withPrice( TEST_RESOURCE_PRICE )
				.withMarketData( marketData )
				.build();
		Assertions.assertNotNull( resource );
	}

	@Test
	public void getterContract() {
		// Given
		final Double TEST_BEST_SELL_PRICE = 4368.62;
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final MarketData marketData = Mockito.mock( MarketData.class );
		// When
		Mockito.when( marketData.getBestSellPrice() ).thenReturn( TEST_BEST_SELL_PRICE );
		// Test
		final PricedResource resource = new PricedResource.Builder()
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.withPrice( TEST_RESOURCE_PRICE )
				.withMarketData( marketData )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_BEST_SELL_PRICE, resource.getMarketPrice(), 0.01 );
		Assertions.assertNotNull( resource.getMarketData() );
	}
}