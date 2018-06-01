//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.industry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Blueprint;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

// - CLASS IMPLEMENTATION ...................................................................................
public class AbstractManufactureProcess {

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger									= LoggerFactory.getLogger("T2ManufactureProcess");
	private static long												GENERATED_ASSETCOUNTER	= 30001;
	private static final double								T2PE_LEVEL							= -4.0;
	private static final double								REFINING_EFFICIENCY			= 0.52;

	// - F I E L D - S E C T I O N ............................................................................
	/** The main element used for the manufacture job. */
	protected Blueprint blueprint								= null;
	/** The Pilot owner of the job and blueprint. Required to get the characterID. */
	protected EveChar													pilot										= null;
	/** New and locally used AssetsManager used to process the job requests. */
	protected AssetsManager										industryAssetsManager		= null;
	protected int															bpid										= -1;
	protected int															moduleid								= -1;
	protected ArrayList<Resource> lom											= null;
	protected double													cost										= -1.0;
	protected int															index										= -1;
	private boolean														totalcalculated					= false;
	protected int															totalManufacturable			= -1;

	// - A C T I O N   P R O C E S S I N G
	protected HashMap<Long, Property>					actions4Item						= null;
	protected EveLocation manufactureLocation			= null;
	protected String													region									= null;
	protected ArrayList<Resource>							requirements						= new ArrayList<Resource>();
	protected final HashMap<Integer, Action>	actionsRegistered				= new HashMap<Integer, Action>();
	protected Action													currentAction						= null;
	protected int															pointer									= -1;
	protected int															runs										= 10;
	protected int															threads									= 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractManufactureProcess(final AssetsManager manager) {
		super();
		if (null == manager) {
			this.industryAssetsManager = new AssetsManager(null);
		} else {
			this.industryAssetsManager = manager;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Calculates the minimum number of blueprints that can be manufactured with the resources stored at the
	 * blueprint location. It is supposed that the IndustryMananger has subtracted the pending jobs resources
	 * from those at the blueprint location prior to the calculations.
	 * 
	 * @param itemasset
	 * @return
	 */
	public int getManufacturableCount() {
		// Check for blueprints not related to assets. They are PROTO blueprints.
		if (this.blueprint.isPrototype()) return 0;
		// If already calculated then do not do it again.
		if (!this.totalcalculated) {
			final EveLocation location = this.blueprint.getLocation();
			int count = 999999;
			final ArrayList<Resource> resourceList = AppConnector.getDBConnector().searchListOfMaterials(this.bpid);
			for (final Resource resource : resourceList) {
				// Remove blueprints from the list of assets.
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					continue;
				}
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					continue;
				}
				// Get the corresponding resource quantity from the location.
				final ArrayList<Asset> available = getAsset4Type(resource.getTypeID());
				Log.i("EVEI", "-- T2ManufactureProcess.getManufacturableCount - available:" + available);
				int resourceCount = 0;
				for (final Asset asset : available)
					if (asset.getLocationID() == location.getID()) {
						resourceCount += asset.getQuantity();
					}
				Log.i("EVEI", "-- T2ManufactureProcess.getManufacturableCount - resource count " + resource + " ["
						+ resourceCount + "]");
				final int range = resourceCount / resource.getQuantity();
				if (range < count) {
					count = range;
				}
			}
			this.totalManufacturable = count;
			this.totalcalculated = true;
		}
		return this.totalManufacturable;
	}

	public int getProductID() {
		return this.moduleid;
	}

	public void setAssetsManager(final AssetsManager iam) {
		this.industryAssetsManager = iam;
		this.totalcalculated = false;
		if (null != this.industryAssetsManager) {
			this.industryAssetsManager.setPilot(getPilot());
		} else {
			this.industryAssetsManager = new AssetsManager(getPilot());
		}
	}

	public void setPilot(final EveChar pilot) {
		this.pilot = pilot;
		this.industryAssetsManager.setPilot(pilot);
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("AbstractManufactureProcess [");
		buffer.append(this.blueprint).append(" ");
		buffer.append("runs:").append(this.runs).append(" ");
		buffer.append("threads:").append(this.threads).append(" ");
		buffer.append("actions:").append(this.actionsRegistered).append(" ");
		buffer.append(" ]");
		return buffer.toString();
	}

