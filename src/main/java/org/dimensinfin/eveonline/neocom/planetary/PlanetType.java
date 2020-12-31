package org.dimensinfin.eveonline.neocom.planetary;

public enum PlanetType {
	BARREN(2016, "Barren"),
	TEMPERATE(2016, "Temperate"),
	OCEANIC(2016, "Oceanic"),
	ICE(2016, "Ice"),
	GAS(2016, "Gas"),
	LAVA(2016, "Lava"),
	STORM(2016, "Storm"),
	PLASMA(2016, "Plasma");

	private final int type;
	private final String prefix;

	PlanetType( final int planetType, final String prefix ) {
		this.type = planetType;
		this.prefix = prefix;
	}

	public String getPrefix() {
		return this.prefix;
	}
}
