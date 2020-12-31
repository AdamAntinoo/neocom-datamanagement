package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public class Ship extends NeoAssetAssetContainer {
	private Ship() {}

	// - B U I L D E R
	public static class Builder {
		private Ship onConstruction;

		public Builder() {
			this.onConstruction = new Ship();
		}

		public Ship build() {
			return this.onConstruction;
		}

		public Ship.Builder withFace( final NeoAsset asset ) {
			Objects.requireNonNull( asset );
			this.onConstruction.containerFace = asset;
			if (asset.isShip()) this.onConstruction.type = AssetContainerType.SHIP;
			else throw new NeoComRuntimeException( "The ships can only have facet assets of type SHIP." );
			return this;
		}
	}
}