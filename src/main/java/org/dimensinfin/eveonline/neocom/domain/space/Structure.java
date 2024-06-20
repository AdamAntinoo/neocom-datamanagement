package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

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

	public LocationIdentifierType getLocationType() {
		return LocationIdentifierType.STRUCTURE;
	}

	@Override
	public Long getStationId() {
		return this.structureId;
	}

	@Override
	public String getStationName() {return this.structureName;}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.structureId )
				.append( this.structureName )
				.append( this.ownerId )
				.append( this.structureTypeId )
				.append( this.corporationId )
				.append( this.corporationName ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) return true;
		if ( !(o instanceof Structure) ) return false;
		final Structure structure = (Structure) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.structureId, structure.structureId )
				.append( this.structureName, structure.structureName )
				.append( this.ownerId, structure.ownerId )
				.append( this.structureTypeId, structure.structureTypeId )
				.append( this.corporationId, structure.corporationId )
				.append( this.corporationName, structure.corporationName ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "structureId", this.structureId )
				.append( "structureName", this.structureName )
				.append( "ownerId", this.ownerId )
				.append( "structureTypeId", this.structureTypeId )
				.append( "corporationId", this.corporationId )
				.append( "corporationName", this.corporationName )
				.append( "spaceLocation", super.toString() )
				.toString();
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

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
			this.onConstruction.stationId = structure.getStationId();
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
			this.onConstruction.stationId = Objects.requireNonNull( structureId );
			this.onConstruction.stationName = structure.getName();
			return this;
		}
	}
}