package org.dimensinfin.eveonline.neocom.domain.space;

import java.io.Serializable;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public interface SpaceLocation extends Serializable, ICollaboration {
	Long getLocationId();

	LocationIdentifierType getLocationType();
}
