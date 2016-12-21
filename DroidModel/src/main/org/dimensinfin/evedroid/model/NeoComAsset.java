//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;
import org.dimensinfin.evedroid.interfaces.INamed;

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
public class NeoComAsset extends AbstractNeoComNode implements /* IAsset, */ INamed {
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
	private double								iskvalue					= 0.0;

	// - C A C H E D   F I E L D S
	private transient NeoComAsset	parentAssetCache	= null;
	private transient EveLocation	locationCache			= null;
	private transient EveItem			itemCache					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComAsset() {
		super();
		id = -2;
		locationID = -1;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Assets should collaborate to the model adding their children. Most of the assets will not have children
	 * but the containers that maybe will use this code or be created as other kind of specialized asset.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results.addAll((Collection<? extends AbstractComplexNode>) this.getChildren());
		return results;
	}

	public long getAssetID() {
		return assetID;
	}

	public String getCategory() {
		if (null == category) return "NOCAT";
		return category;
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

	public double getIskvalue() {
		return iskvalue;
	}

	/**
	 * New optimization will leave this filed for lazy evaluation. So check if this is empty before getting any
	 * access and if so download from the Item Cache.
	 * 
	 * @return
	 */
	public EveItem getItem() {
		if (null == itemCache) {
			itemCache = AppConnector.getDBConnector().searchItembyID(typeID);
		}
		return itemCache;
	}

	public String getItemName() {
		return this.getItem().getName();
	}

	public EveLocation getLocation() {
		if (null == locationCache) {
			locationCache = AppConnector.getDBConnector().searchLocationbyID(locationID);
		}
		return locationCache;
	}

	public long getLocationID() {
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

	public NeoComAsset getParentContainer() {
		if (parentAssetID > 0) if (null == parentAssetCache) {
			parentAssetCache = AppConnector.getDBConnector().searchAssetByID(parentAssetID);
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

	public void setFlag(final int newFlag) {
		flag = newFlag;
	}

	public void setGroupName(final String name) {
		groupName = name;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setIskvalue(final double iskvalue) {
		this.iskvalue = iskvalue;
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
		}
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

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComAsset [");
		buffer.append("#").append(typeID).append(" - ").append(this.getName()).append(" ");
		if (null != this.getUserLabel()) {
			buffer.append("[").append(this.getUserLabel()).append("] ");
		}
		buffer.append("itemID:").append(this.getAssetID()).append(" ");
		//		buffer.append("typeID:")..append(" ");
		buffer.append("locationID:").append(locationID).append(" ");
		buffer.append("ownerID:").append(this.getOwnerID()).append(" ");
		buffer.append("quantity:").append(this.getQuantity()).append(" ");
		buffer.append("]\n");
		return buffer.toString();
	}
}
//- UNUSED CODE ............................................................................................
