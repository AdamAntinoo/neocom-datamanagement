package org.dimensinfin.eveonline.neocom.utility;

public enum StorageUnits {
	KILOBYTES( 1024L ),
	MEGABYTES( KILOBYTES.toBytes(1) * 1024L ),
	GIGABYTES( MEGABYTES.toBytes(1) * 1024L );
	private Long bytes;

	StorageUnits( final Long bytes ) {
		this.bytes = bytes;
	}

	public Long toBytes( final int multiplier ) {
		return this.bytes * multiplier;
	}
}
