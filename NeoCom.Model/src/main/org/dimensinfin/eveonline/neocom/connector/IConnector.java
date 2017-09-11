//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.connector;

import org.dimensinfin.eveonline.neocom.interfaces.INeoComModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IConnector {

	public void addCharacterUpdateRequest(long characterID);

	//	public boolean checkExpiration(final long timestamp, final long window);

	//	public String getAppFilePath(int fileresourceid);

	//	public String getAppFilePath(String fileresourceid);

	public ICCPDatabaseConnector getCCPDBConnector();

	public IDatabaseConnector getDBConnector();

	//	public NeocomPreferences getDefaultSharedPreferences();

	public INeoComModelStore getModelStore();

	//	public String getResourceString(int reference);

	public IConnector getSingleton();

	public IStorageConnector getStorageConnector();

	//	public boolean sdcardAvailable();
}

// - UNUSED CODE ............................................................................................
