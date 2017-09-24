//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.CVariant.EDefaultVariant;
import org.dimensinfin.eveonline.neocom.core.AbstractNeoComNode;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.INamed;
import org.dimensinfin.eveonline.neocom.model.Container;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryManager extends AbstractManager implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID				= 3794750126425122302L;
	private static Logger													logger									= Logger.getLogger("PlanetaryManager");

	// - F I E L D - S E C T I O N ............................................................................
	private boolean																initialized							= false;
	//	private final long												totalAssets							= -1;
	private long																	verificationAssetCount	= 0;
	public double																	totalAssetsValue				= 0.0;
	@JsonInclude
	public final HashMap<Long, Region>						regions									= new HashMap<Long, Region>();
	private final HashMap<Long, EveLocation>			locations								= new HashMap<Long, EveLocation>();
	private final HashMap<Long, NeoComAsset>			containers							= new HashMap<Long, NeoComAsset>();
	@JsonIgnore
	public ArrayList<NeoComAsset>									planetaryAssetList			= null;
	public String																	iconName								= "planets.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	@JsonIgnore
	private transient HashMap<Long, NeoComAsset>	assetMap								= new HashMap<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryManager(final NeoComCharacter pilot) {
		super(pilot);
		// Get all the Planetary assets and classify them into lists.
		jsonClass = "PlanetaryManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Reads from the database all the Planetary assets and classifies them depending on Locations.
	 */
	public void accessAllAssets() {
		//		try {
		// Initialize the model
		regions.clear();
		locations.clear();
		containers.clear();
		// Get all the assets of the Planetary categories.
		int assetCounter = 0;
		try {
			// Read all the assets for this character if not done already.
			planetaryAssetList = AppConnector.getDBConnector().searchAllPlanetaryAssets(this.getPilot().getCharacterID());
			// Move the list to a processing map.
			assetMap = new HashMap<Long, NeoComAsset>(planetaryAssetList.size());
			for (NeoComAsset asset : planetaryAssetList) {
				assetMap.put(asset.getAssetID(), asset);
			}
			// Process the map until all elements are removed.
			Long key = assetMap.keySet().iterator().next();
			NeoComAsset point = assetMap.get(key);
			while (null != point) {
				assetCounter++;
				this.processElement(point);
				key = assetMap.keySet().iterator().next();
				point = assetMap.get(key);
			}
		} catch (NoSuchElementException nsee) {
			// Reached the end of the list of assets to process.
			PlanetaryManager.logger.info("-- [AssetsManager.accessAllAssets]> No more assets to process");
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			PlanetaryManager.logger.severe(
					"RTEX> AssetsByLocationDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		} finally {
			PlanetaryManager.logger.info("-- [AssetsManager.accessAllAssets]> Assets processed: " + assetCounter);
		}
	}

	public long getAssetTotalCount() {
		if (null == planetaryAssetList)
			return 0;
		else
			return planetaryAssetList.size();
	}

	/**
	 * Get the list of Planetary Resources that are at the indicated location. If the location is not found or
	 * the contents are not initializes then return an empty list.
	 * 
	 * @param locationid
	 * @return
	 */
	public Vector<Resource> getLocationContents(final String locationid) {
		long locidnumber = Long.valueOf(locationid);
		for (Long key : locations.keySet()) {
			if (key == locidnumber) {
				Vector<IGEFNode> contents = locations.get(key).getChildren();
				// Transform the contents to a list of Resources
				Vector<Resource> results = new Vector<Resource>(contents.size());
				for (IGEFNode node : contents) {
					if (node instanceof NeoComAsset) {
						NeoComAsset transformedNode = (NeoComAsset) node;
						results.add(new Resource(transformedNode.getTypeID(), transformedNode.getQuantity()));
					}
				}
				return results;
			}
		}
		return new Vector<Resource>();
	}

	public String getOrderingName() {
		return "Planetary Manager";
	}

	public PlanetaryManager initialize() {
		this.accessAllAssets();
		initialized = true;
		return this;
	}

	/**
	 * Checks if the initialization method and the load of the resources has been already executed.
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		if (initialized) if (null != planetaryAssetList) return true;
		return false;
	}

	/**
	 * Search for this container reference on this Location's children until found. Then aggregates the asset to
	 * that container calculating stacking if this is possible. There can be containers inside container like
	 * the case where a container is on the hols of a ship. That special case will not be implemented on this
	 * first approach and all the container will be located at the Location's hangar floor.<br>
	 * Containers also do not have its market value added to the location's aggregation.
	 * 
	 * @param apart
	 */
	private void add2Container(final NeoComAsset asset) {
		PlanetaryManager.logger.info(">> LocationAssetsPart.add2Container");
		// Locate the container if already added to the location.
		NeoComAsset cont = asset.getParentContainer();
		// TODO Check what is the cause of a parent container null and solve it
		if (null != cont) {
			long pcid = cont.getDAOID();
			NeoComAsset target = containers.get(pcid);
			if (null == target) {
				// Add the container to the list of containers.
				PlanetaryManager.logger
						.info("-- [AssetsByLocationDataSource.add2Container]> Created new container: " + cont.getDAOID());
				containers.put(new Long(pcid), cont);
				// Add the container to the list of locations or to another container if not child
				//			if (asset.hasParent()) {
				//				add2Container(cont);
				//			} else {
				//				add2Location(cont);
				//			}
			} else {
				// Add the asset to the children list of the target container
				target.addChild(asset);
			}
		} else {
			// Investigate why the container is null. And maybe we should search for it because it is not our asset.
			long id = asset.getParentContainerId();
			NeoComAsset parentAssetCache = AppConnector.getDBConnector().searchAssetByID(asset.getParentContainerId());
		}
		// This is an Unknown location that should be a Custom Office
	}

	private void add2Location(final NeoComAsset asset) {
		long locid = asset.getLocationID();
		EveLocation target = locations.get(locid);
		if (null == target) {
			target = AppConnector.getCCPDBConnector().searchLocationbyID(locid);
			locations.put(new Long(locid), target);
			this.add2Region(target);
		}
		target.addChild(asset);
	}

	private void add2Region(final EveLocation target) {
		long regionid = target.getRegionID();
		Region region = regions.get(regionid);
		if (null == region) {
			region = new Region(target.getRegion());
			regions.put(new Long(regionid), region);
		}
		region.addChild(target);
	}

	/**
	 * Get one asset and performs some checks to transform it into another type or to process its parentship
	 * because with the flat listing there is only relationship through the location id. <br>
	 * If the Category of the asset is a container or a ship then it is encapsulated into another type that
	 * specializes the view presentation. This is the case of Containers and Ships. <br>
	 * If it found one of those items gets the list of contents to be removed to the to be processed list
	 * because the auto model generation will already include those items. Only Locations or Regions behave
	 * differently.
	 * 
	 * @param asset
	 */
	private void processElement(final NeoComAsset asset) {
		try {
			// Remove the element from the map.
			assetMap.remove(asset.getAssetID());
			// Add the asset to the verification count.
			verificationAssetCount++;
			// Add the asset value to the owner balance.
			totalAssetsValue += asset.getIskValue();
			// Transform the asset if on specific categories like Ship or Container
			//			if (asset.isShip()) {
			//				// Check if the ship is packaged. If packaged leave it as a simple asset.
			//				if (!asset.isPackaged()) {
			//					// Transform the asset to a ship.
			//					Ship ship = new Ship(this.getPilot().getCharacterID()).copyFrom(asset);
			//					ships.put(ship.getAssetID(), ship);
			//					// The ship is a container so add it and forget about this asset.
			//					if (ship.hasParent()) {
			//						this.processElement(ship.getParentContainer());
			//					} //else {
			//					this.add2Location(ship);
			//					// Remove all the assets contained because they will be added in the call to collaborate2Model
			//					// REFACTOR set the default variant as a constant even that information if defined at other project
			//					ArrayList<AbstractComplexNode> removableList = ship.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
			//					// The list returned is not the real list of assets contained but the list of Separators
			//					for (AbstractComplexNode node : removableList) {
			//						this.removeNode(node);
			//					}
			//				} else {
			//					this.add2Location(asset);
			//				}
			//				return;
			//			}
			if (asset.isContainer()) {
				// Check if the asset is packaged. If so leave as asset
				if (!asset.isPackaged()) {
					// Transform the asset to a ship.
					Container container = new Container().copyFrom(asset);
					containers.put(container.getAssetID(), container);
					// The container is a container so add it and forget about this asset.
					if (container.hasParent()) {
						this.processElement(container.getParentContainer());
					} // else {
					this.add2Location(container);
					// Remove all the assets contained because they will be added in the call to collaborate2Model
					// REFACTOR set the default variant as a constant even that information if defined at other project
					ArrayList<AbstractComplexNode> removableList = container
							.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name());
					// The list returned is not the real list of assets contained but the list of Separators
					for (AbstractComplexNode node : removableList) {
						this.removeNode(node);
					}
				} else {
					this.add2Location(asset);
				}
				//				// Remove all the assets contained because they will be added in the call to collaborate2Model
				//				ArrayList<AbstractComplexNode> removable = asset.collaborate2Model("REPLACE");
				//				for (AbstractComplexNode node : removable) {
				//					assetMap.remove(((Container) node).getAssetID());
				//				}
				//	}
				return;
			}
			// Process the asset parent if this is the case because we should add first parent to the hierarchy
			if (asset.hasParent()) {
				NeoComAsset parent = asset.getParentContainer();
				if (null == parent) {
					this.add2Location(asset);
				} else {
					this.processElement(parent);
				}
			} else {
				this.add2Location(asset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Remove the nodes collaborated and their own collaborations recursively from the list of assets to
	 * process.
	 */
	private void removeNode(final AbstractComplexNode node) {
		// Check that the class of the item is an Asset. Anyway check for its collaboration.
		if (node instanceof AbstractNeoComNode) {
			// Try to remove the asset if found
			if (node instanceof NeoComAsset) {
				assetMap.remove(((NeoComAsset) node).getAssetID());
			}
			// Remove also the nodes collaborated by it.
			for (AbstractComplexNode child : ((AbstractNeoComNode) node)
					.collaborate2Model(EDefaultVariant.DEFAULT_VARIANT.name())) {
				this.removeNode(child);
			}
		}
	}

}

// - UNUSED CODE ............................................................................................