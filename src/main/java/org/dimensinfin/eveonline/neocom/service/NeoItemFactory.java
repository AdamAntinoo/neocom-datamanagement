package org.dimensinfin.eveonline.neocom.service;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;

public class NeoItemFactory {
	private static NeoItemFactory singleton = new NeoItemFactory();

	public static NeoItemFactory getSingleton() {
		return singleton;
	}

	public static void setSingleton( final NeoItemFactory factory ) {
		singleton = factory;
	}

	private NeoItemFactory() {}

	public NeoItem getItemById( final Integer itemId ) {
		return new NeoItem( itemId );
	}

}