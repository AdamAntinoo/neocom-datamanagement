package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.List;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;

public interface IAssetSource {
	List<NeoAsset> findAllByOwnerId( Integer ownerId );
	List<NeoAsset> findAllByCorporationId( Integer ownerId );
}
