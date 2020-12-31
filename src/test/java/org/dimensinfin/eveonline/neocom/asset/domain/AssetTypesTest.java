package org.dimensinfin.eveonline.neocom.asset.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssetTypesTest {

	@Test
	void getTypeName() {
		final String expected = "Blueprint";
		Assertions.assertEquals( expected, AssetTypes.BLUEPRINT.getTypeName() );
	}
}
