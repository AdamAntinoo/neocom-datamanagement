package org.dimensinfin.eveonline.neocom.domain;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

import nl.jqno.equalsverifier.EqualsVerifier;

public class NeoItemTest {
	private NeoItem item4Test;
	private ESIUniverseDataProvider esiUniverseDataProvider;

	@BeforeEach
	void setUp() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 18 );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 4 );
		this.esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		this.item4Test = new NeoItem( 34 );
	}

	@Test
	public void constructorTypeId() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final ESIUniverseDataProvider esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );
		Assertions.assertNotNull( item );
	}

	//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( NeoItem.class )
				.usingGetClass().verify();
	}

	@Test
	public void getterContract() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 18 );
		Mockito.when( group.getName() ).thenReturn( "Tool");
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 4 );
		Mockito.when( category.getName() ).thenReturn( "Tool");
		final ESIUniverseDataProvider esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );

		Assertions.assertNotNull( item );
		Assertions.assertEquals( 34, item.getTypeId() );
		Assertions.assertEquals( 18, item.getGroupId() );
		Assertions.assertEquals( 4, item.getCategoryId() );
		Assertions.assertEquals( "not-applies", item.getHullGroup() );
		Assertions.assertEquals( NeoItem.IndustryGroup.ITEMS, item.getIndustryGroup() );
	}

	@Test
	public void setterContract() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 18 );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 4 );
		final ESIUniverseDataProvider esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );

		item.setTypeId( 35 );
		Assertions.assertEquals( 35, item.getTypeId() );
	}

	@Test
	public void getName() {
		final String expected = "Tritanium";
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( esiItem.getName() ).thenReturn( expected );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );

		final String obtained = item.getName();
		Assertions.assertNotNull( item );
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void getVolume() {
		final float expected = 34.5F;
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( esiItem.getVolume() ).thenReturn( expected );
		Mockito.when( esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );

		final double obtained = item.getVolume();
		Assertions.assertNotNull( item );
		Assertions.assertEquals( expected, obtained, 0.001, "The volume should match." );
	}

	//	@Test
	public void isBlueprint_false() {
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( esiDataProvider.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( eveItem );
		Mockito.when( esiDataProvider.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( group );
		Mockito.when( esiDataProvider.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( category );
		Mockito.when( category.getName() ).thenReturn( "Capsuleer Bases" );
//		NeoItem.injectEsiUniverseDataAdapter( esiDataProvider );
		final NeoItem item = new NeoItem( 34 );
		Assertions.assertNotNull( item );
		Assertions.assertFalse( item.isBlueprint() );
	}

	//	@Test
	public void isBlueprint_true() {
		final ESIDataProvider esiDataProvider = Mockito.mock( ESIDataProvider.class );
		final GetUniverseTypesTypeIdOk eveItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( esiDataProvider.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( eveItem );
		Mockito.when( esiDataProvider.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( group );
		Mockito.when( esiDataProvider.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( category );
		Mockito.when( category.getName() ).thenReturn( "Energy Neutralizer Blueprint" );
//		NeoItem.injectEsiUniverseDataAdapter( esiDataProvider );
		final NeoItem item = new NeoItem( 15799 );
		Assertions.assertNotNull( item );
		Assertions.assertFalse( item.isBlueprint() );
	}

	@Test
	public void getPrice() throws IOException {
		final double expected = 34.5F;
		Mockito.when( esiUniverseDataProvider.searchSDEMarketPrice( Mockito.anyInt() ) )
				.thenReturn( expected );
		NeoItem.injectEsiUniverseDataAdapter( esiUniverseDataProvider );
		final NeoItem item = new NeoItem( 34 );

		double obtained = item.getPrice();
		Assertions.assertEquals( expected, obtained, 0.1, "Price expected to be positive value." );
	}
}
