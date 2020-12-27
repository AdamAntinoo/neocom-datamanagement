package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

public interface SpaceRegion extends SpaceLocation {
	Integer getRegionId();

//	GetUniverseRegionsRegionIdOk getRegion();

	String getRegionName();
}
