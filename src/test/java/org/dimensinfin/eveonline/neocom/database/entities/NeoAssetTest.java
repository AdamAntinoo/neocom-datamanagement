package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2EsiAssets200OkConverter;
import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

public class NeoAssetTest {
	private GetUniverseTypesTypeIdOk esiItem;
	private GetUniverseGroupsGroupIdOk esiGroup;
	private GetUniverseCategoriesCategoryIdOk esiCategory;
	private NeoItem neoItem;
	private ESIDataService esiDataService;

	@Test
	public void gettersContract() {
		final GetCharactersCharacterIdAssets200Ok esiAssetOk = new GetCharactersCharacterIdAssets200Ok();
		esiAssetOk.setItemId( 123456L );
		esiAssetOk.setTypeId( 34 );
		esiAssetOk.setLocationId( 54321L );
		esiAssetOk.setQuantity( 10 );
		esiAssetOk.setLocationId( 123L );
		esiAssetOk.setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.ASSETSAFETY );
		esiAssetOk.setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM );
		final EsiAssets200Ok esiAsset = new GetCharactersCharacterIdAsset2EsiAssets200OkConverter().convert( esiAssetOk );
		final NeoAsset asset = new NeoAsset();
		asset.setAssetDelegate( esiAsset );
		asset.setItemDelegate( new NeoItem( esiAssetOk.getTypeId() ) );
		Assertions.assertNotNull( asset );
		Assertions.assertEquals( 34, asset.getTypeId() );
		Assertions.assertEquals( 4, asset.getCategoryId() );
		Assertions.assertEquals( "Material", asset.getCategoryName() );
		Assertions.assertEquals( 18, asset.getGroupId() );
		Assertions.assertEquals( "Mineral", asset.getGroupName() );
		Assertions.assertEquals( 10, asset.getQuantity().longValue() );
		Assertions.assertEquals( "Tritanium", asset.getName() );
		Assertions.assertEquals( "Tech I", asset.getTech() );
		Assertions.assertEquals( 0.01, asset.getVolume(), 0.01 );
		Assertions.assertEquals( false, asset.isBlueprint() );
		Assertions.assertEquals( "https://image.eveonline.com/Type/34_64.png", asset.getURLForItem() );
	}

	@Before
	public void setUp() throws Exception {
		this.esiItem = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		Mockito.when( this.esiItem.getGroupId() ).thenReturn( 321 );
		Mockito.when( this.esiItem.getName() ).thenReturn( "Tritanium" );
		this.esiGroup = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		Mockito.when( this.esiGroup.getGroupId() ).thenReturn( 18 );
		Mockito.when( this.esiGroup.getName() ).thenReturn( "Mineral" );
		Mockito.when( this.esiGroup.getCategoryId() ).thenReturn( 654 );
		this.esiCategory = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		Mockito.when( this.esiCategory.getCategoryId() ).thenReturn( 4 );
		Mockito.when( this.esiCategory.getName() ).thenReturn( "Material" );
		this.esiDataService = Mockito.mock( ESIDataService.class );
		Mockito.when( this.esiDataService.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( this.esiItem );
		Mockito.when( this.esiDataService.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( this.esiGroup );
		Mockito.when( this.esiDataService.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( this.esiCategory );
		NeoItem.injectEsiUniverseDataAdapter( this.esiDataService );
	}
}
