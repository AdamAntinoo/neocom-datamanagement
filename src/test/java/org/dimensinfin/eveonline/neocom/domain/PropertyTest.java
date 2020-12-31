package org.dimensinfin.eveonline.neocom.domain;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PropertyTest {
	private Property property4Test;

	@Before
	public void setUp() throws Exception {
		property4Test = new Property.Builder()
				.withPropertyType( PropertyTypes.LOCATIONPROPERTY )
				.withOwnerId( 123L )
				.withTargetId( 321 )
				.build();
	}

	@Test
	public void gettersContract() {
		Assert.assertNotNull( property4Test );
		Assert.assertEquals( -2, property4Test.getId() );
		Assert.assertEquals( 123L, property4Test.getOwnerId() );
		Assert.assertEquals( 321, property4Test.getTargetId() );
		Assert.assertEquals( PropertyTypes.LOCATIONPROPERTY, property4Test.getPropertyType() );
		property4Test.setNumericValue( 1000 );
		Assert.assertEquals( 1000, property4Test.getNumericValue(), 0.1 );
		property4Test.setStringValue( "-PROPERTY-VALUE-" );
		Assert.assertEquals( "-PROPERTY-VALUE-", property4Test.getStringValue() );
	}

	@Test
	public void settersContract() {
		property4Test.setNumericValue( 1000 );
		Assert.assertEquals( 1000, property4Test.getNumericValue(), 0.1 );
		property4Test.setStringValue( "-PROPERTY-VALUE-" );
		Assert.assertEquals( "-PROPERTY-VALUE-", property4Test.getStringValue() );
		property4Test.setTargetId( 765 );
		Assert.assertEquals( 765, property4Test.getTargetId() );
	}

	@Test
	public void toStringContract() {
		Assert.assertEquals( "Property [ [0.0] Type:LOCATIONPROPERTY ]", property4Test.toString() );
	}
}