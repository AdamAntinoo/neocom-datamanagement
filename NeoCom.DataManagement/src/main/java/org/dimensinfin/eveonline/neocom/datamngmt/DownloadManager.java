//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.core.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.Job;
import org.dimensinfin.eveonline.neocom.database.entity.MarketOrder;
import org.dimensinfin.eveonline.neocom.database.entity.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.PostCharactersCharacterIdAssetsNames200Ok;
import org.dimensinfin.eveonline.neocom.interfaces.ILocatableAsset;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class DownloadManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("DownloadManager");

	// - F I E L D - S E C T I O N ............................................................................
	private transient Credential credential = null;
	private final List<NeoComAsset> unlocatedAssets = new ArrayList<>();
	private final List<NeoComBlueprint> unlocatedBlueprints = new ArrayList<>();
	private transient final List<Long> id4Names = new ArrayList<>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DownloadManager() {
		super();
	}

	public DownloadManager( final Credential credential ) {
		this();
		this.credential = credential;
		// Preload the dao.
		//		try {
		//			assetDao = new GlobalDataManager().getNeocomDBHelper().getAssetDao();
		//		} catch (SQLException sqle) {
		//			sqle.printStackTrace();
		//		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// - A S S E T S

	/**
	 * This downloader will use the new ESI api to get access to the full list of assets for this character.
	 * Once the list is processed we should create an instance as close as possible to the older XML
	 * instances generated by the XML processing.<br>
	 * That instance will then get stored at the database and then we should make the trick of asset
	 * replacing.<br>
	 * The new processing will filter the assets with Unknown locations for a second pass processing so the
	 * final list on the database will have the correct parentship hierarchy set up.<br>
	 * <br>
	 * The assets downloaded are being written to a special set of records in the User database with an special
	 * <code>ownerid</code> so we can work with a new set of records for an specific Character without
	 * disturbing the access to the old asset list for the same Character. After all the assets are processed
	 * and stored in the database we remove the old list and replace the owner of the new list to the right one.<br>
	 */
	public boolean downloadPilotAssetsESI() {
		DownloadManager.logger.info(">> [AssetsManager.downloadPilotAssetsESI]");
		try {
			// Clear any previous record with owner -1 from database.
			new GlobalDataManager().getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
			// Download the list of assets.
			final List<GetCharactersCharacterIdAssets200Ok> assetOkList = ESINetworkManager.getCharactersCharacterIdAssets(
					credential.getAccountId(), credential.getRefreshToken(), null
			);
			if ( (null == assetOkList) || (assetOkList.size() < 1) ) return false;
			// Create the list for orphaned locations assets. They should be processed later.
			this.unlocatedAssets.clear();
			// Assets may be parent of other assets so process them recursively if the hierarchical mode is selected.
			for (final GetCharactersCharacterIdAssets200Ok assetOk : assetOkList) {
				//--- A S S E T   P R O C E S S I N G
				try {
					// Convert the asset from the OK format to a MVC compatible structure.
					final NeoComAsset myasset = this.convert2AssetFromESI(assetOk);
					if ( myasset.getCategoryName().equalsIgnoreCase("Ship") ) {
						myasset.setShip(true);
					}
					if ( myasset.getCategoryName().equalsIgnoreCase("Blueprint") ) {
						myasset.setBlueprintType(assetOk.getQuantity());
					}
					if ( myasset.isShip() ) {
						downloadAssetEveName(myasset.getAssetId());
					}
					if ( myasset.isContainer() ) {
						downloadAssetEveName(myasset.getAssetId());
					}
					// Mark the asset owner to the work in progress value.
					myasset.setOwnerId(credential.getAccountId() * -1);
					// With assets separate the update from the creation because they use a generated unique key.
					new GlobalDataManager().getNeocomDBHelper().getAssetDao().create(myasset);
					DownloadManager.logger.info("-- Wrote asset to database id [" + myasset.getAssetId() + "]");

					//--- L O C A T I O N   P R O C E S S I N G
					// Check the asset location. The location can be a known game station, a known user structure, another asset
					// or an unknown player structure. Check which one is this location.
					EveLocation targetLoc = new GlobalDataManager().searchLocation4Id(myasset.getLocationId());
					if ( targetLoc.getTypeId() == ELocationType.UNKNOWN ) {
						// Add this asset to the list of items to be reprocessed.
						this.unlocatedAssets.add(myasset);
					}
				} catch (final RuntimeException rtex) {
					DownloadManager.logger.info("RTEX ´[AssetsManager.downloadPilotAssetsESI]> Processing asset: {} - {}"
							, assetOk.getItemId(), rtex.getMessage());
					rtex.printStackTrace();
				}
			}
			//--- O R P H A N   L O C A T I O N   A S S E T S
			// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
			for (NeoComAsset asset : this.unlocatedAssets) {
				this.validateLocation(asset);
			}
			// Assign the assets to the pilot.
			new GlobalDataManager().getNeocomDBHelper().replaceAssets(credential.getAccountId());
			// Remove from memory the managers that contain now stale data.
			//TODO Removed until this is checked if required.
			//			GlobalDataManager.dropAssetsManager(credential.getAccountId());
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadPilotAssetsESI]");
		return true;
	}

	/**
	 * The new reingeneered blueprint download should behave as the assets download. Now blueprints are not listed under the
	 * assets api but at the character and generate a new paged list of the blueprints. As the new list of assets the blueprint
	 * has two location fields, una is the location identifier and the other is the flag that tells which type of locations this
	 * identifier represents.
	 *
	 * Once the initial list of blueprints is processed, we should do two new actions, the first is to replace the location by a
	 * real location running up the parent hierarchy list and setting the location identifier as the parent container parent. The
	 * second action is to pack the blueprints of identical characteristics and at the same location under a unique record, but
	 * keeping the list of former blueprint records to be able to reconstruct the original list. To do this we generate a
	 * blueprint stack identifier and merge all the blueprints with the same stack identifier into a single database record.
	 *
	 * The download method will also precalculate the Industrial indexes for the blueprint stack and store the result into the
	 * database record.
	 * @return
	 */
	public boolean downloadPilotBlueprintsESI() {
		DownloadManager.logger.info(">> [AssetsManager.downloadPilotBlueprintsESI]");
		try {
			// Clear any previous record with owner -1 from database.
			new GlobalDataManager().getNeocomDBHelper().clearInvalidRecords(credential.getAccountId());
			// Download the list of blueprints.
			final List<GetCharactersCharacterIdBlueprints200Ok> blueprintOkList = ESINetworkManager
					.getCharactersCharacterIdBlueprints(
							credential.getAccountId()
							, credential.getRefreshToken()
							, null
					);
			if ( (null == blueprintOkList) || (blueprintOkList.size() < 1) ) return false;
			//			// Create the list for orphaned locations assets. They should be processed later.
			this.unlocatedBlueprints.clear();
			final List<NeoComBlueprint> bplist = new ArrayList<NeoComBlueprint>();
			for (final GetCharactersCharacterIdBlueprints200Ok blueprintOk : blueprintOkList) {
				//--- B L U E P R I N T   P R O C E S S I N G
				NeoComBlueprint newBlueprint = null;
				try {
					// Convert the blueprint from the OK format to a MVC compatible structure.
					newBlueprint = new NeoComBlueprint(blueprintOk.getItemId(), blueprintOk.getTypeId())
							//							.setTypeId(blueprintOk.getTypeId())
							.setLocationId(blueprintOk.getLocationId())
							.setLocationFlag(blueprintOk.getLocationFlag())
							.setQuantity(blueprintOk.getQuantity())
							.setTimeEfficiency(blueprintOk.getTimeEfficiency())
							.setMaterialEfficiency(blueprintOk.getMaterialEfficiency())
							.setRuns(blueprintOk.getRuns())
							.setPackaged((blueprintOk.getQuantity() < 0) ? true : false);
					bplist.add(newBlueprint);
					// Detect if BPO or BPC and set the flag.
					if ( blueprintOk.getRuns() == -1 ) {
						newBlueprint.setBpo(true);
					}
					// Mark the asset owner to the work in progress value.
					newBlueprint.setOwnerId(this.credential.getAccountId() * -1);
					// With blueprints separate the update from the creation because they use a generated unique key.
					new GlobalDataManager().getNeocomDBHelper().getBlueprintDao().create(newBlueprint);
					DownloadManager.logger.info("-- Wrote blueprint to database id [" + newBlueprint.getBlueprintId() + "]");

					//--- L O C A T I O N   P R O C E S S I N G
					// Check the blueprint location. The location can be a known game station, a known user structure, another asset
					// or an unknown player structure. Check which one is this location.
					EveLocation targetLoc = new GlobalDataManager().searchLocation4Id(newBlueprint.getLocationId());
					if ( targetLoc.getTypeId() == ELocationType.UNKNOWN ) {
						// Add this blueprint to the list of items to be reprocessed.
						this.unlocatedBlueprints.add(newBlueprint);
					}
				} catch (final RuntimeException rtex) {
					DownloadManager.logger.info("RTEX ´[AssetsManager.downloadPilotBlueprintsESI]> Processing blueprint: {} - {}"
							, newBlueprint.getBlueprintId(), rtex.getMessage());
					rtex.printStackTrace();
				}
			}
			//--- O R P H A N   L O C A T I O N   A S S E T S
			// Second pass. All the assets in unknown locations should be readjusted for hierarchy changes.
			for (NeoComBlueprint blu : this.unlocatedBlueprints) {
				this.validateLocation(blu);
			}

			// Pack the blueprints and store them on the database.
			storeBlueprints(bplist);
			// Assign the blueprints to the pilot.
			new GlobalDataManager().getNeocomDBHelper().replaceBlueprints(credential.getAccountId());
			// Remove from memory the managers that contain now stale data.
			//TODO Removed until this is checked if required.
			//			GlobalDataManager.dropAssetsManager(credential.getAccountId());
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		DownloadManager.logger.info("<< [AssetsManager.downloadPilotBlueprintsESI]");
		return true;
	}

	// - I N D U S T R Y
	public void downloadPilotJobsESI() {
		DownloadManager.logger.info(">> [DownloadManager.downloadPilotJobsESI]");
		try {
			List<Job> jobsList = GlobalDataManager.downloadIndustryJobs4Credential(credential);
		} finally {
			DownloadManager.logger.info("<< [DownloadManager.downloadPilotJobsESI]");
		}
	}

	/**
	 * Mining actions are small records that register the ore collected by a Pilot or the Moon mining done by a Corporation. The time base
	 * is 10 minutes and I suppose that those records are aggregated during a day. The data is a list of entries, each one declaring the
	 * quantity of one ore mined on a date and related to a single star system.
	 *
	 * The processing algoritm should add the capturing hour of day to the search index and update with the current value or create a new
	 * record when the index matches. The <b>id</b> of the record is changed to an string having the next fields:
	 * YEAR/MONTH/DAY:HOUR-TYPEID-SYSTEMID-OWNERID.
	 * Once one record if found instead storing on the database a single record per day/item/system/owner we store this same data for each
	 * hour until the date changes and then we stop processing entries until we have new current mining extractions.
	 */
	public List<MiningExtraction> downloadPilotMiningActionsESI() {
		DownloadManager.logger.info(">> [DownloadManager.downloadPilotMiningActionsESI]");
		List<MiningExtraction> oreExtractions = new ArrayList<>();
		try {
			final Dao<MiningExtraction, String> miningDao = new GlobalDataManager().getNeocomDBHelper().getMiningExtractionDao();
			// Get to the Network and download the data from the ESI api.
			final List<GetCharactersCharacterIdMining200Ok> miningActionsOk = ESINetworkManager.getCharactersCharacterIdMining(credential.getAccountId()
					, credential.getRefreshToken()
					, GlobalDataManager.SERVER_DATASOURCE);
			if ( null != miningActionsOk ) {
				// Process the data and convert it to structures compatible with MVC.
				for (GetCharactersCharacterIdMining200Ok extractionOk : miningActionsOk) {
					// Before doing any store of the data, see if this is a delta. Search for an already existing record.
					final String recordId = MiningExtraction.generateRecordId(extractionOk.getDate(), extractionOk.getTypeId()
							, extractionOk.getSolarSystemId(), credential.getAccountId());
					MiningExtraction recordFound = null;
					try {
						recordFound = new GlobalDataManager().getNeocomDBHelper().getMiningExtractionDao().queryForId(recordId);
					} catch (NeoComRuntimeException nrex) {
						logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Credential not found in the list. Exception: {}"
								, nrex.getMessage());
					}
					// If we found and exact record then we can update the value that can have changed or not.
					if ( null != recordFound ) {
						recordFound.setQuantity(extractionOk.getQuantity());
						logger.info("-- [DownloadManager.downloadPilotMiningActionsESI]> Updating mining extraction: {}"
								, recordId);
					} else {
						final MiningExtraction newExtraction = new MiningExtraction()
								.setTypeId(extractionOk.getTypeId())
								.setSolarSystemId(extractionOk.getSolarSystemId())
								.setExtractionDate(extractionOk.getDate())
								.setQuantity(extractionOk.getQuantity())
								.setOwnerId(credential.getAccountId())
								.create(recordId);
						logger.info("-- [DownloadManager.downloadPilotMiningActionsESI]> Creating new mining extraction: {}"
								, recordId);
					}
				}
			}
		} catch (SQLException sqle) {
			logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Credential not found in the list. Exception: {}"
					, sqle.getMessage());
		} catch (NeoComRuntimeException nrex) {
			logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Credential not found in the list. Exception: {}"
					, nrex.getMessage());
		} catch (RuntimeException ntex) {
			logger.info("EX [DownloadManager.downloadPilotMiningActionsESI]> Mapping error - {}"
					, ntex.getMessage());
		} finally {
			DownloadManager.logger.info("<< [DownloadManager.downloadPilotMiningActionsESI]");
			return oreExtractions;
		}
	}

	// - M A R K E T   O R D E R S
	public void downloadPilotMarketOrdersESI() {
		DownloadManager.logger.info(">> [DownloadManager.downloadPilotMarketOrdersESI]");
		try {
			List<MarketOrder> ordersList = GlobalDataManager.downloadMarketOrders4Credential(credential);
			ordersList = GlobalDataManager.downloadMarketOrdersHistory4Credential(credential);
		} finally {
			DownloadManager.logger.info("<< [DownloadManager.downloadPilotMarketOrdersESI]");
		}
	}

	// --- P R I V A T E   M E T H O D S
	private NeoComAsset convert2AssetFromESI( final GetCharactersCharacterIdAssets200Ok asset200Ok ) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset(asset200Ok.getTypeId())
				.setAssetId(asset200Ok.getItemId());
		// TODO -- Location management is done ourside this transormation. This is duplicated code.
		Long locid = asset200Ok.getLocationId();
		if ( null == locid ) {
			locid = (long) -2;
		}
		newAsset.setLocationId(locid)
		        .setLocationType(asset200Ok.getLocationType())
		        .setQuantity(asset200Ok.getQuantity())
		        .setLocationFlag(asset200Ok.getLocationFlag())
		        .setSingleton(asset200Ok.getIsSingleton());
		// Get access to the Item and update the copied fields.
		final EveItem item = new GlobalDataManager().searchItem4Id(newAsset.getTypeId());
		if ( null != item ) {
			//			try {
			newAsset.setName(item.getName());
			newAsset.setCategory(item.getCategoryName());
			newAsset.setGroupName(item.getGroupName());
			newAsset.setTech(item.getTech());
			//				if (item.isBlueprint()) {
			//					//			newAsset.setBlueprintType(eveAsset.getRawQuantity());
			//				}
			//			} catch (RuntimeException rtex) {
			//			}
		}
		// Add the asset value to the database.
		newAsset.setIskValue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	private synchronized double calculateAssetValue( final NeoComAsset asset ) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if ( null != asset ) {
			EveItem item = asset.getItem();
			if ( null != item ) {
				String category = item.getCategoryName();
				String group = item.getGroupName();
				if ( null != category ) if ( !category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint) ) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = 0.0;
					try {
						// First try to set the average market price. If it fails search for the market data.
						price = asset.getItem().getPrice();
						if ( price < 0 )
							price = asset.getItem().getHighestBuyerPrice().getPrice();
					} catch (ExecutionException ee) {
						price = asset.getItem().getPrice();
					} catch (InterruptedException ee) {
						price = asset.getItem().getPrice();
					}
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	/**
	 * Checks if the Location can be found on the two lists of Locations, the CCP game list or the player
	 * compiled list. If the Location can't be found on any of those lists then it can be another asset
	 * (Container, Ship, etc) or another player/corporation structure resource that is not listed on the asset
	 * list.
	 */
	private ELocationType validateLocation( final ILocatableAsset locatable ) {
		long targetLocationid = locatable.getLocationId();
		EveLocation targetLoc = new GlobalDataManager().searchLocation4Id(targetLocationid);
		if ( targetLoc.getTypeId() == ELocationType.UNKNOWN ) {
			try {
				// Need to check if asset or unreachable location. Search for asset with locationid.
				List<NeoComAsset> targetList = new GlobalDataManager().getNeocomDBHelper().getAssetDao()
				                                                      .queryForEq("assetId", Long.valueOf(targetLocationid));
				NeoComAsset target = null;
				if ( targetList.size() > 0 ) target = targetList.get(0);
				if ( null == target )
					return ELocationType.UNKNOWN;
				else {
					// Change the asset parentship and update the asset location with the location of the parent.
					locatable.setParentId(targetLocationid);

					// Search recursively on the parentship chain until a leaf is found. Then check that location.
					long parentIdentifier = target.getParentContainerId();
					while (parentIdentifier != -1) {
						validateLocation(target);
						targetList = new GlobalDataManager().getNeocomDBHelper().getAssetDao()
						                                    .queryForEq("assetId", Long.valueOf(parentIdentifier));
						if ( targetList.size() > 0 ) target = targetList.get(0);
						parentIdentifier = target.getParentContainerId();
					}
					// Now target contains a parent with parentship -1.
					// Set to this asset the parent location whichever it is.
					locatable.setLocationId(target.getLocationId());
					locatable.setLocationType(target.getLocationType());
					locatable.setLocationFlag(target.getFlag());
					locatable.store();
					return target.getLocation().getTypeId();
				}
			} catch (SQLException sqle) {
				return ELocationType.UNKNOWN;
			}
		} else
			return targetLoc.getTypeId();
	}

	/**
	 * Aggregates ids for some of the assets until it reached 10 and then posts and update for the whole batch.
	 */
	private void downloadAssetEveName( final long assetId ) {
		this.id4Names.add(assetId);
		if ( this.id4Names.size() > 9 ) {
			postUserLabelNameDownload();
			this.id4Names.clear();
		}
	}

	private void postUserLabelNameDownload() {
		// Launch the download of the names block.
		final List<Long> idList = new ArrayList<>();
		idList.addAll(id4Names);
		GlobalDataManager.submitJob2Download(() -> {
			// Copy yhe list of assets to local to allow parallel use.
			final List<Long> localIdList = new ArrayList<>();
			localIdList.addAll(idList);
			try {
				final List<PostCharactersCharacterIdAssetsNames200Ok> itemNames = ESINetworkManager.postCharactersCharacterIdAssetsNames(credential.getAccountId(), localIdList, credential.getRefreshToken(), null);
				for (final PostCharactersCharacterIdAssetsNames200Ok name : itemNames) {
					final List<NeoComAsset> assetsMatch = new GlobalDataManager().getNeocomDBHelper().getAssetDao().queryForEq("assetId",
							name.getItemId());
					for (NeoComAsset asset : assetsMatch) {
						logger.info("-- [DownloadManager.downloadAssetEveName]> Setting UserLabel name {} for asset {}.", name
								.getName(), name.getItemId());
						asset.setUserLabel(name.getName())
						     .store();
					}
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		});
	}

	/**
	 * Gets the list of blueprints from the API processor and packs them into stacks aggregated by some keys.
	 * This will simplify the quantity of data exported to presentation layers.<br>
	 * Aggregation is performed by TYPEID-LOCATION-CONTAINER-RUNS
	 * @param bplist list of newly created Blueprints from the CCP API download
	 */
	protected void storeBlueprints( final List<NeoComBlueprint> bplist ) {
		logger.info(">> [DownloadManager.storeBlueprints]");
		HashMap<String, NeoComBlueprint> bpStacks = new HashMap<String, NeoComBlueprint>();
		for (NeoComBlueprint blueprint : bplist) {
			this.checkBPCStacking(bpStacks, blueprint);
		}

		// Extract stacks and store them into the caches.
		//		blueprintCache.addAll(bpStacks.values());
		// Update the database information.
		try {
			final Dao<NeoComBlueprint, String> blueprintDao = new GlobalDataManager().getNeocomDBHelper().getBlueprintDao();
			for (NeoComBlueprint blueprint : bpStacks.values()) {
				try {
					// Set new calculated values to reduce the time for blueprint part rendering.
					// TODO - This has to be rewrite to allow this calculation on download time.
					//									IJobProcess process = JobManager.generateJobProcess(this.credential, blueprint, EJobClasses.MANUFACTURE);
					//									blueprint.setManufactureIndex(process.getProfitIndex());
					//									blueprint.setJobProductionCost(process.getJobCost());
					//									blueprint.setManufacturableCount(process.getManufacturableCount());
					blueprintDao.create(blueprint);
					DownloadManager.logger.info("-- [DownloadManager.storeBlueprints]> Wrote blueprint to database id [" + blueprint
							.getBlueprintId() + "]");
				} catch (final SQLException sqle) {
					DownloadManager.logger.error("E> [DownloadManager.storeBlueprints]> Unable to create the new blueprint [" + blueprint
							.getBlueprintId() + "]. "
							+ sqle.getMessage());
					sqle.printStackTrace();
				} catch (final RuntimeException rtex) {
					DownloadManager.logger.error("E> [DownloadManager.storeBlueprints]> Unable to create the new blueprint [" + blueprint
							.getBlueprintId() + "]. "
							+ rtex.getMessage());
					rtex.printStackTrace();
				}
			}
		} catch (final SQLException sqle) {
			//			DownloadManager.logger.error("E> [DownloadManager.storeBlueprints]> Unable to create the new blueprint [" + blueprint
			//					.getBlueprintId() + "]. "
			//					+ sqle.getMessage());
			sqle.printStackTrace();
		}
		logger.info("<< [DownloadManager.storeBlueprints]");
	}

	/**
	 * Stacks blueprints that are equal and that are located on the same location. The also should be inside the
	 * same container so the locationID, the parentContainer and the typeId should match to perform the
	 * aggregation.<br>
	 * Aggregation key: ID-LOCATION-CONTAINER
	 * @param targetContainer the stack storage that contains the list of registered blueprints
	 * @param bp              the blueprint part to be added to the hierarchy
	 */
	private void checkBPCStacking( final HashMap<String, NeoComBlueprint> targetContainer, final NeoComBlueprint bp ) {
		// If the blueprint is a BPO then do not stack.
		if ( bp.isBpo() ) targetContainer.put(Long.valueOf(bp.getBlueprintId()).toString(), bp);
		else {
			// Get the unique identifier for a blueprint related to stack aggregation. TYPEID.LOCATIONID.RUNS
			String id = bp.getStackId();
			NeoComBlueprint hit = targetContainer.get(id);
			if ( null == hit ) {
				// Miss. The blueprint is not registered.
				DownloadManager.logger.info("-- [DownloadManager.checkBPCStacking]> Stacking blueprint {} into {}"
						, bp.getBlueprintId(), id);
				bp.registerReference(bp.getBlueprintId());
				targetContainer.put(id, bp);
				// Count as 1.
				bp.setQuantity(1);
			} else {
				DownloadManager.logger.info("-- [DownloadManager.checkBPCStacking]> Stacking blueprint {} into {}"
						, bp.getBlueprintId(), id);
				// Hit. Increment the counter for this stack. And store the id
				hit.setQuantity(hit.getQuantity() + 1);
				hit.registerReference(bp.getBlueprintId());
			}
		}
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		if ( null != credential ) return new StringBuffer("DownloadManager [")
				.append("owner:").append(credential.getAccountId()).append(" ")
				.append("]")
				.append("->").append(super.toString())
				.toString();
		else return new StringBuffer("DownloadManager []").toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
