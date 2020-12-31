package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ResourceTest {
	@Test
	public void addStackMulti() {
		final Resource resource = new Resource( 34, 321, 10 );
		Assertions.assertEquals( 10, resource.getStackSize() );
		resource.add( 12345 );
		Assertions.assertEquals( 12345 + 3210, resource.getBaseQuantity() );
		Assertions.assertEquals( 12345 + 3210, resource.getQuantity() );
		Assertions.assertEquals( 1, resource.getStackSize() );
	}

	@Test
	public void addStackOne() {
		final Resource resource = new Resource( 34, 321, 1 );
		resource.add( 12345 );
		Assertions.assertEquals( 12345 + 321, resource.getBaseQuantity() );
		Assertions.assertEquals( 12345 + 321, resource.getQuantity() );
	}

	@Test
	public void addition() {
		final Resource resource = new Resource( 34, 321, 12 );
		resource.addition( new Resource( 34, 123, 10 ) );
		Assertions.assertEquals( 321 * 12 + 123 * 10, resource.getBaseQuantity() );
		Assertions.assertEquals( 321 * 12 + 123 * 10, resource.getQuantity() );
		Assertions.assertEquals( 1, resource.getStackSize() );
	}

	//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( Resource.class )
				.usingGetClass().verify();
	}

	@Test
	public void gettersContract() {
		// Given
		final NeoItemFactory factory = Mockito.mock( NeoItemFactory.class );
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		NeoItemFactory.setSingleton( factory );
		// When
		Mockito.when( factory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getTypeId() ).thenReturn( 34 );
		Mockito.when( neoItem.getCategoryName() ).thenReturn( "Mineral" );
		Mockito.when( neoItem.getGroupName() ).thenReturn( "-GROUP-NAME-" );
		Mockito.when( neoItem.getName() ).thenReturn( "-NAME-" );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "-URL-FOR-ITEM-" );
		Mockito.when( neoItem.getVolume() ).thenReturn( 100.0 );
		Mockito.when( neoItem.getPrice() ).thenReturn( 4321.09 );
		final Resource resource = new Resource( 34, 321, 12 );

		// Assertions
		Assertions.assertEquals( 34, resource.getTypeId() );
		Assertions.assertEquals( 321, resource.getBaseQuantity() );
		Assertions.assertEquals( "Mineral", resource.getCategory() );
		Assertions.assertEquals( "-GROUP-NAME-", resource.getGroupName() );
		Assertions.assertTrue( neoItem.equals( resource.getItem() ) );
		Assertions.assertEquals( "-NAME-", resource.getName() );
		Assertions.assertEquals( "-URL-FOR-ITEM-", resource.getURLForItem() );
		Assertions.assertEquals( 12, resource.getStackSize() );
		Assertions.assertEquals( 321, resource.getBaseQuantity() );
		Assertions.assertEquals( 321 * 12, resource.getQuantity() );
		Assertions.assertEquals( 100.0, resource.getVolume() );
		Assertions.assertEquals( 4321.09, resource.getPrice() );
	}

	@Test
	public void toStringContract() {
		// Given
		final NeoItemFactory factory = Mockito.mock( NeoItemFactory.class );
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		NeoItemFactory.setSingleton( factory );
		// When
		Mockito.when( factory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getTypeId() ).thenReturn( 34 );
		Mockito.when( neoItem.getCategoryName() ).thenReturn( "Mineral" );
		Mockito.when( neoItem.getGroupName() ).thenReturn( "-GROUP-NAME-" );
		Mockito.when( neoItem.getName() ).thenReturn( "-NAME-" );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "-URL-FOR-ITEM-" );
		Mockito.when( neoItem.getVolume() ).thenReturn( 100.0 );
		Mockito.when( neoItem.getPrice() ).thenReturn( 4321.09 );
		final Resource resource = new Resource( 34, 321, 12 );
		// Assertions
		final String expected = "{\"baseQty\":321,\"stackSize\":12,\"name\":\"-NAME-\",\"typeId\":34,\"quantity\":3852,\"volume\":100.0,\"price\":4321.09,\"jsonClass\":\"Resource\"}";
		Assertions.assertEquals( expected, resource.toString() );
	}
}
