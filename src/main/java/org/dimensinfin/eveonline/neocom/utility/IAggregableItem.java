package org.dimensinfin.eveonline.neocom.utility;

import org.dimensinfin.core.interfaces.ICollaboration;

public interface IAggregableItem extends ICollaboration {
	int getQuantity();
	double getVolume();
	double getPrice();
}
