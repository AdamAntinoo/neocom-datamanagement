//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.interfaces;

// - IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public interface INeoComDirector extends IDirector {
	//	public abstract boolean checkActivation(EveChar pilot);

	public abstract int getIconReferenceActive();

	public abstract int getIconReferenceInactive();

	public abstract String getName();
}

// - UNUSED CODE ............................................................................................
