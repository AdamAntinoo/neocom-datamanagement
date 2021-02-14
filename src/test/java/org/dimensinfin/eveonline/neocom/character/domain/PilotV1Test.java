package org.dimensinfin.eveonline.neocom.character.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PILOT_SKILL_POINTS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PILOT_WALLET_BALANCE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_ID;

public class PilotV1Test {
	private GetCharactersCharacterIdOk pilotPublicData;
	private GetUniverseAncestries200Ok ancestryData;
	private GetUniverseBloodlines200Ok bloodlineData;
	private GetUniverseRaces200Ok raceData;
	private PublicCorporationV1 corporation;
	private SpaceLocation location;
	private EsiType shipType;
	private GetCharactersCharacterIdShipOk esiPilotShip;

	@BeforeEach
	public void beforeEach() {
		this.pilotPublicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		this.ancestryData = Mockito.mock( GetUniverseAncestries200Ok.class );
		this.bloodlineData = Mockito.mock( GetUniverseBloodlines200Ok.class );
		this.raceData = Mockito.mock( GetUniverseRaces200Ok.class );
		this.corporation = Mockito.mock( PublicCorporationV1.class );
		this.location = Mockito.mock( SpaceLocation.class );
		this.shipType = Mockito.mock( EsiType.class );
		this.esiPilotShip = Mockito.mock( GetCharactersCharacterIdShipOk.class );
	}

	@Test
	public void buildContract() {
		final PilotV1 pilot = new PilotV1.Builder()
				.withPilotId( TEST_PUBLIC_PILOT_ID )
				.withPilotPublicData( this.pilotPublicData )
				.withAncestryData( this.ancestryData )
				.withBloodlineData( this.bloodlineData )
				.withRaceData( this.raceData )
				.withCorporation( this.corporation )
				.withLastKnownLocation( this.location )
				.withCurrentShipType( this.shipType )
				.withCurrentShip( this.esiPilotShip )
				.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
				.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
				.build();
		Assertions.assertNotNull( pilot );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( null )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.withLastKnownLocation( this.location )
					.withCurrentShipType( this.shipType )
					.withCurrentShip( this.esiPilotShip )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( null )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.withLastKnownLocation( this.location )
					.withCurrentShipType( this.shipType )
					.withCurrentShip( this.esiPilotShip )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( null )
					.withLastKnownLocation( this.location )
					.withCurrentShipType( this.shipType )
					.withCurrentShip( this.esiPilotShip )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.withLastKnownLocation( null )
					.withCurrentShipType( this.shipType )
					.withCurrentShip( this.esiPilotShip )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.withLastKnownLocation( this.location )
					.withCurrentShipType( null )
					.withCurrentShip( this.esiPilotShip )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotV1.Builder()
					.withPilotId( TEST_PUBLIC_PILOT_ID )
					.withPilotPublicData( this.pilotPublicData )
					.withAncestryData( this.ancestryData )
					.withBloodlineData( this.bloodlineData )
					.withRaceData( this.raceData )
					.withCorporation( this.corporation )
					.withLastKnownLocation( this.location )
					.withCurrentShipType( this.shipType )
					.withCurrentShip( null )
					.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
					.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		// Test
		final PilotV1 pilot = new PilotV1.Builder()
				.withPilotId( TEST_PUBLIC_PILOT_ID )
				.withPilotPublicData( this.pilotPublicData )
				.withAncestryData( this.ancestryData )
				.withBloodlineData( this.bloodlineData )
				.withRaceData( this.raceData )
				.withCorporation( this.corporation )
				.withLastKnownLocation( this.location )
				.withCurrentShipType( this.shipType )
				.withCurrentShip( this.esiPilotShip )
				.withTotalSkillPoints( TEST_PILOT_SKILL_POINTS )
				.withWalletBalance( TEST_PILOT_WALLET_BALANCE )
				.build();
		// Assertions
		Assertions.assertNotNull( pilot.getCurrentShip() );
		Assertions.assertNotNull( pilot.getCurrentShipType() );
		Assertions.assertNotNull( pilot.getLastKnownLocation() );
		Assertions.assertEquals( TEST_PILOT_SKILL_POINTS, pilot.getTotalSkillpoints() );
		Assertions.assertEquals( TEST_PILOT_WALLET_BALANCE, pilot.getWalletBalance() );
	}
}