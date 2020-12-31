package org.dimensinfin.eveonline.neocom.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

public class CorporationTest {
	private Corporation corporationNoAlliance;
	private Corporation corporationInAlliance;
	private GetCorporationsCorporationIdOk publicData;
	private Pilot ceo;

	@Before
	public void setUp() throws Exception {
		publicData = new GetCorporationsCorporationIdOk();
		publicData.setAllianceId( 117383987 );
		publicData.setCeoId( 92223647 );
		final GetCharactersCharacterIdOk ceoPublicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		final Credential credential = Mockito.mock( Credential.class );
		final GetAlliancesAllianceIdOk alliance = Mockito.mock( GetAlliancesAllianceIdOk.class );
		ceo = new Pilot.Builder()
				.withPilotIdentifier( 123456 )
				.withCharacterPublicData( ceoPublicData )
				.withRaceData( null )
				.withAncestryData( null )
				.withBloodlineData( null )
				.build();
		corporationNoAlliance = new Corporation.Builder()
				.withCorporationId( 98384726 )
				.withCorporationPublicData( publicData )
				.withCeoPilotData( ceo )
				.build();
		corporationInAlliance = new Corporation.Builder()
				.withCorporationId( 98384726 )
				.withCorporationPublicData( publicData )
				.withCeoPilotData( ceo )
				.optionslAlliance( alliance )
				.build();
	}

	@Test
	public void gettersContract() {
		Assert.assertNotNull( corporationNoAlliance );
		Assert.assertNotNull( corporationNoAlliance.getCorporationPublicData() );
		Assert.assertNotNull( corporationNoAlliance.getCeoPilotData() );
		Assert.assertNull( corporationNoAlliance.getAlliance() );
		Assert.assertEquals( 98384726, corporationNoAlliance.getCorporationId().intValue() );
		Assert.assertEquals( 117383987, corporationNoAlliance.getAllianceId().intValue() );
		Assert.assertEquals( 123456, corporationNoAlliance.getCeoPilotData().getPilotId() );

		Assert.assertNotNull( corporationInAlliance );
		Assert.assertNotNull( corporationInAlliance.getCorporationPublicData() );
		Assert.assertNotNull( corporationInAlliance.getCeoPilotData() );
		Assert.assertNotNull( corporationInAlliance.getAlliance() );
		Assert.assertEquals( "http://image.eveonline.com/Corporation/98384726_64.png",
				corporationInAlliance.getUrl4Icon() );
	}

	@Test
	public void needsRefresh() {
		Assert.assertTrue( corporationInAlliance.needsRefresh() );
		corporationNoAlliance.timeStamp();
		Assert.assertFalse( corporationNoAlliance.needsRefresh() );
	}

	@Test
	public void collaborate2Model() {
		final List<ICollaboration> collaboration = corporationInAlliance.collaborate2Model( "-TEST-VARIANT-" );
		Assert.assertTrue( collaboration.size() == 0 );
	}
}