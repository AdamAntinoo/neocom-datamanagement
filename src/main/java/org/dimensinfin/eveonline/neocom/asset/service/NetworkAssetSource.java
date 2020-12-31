package org.dimensinfin.eveonline.neocom.asset.service;

import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.domain.IAssetSource;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class NetworkAssetSource implements IAssetSource {
	// -  C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private CredentialRepository credentialRepository;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;

	private NetworkAssetSource() {}

	@Override
	public List<NeoAsset> findAllByOwnerId( final Integer ownerId ) {
		return null;
	}

	@Override
	public List<NeoAsset> findAllByCorporationId( final Integer corporationId ) {
		return new AssetDownloadProcessorJob.Builder()
				.withCredential( this.credential )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.build()
				.downloadCorporationAssets( corporationId );
	}

	// - B U I L D E R
	public static class Builder {
		private NetworkAssetSource onConstruction;

		public Builder() {
			this.onConstruction = new NetworkAssetSource();
		}

		public NetworkAssetSource build() {
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.esiDataProvider );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.assetRepository );
			Objects.requireNonNull( this.onConstruction.credentialRepository );
			return this.onConstruction;
		}

		public NetworkAssetSource.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.onConstruction.assetRepository = assetRepository;
			return this;
		}

		public NetworkAssetSource.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public NetworkAssetSource.Builder withCredentialRepository( final CredentialRepository credentialRepository ) {
			Objects.requireNonNull( credentialRepository );
			this.onConstruction.credentialRepository = credentialRepository;
			return this;
		}

		public NetworkAssetSource.Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public NetworkAssetSource.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}
	}
}
