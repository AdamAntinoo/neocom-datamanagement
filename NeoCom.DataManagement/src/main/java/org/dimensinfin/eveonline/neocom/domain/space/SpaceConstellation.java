package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;

public interface SpaceConstellation extends SpaceRegion {
	Integer getConstellationId();

//	GetUniverseConstellationsConstellationIdOk getConstellation();

	String getConstellationName();
}
