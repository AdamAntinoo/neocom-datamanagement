package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class Structure extends SpaceLocationImplementation implements SpaceSystem {
	private Long structureId;
	private String structureName;
	private Integer ownerId;
	private Integer structureTypeId;
	private Integer corporationId;
	private String corporationName;

	// - C O N S T R U C T O R S
	protected Structure() {super();}

	// - G E T T E R S   &   S E T T E R S
	public Integer getCorporationId() {
		return this.corporationId;
	}

	public String getCorporationName() {
		return this.corporationName;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public Long getStationId() {
		return this.structureId;
	}

	public String getStationName() {return this.structureName;}

	public Long getStructureId() {
		return this.structureId;
	}

	public String getStructureName() {
		return this.structureName;
	}

	public Integer getStructureTypeId() {
		return this.structureTypeId;
	}

	// - B U I L D E R
	public static class Builder {
		private Structure onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new Structure();
		}

		public Structure build() {
			Objects.requireNonNull( this.onConstruction.regionId );
			Objects.requireNonNull( this.onConstruction.regionName );
			Objects.requireNonNull( this.onConstruction.constellationId );
			Objects.requireNonNull( this.onConstruction.constellationName );
			Objects.requireNonNull( this.onConstruction.solarSystemId );
			Objects.requireNonNull( this.onConstruction.solarSystemName );
			Objects.requireNonNull( this.onConstruction.stationId );
			Objects.requireNonNull( this.onConstruction.stationName );
			Objects.requireNonNull( this.onConstruction.corporationId );
			Objects.requireNonNull( this.onConstruction.corporationName );
			return this.onConstruction;
		}

		public Structure fromStructure( final Structure structure ) {
			Objects.requireNonNull( structure );
			this.onConstruction.regionId = structure.getRegionId();
			this.onConstruction.regionName = structure.getRegionName();
			this.onConstruction.constellationId = structure.getConstellationId();
			this.onConstruction.constellationName = structure.getConstellationName();
			this.onConstruction.solarSystemId = structure.getSolarSystemId();
			this.onConstruction.solarSystemName = structure.getSolarSystemName();
			this.onConstruction.stationId = Math.toIntExact( structure.getStationId() );
			this.onConstruction.stationName = structure.getStationName();
			this.onConstruction.securityClass = structure.getSecurityClass();
			this.onConstruction.securityStatus = structure.getSecurityStatus();
			this.onConstruction.corporationId = structure.getCorporationId();
			this.onConstruction.corporationName = structure.getCorporationName();
			this.onConstruction.structureId = structure.getStructureId();
			this.onConstruction.structureName = structure.getStructureName();
			this.onConstruction.ownerId = structure.getOwnerId();
			this.onConstruction.structureTypeId = structure.getStructureTypeId();
			return this.onConstruction;
		}

		public Structure.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellationName = constellation.getName();
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public Structure.Builder withCorporation( final Integer corporationId, final String corporationName ) {
			this.onConstruction.corporationId = Objects.requireNonNull( corporationId );
			this.onConstruction.corporationName = Objects.requireNonNull( corporationName );
			return this;
		}

		public Structure.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.regionName = region.getName();
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public Structure.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.solarSystemName = solarSystem.getName();
			this.onConstruction.solarSystemId = solarSystem.getSystemId();
			this.onConstruction.securityClass = solarSystem.getSecurityClass();
			this.onConstruction.securityStatus = solarSystem.getSecurityStatus();
			return this;
		}

		public Structure.Builder withStructure( final Long structureId,
		                                        final GetUniverseStructuresStructureIdOk structure ) {
			Objects.requireNonNull( structure );
			this.onConstruction.structureId = Objects.requireNonNull( structureId );
			this.onConstruction.structureName = structure.getName();
			this.onConstruction.ownerId = structure.getOwnerId();
			this.onConstruction.structureTypeId = structure.getTypeId();
			this.onConstruction.stationId = Math.toIntExact( structureId );
			this.onConstruction.stationName = structure.getName();
			return this;
		}
	}
}