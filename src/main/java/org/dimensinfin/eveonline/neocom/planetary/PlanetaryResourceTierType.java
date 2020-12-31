package org.dimensinfin.eveonline.neocom.planetary;

public enum PlanetaryResourceTierType {
	RAW("R", "Planetary Commodities"),
	TIER1("T1", "Basic Commodities - Tier 1"),
	TIER2("T2", "Refined Commodities - Tier 2"),
	TIER3("T3", "Specialized Commodities - Tier 3"),
	TIER4("T4", "Advanced Commodities - Tier 4");

	public static PlanetaryResourceTierType searchTierType4Group( final String groupName ) {
		if ( PlanetaryResourceTierType.TIER1.getTierGroup().equalsIgnoreCase(groupName)) return PlanetaryResourceTierType.TIER1;
		if ( PlanetaryResourceTierType.TIER2.getTierGroup().equalsIgnoreCase(groupName)) return PlanetaryResourceTierType.TIER2;
		if ( PlanetaryResourceTierType.TIER3.getTierGroup().equalsIgnoreCase(groupName)) return PlanetaryResourceTierType.TIER3;
		if ( PlanetaryResourceTierType.TIER4.getTierGroup().equalsIgnoreCase(groupName)) return PlanetaryResourceTierType.TIER4;
		return PlanetaryResourceTierType.RAW;
	}

	private String typeCode;
	private String tierGroup;

	PlanetaryResourceTierType( final String typeCode, final String tierGroup ) {
		this.typeCode = typeCode;
		this.tierGroup = tierGroup;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public String getTierGroup() {
		return this.tierGroup;
	}
}
