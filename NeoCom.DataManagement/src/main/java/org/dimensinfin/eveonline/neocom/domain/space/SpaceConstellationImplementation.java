package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

@Deprecated
public class SpaceConstellationImplementation extends NeoComNode implements SpaceConstellation {
	private static final long serialVersionUID = -9105742282576579945L;
	private SpaceLocationImplementation spaceLocation;

	private SpaceConstellationImplementation() {super();}

	// - D E L E G A T E S
	@Override
	public Long getLocationId() {return spaceLocation.getLocationId();}

	@Override
	public LocationIdentifierType getLocationType() {
		return null;
	}

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

	// - B U I L D E R
	public static class Builder {
		private SpaceConstellationImplementation onConstruction;
		private GetUniverseRegionsRegionIdOk region;
		private GetUniverseConstellationsConstellationIdOk constellation;

		public Builder() {
			this.onConstruction = new SpaceConstellationImplementation();
		}

		public SpaceConstellationImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.region = region;
			return this;
		}

		public SpaceConstellationImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.constellation = constellation;
			return this;
		}

		public SpaceConstellation build() {
			Objects.requireNonNull( this.region );
			Objects.requireNonNull( this.constellation );
			this.onConstruction.spaceLocation = new SpaceLocationImplementation.Builder()
					.withRegion( this.region )
					.withConstellation( this.constellation )
					.build();
			return this.onConstruction;
		}
	}
}
