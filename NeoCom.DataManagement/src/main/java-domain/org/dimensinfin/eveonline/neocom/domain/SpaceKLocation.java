package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

public class SpaceKLocation extends NeoComNode {
	private static final long serialVersionUID = -9028958348146320642L;
	protected Integer systemId;
	protected GetUniverseSystemsSystemIdOk system;
	protected Integer constellationId;
	protected GetUniverseConstellationsConstellationIdOk constellation;
	protected Integer regionId;
	protected GetUniverseRegionsRegionIdOk region;

	public Integer getRegionId() {
		return regionId;
	}

	public GetUniverseRegionsRegionIdOk getRegion() {
		return region;
	}

	// - B U I L D E R
	public static class Builder {
		private SpaceKLocation onConstruction;

		public Builder() {
			this.onConstruction = new SpaceKLocation();
		}

		public SpaceKLocation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.onConstruction.region = region;
			this.onConstruction.regionId = region.getRegionId();
			return this;
		}

		public SpaceKLocation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.onConstruction.constellation = constellation;
			this.onConstruction.constellationId = constellation.getConstellationId();
			return this;
		}

		public SpaceKLocation.Builder withSystem( final GetUniverseSystemsSystemIdOk system ) {
			Objects.requireNonNull( system );
			this.onConstruction.system = system;
			this.onConstruction.systemId = system.getSystemId();
			return this;
		}

		public SpaceKLocation build() {
			Objects.requireNonNull( this.onConstruction.region );
			Objects.requireNonNull( this.onConstruction.constellation );
			Objects.requireNonNull( this.onConstruction.system );
			return this.onConstruction;
		}
	}
}