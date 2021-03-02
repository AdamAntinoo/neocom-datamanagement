package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

public class FittingTest {
	private NeoItem item4Test;
	private ESIDataService esiDataService;

	@Test
	public void buildComplete() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assertions.assertNotNull( fitting );
	}

	@Test
	public void buildMissingWithA() {
		final NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new Fitting.Builder()
						.withFittingData( null )
						.build(),
				"Expected Fitting.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void buildMissingWithB() {
		final NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new Fitting.Builder()
						.build(),
				"Expected Fitting.Builder() to throw null verification, but it didn't." );

	}

	//	@Test
	public void downloadFittingItems() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		final List<CharacterscharacterIdfittingsItems> itemList = new ArrayList<>();
		final CharacterscharacterIdfittingsItems item = new CharacterscharacterIdfittingsItems();
		item.setTypeId( 12056 );
		item.setQuantity( 1 );
		item.setFlag( CharacterscharacterIdfittingsItems.FlagEnum.MEDSLOT0 );
		itemList.add( item );
		Mockito.when( fittingData.getItems() ).thenReturn( itemList );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assertions.assertNotNull( fitting );
	}

	@Test
	public void downloadHullData() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		Mockito.when( fittingData.getShipTypeId() ).thenReturn( 32880 );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assertions.assertNotNull( fitting );
	}

	//	@Test
	public void gettersContract() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		Mockito.when( fittingData.getFittingId() ).thenReturn( 1234 );
		Mockito.when( fittingData.getName() ).thenReturn( "Test Fitting name" );
		Mockito.when( fittingData.getDescription() ).thenReturn( "Test Fitting description" );
		Mockito.when( fittingData.getShipTypeId() ).thenReturn( 32880 );
		Mockito.when( fittingData.getItems() ).thenReturn( new ArrayList<>() );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assertions.assertNotNull( fitting );

		Assertions.assertEquals( 1234, fitting.getFittingId() );
		Assertions.assertEquals( "Test Fitting name", fitting.getName() );
		Assertions.assertEquals( "Test Fitting description", fitting.getDescription() );
		Assertions.assertEquals( 32880, fitting.getShipTypeId() );
		Assertions.assertEquals( new ArrayList<>().size(), fitting.getItems().size() );
		Assertions.assertEquals( "Frigate", fitting.getGroupName() );
		Assertions.assertEquals( "frigate", fitting.getHullGroup() );
	}

	@BeforeEach
	public void setUp() {
		final GetUniverseTypesTypeIdOk esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( group.getGroupId() ).thenReturn( 25 );
		Mockito.when( group.getName() ).thenReturn( "Frigate" );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 6 );
		Mockito.when( category.getName() ).thenReturn( "Ship" );
		this.esiDataService = Mockito.mock( ESIDataService.class );
		Mockito.when( this.esiDataService.searchEsiItem4Id( Mockito.anyInt() ) )
				.thenReturn( esiItem );
		Mockito.when( this.esiDataService.searchItemGroup4Id( Mockito.anyInt() ) )
				.thenReturn( group );
		Mockito.when( this.esiDataService.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		NeoItem.injectEsiUniverseDataAdapter( this.esiDataService );
	}
}
