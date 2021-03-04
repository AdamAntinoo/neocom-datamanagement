package org.dimensinfin.eveonline.neocom.character.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

import static org.dimensinfin.eveonline.neocom.character.domain.PublicCorporationV1.CORPORATION_ICON_URL_PREFIX;
import static org.dimensinfin.eveonline.neocom.character.domain.PublicCorporationV1.CORPORATION_ICON_URL_SUFFIX;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CorporationsConstants.TEST_CORPORATION_CEO_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CorporationsConstants.TEST_CORPORATION_ID;

public class PublicCorporationV1Test {
	@Test
	public void buildContract() {
		final GetCorporationsCorporationIdOk publicData = Mockito.mock( GetCorporationsCorporationIdOk.class );
		final GetAlliancesAllianceIdOk alliance = Mockito.mock( GetAlliancesAllianceIdOk.class );
		final PublicCorporationV1 publicCorporationV1 = new PublicCorporationV1.Builder()
				.withCorporationId( TEST_CORPORATION_ID )
				.withCeoPilotId( TEST_CORPORATION_CEO_ID )
				.withCorporationPublicData( publicData )
				.withAlliance( alliance )
				.build();
		Assertions.assertNotNull( publicCorporationV1 );
	}

	@Test
	public void gettersContract() {
		final GetCorporationsCorporationIdOk publicData = Mockito.mock( GetCorporationsCorporationIdOk.class );
		final GetAlliancesAllianceIdOk alliance = Mockito.mock( GetAlliancesAllianceIdOk.class );
		// Test
		final PublicCorporationV1 publicCorporationV1 = new PublicCorporationV1.Builder()
				.withCorporationId( TEST_CORPORATION_ID )
				.withCeoPilotId( TEST_CORPORATION_CEO_ID )
				.withCorporationPublicData( publicData )
				.withAlliance( alliance )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_CORPORATION_CEO_ID, publicCorporationV1.getCeoId() );
		Assertions.assertNotNull( publicCorporationV1.getAlliance() );
		Assertions.assertNotNull( publicCorporationV1.getCorporationPublicData() );
		Assertions.assertEquals( TEST_CORPORATION_ID, publicCorporationV1.getCorporationId() );
		Assertions.assertEquals( CORPORATION_ICON_URL_PREFIX + TEST_CORPORATION_ID + CORPORATION_ICON_URL_SUFFIX, publicCorporationV1.getUrl4Icon() );
	}
}