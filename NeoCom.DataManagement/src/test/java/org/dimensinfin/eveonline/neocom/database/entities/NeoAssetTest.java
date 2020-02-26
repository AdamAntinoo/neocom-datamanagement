package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

public class NeoAssetTest {
	private GetUniverseTypesTypeIdOk esiItem;
	private GetUniverseGroupsGroupIdOk esiGroup;
	private GetUniverseCategoriesCategoryIdOk esiCategory;
	private NeoItem neoItem;
	private ESIUniverseDataProvider esiDataProvider;

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
		this.esiDataProvider = Mockito.mock( ESIUniverseDataProvider.class );
		Mockito.when( this.esiDataProvider.searchEsiItem4Id( Mockito.anyInt() ) ).thenReturn( this.esiItem );
		Mockito.when( this.esiDataProvider.searchItemGroup4Id( Mockito.anyInt() ) ).thenReturn( this.esiGroup );
		Mockito.when( this.esiDataProvider.searchItemCategory4Id( Mockito.anyInt() ) ).thenReturn( this.esiCategory );
		NeoItem.injectEsiUniverseDataAdapter( this.esiDataProvider );
	}

	@Test
	public void gettersContract() {
		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
		esiAsset.setItemId( 1234567L );
		esiAsset.setTypeId( 34 );
		esiAsset.setQuantity( 10 );
		final NeoAsset asset = new NeoAsset();
		asset.setAssetDelegate( esiAsset );
		asset.setItemDelegate( new NeoItem( esiAsset.getTypeId() ) );
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
}
