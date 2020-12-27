package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

public class SpaceRegionImplementation extends NeoComNode implements SpaceRegion {
	private static final long serialVersionUID = 3308318591307148073L;
	private SpaceLocationImplementation spaceLocation;

	private SpaceRegionImplementation() {super();}

	// - D E L E G A T E S
	@Override
	public Long getLocationId() {return spaceLocation.getLocationId();}

	@Override
	public Integer getRegionId() {return spaceLocation.getRegionId();}

//	@Override
//	public GetUniverseRegionsRegionIdOk getRegion() {return spaceLocation.getRegion();}

	@Override
	public String getRegionName() {return spaceLocation.getRegionName();}

	// - B U I L D E R
	public static class Builder {
		private SpaceRegionImplementation onConstruction;
		private GetUniverseRegionsRegionIdOk region;

		public Builder() {
			this.onConstruction = new SpaceRegionImplementation();
		}

		public SpaceRegionImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.region = region;
			return this;
		}

		public SpaceRegion build() {
			Objects.requireNonNull( this.region );
			this.onConstruction.spaceLocation = new SpaceLocationImplementation.Builder()
					.withRegion( this.region )
					.build();
			return this.onConstruction;
		}
	}
}
