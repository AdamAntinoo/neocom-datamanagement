package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoLocation;

public class NeoLocationTest {
	@Test
	public void buildComplete() {
		final LocationIdentifier identifier = Mockito.mock( LocationIdentifier.class );
		final NeoLocation location = new NeoLocation.Builder()
				.withLocationIdentifier( identifier )
				.build();
		Assert.assertNotNull( location );
	}
}