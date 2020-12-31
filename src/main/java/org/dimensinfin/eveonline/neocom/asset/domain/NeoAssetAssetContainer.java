package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.List;
import java.util.Objects;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;

public class NeoAssetAssetContainer extends ExpandableContainer<INeoAsset> implements INeoAsset {
	protected AssetContainerType type = AssetContainerType.CONTAINER;
	protected INeoAsset containerFace;

	protected NeoAssetAssetContainer() {super();}

	public AssetContainerType getType() {
		return this.type;
	}

	public INeoAsset getContainerFace() {
		return this.containerFace;
	}

	// - D E L E G A T E S
	@Override
	public Long getAssetId() {return containerFace.getAssetId();}

	@Override
	public LocationIdentifier getLocationId() {return this.containerFace.getLocationId();}

	@Override
	public boolean isContainer() {return this.containerFace.isContainer();}

	@Override
	public boolean isShip() {return this.containerFace.isShip();}

	@Override
	public boolean isOffice() {return this.containerFace.isOffice();}

	@Override
	public Long getParentContainerId() {return containerFace.getParentContainerId();}

	@Override
	public boolean hasParentContainer() {return this.containerFace.hasParentContainer();}

	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {return this.containerFace.collaborate2Model( variant );}

	@Override
	public int compareTo( final Object o ) {return this.containerFace.compareTo( o );}

	// - B U I L D E R
	public static class Builder {
		private NeoAssetAssetContainer onConstruction;

		public Builder() {
			this.onConstruction = new NeoAssetAssetContainer();
		}

		public NeoAssetAssetContainer build() {
			return this.onConstruction;
		}

		public NeoAssetAssetContainer.Builder withFace( final INeoAsset asset ) {
			Objects.requireNonNull( asset );
			this.onConstruction.containerFace = asset;
			if (asset.isOffice()) this.onConstruction.type = AssetContainerType.OFFICE;
			if (asset.isShip()) this.onConstruction.type = AssetContainerType.SHIP;
			if (asset.isContainer()) this.onConstruction.type = AssetContainerType.CONTAINER;
			return this;
		}
	}
}
