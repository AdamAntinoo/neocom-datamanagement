package org.dimensinfin.eveonline.neocom.domain.space;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;

public interface Station extends SpaceSystem {
	Integer getStationId();

//	GetUniverseStationsStationIdOk getStation();

	String getStationName();
}
