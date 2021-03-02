package org.dimensinfin.eveonline.neocom.asset.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.support.NeoItemDependingTest;

public class EsiAssets200Ok2NeoAssetConverterTest extends NeoItemDependingTest {
	@Test
	public void checkIfContainerCheckA() {
		final EsiAssets200Ok esiConversion1 = new EsiAssets200Ok.Builder()
				.withItemId( 100L )
				.withTypeId( 100 )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withQuantity( 1 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.STATION )
				.withLocationId( 100L )
				.build();
		// It is a blueprint so not a container
		Assertions.assertFalse( new EsiAssets200Ok2NeoAssetConverter().convert( esiConversion1 ).isContainer() );
		// It is a ship so it is a container
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( category.getCategoryId() ).thenReturn( 4 );
		Mockito.when( category.getName() ).thenReturn( "Ship" );
		Mockito.when( this.esiDataService.searchItemCategory4Id( Mockito.anyInt() ) )
				.thenReturn( category );
		Assertions.assertTrue( new EsiAssets200Ok2NeoAssetConverter().convert( esiConversion1 ).isContainer() );
	}

	@Test
	public void checkIfContainerCheckB() {
		final EsiAssets200Ok esiConversion2 = new EsiAssets200Ok.Builder()
				.withItemId( 100L )
				.withTypeId( 11490 )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withQuantity( 1 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.ASSETSAFETY )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.STATION )
				.withLocationId( 100L )
				.build();
		// It is a type on the container type list.
		Assertions.assertTrue( new EsiAssets200Ok2NeoAssetConverter().convert( esiConversion2 ).isContainer() );
	}

	@Test
	public void checkIfContainerCheckC() {
		final EsiAssets200Ok esiConversion3 = new EsiAssets200Ok.Builder()
				.withItemId( 100L )
				.withTypeId( 100 )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withQuantity( 1 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.OFFICEFOLDER )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.STATION )
				.withLocationId( 100L )
				.build();
		// It is an office.
		Assertions.assertTrue( new EsiAssets200Ok2NeoAssetConverter().convert( esiConversion3 ).isContainer() );
		final EsiAssets200Ok esiConversion4 = new EsiAssets200Ok.Builder()
				.withItemId( 100L )
				.withTypeId( 100 )
				.withIsBlueprintCopy( true )
				.withIsSingleton( false )
				.withQuantity( 1 )
				.withLocationFlag( GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum.IMPOUNDED )
				.withLocationType( GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum.STATION )
				.withLocationId( 100L )
				.build();
		Assertions.assertTrue( new EsiAssets200Ok2NeoAssetConverter().convert( esiConversion4 ).isContainer() );
	}
}