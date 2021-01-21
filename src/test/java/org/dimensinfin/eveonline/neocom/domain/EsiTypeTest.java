package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_CATEGORY_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_GROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_HULLGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_INDUSTRYGROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TECH;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_TYPEICON_URL;

public class EsiTypeTest {

	private GetUniverseTypesTypeIdOk type;
	private GetUniverseGroupsGroupIdOk group;
	private GetUniverseCategoriesCategoryIdOk category;

	@BeforeEach
	public void beforeEach() {
		this.type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		this.group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		this.category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
	}

	@Test
	public void buildContract() {
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		// Assertions
		Assertions.assertNotNull( esiType );
	}

	@Test
	public void buildContractFailureMissing() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( this.group )
					.build();
		} );
	}

	@Test
	public void buildContractFailureNull() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( null )
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( null )
					.withGroup( this.group )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( null )
					.withCategory( this.category )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new EsiType.Builder()
					.withTypeId( TEST_ESITYPE_ID )
					.withItemType( this.type )
					.withGroup( this.group )
					.withCategory( null )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.type.getName() ).thenReturn( TEST_ESITYPE_NAME );
		Mockito.when( this.group.getName() ).thenReturn( TEST_ESITYPE_GROUP_NAME );
		Mockito.when( this.category.getName() ).thenReturn( TEST_ESITYPE_CATEGORY_NAME );
		// Test
		final EsiType esiType = new EsiType.Builder()
				.withTypeId( TEST_ESITYPE_ID )
				.withItemType( this.type )
				.withGroup( this.group )
				.withCategory( this.category )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_ESITYPE_ID, esiType.getTypeId() );
		Assertions.assertEquals( TEST_ESITYPE_NAME, esiType.getName() );
		Assertions.assertEquals( TEST_ESITYPE_GROUP_NAME, esiType.getGroupName() );
		Assertions.assertEquals( TEST_ESITYPE_CATEGORY_NAME, esiType.getCategoryName() );
		Assertions.assertEquals( TEST_ESITYPE_TYPEICON_URL, esiType.getTypeIconURL() );
		Assertions.assertEquals( TEST_ESITYPE_INDUSTRYGROUP_NAME, esiType.getIndustryGroup() );
		Assertions.assertEquals( TEST_ESITYPE_HULLGROUP_NAME, esiType.getHullGroup() );
		Assertions.assertEquals( TEST_ESITYPE_TECH, esiType.getTech() );
		Assertions.assertNotNull( esiType.getType() );
		Assertions.assertNotNull( esiType.getGroup() );
		Assertions.assertNotNull( esiType.getCategory() );
	}
}
/*
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
 */