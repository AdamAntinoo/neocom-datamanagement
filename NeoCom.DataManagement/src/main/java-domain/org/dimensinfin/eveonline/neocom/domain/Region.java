package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

/**
 * Reduce the exported data reflected by the SpaceK class. All dta returned is only the Region data.
 */
public class Region extends SpaceKLocation {
	private static final long serialVersionUID = 3623925848703776069L;
//	private List<SpaceKLocation> locations = new ArrayList<>();

	public Integer getConstellationId() {
		return this.regionId;
	}

	public Integer getSystemId() {
		return this.regionId;
	}

//	public int addLocation( final SpaceKLocation location ) {
//		this.locations.add( location );
//		return this.locations.size();
//	}

	// - B U I L D E R
	public static class Builder {
		private Region onConstruction;

		public Builder() {
			this.onConstruction = new Region();
		}

		public Region.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}
		public Region build() {
			Objects.requireNonNull( this.onConstruction.region );
			return this.onConstruction;
		}
	}
}