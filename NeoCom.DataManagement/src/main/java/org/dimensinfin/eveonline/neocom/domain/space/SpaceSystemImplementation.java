package org.dimensinfin.eveonline.neocom.domain.space;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;

public class SpaceSystemImplementation extends NeoComNode implements SpaceSystem {
	private static final long serialVersionUID = -7621919707515211155L;
	private SpaceLocationImplementation spaceLocation;

	private SpaceSystemImplementation() {super();}

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
	public String getSolarSystemName() {return spaceLocation.getSolarSystemName();}

	// - B U I L D E R
	public static class Builder {
		private SpaceSystemImplementation onConstruction;
		private GetUniverseRegionsRegionIdOk region;
		private GetUniverseConstellationsConstellationIdOk constellation;
		private GetUniverseSystemsSystemIdOk solarSystem;

		public Builder() {
			this.onConstruction = new SpaceSystemImplementation();
		}

		public SpaceSystemImplementation.Builder withRegion( final GetUniverseRegionsRegionIdOk region ) {
			Objects.requireNonNull( region );
			this.region = region;
			return this;
		}

		public SpaceSystemImplementation.Builder withConstellation( final GetUniverseConstellationsConstellationIdOk constellation ) {
			Objects.requireNonNull( constellation );
			this.constellation = constellation;
			return this;
		}

		public SpaceSystemImplementation.Builder withSolarSystem( final GetUniverseSystemsSystemIdOk solarSystem ) {
			Objects.requireNonNull( solarSystem );
			this.solarSystem = solarSystem;
			return this;
		}

		public SpaceSystem build() {
			Objects.requireNonNull( this.region );
			Objects.requireNonNull( this.constellation );
			Objects.requireNonNull( this.solarSystem );
			this.onConstruction.spaceLocation = new SpaceLocationImplementation.Builder()
					.withRegion( this.region )
					.withConstellation( this.constellation )
					.withSolarSystem( this.solarSystem )
					.build();
			return this.onConstruction;
		}
	}
}
