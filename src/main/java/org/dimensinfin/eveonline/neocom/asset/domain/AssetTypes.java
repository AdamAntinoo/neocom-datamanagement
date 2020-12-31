package org.dimensinfin.eveonline.neocom.asset.domain;

public enum AssetTypes {
	BLUEPRINT("Blueprint"),
	SHIP( "Ship" ),
	CONTAINER( "Container" );

	String typeName;

	AssetTypes( final String typeName ) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return this.typeName;
	}
}