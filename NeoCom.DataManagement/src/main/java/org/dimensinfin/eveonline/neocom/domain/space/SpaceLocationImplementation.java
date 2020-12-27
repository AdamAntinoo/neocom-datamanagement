package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public class SpaceLocationImplementation extends NeoComNode implements SpaceLocation {
	private static final long serialVersionUID = -9028958348146320642L;

	private Integer regionId;
	private String regionName;
	//	private GetUniverseRegionsRegionIdOk region;
	private Integer constellationId;
	private String constellationName;
	//	private GetUniverseConstellationsConstellationIdOk constellation;
	private Integer solarSystemId;
	private String solarSystemName;
	//	private GetUniverseSystemsSystemIdOk solarSystem;
	private Integer stationId;
	private String stationName;
	//	private GetUniverseStationsStationIdOk station;
	private Double security;
	private Integer corporationId;
	private GetCorporationsCorporationIdOk corporation;
	private Long structureId;
	private GetUniverseStructuresStructureIdOk structure;

	// - C O N S T R U C T O R S
	private SpaceLocationImplementation() {super();}

	// - G E T T E R S   &   S E T T E R S
	public Integer getConstellationId() {
		return this.constellationId;
	}

	//	public GetUniverseRegionsRegionIdOk getRegion() {
	//		return this.region;
	//	}

	public String getConstellationName() {return this.constellationName;}

	// - V I R T U A L
	public Long getLocationId() {
		if (null != this.stationId) return this.stationId.longValue();
		if (null != this.solarSystemId) return this.solarSystemId.longValue();
		if (null != this.constellationId) return this.constellationId.longValue();
		if (null != this.regionId) return this.regionId.longValue();
		throw new NeoComRuntimeException( "The SpaceLocation is invalid. There is not any of the minimum information." );
	}

	//	public GetUniverseConstellationsConstellationIdOk getConstellation() {
	//		return this.constellation;
	//	}

	public Integer getRegionId() {
		return this.regionId;
	}

	public String getRegionName() {return this.regionName;}

	//	public GetUniverseSystemsSystemIdOk getSolarSystem() {
	//		return this.solarSystem;
	//	}

	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	public String getSolarSystemName() {return this.solarSystemName;}

	//	public GetUniverseStationsStationIdOk getStation() {
	//		return this.station;
	//	}

	public Integer getStationId() {
		return this.stationId;
	}

	public String getStationName() {return this.stationName;}

	public GetUniverseStructuresStructureIdOk getStructure() {
		return this.structure;
	}

	public Long getStructureId() {
		return this.structureId;
	}

	public String getStructureName() {return this.structure.getName();}

	// - B U I L D E R
	public static class Builder {
		private SpaceLocationImplementation onConstruction;

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
			this.onConstruction.stationId = ((Station) location).getStationId();
			this.onConstruction.stationName = ((Station) location).getStationName();
			return this.onConstruction;
		}

		public SpaceLocationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellationName = constellation.getName();
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public SpaceLocationImplementation.Builder withCorporation( final Integer corporationId,
		                                                            final GetCorporationsCorporationIdOk corporation ) {
			Objects.requireNonNull( corporationId );
			Objects.requireNonNull( corporation );
			this.onConstruction.corporationId = corporationId;
			this.onConstruction.corporation = corporation;
			return this;
		}

		public SpaceLocationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.regionName = region.getName();
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public SpaceLocationImplementation.Builder withSecurity( final Double security ) {
			Objects.requireNonNull( security );
			this.onConstruction.security = security;
			return this;
		}

		public SpaceLocationImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.onConstruction.solarSystemName = solarSystem.getName();
			this.onConstruction.solarSystemId = solarSystem.getSystemId();
			return this;
		}

		public SpaceLocationImplementation.Builder withStation( final GetUniverseStationsStationIdOk station ) {
			Objects.requireNonNull( station );
			this.onConstruction.stationName = station.getName();
			this.onConstruction.stationId = station.getStationId();
			return this;
		}

		public SpaceLocationImplementation.Builder withStructure( final Long structureId,
		                                                          final GetUniverseStructuresStructureIdOk structure ) {
			Objects.requireNonNull( structureId );
			Objects.requireNonNull( structure );
			this.onConstruction.structureId = structureId;
			this.onConstruction.structure = structure;
			return this;
		}
	}
}
