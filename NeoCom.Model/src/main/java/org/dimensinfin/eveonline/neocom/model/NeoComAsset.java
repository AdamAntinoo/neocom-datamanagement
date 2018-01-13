//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * Assets are collections of <code>EveItem</code>s of the same type and characteristics that are grouped on
 * game stacks and that belong to a single <code>EveChar</code> and that are located on a
 * <code>EveLocation</code>. Most of the data for an asset can be found on those classes but is being added to
 * the persistence implementation to allow DAO searches and easier database interaction so the asset
 * management that is a quite memory intensive activity may be performed with the few memory possible.<br>
 * The identifier information and the key dta are downloaded from CCP data with an API call. The data is then
 * expanded and some of the assets are grouped while being stored on the database with a neutral character
 * identifier until the end of the processing when all those assets are transferred to the correct owner.<br>
 * Persistence is implemented on the "Assets" table on the <code>evedroid.db</code> user database.
 * 
 * @author Adam Antinoo
 */

@DatabaseTable(tableName = "Assets")
public class NeoComAsset extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long			serialVersionUID	= -2662145568311324496L;
	private static Logger					logger						= Logger.getLogger("Asset");

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is a generated identifier to allow having duplicated asset numbers when processing updates. This is
	 * the primary key identifier and it is generated by an incremental sequence.
	 */
	@DatabaseField(generatedId = true)
	private long									id								= -2;
	// - A P I   C C P   F I E L D S
	@DatabaseField(index = true)
	private long									assetID;
	@DatabaseField(index = true)
	private long									locationID				= -1;
	@DatabaseField(index = true)
	private int										typeID;
	@DatabaseField
	private int										quantity					= 0;
	@DatabaseField
	private int										flag;
	@DatabaseField
	private boolean								singleton					= false;
	@DatabaseField
	private long									parentAssetID			= -1;

	//- D E R I V E D   F I E L D S
	/** Here starts the fields that come form item data but useful for search operations. */
	@DatabaseField
	private long									ownerID						= -1;
	@DatabaseField
	private String								name							= null;
	@DatabaseField(index = true)
	private String								category					= null;
	@DatabaseField(index = true)
	private String								groupName					= null;
	@DatabaseField
	private String								tech							= null;
	@DatabaseField
	private boolean								blueprintFlag			= false;
	@DatabaseField
	private String								userLabel					= null;
	@DatabaseField
	private boolean								shipFlag					= false;
	@DatabaseField
	private boolean								containerFlag			= false;
	@DatabaseField
	private double								iskValue					= 0.0;

	// - C A C H E D   F I E L D S
	private transient NeoComAsset	parentAssetCache	= null;
	@JsonIgnore
	private transient EveLocation	locationCache			= null;
	@JsonIgnore
	private transient EveItem			itemCache					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComAsset() {
		super();
		jsonClass = "NeoComAsset";
		id = -2;
		locationID = -1;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Assets should collaborate to the model adding their children. Most of the assets will not have children
	 * but the containers that maybe will use this code or be created as other kind of specialized asset.
	 */
	@Override
	public List<ICollaboration> collaborate2Model(final String variant) {
		final ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		//		results = this.concatenateChildren(results, this.getChildren());
		return results;
	}

	public long getAssetID() {
		return assetID;
	}

	public String getCategory() {
		if (null == category) return "NOCAT";
		return category;
	}

	@Deprecated
	public int getContentCount() {
		return 0;
	}

	public long getDAOID() {
		return id;
	}

	public int getFlag() {
		return flag;
	}

	public String getGroupName() {
		return groupName;
	}

	public double getIskValue() {
		return iskValue;
	}

	/**
	 * New optimization will leave this filed for lazy evaluation. So check if this is empty before getting any
	 * access and if so download from the Item Cache.
	 * 
	 * @return
	 */
	public EveItem getItem() {
		if (null == itemCache) {
			itemCache = ModelAppConnector.getSingleton().getCCPDBConnector().searchItembyID(typeID);
		}
		return itemCache;
	}

	public String getItemName() {
		return this.getItem().getName();
	}

	@JsonIgnore
	public EveLocation getLocation() {
		if (null == locationCache) {
			locationCache = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(locationID);
		}
		return locationCache;
	}

	/**
	 * Return the location id of the asset. If the asset is in a container then the location is lost and the
	 * value is -1. In that cases we should search for the location on the parent asset if that exists.
	 * 
	 * @return
	 */
	public long getLocationID() {
		if (locationID == -1) {
			if (this.getParentContainerId() == -1)
				return -1L;
			else {
				// Get the location from the parent.
				final NeoComAsset par = this.getParentContainer();
				if (null == par)
					return -1L;
				else
					return par.getLocationID();
			}
		} else
			return locationID;
	}

	public String getName() {
		return name;
	}

	public String getOrderingName() {
		return name;
	}

	public long getOwnerID() {
		return ownerID;
	}

	/**
	 * This method usually return the parent container of an asset. This is valid when the asset is inside a
	 * container or a ship or any other asset, but when the asset is located on other corporation POCO or other
	 * unknown or unaccessible asset then this method fails to get the parent asset. <br>
	 * In that case we should replace the parent pointer to a new location pointer that gets stored into our new
	 * Locations table. With that change assets will be reallocated to a valid place and all code will
	 * understand the changes and behave correctly.
	 * 
	 * @return
	 */
	public NeoComAsset getParentContainer() {
		if (parentAssetID > 0) if (null == parentAssetCache) {
			// Search for the parent asset. If not found then go to the transformation method.
			parentAssetCache = ModelAppConnector.getSingleton().getDBConnector().searchAssetByID(parentAssetID);
			if (null == parentAssetCache) {
				final long newlocationId = parentAssetID;
				final EveLocation loc = this.moveAssetToUnknown(newlocationId);
				this.setLocationID(newlocationId);
				parentAssetID = -1;
				locationCache = loc;
				// Save the asset to disk.
				try {
					final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
					// Try to create the pair. It fails then  it was already created.
					assetDao.createOrUpdate(this);
				} catch (final SQLException sqle) {
					sqle.printStackTrace();
					this.setDirty(true);
				}
				//				// Create a dummy container to be replaced by the missing parent and that will get stored into the right location
				//				Container container = new Container();
				//				container.setAssetID(newlocationId);
				//				container.setLocationID(newlocationId);
				//				container.setTypeID(17366);
				//				container.setQuantity(1);
				//				container.setSingleton(false);
				//
				//				//- D E R I V E D   F I E L D S
				//				container.setOwnerID(this.getOwnerID());
				//				container.setName("Undefined #" + newlocationId);
				//				container.setCategory("Celestial");
				//				container.setGroupName("Undefined Location");
				//				container.setContainer(true);
				return null;
			}
		}
		return parentAssetCache;
	}

	public long getParentContainerId() {
		return parentAssetID;
	}

	public double getPrice() {
		return this.getItem().getPrice();
	}

	public int getQuantity() {
		return quantity;
	}

	public String getRegion() {
		return this.getLocation().getRegion();
	}

	public String getTech() {
		return this.getItem().getTech();
	}

	public int getTypeID() {
		return typeID;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public boolean hasParent() {
		if (parentAssetID > 0)
			return true;
		else
			return false;
	}

	public boolean isBlueprint() {
		return blueprintFlag;
	}

	public boolean isContainer() {
		if (this.isBlueprint()) return false;
		// Use a list of types to set what is a container
		if (this.getTypeID() == 11488) return true;
		if (this.getTypeID() == 11489) return true;
		if (this.getTypeID() == 11490) return true;
		if (this.getTypeID() == 17363) return true;
		if (this.getTypeID() == 17364) return true;
		if (this.getTypeID() == 17365) return true;
		if (this.getTypeID() == 17366) return true;
		if (this.getTypeID() == 17367) return true;
		if (this.getTypeID() == 17368) return true;
		if (this.getTypeID() == 2263) return true;
		if (this.getTypeID() == 23) return true;
		if (this.getTypeID() == 24445) return true;
		if (this.getTypeID() == 28570) return true;
		if (this.getTypeID() == 3293) return true;
		if (this.getTypeID() == 3296) return true;
		if (this.getTypeID() == 3297) return true;
		if (this.getTypeID() == 33003) return true;
		if (this.getTypeID() == 33005) return true;
		if (this.getTypeID() == 33007) return true;
		if (this.getTypeID() == 33009) return true;
		if (this.getTypeID() == 33011) return true;
		if (this.getTypeID() == 3465) return true;
		if (this.getTypeID() == 3466) return true;
		if (this.getTypeID() == 3467) return true;
		if (this.getTypeID() == 3468) return true;
		if (this.getTypeID() == 41567) return true;
		if (this.getName().contains("Container"))
			return true;
		else
			return false;
	}

	public boolean isPackaged() {
		return !singleton;
	}

	public boolean isShip() {
		return shipFlag;
	}

	public boolean isShipFlag() {
		return shipFlag;
	}

	public void setAssetID(final long assetIdentifier) {
		assetID = assetIdentifier;
	}

	public void setBlueprintType(final int rawQuantity) {
		if (-1 == rawQuantity) {
			this.setName(name + " (BPO)");
		} else {
			this.setName(name + " (BPC)");
		}
		blueprintFlag = true;
	}

	public void setCategory(final String category) {
		this.category = category;
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Ship)) {
			shipFlag = true;
		}
	}

	public void setContainer(final boolean value) {
		containerFlag = value;
	}

	public void setDirty(final boolean flag) {
		if (flag) {
			try {
				final Dao<NeoComAsset, String> assetDao = ModelAppConnector.getSingleton().getDBConnector().getAssetDAO();
				// Try to create the pair. It fails then  it was already created.
				assetDao.createOrUpdate(this);
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
				//	this.setDirty(true);
			}
		}
	}

	public void setFlag(final int newFlag) {
		flag = newFlag;
	}

	public void setGroupName(final String name) {
		groupName = name;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setIskValue(final double iskvalue) {
		iskValue = iskvalue;
	}

	public void setLocationID(final long location) {
		locationID = location;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOwnerID(final long ownerID) {
		this.ownerID = ownerID;
	}

	public void setParentContainer(final NeoComAsset newParent) {
		if (null != newParent) {
			parentAssetCache = newParent;
			parentAssetID = newParent.getAssetID();
			// Trigger an update of the record at the database.
			this.setDirty(true);
		}
	}

	public void setParentId(final long pid) {
		parentAssetID = pid;
	}

	public void setQuantity(final int count) {
		quantity = count;
	}

	public void setShip(final boolean value) {
		shipFlag = value;
	}

	public void setShipFlag(final boolean shipFlag) {
		this.shipFlag = shipFlag;
	}

	public void setSingleton(final boolean newSingleton) {
		singleton = newSingleton;
	}

	public void setTech(final String newTech) {
		tech = newTech;
	}

	public void setTypeID(final int newTypeID) {
		typeID = newTypeID;
	}

	public void setUserLabel(final String label) {
		if (null != label) {
			userLabel = label;
		}
	}

	/**
	 * Try to reduce the calls to methods to compose this information since that affects the code to be run.
	 */
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComAsset [");
		buffer.append("#").append(typeID).append(" - ").append(this.getName()).append(" ");
		if (null != this.getUserLabel()) {
			buffer.append("[").append(this.getUserLabel()).append("] ");
		}
		buffer.append("itemID:").append(assetID).append(" ");
		buffer.append("locationID:").append(locationID).append(" ");
		buffer.append("containerID:").append(parentAssetID).append(" ");
		buffer.append("ownerID:").append(ownerID).append(" ");
		buffer.append("quantity:").append(quantity).append(" ");
		buffer.append("]\n");
		return buffer.toString();
	}

	/**
	 * Replaces a non reachable parent asset into an Unknown Location.
	 * 
	 * @param newlocationid
	 * @return
	 */
	private EveLocation moveAssetToUnknown(final long newlocationid) {
		final EveLocation newundefloc = new EveLocation();
		newundefloc.setId(newlocationid);
		newundefloc.setRegion("SPACE");
		newundefloc.setSystem("Undefined");
		newundefloc.setStation("Station#" + newlocationid);
		// Save this new location ot the database.
		//		try {
		//			Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
		//			// Try to create the pair. It fails then  it was already created.
		//			locationDao.createOrUpdate(newundefloc);
		//		} catch (final SQLException sqle) {
		//			sqle.printStackTrace();
		//			this.setDirty(true);
		//		}
		return newundefloc;
	}
}
//- UNUSED CODE ............................................................................................