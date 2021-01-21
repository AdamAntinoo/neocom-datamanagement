package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

public class FittingItemTest {
//	private NeoItem item4Test;
	private ESIUniverseDataProvider esiUniverseDataProvider;


	@BeforeEach
	public	void setUp() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( esiItem.getName() ).thenReturn( "10MN Afterburner I" );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 46 );
		Mockito.when( group.getName() ).thenReturn( "Propulsion Module" );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 7 );
		Mockito.when( category.getName() ).thenReturn( "Module" );
		this.esiUniverseDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( this.esiUniverseDataProvider.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( this.esiUniverseDataProvider.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( this.esiUniverseDataProvider.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
//		NeoItem.injectEsiUniverseDataAdapter( this.esiUniverseDataProvider );
	}
	@Test
	public void buildComplete() {
		final CharacterscharacterIdfittingsItems fittingData = Mockito.mock( CharacterscharacterIdfittingsItems.class );
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingItem( fittingData )
				.build();
		Assertions.assertNotNull( fittingItem );
	}

	@Test
	public void buildMissingWithA() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new FittingItem.Builder()
						.withFittingItem( null )
						.build(),
				"Expected FittingItem.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void buildMissingWithB() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new FittingItem.Builder()
						.build(),
				"Expected FittingItem.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void gettersContract() {
		final CharacterscharacterIdfittingsItems fittingData = Mockito.mock( CharacterscharacterIdfittingsItems.class );
		Mockito.when( fittingData.getTypeId() ).thenReturn( 12056 );
		Mockito.when( fittingData.getFlag() ).thenReturn( CharacterscharacterIdfittingsItems.FlagEnum.CARGO );
		Mockito.when( fittingData.getQuantity() ).thenReturn( 2 );
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingItem( fittingData )
				.build();
		Assertions.assertNotNull( fittingItem );

		Assertions.assertEquals( 12056, fittingItem.getTypeId() );
		Assertions.assertEquals( CharacterscharacterIdfittingsItems.FlagEnum.CARGO, fittingItem.getFlag() );
		Assertions.assertEquals( 2, fittingItem.getQuantity() );
		Assertions.assertEquals( "10MN Afterburner I", fittingItem.getTypeName() );
	}
}
