package org.dimensinfin.eveonline.neocom.domain.space;

public interface SpaceSystem extends SpaceConstellation {
	Float getSecurityStatus();

	String getSecurityClass();

	Integer getSolarSystemId();

	String getSolarSystemName();
}
