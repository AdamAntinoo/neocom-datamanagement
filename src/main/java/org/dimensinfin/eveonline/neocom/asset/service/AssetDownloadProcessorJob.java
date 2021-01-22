package org.dimensinfin.eveonline.neocom.asset.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.dimensinfin.annotation.LogEnterExit;
import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.asset.converter.EsiAssets200Ok2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2EsiAssets200OkConverter;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersCharacterIdAsset2NeoAssetConverter;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCorporationsCorporationAsset2EsiAssets200OkConverter;
import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCorporationsCorporationIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;
import org.dimensinfin.logging.LogWrapper;

public class AssetDownloadProcessorJob extends Job {
	private final Map<Long, NeoAsset> convertedAssetList = new HashMap<>();
	// - I N T E R N A L   W O R K   F I E L D S
	private Map<Long, EsiAssets200Ok> assetsMap = new HashMap<>();
	private double miningResourceValue = 0.0;
	// -  C O M P O N E N T S
	private Credential credential;
	private AssetRepository assetRepository;
	private CredentialRepository credentialRepository;
	private ESIDataService esiDataProvider;
	private LocationCatalogService locationCatalogService;

// - C O N S T R U C T O R S
	private AssetDownloadProcessorJob() {super();}

// - G E T T E R S   &   S E T T E R S
	// - J O B
	@Override
	public int getUniqueIdentifier() {
		return new HashCodeBuilder( 97, 137 )
				.append( this.credential.getUniqueCredential() )
				.append( this.credential.getAccountName() )
				.append( this.getClass().getSimpleName() )
				.toHashCode();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( credential )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final AssetDownloadProcessorJob that = (AssetDownloadProcessorJob) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.credential, that.credential )
				.isEquals();
	}

	/**
	 * Download the list of assets that belong to a character or corporation and process their location references while
	 * converting to the application data version.
	 *
	 * @return true if the process completes successfully.
	 */
	@TimeElapsed
	public Boolean call() throws Exception {
		return this.processCharacterAssets();
	}

	@LogEnterExit
	protected List<NeoAsset> downloadCorporationAssets( final Integer corporationId ) {
		this.convertedAssetList.clear();
		// TODO - Add the code to connect the office names to the office assets.
		//		this.corporationDivisions = this.esiDataProvider.getCorporationsCorporationIdDivisions( corporationId, this.credential );
		//		Objects.requireNonNull( this.corporationDivisions );
		final List<GetCorporationsCorporationIdAssets200Ok> assetOkList = this.esiDataProvider
				.getCorporationsCorporationIdAssets( this.credential, corporationId );
		this.assetsMap = this.transformCorporation200OkAssets( assetOkList );
		for (final EsiAssets200Ok assetOk : this.assetsMap.values()) {
			// - A S S E T   P R O C E S S I N G
			try {
				// Convert the asset from the OK format to a MVC compatible structure.
				final NeoAsset targetAsset = new EsiAssets200Ok2NeoAssetConverter().convert( assetOk );
				targetAsset.setOwnerId( corporationId );
				// - L O C A T I O N   P R O C E S S I N G
				this.locationProcessing( targetAsset );
				// - U S E R   L A B E L
				if (targetAsset.isShip() || targetAsset.isContainer())
					targetAsset.setUserLabel( this.downloadCorporationAssetEveName( targetAsset.getAssetId() ) );

				convertedAssetList.put( targetAsset.getAssetId(), targetAsset );
			} catch (final RuntimeException rtex) {
				LogWrapper.error( ErrorInfoCatalog.RUNTIME_PROCESSING_ASSET.getErrorMessage( assetOk.getItemId().toString() ), rtex );
			}
		}
		for (final NeoAsset asset : this.convertedAssetList.values()) {
			// - P A R E N T   P R O C E S S I N G
			this.parentProcessing( asset );
		}
		return new ArrayList<>( convertedAssetList.values() );
	}

	/**
	 * This downloader will use the new ESI api to get access to the full list of assets for this character.
	 * Once the list is processed we should create an instance as close as possible to the older XML instances generated by the
	 * XML processing.
	 * That instance will then get stored at the database and then we should make the trick of asset replacing.
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the final list on the
	 * database will have the correct parent's hierarchy set up.
	 *
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
	 */
	@LogEnterExit
	protected List<NeoAsset> downloadPilotAssets() {
		final List<NeoAsset> results = new ArrayList<>();
		final List<GetCharactersCharacterIdAssets200Ok> assetOkList = this.esiDataProvider
				.getCharactersCharacterIdAssets( credential );
		this.assetsMap = this.transformCharacter200OkAssets( assetOkList );
		for (final GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
			// - A S S E T   P R O C E S S I N G
			try {
				// Convert the asset from the OK format to a MVC compatible structure.
				final NeoAsset targetAsset = new GetCharactersCharacterIdAsset2NeoAssetConverter().convert( assetOk );
				targetAsset.setOwnerId( this.credential.getAccountId() );
				// - L O C A T I O N   P R O C E S S I N G
				this.locationProcessing( targetAsset );
				results.add( targetAsset );
				// TODO - Complete the code to read the assets userLabel after all assets are processed and persisted.
			} catch (final RuntimeException rtex) {
				LogWrapper.error( "Processing asset: " + assetOk.getItemId().toString() + " - {}", rtex );
			}
		}
		return results;
	}

	/**
	 * Aggregates ids for some of the assets until it reached 10 and then posts and update for the whole batch.
	 */
	private String downloadCorporationAssetEveName( final long assetId ) {
		final List<Long> idList = new ArrayList<>();
		idList.add( assetId );
		final List<PostCorporationsCorporationIdAssetsNames200Ok> itemNames = this.esiDataProvider
				.postCorporationsCorporationIdAssetsNames( idList, this.credential );
		if (null != itemNames)
			for (final PostCorporationsCorporationIdAssetsNames200Ok name : itemNames)
				if (assetId == name.getItemId()) return name.getName();
		return null;
	}

	//	private String getDivisionName( final EsiAssets200Ok.LocationFlagEnum locationFlag,
	//	                                final List<GetCorporationsCorporationIdDivisionsOkHangar> hangarNames ) {
	//		return hangarNames.get( officeContainerLocationFlags.get( locationFlag ) ).getName();
	//	}

	private boolean isMiningResource( final NeoAsset asset2Test ) {
		if (asset2Test.getCategoryName().equalsIgnoreCase( "Asteroid" )) return true;
		if ((asset2Test.getCategoryName().equalsIgnoreCase( "Material" )) &&
				(asset2Test.getGroupName().equalsIgnoreCase( "Mineral" ))) return true;
		return false;
	}

	//	private boolean isOfficeContainerLocation( final NeoAsset target ) {
	//		return officeContainerLocationFlags.contains( target.getLocationId().getLocationFlag() );
	//	}

	private void locationProcessing( final NeoAsset targetAsset ) {
		try {
			// If the preliminary calculation returns UNKNOWN then search if the location is reachable.
			if (targetAsset.getLocationId().getType() == LocationIdentifierType.UNKNOWN) {
				final LocationIdentifier workLocationId = targetAsset.getLocationId();
				// Check if location is a user asset.
				if (this.assetsMap.containsKey( workLocationId.getSpaceIdentifier() )) {
					// SIDE EFFECTS. This is modifying the asset location.
					workLocationId.setType( LocationIdentifierType.CONTAINER );
					// SIDE EFFECTS. This is modifying the asset location.
				} else {
					// Check if the location is a public reachable structure.
					final SpaceLocation structure = this.locationCatalogService
							.searchStructure4Id( targetAsset.getLocationId().getSpaceIdentifier(),
									this.credential );
					if (null != structure) {
						// SIDE EFFECTS. This is modifying the asset location.
						// TODO-Review if this code should be uncommented.
						workLocationId.setType( LocationIdentifierType.STRUCTURE );
						//						workLocationId.set( workLocationId.getSpaceIdentifier() );
						// SIDE EFFECTS. This is modifying the asset location.
					}
				}
			}
		} catch (final RuntimeException rtex) {
			LogWrapper.error( rtex );
		}
	}

	private void parentProcessing( final NeoAsset targetAsset ) {
		// If the asset has a parent identifier, search for it on the asset map.
		if (targetAsset.hasParentContainer()) {
			final NeoAsset hit = this.convertedAssetList.get( targetAsset.getParentContainerId() );
			if (null != hit)
				targetAsset.setParentContainer( hit );
		}
	}

	private Boolean processCharacterAssets() {
		this.downloadPilotAssets();
		return true;
	}

	private boolean storeCharacterAssets() throws SQLException {
		final List<NeoAsset> results = new ArrayList<>();
		final List<GetCharactersCharacterIdAssets200Ok> assetOkList = this.esiDataProvider
				.getCharactersCharacterIdAssets( credential );
		if (null == assetOkList) return false;
		if (assetOkList.isEmpty()) return false;
		//		this.createPilotAssetMap( assetOkList ); // Map of asset for easy lookup.
		this.assetRepository.clearInvalidRecords( this.credential.getAccountId() );
		for (final GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
			// - A S S E T   P R O C E S S I N G
			try {
				// Convert the asset from the OK format to a MVC compatible structure.
				final NeoAsset targetAsset = new GetCharactersCharacterIdAsset2NeoAssetConverter().convert( assetOk );
				// TODO - Complete the code to read the assets userLabel after all assets are processed and persisted.
				//				if (targetAsset.isShip()) downloadAssetEveName( targetAsset.getAssetId() );
				//				if (targetAsset.isContainer()) downloadAssetEveName( targetAsset.getAssetId() );
				// Mark the asset owner to the work in progress value.
				targetAsset.setOwnerId( this.credential.getAccountId() * -1 );
				// Do asset calculations like add the value is a mining resource.
				if (this.isMiningResource( targetAsset ))
					this.miningResourceValue += targetAsset.getQuantity() * targetAsset.getPrice();

				// - L O C A T I O N   P R O C E S S I N G
				this.locationProcessing( targetAsset );
				// With assets separate the update from the creation because they use a generated unique key.
				this.assetRepository.persist( targetAsset );
			} catch (final SQLException | RuntimeException sqle) {
				LogWrapper.error( "Processing asset: " + assetOk.getItemId().toString() + " - {}", sqle );
			}
		}
		// - O R P H A N   L O C A T I O N   A S S E T S
		// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
		//		for (NeoComAsset asset : this.unlocatedAssets) {
		//			this.validateLocation( asset );
		//		}
		// Assign the assets to the pilot.
		this.assetRepository.replaceAssets( this.credential.getAccountId() );
		// Update the mining value on the Credential.
		this.credential.setMiningResourcesEstimatedValue( this.miningResourceValue );
		this.credentialRepository.persist( this.credential );
		LogWrapper.exit();
		return true;
	}

	private Map<Long, EsiAssets200Ok> transformCharacter200OkAssets( final List<GetCharactersCharacterIdAssets200Ok> assetOkList ) {
		final Map<Long, EsiAssets200Ok> transformedAssets = new HashMap<>();
		for (GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
			final EsiAssets200Ok esiAsset = new GetCharactersCharacterIdAsset2EsiAssets200OkConverter().convert( assetOk );
			transformedAssets.put( esiAsset.getItemId(), esiAsset );
		}
		return transformedAssets;
	}

	private Map<Long, EsiAssets200Ok> transformCorporation200OkAssets( final List<GetCorporationsCorporationIdAssets200Ok> assetOkList ) {
		final Map<Long, EsiAssets200Ok> transformedAssets = new HashMap<>();
		for (GetCorporationsCorporationIdAssets200Ok assetOk : assetOkList) {
			final EsiAssets200Ok esiAsset = new GetCorporationsCorporationAsset2EsiAssets200OkConverter().convert( assetOk );
			transformedAssets.put( esiAsset.getItemId(), esiAsset );
		}
		return transformedAssets;
	}

	// - B U I L D E R
	public static class Builder extends Job.Builder<AssetDownloadProcessorJob, AssetDownloadProcessorJob.Builder> {
		private AssetDownloadProcessorJob onConstruction;

		public AssetDownloadProcessorJob.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.getActual().assetRepository = assetRepository;
			return this;
		}

		public AssetDownloadProcessorJob.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.getActual().credential = credential;
			return this;
		}

		public AssetDownloadProcessorJob.Builder withCredentialRepository( final CredentialRepository credentialRepository ) {
			Objects.requireNonNull( credentialRepository );
			this.getActual().credentialRepository = credentialRepository;
			return this;
		}

		public AssetDownloadProcessorJob.Builder withEsiDataProvider( final ESIDataService esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public AssetDownloadProcessorJob.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		@Override
		protected AssetDownloadProcessorJob getActual() {
			if (null == this.onConstruction) this.onConstruction = new AssetDownloadProcessorJob();
			return this.onConstruction;
		}

		@Override
		protected AssetDownloadProcessorJob.Builder getActualBuilder() {
			return this;
		}

		public AssetDownloadProcessorJob build() {
			final AssetDownloadProcessorJob instance = super.build();
			Objects.requireNonNull( instance.credential );
			Objects.requireNonNull( instance.esiDataProvider );
			Objects.requireNonNull( instance.locationCatalogService );
			Objects.requireNonNull( instance.assetRepository );
			return instance;
		}
	}
}
