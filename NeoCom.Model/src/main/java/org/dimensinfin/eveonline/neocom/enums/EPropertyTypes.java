//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.enums;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public enum EPropertyTypes {
	UNDEFINED, USERLABEL, MANUFACTUREACTION, LOCATIONROLE;

	public static EPropertyTypes decode(final String propertyName) {
		if (propertyName.equalsIgnoreCase("USERLABEL")) return USERLABEL;
		if (propertyName.equalsIgnoreCase("MANUFACTUREACTION")) return MANUFACTUREACTION;
		if (propertyName.equalsIgnoreCase("LOCATIONROLE")) return LOCATIONROLE;
		return UNDEFINED;
	}
}

// - UNUSED CODE ............................................................................................