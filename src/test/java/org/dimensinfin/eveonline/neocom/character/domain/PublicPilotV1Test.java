package org.dimensinfin.eveonline.neocom.character.domain;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_BIRTHDATE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_DESCRIPTION;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_FACTION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_SECURITY_STATUS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_TITLE;

public class PublicPilotV1Test {
	private GetCharactersCharacterIdOk pilotPublicData;
	private GetUniverseAncestries200Ok ancestryData;
	private GetUniverseBloodlines200Ok bloodlineData;
	private GetUniverseRaces200Ok raceData;
	private PublicCorporationV1 corporation;

	@BeforeEach
	public void beforeEach() {
		this.pilotPublicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		this.ancestryData = Mockito.mock( GetUniverseAncestries200Ok.class );
		this.bloodlineData = Mockito.mock( GetUniverseBloodlines200Ok.class );
		this.raceData = Mockito.mock( GetUniverseRaces200Ok.class );
		this.corporation = Mockito.mock( PublicCorporationV1.class );
	}

	@Test
	public void buildContract() {
		final PublicPilotV1 pilot = new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
				.withPilotId( TEST_PUBLIC_PILOT_ID )
				.withPilotPublicData( this.pilotPublicData )
				.withAncestryData( this.ancestryData )
				.withBloodlineData( this.bloodlineData )
				.withRaceData( this.raceData )
				.withCorporation( this.corporation )
				.build();
		Assertions.assertNotNull( pilot );
	}

	@Test
	public void buildFailureMissing() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
					.withPilotId( null )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( null )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
					.withPilotId( null )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( null )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( null )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.pilotPublicData.getBirthday() ).thenReturn( TEST_PUBLIC_PILOT_BIRTHDATE );
		Mockito.when( this.corporation.getCorporationId() ).thenReturn( TEST_PUBLIC_PILOT_CORPORATION_ID );
		Mockito.when( this.pilotPublicData.getDescription() ).thenReturn( TEST_PUBLIC_PILOT_DESCRIPTION );
		Mockito.when( this.pilotPublicData.getFactionId() ).thenReturn( TEST_PUBLIC_PILOT_FACTION_ID );
		Mockito.when( this.pilotPublicData.getGender() ).thenReturn( GetCharactersCharacterIdOk.GenderEnum.MALE );
		Mockito.when( this.pilotPublicData.getName() ).thenReturn( TEST_PUBLIC_PILOT_NAME );
		Mockito.when( this.pilotPublicData.getSecurityStatus() ).thenReturn( TEST_PUBLIC_PILOT_SECURITY_STATUS );
		Mockito.when( this.pilotPublicData.getTitle() ).thenReturn( TEST_PUBLIC_PILOT_TITLE );
		// Test
		final PublicPilotV1 pilot = new PublicPilotV1.Builder<PublicPilotV1, PublicPilotV1.Builder>()
				.withPilotId( TEST_PUBLIC_PILOT_ID )
				.withPilotPublicData( this.pilotPublicData )
				.withAncestryData( this.ancestryData )
				.withBloodlineData( this.bloodlineData )
				.withRaceData( this.raceData )
				.withCorporation( this.corporation )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_PUBLIC_PILOT_ID, pilot.getPilotId() );
		Assertions.assertNotNull( pilot.getPilotPublicData() );
		Assertions.assertNotNull( pilot.getAncestryData() );
		Assertions.assertNotNull( pilot.getBloodlineData() );
		Assertions.assertNotNull( pilot.getRaceData() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_BIRTHDATE, pilot.getBirthday() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_CORPORATION_ID, pilot.getCorporationId() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_DESCRIPTION, pilot.getDescription() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_FACTION_ID, pilot.getFactionId() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_FACTION_ID, pilot.getFactionId() );
		Assertions.assertEquals( GetCharactersCharacterIdOk.GenderEnum.MALE, pilot.getGender() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_NAME, pilot.getName() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_SECURITY_STATUS, pilot.getSecurityStatus() );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_TITLE, pilot.getTitle() );
		Assertions.assertEquals( "https://image.eveonline.com/Character/91734031_256.jpg", pilot.getUrl4Icon() );
	}
}