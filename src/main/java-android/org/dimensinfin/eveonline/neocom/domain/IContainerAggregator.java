package org.dimensinfin.eveonline.neocom.domain;

public interface IContainerAggregator<T> {
	int getContentSize();

	int addPack( final T itemPack );
}
