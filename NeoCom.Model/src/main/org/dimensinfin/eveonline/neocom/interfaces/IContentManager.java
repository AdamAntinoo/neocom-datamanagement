//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.interfaces;

import java.util.List;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IContentManager {

	public int add(NeoComAsset child);

	public List<AbstractComplexNode> collaborate2Model(String variant);

	public List<NeoComAsset> getContents();

	public int getContentSize();

	public boolean isEmpty();
}

// - UNUSED CODE ............................................................................................