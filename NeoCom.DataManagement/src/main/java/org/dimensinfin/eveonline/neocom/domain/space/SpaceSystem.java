package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public interface SpaceSystem extends SpaceConstellation {
	Integer getSolarSystemId();

//	GetUniverseSystemsSystemIdOk getSolarSystem();

	String getSolarSystemName();
}
