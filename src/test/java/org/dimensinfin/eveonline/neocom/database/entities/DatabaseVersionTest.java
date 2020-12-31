package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import nl.jqno.equalsverifier.EqualsVerifier;

public class DatabaseVersionTest {
	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( DatabaseVersion.class );
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( DatabaseVersion.class ).verify();
	}

	@Test
	public void toStringContract() {
		final String expected = "{\"versionNumber\":100}";
		Assert.assertEquals( expected, new DatabaseVersion( 100 ).toString() );
	}

	@Test
	public void constructorNumber() {
		final DatabaseVersion versionStringValid = new DatabaseVersion( 2000 );
		Assert.assertNotNull( versionStringValid );
		Assert.assertEquals( 2000, versionStringValid.getVersionNumber() );
	}

//	@Test
//	public void constructor_string() {
//		final DatabaseVersion versionStringValid = new DatabaseVersion( 1000 );
//		Assert.assertNotNull( versionStringValid );
//		Assert.assertEquals( 1000, versionStringValid.getVersionNumber() );
//		final DatabaseVersion versionStringInvalid = new DatabaseVersion( 100 );
//		Assert.assertNotNull( versionStringInvalid );
//		Assert.assertEquals( 100, versionStringInvalid.getVersionNumber() );
//	}
}