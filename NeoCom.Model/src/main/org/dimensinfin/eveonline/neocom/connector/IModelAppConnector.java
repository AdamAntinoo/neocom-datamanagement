//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.connector;

import org.dimensinfin.eveonline.neocom.interfaces.INeoComModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IModelAppConnector {
	//	public boolean checkExpiration(final Instant timestamp, final long window);
	//
	//	public boolean checkExpiration(final long timestamp, final long window);

	public ICacheConnector getCacheConnector();

	public ICCPDatabaseConnector getCCPDBConnector();

	public IDatabaseConnector getDBConnector();

	public INeoComModelStore getModelStore();

	//	public void startChrono();
	//
	//	public Duration timeLapse();
}

// - UNUSED CODE ............................................................................................
