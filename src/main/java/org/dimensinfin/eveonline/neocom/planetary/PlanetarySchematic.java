package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.database.repositories.PlanetaryRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Defines all the data related to a planetary transformation schematic. On creation generates and accesses the resource for the inputs and outputs.
 * Adds method to operate with the schematic.
 */
public class PlanetarySchematic implements Serializable {
	private static final long serialVersionUID = 3353280246278708239L;
	private PlanetaryRepository planetaryRepository;
	private int schematicId;
	protected final List<Schematics> inputList = new ArrayList<>();
	protected Schematics output;

	public PlanetarySchematic( final Integer schematicId ) {
		this.schematicId = schematicId;
	}

	public int getSchematicId() {
		return this.schematicId;
	}

	public Schematics getOutput() {
		return this.output;
	}

	public String getGroupName() {
		return this.output.getGroupName();
	}

	public int getCycleTime() {
		return this.output.getCycleTime();
	}

	public int getInputRequiredQuantity() {
		return this.inputList.get(0).getQuantity();
	}

	// - B U I L D E R
	public static class Builder {
		private PlanetarySchematic onConstruction;

		public Builder( final Integer schematicId ) {
			this.onConstruction = new PlanetarySchematic(schematicId);
		}

		public Builder withPlanetaryRepository( final PlanetaryRepository planetaryRepository ) {
			this.onConstruction.planetaryRepository = planetaryRepository;
			return this;
		}

		public PlanetarySchematic build() {
			Objects.requireNonNull(this.onConstruction.planetaryRepository);
			// Read from the SDE the schematic data.
			final List<Schematics> schematics = this.onConstruction.planetaryRepository.searchSchematics4Id(this.onConstruction.schematicId);
			for (Schematics schematic : schematics) {
				if (schematic.getDirection() == SchematicDirection.INPUT) this.onConstruction.inputList.add(schematic);
				else this.onConstruction.output = schematic;
			}
			return this.onConstruction;
		}
	}
}
