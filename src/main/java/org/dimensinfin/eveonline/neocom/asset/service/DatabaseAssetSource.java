package org.dimensinfin.eveonline.neocom.asset.service;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.asset.domain.IAssetSource;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;

public class DatabaseAssetSource implements IAssetSource {
	private DatabaseAssetSource() {}

	@Override
	public List<NeoAsset> findAllByOwnerId( final Integer ownerId ) {
		return new ArrayList<>(  );
	}

	@Override
	public List<NeoAsset> findAllByCorporationId( final Integer ownerId ) {
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private DatabaseAssetSource onConstruction;

		public Builder() {
			this.onConstruction = new DatabaseAssetSource();
		}

		public DatabaseAssetSource build() {
			return this.onConstruction;
		}
	}
}
