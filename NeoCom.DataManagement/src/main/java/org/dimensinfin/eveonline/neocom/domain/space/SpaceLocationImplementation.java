package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class SpaceLocationImplementation extends NeoComNode implements Station {
	private static final long serialVersionUID = -9028958348146320642L;

	protected Integer regionId;
	protected String regionName;
	protected Integer constellationId;
	protected String constellationName;
	protected Integer solarSystemId;
	protected String solarSystemName;
	protected Integer stationId;
	protected String stationName;
	protected String securityClass;
	protected Float securityStatus;

	// - C O N S T R U C T O R S
	protected SpaceLocationImplementation() {super();}

	// - G E T T E R S   &   S E T T E R S
	public Integer getConstellationId() {
		return this.constellationId;
	}

	public String getConstellationName() {return this.constellationName;}

	// - V I R T U A L
	public Long getLocationId() {
		if (null != this.stationId) return this.stationId.longValue();
		if (null != this.solarSystemId) return this.solarSystemId.longValue();
		if (null != this.constellationId) return this.constellationId.longValue();
		return this.regionId.longValue();
	}

	public LocationIdentifierType getLocationType() {
		if (null != this.stationId) return LocationIdentifierType.STATION;
		if (null != this.solarSystemId) return LocationIdentifierType.SOLAR_SYSTEM;
		if (null != this.constellationId) return LocationIdentifierType.CONSTELLATION;
		return LocationIdentifierType.REGION;
	}

	public Integer getRegionId() {
		return this.regionId;
	}

	public String getRegionName() {return this.regionName;}

	public Float getSecurityStatus() {
		return this.securityStatus;
	}

	public String getSecurityClass() {
		return this.securityClass;
	}

	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	public String getSolarSystemName() {return this.solarSystemName;}

	public Long getStationId() {
		return (long) this.stationId;
	}

	public String getStationName() {return this.stationName;}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.regionId )
				.append( this.regionName )
				.append( this.constellationId )
				.append( this.constellationName )
				.append( this.solarSystemId )
				.append( this.solarSystemName )
				.append( this.stationId )
				.append( this.stationName )
				.append( this.securityClass )
				.append( this.securityStatus ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof SpaceLocationImplementation)) return false;
		final SpaceLocationImplementation that = (SpaceLocationImplementation) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) )
				.append( this.regionId, that.regionId )
				.append( this.regionName, that.regionName )
				.append( this.constellationId, that.constellationId )
				.append( this.constellationName, that.constellationName )
				.append( this.solarSystemId, that.solarSystemId )
				.append( this.solarSystemName, that.solarSystemName )
				.append( this.stationId, that.stationId )
				.append( this.stationName, that.stationName )
				.append( this.securityClass, that.securityClass )
				.append( this.securityStatus, that.securityStatus )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "regionId", this.regionId )
				.append( "regionName", this.regionName )
				.append( "constellationId", this.constellationId )
				.append( "constellationName", this.constellationName )
				.append( "solarSystemId", this.solarSystemId )
				.append( "solarSystemName", this.solarSystemName )
				.append( "stationId", this.stationId )
				.append( "stationName", this.stationName )
				.append( "securityClass", this.securityClass )
				.append( "securityStatus", this.securityStatus )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final SpaceLocationImplementation onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new SpaceLocationImplementation();
		}

		/**
		 * At least the region should be specified so this instance can be converted on a <code>Region</code>.
		 */
		public SpaceLocationImplementation build() {
			Objects.requireNonNull( this.onConstruction.regionId );
			return this.onConstruction;
		}

		public SpaceLocationImplementation fromSpaceLocation( final SpaceLocation location ) {
			Objects.requireNonNull( location );
			this.onConstruction.regionId = ((SpaceRegion) location).getRegionId();
			this.onConstruction.regionName = ((SpaceRegion) location).getRegionName();
			this.onConstruction.constellationId = ((SpaceConstellation) location).getConstellationId();
			this.onConstruction.constellationName = ((SpaceConstellation) location).getConstellationName();
			this.onConstruction.solarSystemId = ((SpaceSystem) location).getSolarSystemId();
			this.onConstruction.solarSystemName = ((SpaceSystem) location).getSolarSystemName();
			this.onConstruction.securityClass = ((SpaceSystem) location).getSecurityClass();
			this.onConstruction.securityStatus = ((SpaceSystem) location).getSecurityStatus();
			this.onConstruction.stationId = Math.toIntExact( ((Station) location).getStationId() );
			this.onConstruction.stationName = ((Station) location).getStationName();
			return this.onConstruction;
		}

		public SpaceLocationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellationName = constellation.getName();
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public SpaceLocationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.regionName = region.getName();
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public SpaceLocationImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.solarSystemName = solarSystem.getName();
			this.onConstruction.solarSystemId = solarSystem.getSystemId();
			this.onConstruction.securityClass = solarSystem.getSecurityClass();
			this.onConstruction.securityStatus = solarSystem.getSecurityStatus();
			return this;
		}

		public SpaceLocationImplementation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.stationName = station.getName();
			this.onConstruction.stationId = station.getStationId();
			return this;
		}
	}
}
