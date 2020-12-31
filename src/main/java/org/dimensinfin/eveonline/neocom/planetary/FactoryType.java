package org.dimensinfin.eveonline.neocom.planetary;

public enum FactoryType {
	BASIC_INDUSTRY(3000, 20),
	ADVANCED_INDUSTRY(40, 5),
	ADVANCED_INDUSTRY_MULTICOMPONENT(10, 3),
	HIGH_INDUSTRY(6, 1);

	public static FactoryType facility4Schematics( final int schematic ) {
		if (schematic == 104) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		if (schematic == 111) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		if (schematic == 95) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		if (schematic == 110) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		if (schematic == 103) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		if (schematic == 96) return ADVANCED_INDUSTRY_MULTICOMPONENT;

		if (schematic == 117) return HIGH_INDUSTRY;
		if (schematic == 118) return HIGH_INDUSTRY;
		if (schematic == 114) return HIGH_INDUSTRY;
		if (schematic == 112) return HIGH_INDUSTRY;
		if (schematic == 116) return HIGH_INDUSTRY;
		if (schematic == 115) return HIGH_INDUSTRY;
		if (schematic == 113) return HIGH_INDUSTRY;
		if (schematic == 119) return HIGH_INDUSTRY;
		return ADVANCED_INDUSTRY;
	}

	private final long capacity;
	private final long output;

	FactoryType( final long capacity, final long output ) {
		this.capacity = capacity;
		this.output = output;
	}

	public Long getCapacity() {
		return this.capacity;
	}

	public Long getOutput() {
		return this.output;
	}
}
