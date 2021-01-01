package org.dimensinfin.eveonline.neocom.planetary.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

public class PlanetaryResourceTest {
	private static final Integer TEST_PLANETARY_RESOURCE_TYPE_ID = 2267;

	@Test
	public void constructorContract() {
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final PlanetaryResource planetaryResource = new PlanetaryResource.Builder()
				.withTypeId( TEST_PLANETARY_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build();
		Assertions.assertNotNull( planetaryResource );
	}

	@Test
	public void getTier() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		// When
		Mockito.when( group.getName() ).thenReturn( "Specialized Commodities - Tier 3" );
		// Test
		final PlanetaryResource planetaryResource = new PlanetaryResource.Builder()
				.withTypeId( TEST_PLANETARY_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build();
		final PlanetaryResourceTierType expected = PlanetaryResourceTierType.TIER3;
		final PlanetaryResourceTierType obtained = planetaryResource.getTier();
		// Assertions
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void toStringContract() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		// When
		Mockito.when( group.getName() ).thenReturn( "Specialized Commodities - Tier 3" );
		// Test
		final PlanetaryResource planetaryResource = new PlanetaryResource.Builder()
				.withTypeId( TEST_PLANETARY_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build();
		final String expected = "{\"resource\":\"{\\\"baseQty\\\":0,\\\"name\\\":null,\\\"typeId\\\":2267,\\\"quantity\\\":0,\\\"volume\\\":0.0,\\\"price\\\":0.0,\\\"jsonClass\\\":\\\"PlanetaryResource\\\"}\",\"tier\":\"TIER3\"}";
		// Assertions
		Assertions.assertEquals( expected, planetaryResource.toString() );
	}
}
