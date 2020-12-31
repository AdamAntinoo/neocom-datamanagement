package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public class LocationAssetContainer extends ExpandableContainer<INeoAsset> {
	private AssetContainerType type = AssetContainerType.SPACE;
	private SpaceLocation spaceLocation;

	protected LocationAssetContainer() {super();}

	public AssetContainerType getType() {
		return this.type;
	}

	public SpaceLocation getSpaceLocation() {
		return this.spaceLocation;
	}

	// - B U I L D E R
	public static class Builder {
		private LocationAssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new LocationAssetContainer();
		}

		public LocationAssetContainer build() {
			return this.onConstruction;
		}
		public LocationAssetContainer.Builder withSpaceLocation( final SpaceLocation spaceLocation ) {
			Objects.requireNonNull( spaceLocation );
			this.onConstruction.spaceLocation = spaceLocation;
			this.onConstruction.type = AssetContainerType.SPACE;
			return this;
		}
	}
}
