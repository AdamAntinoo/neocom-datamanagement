//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import com.fasterxml.jackson.annotation.JsonIgnore;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractManager extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= -3012043551959443176L;
	protected static Logger						logger						= Logger.getLogger("AbstractManager");

	// - F I E L D - S E C T I O N ............................................................................
	@JsonIgnore
	private transient NeoComCharacter	pilot							= null;
	protected boolean									initialized				= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractManager(final NeoComCharacter pilot) {
		super();
		this.setPilot(pilot);
		jsonClass = "AbstractManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return new ArrayList<AbstractComplexNode>();
	}

	public int getContentCount() {
		return 0;
	}

	@JsonIgnore
	public NeoComCharacter getPilot() {
		return pilot;
	}

	public abstract AbstractManager initialize();

	/**
	 * Checks if the initialization method and the load of the resources has been already executed.
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}

	public void setPilot(final NeoComCharacter newPilot) {
		pilot = newPilot;
	}

}

// - UNUSED CODE ............................................................................................
