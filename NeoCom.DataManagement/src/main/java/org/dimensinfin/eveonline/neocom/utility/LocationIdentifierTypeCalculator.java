package org.dimensinfin.eveonline.neocom.utility;

import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;

public class LocationIdentifierTypeCalculator {
	public enum LocationIdentifierType {
		UNKNOWN
	}

	public static LocationIdentifierType calculate( final LocationIdentifier identifier ) {
		return LocationIdentifierType.UNKNOWN;
	}
}