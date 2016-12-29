//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.connector.AppConnector;

import com.beimin.eveapi.model.eve.Station;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.nikr.eve.jeveasset.data.Citadel;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class encapsulates the concept of Eve Location. There are different types of locations and the new CCP
 * release is adding even more. The first concept is Region. This is when the constellation and system are not
 * defined. The second level is constellation, then system and inside a system we can find some more elements.
 * <br>
 * Once we have the system id we have to check if the Location is a point in space, a NPC station (in the CCP
 * database catalog), a corporation Outpost (in the list of outposts) or it can be a Player Structure. Now we
 * have to differentiate the player structures and also this can change because POS are going to be replaced
 * by Citadels. I have a reference to get the list of Citadels until that API entry point is available on the
 * new CCP api. <br>
 * Once we know the type then we check if on the database cache and add or update as needed.
 * 
 * @author Adam Antinoo
 */
@DatabaseTable(tableName = "Locations")
public class EveLocation extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1522765618286937377L;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	private long							id								= -2;
	//	private transient String	location					= "<LOCATION-UNDEFINED>";
	@DatabaseField
	private long							stationID					= -1;
	@DatabaseField
	private String						station						= "<STATION>";
	@DatabaseField
	private long							systemID					= -1;
	@DatabaseField
	private String						system						= "<SYSTEM>";
	@DatabaseField
	private long							constellationID		= -1;
	@DatabaseField
	private String						constellation			= "<CONSTELLATION>";
	@DatabaseField
	private long							regionID					= -1;
	@DatabaseField
	private String						region						= "<REGION>";
	@DatabaseField
	private String						security					= "0.0";
	@DatabaseField
	protected int							typeID						= -1;
	protected boolean					citadel						= false;
	//	private final boolean empty=true;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveLocation() {
	}

	public EveLocation(final long locationID) {
		stationID = locationID;
	}

	//		public EveLocation(Citadel citadel) {
	public EveLocation(final long citadelid, final Citadel cit) {
		try {
			Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
			// calculate the ocationID from the sure item and update the rest of the fields.
			this.updateFromCitadel(citadelid, cit);
			id = citadelid;
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	public EveLocation(final Outpost out) {
		try {
			Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(out.getSolarSystem());
			id = out.getFacilityID();
			this.setStation(out.getName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	public EveLocation(final Station station) {
		try {
			Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(station.getSolarSystemID());
			id = station.getStationID();
			this.setStation(station.getStationName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.setDirty(true);
		}
	}

	/**
	 * Ship locations collaborate to the model by adding all their children because we store there the items
	 * located at the selected real location.
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results.addAll((Collection<? extends AbstractComplexNode>) this.getChildren());
		return results;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean equals(final EveLocation obj) {
		if (!this.getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!this.getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!this.getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	public String getConstellation() {
		return constellation;
	}

	public long getConstellationID() {
		return constellationID;
	}

	public String getFullLocation() {
		return "[" + security + "] " + station + " - " + region + " > " + system;
	}

	public long getId() {
		return id;
	}

	public long getID() {
		return Math.max(Math.max(Math.max(stationID, systemID), constellationID), regionID);
	}

	/**
	 * This return some understandable location name. This is not valid for most locations that are not
	 * stations.
	 * 
	 * @return
	 */
	public String getName() {
		return system + " - " + station;
	}

	public String getRegion() {
		return region;
	}

	public long getRegionID() {
		return regionID;
	}

	public String getSecurity() {
		return security;
	}

	public double getSecurityValue() {
		try {
			return Double.parseDouble(security);
		} catch (RuntimeException rtex) {
		}
		return 0.0;
	}

	public String getStation() {
		return station;
	}

	public long getStationID() {
		return stationID;
	}

	public String getSystem() {
		return system;
	}

	public long getSystemID() {
		return systemID;
	}

	public final boolean isCitadel() {
		return citadel;
	}

	public final boolean isRegion() {
		return ((this.getStationID() == 0) && (this.getSystemID() == 0) && (this.getRegionID() != 0));
	}

	public final boolean isStation() {
		return ((this.getStationID() != 0) && (this.getSystemID() != 0) && (this.getRegionID() != 0));
	}

	public final boolean isSystem() {
		return ((this.getStationID() == 0) && (this.getSystemID() != 0) && (this.getRegionID() != 0));
	}

	public void setConstellation(final String constellation) {
		this.constellation = constellation;
	}

	public void setConstellationID(final long constellationID) {
		this.constellationID = constellationID;
	}

	@Override
	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<EveLocation, String> locationDao = AppConnector.getDBConnector().getLocationDAO();
				locationDao.update(this);
				//		logger.finest("-- Wrote blueprint to database id [" + blueprint.getAssetID() + "]");
			} catch (final SQLException sqle) {
				//		logger.severe("E> Unable to create the new blueprint [" + blueprint.getAssetID() + "]. " + sqle.getMessage());
				sqle.printStackTrace();
			}
		}
	}

	public void setId(final long newid) {
		id = newid;
	}

	public void setLocationID(final long stationID) {
		this.stationID = stationID;
		//		setDirty(true);
	}

	public void setRegion(final String region) {
		this.region = region;
		//		setDirty(true);
	}

	public void setRegionID(final long regionID) {
		this.regionID = regionID;
		//		setDirty(true);
	}

	public void setSecurity(final String security) {
		this.security = security;
		//		setDirty(true);
	}

	public void setStation(final String station) {
		this.station = station;
		//		setDirty(true);
	}

	public void setStationID(final long stationID) {
		this.stationID = stationID;
	}

	public void setSystem(final String system) {
		this.system = system;
		//		setDirty(true);
	}

	public void setSystemID(final long systemID) {
		this.systemID = systemID;
		//		setDirty(true);
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
		//		setDirty(true);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("NeoComLocation [");
		buffer.append("#").append(this.getID()).append(" ");
		buffer.append("(").append(this.getChildren().size()).append(") ");
		buffer.append("[").append(this.getRegion()).append("] ");
		if (null != system) {
			buffer.append("system: ").append(system).append(" ");
		}
		if (null != station) {
			buffer.append("station: ").append(station).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void updateFromCitadel(final long id, final Citadel cit) {
		this.updateFromSystem(cit.systemId);
		//MyLocation myloc = citadel.getLocation(id);
		// Copy the data from the citadel location.
		stationID = id;
		station = cit.name;
		systemID = cit.systemId;
		citadel = true;
	}

	private void updateFromSystem(final long id) {
		// Get the system information from the CCP location tables.
		EveLocation systemLocation = AppConnector.getDBConnector().searchLocationbyID(id);
		systemID = systemLocation.getSystemID();
		system = systemLocation.getSystem();
		constellationID = systemLocation.getConstellationID();
		constellation = systemLocation.getConstellation();
		regionID = systemLocation.getRegionID();
		region = systemLocation.getRegion();
		security = systemLocation.getSecurity();
	}

	private void updateLocationID() {
		if (citadel) {
			id = stationID;
		} else {
			id = this.getID();
		}
	}
}

// - UNUSED CODE ............................................................................................
