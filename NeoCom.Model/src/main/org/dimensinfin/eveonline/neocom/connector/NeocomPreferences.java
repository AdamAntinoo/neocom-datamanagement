//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeocomPreferences {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeocomPreferences.java");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeocomPreferences() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getString(final String prefname, final String prefdefault) {
		String value = R.getResourceString(prefname);
		if (null == value)
			return prefdefault;
		else
			return value;
	}

}

// - UNUSED CODE ............................................................................................