	protected ArrayList<Action> getActions() {
		final ArrayList<Action> result = new ArrayList<Action>();
		for (final Action action : this.actionsRegistered.values()) {
			result.add(action);
		}
		return result;
	}

	/**
	 * Return the set of assets for a character that have an specific item type id. We have to make a local copy
	 * of the assets because they are going to be modified during the task creation process. So we can implement
	 * a cache of those assets so we only read them from the database the first time and later we only use the
	 * local copy.
	 * 
	 * @param typeID
	 * @return
	 */
	protected ArrayList<Asset> getAsset4Type(final int typeID) {
		ArrayList<Asset> hit = this.industryAssetsManager.assetCache.get(Long.valueOf(typeID));
		if (null == hit) {
			hit = AppConnector.getDBConnector().searchAsset4Type(this.pilot.getCharacterID(), typeID);
			this.industryAssetsManager.assetCache.put(Long.valueOf(typeID), hit);
		}
		return hit;
	}

	protected EveChar getPilot() {
		return this.pilot;
	}

	protected void processAction(final EveTask newTask) {
		final String category = newTask.getItem().getCategory();
		// Check the special case for T2 BPC to transform them to default INVENTION.
		if (newTask.getTaskType() == ETaskType.REQUEST)
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				final String tech = newTask.getItem().getTech();
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					Log.i("EVEI", "-- AbstractManufactureProcess.processRequest T2 Blueprint - request INVENTION");
					newTask.setTaskType(ETaskType.INVENTION);
					processRequest(newTask);
					return;
				}
				// If we request a T1 blueprint then we have to check if we can create copies. This is a default
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
					//					ArrayList<Asset> bpcs = getAsset4Type(newTask.getItem().getTypeID());
					//					// Search each blueprint to locate the BPO and then create the copies.
					//					for (Asset asset : bpcs) {
					//						Blueprint bp = industryAssetsManager.searchBlueprintByID(asset.getAssetID());
					//						if (bp.isBpo()) {
					newTask.setTaskType(ETaskType.COPY);
					processRequest(newTask);
					return;
					//						}
					//					}
					//					processBuy(newTask);
					//					return;
				}
			}

		// Check if the user has an action for this type of item. This can be tested for only some categories.
		// USER ACTIONS
		final Property action = this.actions4Item.get(new Long(newTask.getTypeID()));
		if (null != action) {
			// Store the action on the Task for later presentation.
			this.currentAction.setUserAction(action.getStringValue());
			// New code to handle reactions.
			if (newTask.getItem().getIndustryGroup() == EIndustryGroup.REACTIONMATERIALS) {
				processReaction(newTask);
			}
			if (category.equalsIgnoreCase("Planetary Commodities")) // Action is limited to PRODUCE or BUY
				if (action.getStringValue().equalsIgnoreCase("PRODUCE")) {
					final EveLocation planetaryLocation = this.pilot.getLocation4Role("PLANETARY PROCESSING", this.region);
					newTask.setTaskType(ETaskType.PRODUCE);
					if (null != planetaryLocation) {
						newTask.setLocation(planetaryLocation);
					}
					newTask.setDestination(this.manufactureLocation);
					registerTask(500, newTask);
					return;
				}
			if (category.equalsIgnoreCase("Material")) // Action is limited to EXTRACT or BUY
				if (action.getStringValue().equalsIgnoreCase("MATERIAL REFINE")) {
					processRefine(newTask);
					return;
				}
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				// There can be two types. Check the corresponding action.
				final String tech = newTask.getItem().getTech();
				if (tech.equalsIgnoreCase("Tech I")) {
					final EveLocation copyLocation = this.pilot.getLocation4Role("COPY", this.region);
					logger.info("-- COPY location searched at " + copyLocation);
					newTask.setTaskType(ETaskType.COPY);
					if (null != copyLocation) {
						newTask.setLocation(copyLocation);
					}
					final EveLocation inventLocation = this.pilot.getLocation4Role("INVENT", this.region);
					logger.info("-- INVENT location searched at " + inventLocation);
					if (null != inventLocation) {
						newTask.setDestination(this.manufactureLocation);
					}
					registerTask(400, newTask);
					return;
				}
			}
			if ((category.equalsIgnoreCase(ModelWideConstants.eveglobal.Module)) || (category.equalsIgnoreCase("Commodity"))
					|| (category.equalsIgnoreCase("Charge"))) // Action is limited to BUILD.
				if (action.getStringValue().equalsIgnoreCase("BUILD")) {
					// Schedule a manufacture request.
					processBuild(newTask);
					return;
				}
		}
		processBuy(newTask);
	}

	protected void processReaction(final EveTask newTask) {
		Log.i("NEOCOM", "-- AbstractManufactureProcess.processReaction Processing state - " + ETaskType.BUILD);
		final int itemID = newTask.getTypeID();
		final int outputMultiplier = AppConnector.getDBConnector().searchReactionOutputMultiplier(itemID);
		final ArrayList<Resource> lom = AppConnector.getDBConnector().searchListOfReaction(itemID);
		for (final Resource resource : lom) {
			logger.info("-- Processing Resource of LOM: " + resource);
			final int runs = newTask.getQty();
			double inputQty = (Math.ceil(runs / outputMultiplier) + 1) * resource.getQuantity();
			resource.setQuantity(Double.valueOf(inputQty).intValue());
			//			resource.setAdaptiveStackSize(1);
			// Add the resource to the set of required resources.
			addResource(resource);
		}
		newTask.setTaskType(ETaskType.BUILD);
		// Change the source location to the blueprint location if found. Otherwise use the manufacture location.
		//		newTask.setLocation(loc);
		registerTask(450, newTask);
	}

	protected void processBuild(final EveTask newTask) {
		Log.i("EVEI", "-- AbstractManufactureProcess.processRequest Processing state - " + ETaskType.BUILD);
		// Get the resources needed to manufacture this request.
		final int itemID = newTask.getTypeID();
		final int bpid = AppConnector.getDBConnector().searchBlueprint4Module(itemID);
		// Check if there is a blueprint of this type on the belongings for this character.
		boolean blueprintExists = false;
		final ArrayList<Asset> bpsofType = AppConnector.getDBConnector()
				.searchAsset4Type(getPilot().getCharacterID(), bpid);
		EveLocation loc = this.manufactureLocation;
		if (bpsofType.size() > 0) {
			blueprintExists = true;
			loc = bpsofType.get(0).getLocation();
		}
		final ArrayList<Resource> lom = AppConnector.getDBConnector().searchListOfMaterials(bpid);
		for (final Resource resource : lom) {
			logger.info("-- Processing Resource of LOM: " + resource);
			final int runs = newTask.getQty();
			resource.setQuantity(resource.getQuantity());
			resource.setAdaptiveStackSize(runs);
			// Add the resource to the set of required resources.
			addResource(resource);
		}
		newTask.setTaskType(ETaskType.BUILD);
		// Change the source location to the blueprint location if found. Otherwise use the manufacture location.
		newTask.setLocation(loc);
		registerTask(450, newTask);
	}

	/**
	 * When a required resource is not found elsewhere we have to generate a BUY action. This is the final
	 * action but still there are two different BUY actions. If there is no Scheduled Buy request on list then
	 * the BUY is a real BUY request that should be differentiated from a BUY when the user has already
	 * requested a buy to the market. This is a visual aid to the user to remember that the buy has already be
	 * requested to the market.
	 * 
	 * @param newTask
	 */
	protected void processBuy(final EveTask newTask) {
		final ArrayList<MarketOrder> scheduledOrders = accessScheduledOrders();
		// Search for an order for this type.
		for (final MarketOrder marketOrder : scheduledOrders)
			if (marketOrder.getItemTypeID() == newTask.getTypeID()) { //if (marketOrder.getQuantity() < newTask.getQty()) {
				final int taskQty = newTask.getQty();
				final int orderQty = marketOrder.getQuantity();
				// Update the tasks depending on those two quantities.
				// Generate two orders, one with the covered buy and maybe other with the rest.
				newTask.setTaskType(ETaskType.BUYCOVERED);
				newTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
				newTask.setQty(Math.min(taskQty, orderQty));
				registerTask(300, newTask);
				final int diff = taskQty - orderQty;
				if (diff > 0) {
					final EveTask partialTask = new EveTask(ETaskType.BUY, newTask.getResource());
					partialTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
					partialTask.setQty(diff);
					registerTask(300, partialTask);
				}
				return;
			}
		newTask.setTaskType(ETaskType.BUY);
		newTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
		registerTask(300, newTask);
	}

	protected void processInvent(final EveTask newTask) {
		registerTask(500, newTask);
	}

	//	protected void processSell() {
	//		ArrayList<Asset> modules = getAsset4T2Modules();
	//		for (Asset asset : modules) {
	//			if (asset.getQuantity() > T2MODULE_STACK_LIMIT) {
	//				Resource resource = new Resource(asset.getTypeID(), asset.getQuantity());
	//				currentAction = new Action(resource);
	//				registerAction(currentAction);
	//				EveTask sellTask = new EveTask(ETaskType.REQUEST, resource);
	//				sellTask.setQty(resource.getQuantity());
	//				sellTask.setTaskType(ETaskType.SELL);
	//				// For sell operations the price if the BUY price.
	////				MarketDataSet data = AppConnector.getDBConnector().searchMarketData(resource.getTypeID(), EMarketSide.BUYER);
	////				sellTask.setMarketData(data);
	//				//				sellTask.updateMarketData();
	////				MarketDataEntry best = sellTask.getMarketData().getBestMarket();
	////				EveLocation buyloc = best.transformLocation();
	//				sellTask.setLocation(sellTask.getResource().getItem().getHighestBuyPrice().getLocation());
	//				registerTask(300, sellTask);
	//			}
	//		}
	//	}

	protected void processMove(final Asset asset, final EveTask newTask) {
		final EveLocation loc = asset.getLocation();
		final int requestQty = newTask.getQty();
		final int qty = asset.getQuantity();
		if (qty >= requestQty) {
			// This single move task covers the requirement.
			final EveTask moveTask = new EveTask(ETaskType.MOVE, newTask.getResource());
			moveTask.setQty(requestQty);
			moveTask.setLocation(asset.getLocation());
			moveTask.setDestination(this.manufactureLocation);
			// Treat the special case of assets already present on the Manufacture location.
			if (this.manufactureLocation.getID() == loc.getID()) {
				// Convert the move to AVAILABLE because the locations match.
				// If the owner is -1 than this resource comes from a reprocessing.
				if (asset.getOwnerID() == -1) {
					moveTask.setTaskType(ETaskType.EXTRACT);
				} else {
					moveTask.setTaskType(ETaskType.AVAILABLE);
				}
				registerTask(90, moveTask, asset);
			} else {
				registerTask(400, moveTask, asset);
			}
			return;
		} else {
			// We need more locations to complete the request.
			// Cover part with this and the search for more.
			final EveTask moveTask = new EveTask(ETaskType.MOVE, newTask.getResource());
			moveTask.setQty(qty);
			moveTask.setLocation(asset.getLocation());
			moveTask.setDestination(this.manufactureLocation);
			// Treat the special case of assets already present on the Manufacture location.
			if (this.manufactureLocation.getID() == loc.getID()) {
				// If the owner is -1 than this resource comes from a reprocessing.
				if (asset.getOwnerID() == -1) {
					moveTask.setTaskType(ETaskType.EXTRACT);
				} else {
					moveTask.setTaskType(ETaskType.AVAILABLE);
				}
				registerTask(90, moveTask, asset);
			} else {
				registerTask(400, moveTask, asset);
			}
			final EveTask newRequest = new EveTask(ETaskType.REQUEST, newTask.getResource());
			newRequest.setQty(requestQty - qty);
			processRequest(newRequest);
			return;
		}
	}

	/**
	 * From the request to get some quantity of a mineral resource we search for the available assets
	 * (remembering that this will change the assets visible to the process) of the "Asteroid" type and start
	 * search for asteroids that generate the required mineral. We simplify the reprocessing operation the
	 * complete asteroid stack found and not dividing it into other pieces to improve the refining.<br>
	 * Then we reduce the asset reprocessed and generate new temporal assets for the minerals resulted from the
	 * process.<br>
	 * The decomposition considers a rentability of 50% on the refining and increments the assets with the
	 * result before resetting the calculation process.<br>
	 * 
	 * @param newTask
	 *          the task with the resource to be obtained.
	 */
	protected void processRefine(final EveTask newTask) {
		// Identify the preferred ore.
		final int mineralCode = newTask.getTypeID();
		final int mineralRequested = newTask.getQty();
		int mineralObtained = 0;
		final Asset oreSelected = searchOREAsset(newTask);
		if (null != oreSelected) {
			// Get access to the refining parameters
			final ArrayList<Resource> refineParameters = AppConnector.getDBConnector().refineOre(oreSelected.getTypeID());
			// Refine the asteroid stack and generate the new minerals.
			for (final Resource rc : refineParameters) {
				final double mineral = Math.floor(Math.floor(oreSelected.getQuantity() / rc.getStackSize())
						* (rc.getBaseQuantity() * REFINING_EFFICIENCY));
				if (rc.getTypeID() == mineralCode) {
					mineralObtained = Double.valueOf(mineral).intValue();
				}
				registerAssetChange(mineral, rc.item.getTypeID(), oreSelected.getLocationID());
			}
			// Generate the Action changes.
			addResource(new Resource(oreSelected.getTypeID(), oreSelected.getQuantity()));
			// Remove the resource from the available list to not be reprocessed again and again.
			oreSelected.setQuantity(0);

			// If we have refined all required we can stop.
			if (mineralObtained < mineralRequested) {
				// Process again like a AVAILABLE move with the new mineral. But special
				newTask.setQty(mineralRequested);
				processRequest(newTask);
				return;
			} else {
				// Refine completed.
				processRequest(newTask);
				return;
			}
		} else {
			processBuy(newTask);
		}
	}

	/**
	 * This is the main processing entry point. When a task is created it is entered to the manager to check of
	 * the action can be performed with other tasks that are less costly and that have a lower priority. If
	 * those changes generate a new set of resources then the progress structures anr cleared and the process
	 * starts again.
	 * 
	 * @param newTask
	 */
	protected void processRequest(final EveTask newTask) {
		// The task is a request. Check in order.
		final long requestQty = newTask.getQty();
		if (requestQty < 1) return;

		// Check the special case for Asteroids
		if (newTask.getTaskType() == ETaskType.REQUEST) {
			Log.i("EVEI", "-- AbstractManufactureProcess.processRequest Processing state - " + ETaskType.REQUEST + " ["
					+ requestQty + "]");
			final String category = newTask.getItem().getCategory();
			// If the resource is an asteroid then we can Refine it.
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Asteroid)) {
				Log.i("EVEI", "-- AbstractManufactureProcess.processRequest Asteroid - request COMPLETED");
				// Complete the action and add the minerals obtained as tasks.
				this.currentAction.setCompleted(ETaskCompletion.COMPLETED, newTask.getQty());
				// Add the refine of the mineral to the tasks.
				final ArrayList<Resource> refineParameters = AppConnector.getDBConnector().refineOre(newTask.getTypeID());
				for (final Resource rc : refineParameters) {
					final double mineral = Math.floor(Math.floor(newTask.getResource().getQuantity() / rc.getStackSize())
							* (rc.getBaseQuantity() * REFINING_EFFICIENCY));
					final EveTask refineTask = new EveTask(ETaskType.PRODUCE, new Resource(rc.item.getItemID(), Double.valueOf(
							mineral).intValue()));
					refineTask.setQty(Double.valueOf(mineral).intValue());
					refineTask.setLocation(newTask.getLocation());
					registerTask(90, refineTask);
				}
				return;
			}
		}
		if (newTask.getTaskType() == ETaskType.INVENTION) {
			// TODO Implement the process invention
			processInvent(newTask);
			return;
		}
		// Get the Assets that match the current type id.
		final ArrayList<Asset> available = getAsset4Type(newTask.resource.item.getItemID());
		Log.i("EVEI", "-- AbstractManufactureProcess.processRequest availabel assets:" + available);
		// See if there are assets of this type on the manufacture location before moving assets.
		// MOVE - manufacture location
		for (final Asset asset : available) {
			// Removed assets with no count
			if (asset.getQuantity() < 1) {
				continue;
			}
			// Check location on this region.
			final EveLocation loc = asset.getLocation();
			if (loc.toString().equalsIgnoreCase(this.manufactureLocation.toString())) {
				processMove(asset, newTask);
				return;
			}
		}
		// Check the MOVE flag to control if the user allows to search for assets at other locations
		if (moveAllowed()) {

			// See if we have that resource elsewhere ready for transportation.
			// MOVE - manufacture region
			for (final Asset asset : available) {
				// Removed assets with no count
				if (asset.getQuantity() < 1) {
					continue;
				}
				// Check location on this region.
				final EveLocation loc = asset.getLocation();
				if (loc.getRegion().equalsIgnoreCase(this.region)) {
					processMove(asset, newTask);
					return;
				}
			}

			// Assets not in same region or not found. Try without region limits.
			// MOVE - rest of universe
			for (final Asset asset : available) {
				if (asset.getQuantity() < 1) {
					continue;
				}
				processMove(asset, newTask);
				return;
			}
		}

		// If we reach this point we are sure that all other intents have been processed.
		// Continue processing a BUY request or its decomposition.
		processAction(newTask);
	}

	public boolean moveAllowed() {
		// Read the flag values from the preferences.
		boolean moveAllowed = EVEDroidApp.getBooleanPreference(AppWideConstants.preference.PREF_ALLOWMOVEREQUESTS, false);
		return moveAllowed;
	}

	protected void registerAction(final Action action) {
		// Test if already an action of the same item.
		final Action hit = this.actionsRegistered.get(action.getTypeID());
		if (null != hit) {
			this.currentAction = action;
		} else {
			this.actionsRegistered.put(action.getTypeID(), action);
		}
	}

	protected Asset searchResourceAtLocation(final Resource resource, final EveLocation location) {
		final int targetid = resource.getTypeID();
		//	int targetqty = resource.getQuantity();
		final ArrayList<Asset> available = getAssetsAtLocation(location);
		for (final Asset asset : available) {
			final int id = asset.getTypeID();
			if (id == targetid) return asset;
		}
		return null;
	}

	//	private ArrayList<Asset> getAsset4T2Modules() {
	//		long hash = "Tech II|Module".hashCode();
	//		ArrayList<Asset> hit = assetCache.get(hash);
	//		if (null == hit) {
	//			hit = searchAsset4T2Module(pilot.getCharacterID());
	//			assetCache.put(hash, hit);
	//		}
	//		return hit;
	//	}

	private ArrayList<MarketOrder> accessScheduledOrders() {
		// Search for an scheduled buy and get its quantity.
		final ArrayList<MarketOrder> allorders = getPilot().searchMarketOrders();
		final ArrayList<MarketOrder> orders = new ArrayList<MarketOrder>();
		for (final MarketOrder order : allorders)
			if (order.getOrderState() == ModelWideConstants.orderstates.SCHEDULED) {
				orders.add(order);
			}
		return aggregate(orders);
	}

	private void addResource(final Resource resource) {
		for (final Resource current : this.requirements)
			if (resource.item.getItemID() == current.item.getItemID()) {
				// Do special processing for skill. They are not added but the level maxed.
				if (current.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					current.setQuantity(Math.max(resource.getQuantity(), current.getQuantity()));
				} else {
					current.setQuantity(resource.getQuantity() + current.getQuantity());
				}
				current.setStackSize(1);
				logger.info("-- Incrementing Resource requirements: " + current);
				return;
			}
		Log.i("EVEI", "-- AbstractManufactureProcess.addResource - Adding new resource to list. " + resource);
		this.requirements.add(resource);
	}

	@SuppressLint("UseValueOf")
	private ArrayList<MarketOrder> aggregate(final ArrayList<MarketOrder> sourcenodes) {
		final HashMap<Integer, MarketOrder> datamap = new HashMap<Integer, MarketOrder>();
		for (final MarketOrder order : sourcenodes) {
			final MarketOrder hit = datamap.get(new Integer(order.getItemTypeID()));
			if (null == hit) {
				datamap.put(new Integer(order.getItemTypeID()), order);
			} else {
				hit.setVolEntered(hit.getVolEntered() + order.getVolEntered());
			}
		}
		// Unpack the data map into a new list with the quantities aggregated
		return new ArrayList<MarketOrder>(datamap.values());
	}

	/**
	 * Cache the resulting data on the asset cache to allow also the modification and the lateral impact for
	 * these operations.
	 * 
	 * @param category
	 * @return
	 */
	private ArrayList<Asset> getAsset4Category(final String category) {
		final long hash = category.hashCode();
		ArrayList<Asset> hit = this.industryAssetsManager.assetCache.get(hash);
		if (null == hit) {
			hit = searchAsset4Category(this.pilot.getCharacterID(), category);
			this.industryAssetsManager.assetCache.put(hash, hit);
		}
		return hit;
	}

	private ArrayList<Asset> getAsset4Group(final String groupName) {
		final long hash = groupName.hashCode();
		ArrayList<Asset> hit = this.industryAssetsManager.assetCache.get(hash);
		if (null == hit) {
			hit = getPilot().getAssetsManager().searchAsset4Group(groupName);
			this.industryAssetsManager.assetCache.put(hash, hit);
		}
		return hit;
	}

	private ArrayList<Asset> getAssetsAtLocation(final EveLocation location) {
		ArrayList<Asset> hit = this.industryAssetsManager.assetCache.get(location.getID());
		if (null == hit) {
			hit = this.industryAssetsManager.searchAsset4Location(location);
			this.industryAssetsManager.assetCache.put(location.getID(), hit);
		}
		return hit;
	}

	/**
	 * changes the assets counts on the source places where the resources are used or moved
	 * 
	 * @param task
	 * @param targetAsset
	 */
	private void performTask(final EveTask task, final Asset targetAsset) {
		final ETaskType type = task.getTaskType();
		switch (type) {
			case MOVE:
				targetAsset.setQuantity(targetAsset.getQuantity() - task.getQty());
				break;
			case AVAILABLE:
				targetAsset.setQuantity(targetAsset.getQuantity() - task.getQty());
				break;
			case EXTRACT:
				targetAsset.setQuantity(targetAsset.getQuantity() - task.getQty());
				break;
		}
	}

	/**
	 * This is quite complex operation. We have to first check if the process has used the resource and we have
	 * a hit on the cache. If found we then add the new asset to that list so the process will think that asses
	 * comes from the database. If the hit is empty we cannot use it to store the data because we will never go
	 * back to the database to get the assets. Then we can do two things, or to download the assets and generate
	 * a hit and then add to it or store elsewhere and when we download the assets we then add them from this
	 * new storage.<br>
	 * I think that the first is more stable and will make the code simpler because we only change one of the
	 * methods and do not use other data structures.
	 * 
	 * @param qty
	 * @param itemID
	 * @param location
	 */
	private void registerAssetChange(final double qty, final int itemID, final long location) {
		final ArrayList<Asset> hit = this.industryAssetsManager.assetCache.get(Long.valueOf(itemID));
		if (null == hit) {
			// Force a database access and try again.
			getAsset4Type(itemID);
			registerAssetChange(qty, itemID, location);
		} else {
			final EveItem item = AppConnector.getDBConnector().searchItembyID(itemID);
			final Asset newAsset = new Asset();
			//		newAsset.setItem(AppConnector.getDBConnector().searchItembyID(itemID));
			newAsset.setAssetID(GENERATED_ASSETCOUNTER++);
			newAsset.setTypeID(itemID);
			newAsset.setName(item.getName());
			newAsset.setLocationID(location);
			newAsset.setCategory(item.getCategory());
			newAsset.setQuantity(Double.valueOf(qty).intValue());
			hit.add(newAsset);
		}
	}

	/**
	 * Aggregates the new task to the list of tasks. Before adding the task to the list it checks if there is a
	 * task of the same item and type to accumulate the quantities instead of generating different tasks.<br>
	 * After it modifies the list it fires a change so any listeners will trigger update processes.
	 * 
	 * @param pri
	 * @param task
	 */
	private synchronized void registerTask(final int pri, final EveTask task) {
		// Check for completed tasks.
		if (task.getTaskType() == ETaskType.AVAILABLE) {
			this.currentAction.setCompleted(ETaskCompletion.COMPLETED, task.getQty());
		}
		if (task.getTaskType() == ETaskType.EXTRACT) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == ETaskType.SELL) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == ETaskType.REFINE) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == ETaskType.MOVE) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == ETaskType.BUILD) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == ETaskType.BUY) {
			this.currentAction.setCompleted(ETaskCompletion.MARKET, task.getQty());
		}
		if (task.getTaskType() == ETaskType.BUYCOVERED) {
			this.currentAction.setCompleted(ETaskCompletion.PENDING, task.getQty());
		}
		this.currentAction.registerTask(pri, task);
	}

	/**
	 * Register the task on the <code>Action</code>. This method is the one responsible to modify the assets
	 * used to complete the task.
	 * 
	 * @param pri
	 *          priority of the task being registered.
	 * @param task
	 *          the task that completes the request or part of the request.
	 * @param targetAsset
	 *          the asset used to complete the task when this action requires movement or transformation of
	 *          other resources. This is used to change the memory copy of the asset so next actions will found
	 *          an scenery similar to the one in real life and not an infinite number of resources.
	 */
	private void registerTask(final int pri, final EveTask task, final Asset targetAsset) {
		logger.info("-- Registering task request [" + pri + "] " + task);
		performTask(task, targetAsset);
		//Also add the asset as a reference to the task. Useful when activating links.
		task.registerAsset(targetAsset);
		registerTask(pri, task);
	}

	private ArrayList<Asset> searchAsset4Category(final long characterID, final String category) {
		//	Select assets for the owner and woth an specific type id.
		List<Asset> assetList = new ArrayList<Asset>();
		try {
			final Dao<Asset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
			final QueryBuilder<Asset, String> queryBuilder = assetDao.queryBuilder();
			final Where<Asset, String> where = queryBuilder.where();
			where.eq("ownerID", characterID);
			where.and();
			where.eq("category", category);
			final PreparedQuery<Asset> preparedQuery = queryBuilder.prepare();
			assetList = assetDao.query(preparedQuery);
		} catch (final java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return (ArrayList<Asset>) assetList;

	}

	/**
	 * Search for a stack of ORE that is able to generate the quantity of the mineral requested by the task.
	 * First get the list of nearby stacks and then get their list of resources generated. If the target is on
	 * that list then this stack is scheduled for refining. Once we found a target, we refine it and finish the
	 * action. If the resource generated is not enough we will be called again and do this action again.<br>
	 * Tag the stack to be processed with the processing location so that location information is added to the
	 * UI data presented to the user.<br>
	 * Added the alphabetical ordering to allow for a better search for the right ore.
	 * 
	 * @param newTask
	 *          the task that requests the service.
	 * @return an <code>Asset</code> that complies to cover the requested mineral request or at least a part of
	 *         it.
	 */
	private Asset searchOREAsset(final EveTask newTask) {
		// Try to cache the list of assets related to asteroids at partiicualr locations.
		ArrayList<Asset> asteroids = this.industryAssetsManager.asteroidCache.get(this.manufactureLocation.getID());
		final EveLocation refineLocation = getPilot().getLocation4Role(ModelWideConstants.locationroles.REFINE);
		if (null == asteroids) if (null != refineLocation) {
			asteroids = this.industryAssetsManager.asteroidCache.get(refineLocation.getID());
		}
		if (null == asteroids) {
			// Get the list of assets that are asteroids at the manufacture location.
			ArrayList<Asset> stacks = getAssetsAtLocation(this.manufactureLocation);
			asteroids = new ArrayList<Asset>();
			for (final Asset asset : stacks)
				// Filter out the non asteroid stacks
				if (asset.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Asteroid)) {
					asteroids.add(asset);
				}
			this.industryAssetsManager.asteroidCache.put(this.manufactureLocation.getID(), asteroids);

			if (asteroids.size() < 1) // If the list is empty do the same for the stacks at the refining location if exists.
				if (null != refineLocation) {
					stacks = getAssetsAtLocation(refineLocation);
					asteroids = new ArrayList<Asset>();
					for (final Asset asset : stacks)
						// Filter out the non asteroid stacks
						if (asset.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Asteroid)) {
							asteroids.add(asset);
						}
					this.industryAssetsManager.asteroidCache.put(this.manufactureLocation.getID(), asteroids);
				}
		}

		// Scan each stack for the required mineral. Order the asteroids by their ore name first.
		Collections.sort(asteroids, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		for (final Asset asteroid : asteroids) {
			// Filter out all ore with quantity less that the portion size (100)
			if (asteroid.getQuantity() < 100) {
				continue;
			}
			// Get the list of minerals resulting from this refining.
			final ArrayList<Resource> refineParameters = AppConnector.getDBConnector().refineOre(asteroid.getTypeID());
			for (final Resource resource : refineParameters)
				if (resource.getTypeID() == newTask.getTypeID()) return asteroid;
		}
		return null;
	}
}
// - UNUSED CODE ............................................................................................
