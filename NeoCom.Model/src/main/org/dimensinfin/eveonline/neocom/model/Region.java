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

// - CLASS IMPLEMENTATION ...................................................................................
public class Region extends Separator {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3623925848703776069L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * If the region id is -1 this means that this is probable coming from an space structure not registered on
	 * CCP data. So we can assume that this is a User Structure in an unknown place of space.
	 * 
	 * @param regionid
	 * @param regionName
	 */
	public Region(final long regionid, final String regionName) {
		super(regionName);
		jsonClass = "Region";
		// If undefined update the name.
		if (-1 == regionid) {
			this.setTitle("-DEEP SPACE-");
		}
	}

	public Region(final String title) {
		super(title);
		jsonClass = "Region";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
