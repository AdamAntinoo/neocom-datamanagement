package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class StructureImplementation extends NeoComNode implements Structure {
	private static final long serialVersionUID = -646550210810227292L;
	private SpaceLocationImplementation spaceLocation;

	private StructureImplementation() {super();}

	// - D E L E G A T E S
	@Override
	public Long getLocationId() {return spaceLocation.getLocationId();}

	@Override
	public Integer getRegionId() {return spaceLocation.getRegionId();}

//	@Override
//	public GetUniverseRegionsRegionIdOk getRegion() {return spaceLocation.getRegion();}

	@Override
	public String getRegionName() {return spaceLocation.getRegionName();}

	@Override
	public Integer getConstellationId() {return spaceLocation.getConstellationId();}

//	@Override
//	public GetUniverseConstellationsConstellationIdOk getConstellation() {return spaceLocation.getConstellation();}

	@Override
	public String getConstellationName() {return spaceLocation.getConstellationName();}

	@Override
	public Integer getSolarSystemId() {return spaceLocation.getSolarSystemId();}

//	@Override
//	public GetUniverseSystemsSystemIdOk getSolarSystem() {return spaceLocation.getSolarSystem();}

	@Override
	public String getSolarSystemName() {return this.spaceLocation.getSolarSystemName();}

	@Override
	public Long getStructureId() {return this.spaceLocation.getStructureId();}

	@Override
	public GetUniverseStructuresStructureIdOk getStructure() {return this.spaceLocation.getStructure();}

	@Override
	public String getStructureName() {return spaceLocation.getStructureName();}

	// - B U I L D E R
	public static class Builder {
		private StructureImplementation onConstruction;
		private GetUniverseRegionsRegionIdOk region;
		private GetUniverseConstellationsConstellationIdOk constellation;
		private GetUniverseSystemsSystemIdOk solarSystem;
		private Long structureId;
		private GetUniverseStructuresStructureIdOk structure;

		public Builder() {
			this.onConstruction = new StructureImplementation();
		}

		public StructureImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.region = region;
			return this;
		}

		public StructureImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.constellation = constellation;
			return this;
		}

		public StructureImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.solarSystem = solarSystem;
			return this;
		}

		public StructureImplementation.Builder withStructure( final Long structureId,
		                                                      final GetUniverseStructuresStructureIdOk structure ) {
			Objects.requireNonNull( structureId );
			Objects.requireNonNull( structure );
			this.structureId = structureId;
			this.structure = structure;
			return this;
		}

		public Structure build() {
			Objects.requireNonNull( this.region );
			Objects.requireNonNull( this.constellation );
			Objects.requireNonNull( this.solarSystem );
			Objects.requireNonNull( this.structureId );
			Objects.requireNonNull( this.structure );
			this.onConstruction.spaceLocation = new SpaceLocationImplementation.Builder()
					.withRegion( this.region )
					.withConstellation( this.constellation )
					.withSolarSystem( this.solarSystem )
					.withStructure( this.structureId, this.structure )
					.build();
			return this.onConstruction;
		}
	}
}
