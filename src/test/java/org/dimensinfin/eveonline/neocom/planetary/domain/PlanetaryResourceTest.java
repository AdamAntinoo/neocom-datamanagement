package org.dimensinfin.eveonline.neocom.planetary.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

public class PlanetaryResourceTest {
	@Test
	public void toStringContract() {
		// Given
		final ResourceFactory factory=Mockito.mock( ResourceFactory.class);
//		ResourceFactory.setSingleton( factory );
		final NeoItem neoItem= Mockito.mock(NeoItem.class);
		// When
		Mockito.when( factory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getGroupName() ).thenReturn( "Refined Commodities - Tier 2" );
		// Test
		final PlanetaryResource planetaryResource=new PlanetaryResource( 123 );
		final String expected = "{\"tier\":\"TIER2\"}";
		// Assertions
		Assertions.assertEquals( expected, planetaryResource.toString() );
	}

	@Test
	public void getTier() {
		// Given
		final ResourceFactory factory=Mockito.mock( ResourceFactory.class);
//		ResourceFactory.setSingleton( factory );
		final NeoItem neoItem= Mockito.mock(NeoItem.class);
		// When
		Mockito.when( factory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getGroupName() ).thenReturn( "Refined Commodities - Tier 2" );
		// Test
		final PlanetaryResource planetaryResource=new PlanetaryResource( 123 );
		final PlanetaryResourceTierType obtained = planetaryResource.getTier();
		// Assertions
		Assertions.assertEquals( PlanetaryResourceTierType.TIER2, obtained );
	}
}
