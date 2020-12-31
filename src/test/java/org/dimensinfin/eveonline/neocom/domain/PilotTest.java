package org.dimensinfin.eveonline.neocom.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PilotTest {
	private Pilot pilot4Test;
	private GetCharactersCharacterIdOk publicData;
	private DateTime birthDate = DateTime.now();

	@Before
	public void setUp() throws Exception {
		publicData = new GetCharactersCharacterIdOk();
		publicData.setCorporationId( 98765 );
		publicData.setBirthday( birthDate );
		publicData.setRaceId( 100 );
		publicData.setAncestryId( 200 );
		publicData.setBloodlineId( 300 );
		publicData.setDescription( "-TEST-DESCRIPTION-" );
		publicData.setGender( GetCharactersCharacterIdOk.GenderEnum.MALE );
		publicData.setName( "-TEST-PILOT-NAME-" );
		publicData.setSecurityStatus( 0.5F );
		final Credential credential = Mockito.mock( Credential.class );
		final GetUniverseRaces200Ok raceData = Mockito.mock( GetUniverseRaces200Ok.class );
		Mockito.when( raceData.getName() ).thenReturn( "-TEST-RACE-NAME" );
		final GetUniverseAncestries200Ok ancestryData = Mockito.mock( GetUniverseAncestries200Ok.class );
		final GetUniverseBloodlines200Ok bloodlineData = Mockito.mock( GetUniverseBloodlines200Ok.class );
		pilot4Test = new Pilot.Builder()
				.withPilotIdentifier( 123456 )
				.withCharacterPublicData( publicData )
				.withRaceData( raceData )
				.withAncestryData( ancestryData )
				.withBloodlineData( bloodlineData )
				.build();
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( Pilot.class ).verify();
	}

	@Test
	public void gettersContract() {
		Assert.assertNotNull( pilot4Test );
		Assert.assertEquals( 123456, pilot4Test.getPilotId());
		Assert.assertNotNull( pilot4Test.getRace() );
		Assert.assertNotNull( pilot4Test.getAncestry() );
		Assert.assertNotNull( pilot4Test.getBloodline() );
		Assert.assertEquals( "-TEST-RACE-NAME", pilot4Test.getRaceName() );
		Assert.assertEquals( birthDate, pilot4Test.getBirthday() );
		Assert.assertEquals( 100, pilot4Test.getRaceId().intValue() );
		Assert.assertEquals( 200, pilot4Test.getAncestryId().intValue() );
		Assert.assertEquals( 300, pilot4Test.getBloodlineId().intValue() );
		Assert.assertEquals( "-TEST-DESCRIPTION-", pilot4Test.getDescription() );
		Assert.assertEquals( GetCharactersCharacterIdOk.GenderEnum.MALE.name(), pilot4Test.getGender() );
		Assert.assertEquals( "-TEST-PILOT-NAME-", pilot4Test.getName() );
		Assert.assertEquals( 0.5F, pilot4Test.getSecurityStatus(), 0.1 );
		Assert.assertEquals( "https://image.eveonline.com/Character/123456_256.jpg", pilot4Test.getUrl4Icon() );
	}

	@Test
	public void gettersContractFailure() {
		final Pilot emptyPilot = new Pilot.Builder()
				.withPilotIdentifier( 123456 )
				.withCharacterPublicData( publicData )
				.build();
		Assert.assertEquals( "-", emptyPilot.getRaceName() );
	}

	@Test
	public void settersContract() {
		final GetCharactersCharacterIdOk publicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		final GetUniverseRaces200Ok race = Mockito.mock( GetUniverseRaces200Ok.class );
		final GetUniverseAncestries200Ok ancestry = Mockito.mock( GetUniverseAncestries200Ok.class );
		final GetUniverseBloodlines200Ok bloodline = Mockito.mock( GetUniverseBloodlines200Ok.class );
		pilot4Test.setCharacterPublicData( publicData );
		pilot4Test.setRaceData( race );
		pilot4Test.setAncestryData( ancestry );
		pilot4Test.setBloodlineData( bloodline );
		Assert.assertNotNull( pilot4Test.getRace() );
		Assert.assertNotNull( pilot4Test.getAncestry() );
		Assert.assertNotNull( pilot4Test.getBloodline() );
	}

	@Test
	public void getCorporationId() {
		final int expected = 98765;
		Assert.assertEquals( expected, pilot4Test.getCorporationId() );
	}

	@Test
	public void getJsonClass() {
		Assert.assertEquals( "Pilot", pilot4Test.getJsonClass() );
	}
}