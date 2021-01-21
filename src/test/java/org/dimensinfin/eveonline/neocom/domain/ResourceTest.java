package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import nl.jqno.equalsverifier.EqualsVerifier;
import static org.dimensinfin.eveonline.neocom.domain.EsiType.ESI_ICON_URL_PREFIX;
import static org.dimensinfin.eveonline.neocom.domain.EsiType.ESI_ICON_URL_SUFFIX;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_QUANTITY;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_CATEGORY;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_GROUP;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ResourceConstants.TEST_RESOURCE_VOLUME;

public class ResourceTest {
	@Test
	public void add() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final Resource resource = new Resource.Builder()
				.withTypeId( TEST_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build()
				.setQuantity( TEST_RESOURCE_QUANTITY );
		// Test
		resource.add( 123 );
		// Assertions
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY + 123, resource.getQuantity() );
		resource.add( 0 );
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY + 123 + 0, resource.getQuantity() );
		resource.add( -10 );
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY + 123 + 0, resource.getQuantity() );
	}

	@Test
	public void constructorContract() {
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final Resource resource = new Resource.Builder()
				.withTypeId( TEST_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build();
		Assertions.assertNotNull( resource );
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( Resource.class )
				.usingGetClass().verify();
	}

	@Test
	public void gettersContract() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		// When
		Mockito.when( type.getTypeId() ).thenReturn( TEST_RESOURCE_TYPE_ID );
		Mockito.when( category.getName() ).thenReturn( TEST_RESOURCE_CATEGORY );
		Mockito.when( group.getName() ).thenReturn( TEST_RESOURCE_GROUP );
		Mockito.when( type.getName() ).thenReturn( TEST_RESOURCE_NAME );
		Mockito.when( type.getVolume() ).thenReturn( TEST_RESOURCE_VOLUME.floatValue() );
		// Test
		final Resource resource = new Resource.Builder()
				.withTypeId( TEST_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build()
				.setQuantity( TEST_RESOURCE_QUANTITY );
		// Assertions
		Assertions.assertEquals( TEST_RESOURCE_TYPE_ID, resource.getTypeId() );
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY, resource.getQuantity() );
		Assertions.assertEquals( TEST_RESOURCE_CATEGORY, resource.getCategoryName() );
		Assertions.assertEquals( TEST_RESOURCE_GROUP, resource.getGroupName() );
		Assertions.assertEquals( TEST_RESOURCE_NAME, resource.getName() );
		Assertions.assertEquals( ESI_ICON_URL_PREFIX + TEST_RESOURCE_TYPE_ID + ESI_ICON_URL_SUFFIX, resource.getTypeIconURL() );
		Assertions.assertEquals( TEST_RESOURCE_VOLUME, resource.getVolume() );
		Assertions.assertEquals( "Resource", resource.getJsonClass() );
	}

	@Test
	public void sub() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		final Resource resource = new Resource.Builder()
				.withTypeId( TEST_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build()
				.setQuantity( TEST_RESOURCE_QUANTITY );
		// Test
		resource.sub( 123 );
		// Assertions
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY - 123, resource.getQuantity() );
		resource.sub( 0 );
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY - 123 - 0, resource.getQuantity() );
		resource.sub( -10 );
		Assertions.assertEquals( TEST_RESOURCE_QUANTITY - 123 - 0, resource.getQuantity() );
		resource.sub( TEST_RESOURCE_QUANTITY * 2 );
		Assertions.assertEquals( 0, resource.getQuantity() );
	}

	@Test
	public void toStringContract() {
		// Given
		final GetUniverseTypesTypeIdOk type = Mockito.mock( GetUniverseTypesTypeIdOk.class );
		final GetUniverseGroupsGroupIdOk group = Mockito.mock( GetUniverseGroupsGroupIdOk.class );
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock( GetUniverseCategoriesCategoryIdOk.class );
		// When
		Mockito.when( type.getTypeId() ).thenReturn( TEST_RESOURCE_TYPE_ID );
		Mockito.when( category.getName() ).thenReturn( TEST_RESOURCE_CATEGORY );
		Mockito.when( group.getName() ).thenReturn( TEST_RESOURCE_GROUP );
		Mockito.when( type.getName() ).thenReturn( TEST_RESOURCE_NAME );
		Mockito.when( type.getVolume() ).thenReturn( TEST_RESOURCE_VOLUME.floatValue() );
		// Test
		final Resource resource = new Resource.Builder()
				.withTypeId( TEST_RESOURCE_TYPE_ID )
				.withItemType( type )
				.withGroup( group )
				.withCategory( category )
				.build()
				.setQuantity( TEST_RESOURCE_QUANTITY );
		// Assertions
		final String expected = "{\"baseQty\":543,\"name\":\"Silicates\",\"typeId\":16636,\"quantity\":543,\"volume\":0.5,\"price\":0.0,\"jsonClass\":\"Resource\"}";
		Assertions.assertEquals( expected, resource.toString() );
	}
}
