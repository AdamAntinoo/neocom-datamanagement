package org.dimensinfin.eveonline.neocom.utility;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;

public class LocationIdentifierTypeCalculatorTest {

	@Test
	public void calculate() {
		final LocationIdentifier identifier = Mockito.mock(LocationIdentifier.class);
		Assert.assertEquals( LocationIdentifierType.UNKNOWN,
				LocationIdentifierTypeCalculator.calculate( identifier ));
	}
}