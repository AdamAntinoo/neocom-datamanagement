package org.dimensinfin.eveonline.neocom.planetary;

public enum PlanetaryFacilityType {
	COMMAND_CENTER(10),
	STORAGE(20),
	LAUNCHPAD(22),
	PLANETARY_FACTORY(40),
	EXTRACTOR_HEAD(32),
	EXTRACTOR_CONTROL_UNIT(30);

	private int facilityOrderIndex;

	PlanetaryFacilityType( final int facilityOrderIndex ) {
		this.facilityOrderIndex = facilityOrderIndex;
	}

	public int getFacilityOrderIndex() {
		return this.facilityOrderIndex;
	}

	public static PlanetaryFacilityType getTypeByStructureGroup( final int structureGroup ) {
		if (structureGroup == 1027) return PlanetaryFacilityType.COMMAND_CENTER;
		if (structureGroup == 1026) return PlanetaryFacilityType.EXTRACTOR_HEAD;
		if (structureGroup == 1063) return PlanetaryFacilityType.EXTRACTOR_CONTROL_UNIT;
		if (structureGroup == 1030) return PlanetaryFacilityType.LAUNCHPAD;
		if (structureGroup == 1029) return PlanetaryFacilityType.STORAGE;
		return PlanetaryFacilityType.PLANETARY_FACTORY;
	}
}
