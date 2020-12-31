package org.dimensinfin.eveonline.neocom.planetary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FacilityGeoPositionTest {
	@Test
	public void gettersContract() {
		final FacilityGeoPosition facilityGeoPosition = new FacilityGeoPosition();
		Assertions.assertEquals( 0.0F, facilityGeoPosition.getLatitude() );
		Assertions.assertEquals( 0.0F, facilityGeoPosition.getLongitude() );
		Assertions.assertEquals( facilityGeoPosition, facilityGeoPosition.setLatitude( 12.4F ) );
		Assertions.assertEquals( 12.4F, facilityGeoPosition.getLatitude() );
		Assertions.assertEquals( facilityGeoPosition, facilityGeoPosition.setLatitude( 88.65 ) );
		Assertions.assertEquals( 88.65, facilityGeoPosition.getLatitude(), 0.01 );
		Assertions.assertEquals( facilityGeoPosition, facilityGeoPosition.setLongitude( 12.4F ) );
		Assertions.assertEquals( 12.4F, facilityGeoPosition.getLongitude() );
		Assertions.assertEquals( facilityGeoPosition, facilityGeoPosition.setLongitude( 88.65 ) );
		Assertions.assertEquals( 88.65, facilityGeoPosition.getLongitude(), 0.01 );
	}
}
