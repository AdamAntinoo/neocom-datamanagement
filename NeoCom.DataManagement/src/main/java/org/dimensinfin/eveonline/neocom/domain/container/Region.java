package org.dimensinfin.eveonline.neocom.domain.container;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.asset.domain.LocationAssetContainer;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegion;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

public class Region extends ExpandableContainer<LocationAssetContainer> implements SpaceRegion {
	private static final long serialVersionUID = 6515264332647090482L;
	private Integer regionId;
	private GetUniverseRegionsRegionIdOk region;

	private Region() {}

	// - S P A C E R E G I O N
	@Override
	public Long getLocationId() {return this.getLocationId();}

	@Override
	public Integer getRegionId() {
		return this.regionId;
	}

//	@Override
//	public GetUniverseRegionsRegionIdOk getRegion() {
//		return this.region;
//	}

	@Override
	public String getRegionName() {
		return this.region.getName();
	}

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
