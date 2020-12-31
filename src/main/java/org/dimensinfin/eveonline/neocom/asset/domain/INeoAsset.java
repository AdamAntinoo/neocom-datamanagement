package org.dimensinfin.eveonline.neocom.asset.domain;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;

public interface INeoAsset extends ICollaboration {
	Long getAssetId();

	LocationIdentifier getLocationId();

	boolean isContainer();

	boolean isShip();

	boolean isOffice();

	Long getParentContainerId();

	boolean hasParentContainer();
}
