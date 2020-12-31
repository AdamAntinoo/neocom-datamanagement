package org.dimensinfin.eveonline.neocom.planetary;

public interface ICommandCenterFacility {
	void setUpgradeLevel( final int upgradeLevel );

	void setCpuInUse( final int cpuInUse );

	void setPowerInUse( final int powerInUse );

	FacilityGeoPosition getGeoPosition();
}
