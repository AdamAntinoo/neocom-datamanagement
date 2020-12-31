package org.dimensinfin.eveonline.neocom.planetary;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.planetary.domain.PlanetaryResource;

/**
 * This schematic will define a input or a output for a planetary interaction transformation process delivered by a factory. To simplify
 * instance structure each of the components of the transformation have the whole data related to the schematic, both the name and cycle data
 * joined with the specific resource for the transformation interaction.
 */
public class Schematics {
	private Integer schematicId;
	private String schematicName;
	private int cycleTime;
	private PlanetaryResource resource;
	private SchematicDirection direction = SchematicDirection.INPUT;

	private Schematics() {}

	public int getTypeId() {
		return this.resource.getTypeId();
	}

	public String getName() {
		return this.resource.getName();
	}

	public int getQuantity() {
		return this.resource.getQuantity();
	}

	public SchematicDirection getDirection() {
		return this.direction;
	}

	public String getGroupName() {
		return this.resource.getGroupName();
	}

	public int getCycleTime() {
		return this.cycleTime;
	}

	public PlanetaryResourceTierType getTier() {return resource.getTier();}

	// - B U I L D E R
	public static class Builder {
		private Schematics onConstruction;
		private int quantity = 0;

		public Builder() {
			this.onConstruction = new Schematics();
		}

		public Builder withSchematicId( final Integer schematicId ) {
			this.onConstruction.schematicId = schematicId;
			return this;
		}

		public Builder withSchematicName( final String schematicName ) {
			this.onConstruction.schematicName = schematicName;
			return this;
		}

		public Builder withCycleTime( final int cycleTime ) {
			this.onConstruction.cycleTime = cycleTime;
			return this;
		}

		public Builder withResourceTypeId( final int resourceType ) {
			this.onConstruction.resource = new PlanetaryResource(resourceType);
			return this;
		}

		public Builder withQuantity( final int quantity ) {
			this.quantity = quantity;
			return this;
		}

		public Builder withDirection( final boolean direction ) {
			if (direction) this.onConstruction.direction = SchematicDirection.INPUT;
			else this.onConstruction.direction = SchematicDirection.OUTPUT;
			return this;
		}

		public Schematics build() {
			Objects.requireNonNull(this.onConstruction.resource);
			this.onConstruction.resource.setQuantity(this.quantity);
			return this.onConstruction;
		}
	}
}
