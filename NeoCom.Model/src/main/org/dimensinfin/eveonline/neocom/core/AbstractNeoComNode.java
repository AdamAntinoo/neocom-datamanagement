//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.core;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractNeoComNode extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -1735276692612402194L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractNeoComNode() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public abstract ArrayList<AbstractComplexNode> collaborate2Model(final String variant);
}

// - UNUSED CODE ............................................................................................
