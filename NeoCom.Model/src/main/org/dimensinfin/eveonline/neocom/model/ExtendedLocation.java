//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.interfaces.IContentManager;
import org.dimensinfin.eveonline.neocom.manager.DefaultAssetsContentManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class ExtendedLocation extends EveLocation {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -4484922266027865406L;
	private static Logger			logger						= Logger.getLogger("ExtendedLocation.java");
	//	public static ExtendedLocation copyFrom(EveLocation target) {
	//		}

	// - F I E L D - S E C T I O N ............................................................................
	private EveLocation				delegate					= null;
	private IContentManager		contentManager		= new DefaultAssetsContentManager();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ExtendedLocation(final EveLocation delegate) {
		this.delegate = delegate;
		jsonClass = "ExtendedLocation";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int addContent(final NeoComAsset asset) {
		if (null != contentManager)
			return contentManager.add(asset);
		else
			return 0;
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return (ArrayList<AbstractComplexNode>) contentManager.collaborate2Model(variant);
	}

	@Override
	public String getConstellation() {
		return delegate.getConstellation();
	}

	@Override
	public long getConstellationID() {
		return delegate.getConstellationID();
	}

	/**
	 * The identifier to get the contents can change depending on the Locattion type. I have found that for
	 * Citadels the resources are under the <code>parentAssetID</code> and not the <code>locationID</code>.
	 * 
	 * @param download
	 * @return
	 */
	//	@JsonIgnore
	public List<NeoComAsset> getContents() {
		if (null != contentManager)
			return contentManager.getContents();
		else
			return new ArrayList<NeoComAsset>();
	}

	public int getContentSize() {
		if (null != contentManager)
			return contentManager.getContentSize();
		else
			return 0;
	}

	@Override
	public String getFullLocation() {
		return delegate.getFullLocation();
	}

	@Override
	public long getID() {
		return delegate.getID();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public long getRealId() {
		return delegate.getRealId();
	}

	@Override
	public String getRegion() {
		return delegate.getRegion();
	}

	@Override
	public long getRegionID() {
		return delegate.getRegionID();
	}

	@Override
	public String getSecurity() {
		return delegate.getSecurity();
	}

	@Override
	public double getSecurityValue() {
		return delegate.getSecurityValue();
	}

	@Override
	public String getStation() {
		return delegate.getStation();
	}

	@Override
	public long getStationID() {
		return delegate.getStationID();
	}

	@Override
	public String getSystem() {
		return delegate.getSystem();
	}

	@Override
	public long getSystemID() {
		return delegate.getSystemID();
	}

	@Override
	public ELocationType getTypeID() {
		return delegate.getTypeID();
	}

	@Override
	public String getUrlLocationIcon() {
		return delegate.getUrlLocationIcon();
	}

	@Override
	public boolean isDownloaded() {
		return delegate.isDownloaded();
	}

	@Override
	public boolean isEmpty() {
		if (null != contentManager)
			return contentManager.isEmpty();
		else
			return true;
	}

	@Override
	public boolean isExpanded() {
		return delegate.isExpanded();
	}

	@Override
	public boolean isRenderWhenEmpty() {
		return delegate.isRenderWhenEmpty();
	}

	@Override
	public boolean isVisible() {
		return delegate.isVisible();
	}

	@Override
	public void setConstellation(final String constellation) {
		delegate.setConstellation(constellation);
	}

	@Override
	public void setConstellationID(final long constellationID) {
		delegate.setConstellationID(constellationID);
	}

	public void setContentManager(final IContentManager manager) {
		contentManager = manager;
	}

	@Override
	public AbstractComplexNode setDownloaded(final boolean downloadedstate) {
		if (null == delegate)
			return this;
		else
			return delegate.setDownloaded(downloadedstate);
	}

	@Override
	public AbstractComplexNode setExpanded(final boolean newState) {
		return delegate.setExpanded(newState);
	}

	@Override
	public boolean toggleExpanded() {
		return delegate.toggleExpanded();
	}

	@Override
	public boolean toggleVisible() {
		return delegate.toggleVisible();
	}
}

// - UNUSED CODE ............................................................................................