package org.dimensinfin.eveonline.neocom.character.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.character.domain.PilotV1;
import org.dimensinfin.eveonline.neocom.character.domain.PublicPilotV1;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.CHARACTER_SERVICE_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PILOT_LOCATION_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PILOT_WALLET_BALANCE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_CORPORATION_CEO_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_ID;

public class CharacterServiceTest {

	private ESIDataService esiDataService;
	private LocationCatalogService locationCatalogService;
	private ResourceFactory resourceFactory;

	@BeforeEach
	public void beforeEach() {
		this.esiDataService = Mockito.mock( ESIDataService.class );
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.resourceFactory = Mockito.mock( ResourceFactory.class );
	}

	@Test
	public void constructorContract() {
		final CharacterService characterService = new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		Assertions.assertNotNull( characterService );
	}

	@Test
	public void getPilotV1() {
		// Given
		final GetCorporationsCorporationIdOk corporationData = Mockito.mock( GetCorporationsCorporationIdOk.class );
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdOk esiCharacterData = Mockito.mock( GetCharactersCharacterIdOk.class );
		final GetCharactersCharacterIdSkillsOk skillsRecord = Mockito.mock( GetCharactersCharacterIdSkillsOk.class );
		final GetCharactersCharacterIdLocationOk location = Mockito.mock( GetCharactersCharacterIdLocationOk.class );
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );
		final GetCharactersCharacterIdShipOk ship = Mockito.mock( GetCharactersCharacterIdShipOk.class );
		final EsiType type =Mockito.mock(EsiType.class);
		// When
		Mockito.when( this.esiDataService.getCorporationsCorporationId( Mockito.anyInt() ) ).thenReturn( corporationData );
		Mockito.when( esiCharacterData.getCorporationId() ).thenReturn( TEST_PUBLIC_PILOT_CORPORATION_ID );
		Mockito.when( this.esiDataService.getCharactersCharacterId( Mockito.anyInt() ) ).thenReturn( esiCharacterData );
		Mockito.when( this.esiDataService.getCharactersCharacterIdSkills( Mockito.any( Credential.class ) ) )
				.thenReturn( skillsRecord );
		Mockito.when( this.esiDataService.getCharactersCharacterIdWallet( Mockito.any( Credential.class ) ) )
				.thenReturn( TEST_PILOT_WALLET_BALANCE );
		Mockito.when( this.esiDataService.getCharactersCharacterIdLocation( Mockito.any( Credential.class ) ) )
				.thenReturn( location );
		Mockito.when( this.esiDataService.getCharactersCharacterIdShip( Mockito.any( Credential.class ) ) )
				.thenReturn( ship );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_PUBLIC_PILOT_ID );
		Mockito.when( corporationData.getCeoId() ).thenReturn( TEST_PUBLIC_CORPORATION_CEO_ID );
		Mockito.when( location.getStructureId() ).thenReturn( (long) TEST_PILOT_LOCATION_SYSTEM_ID );
		Mockito.when( this.locationCatalogService.searchStructure4Id( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( spaceLocation );
		Mockito.when( location.getSolarSystemId() ).thenReturn( TEST_PILOT_LOCATION_SYSTEM_ID );
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		Mockito.when( this.resourceFactory.generateType4Id(Mockito.anyInt()) ).thenReturn( type );
		// Test
		final CharacterService characterService = new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		final PilotV1 obtained = characterService.getPilotV1( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_ID, obtained.getPilotId() );
		Assertions.assertNotNull(  obtained.getPilotPublicData() );
		Assertions.assertNotNull(  obtained.getCorporationId() );
	}

	@Test
	public void getPilotPublicData() {
		// Given
		final GetCharactersCharacterIdOk esiCharacterData =Mockito.mock(GetCharactersCharacterIdOk.class);
		final GetCorporationsCorporationIdOk corporationData = Mockito.mock( GetCorporationsCorporationIdOk.class );
		// When
		Mockito.when( this.esiDataService.getCharactersCharacterId( Mockito.anyInt() ) ).thenReturn( esiCharacterData );
		Mockito.when( this.esiDataService.getCorporationsCorporationId( Mockito.anyInt() ) ).thenReturn( corporationData );
		Mockito.when( corporationData.getCeoId() ).thenReturn( TEST_PUBLIC_CORPORATION_CEO_ID );
		Mockito.when( esiCharacterData.getCorporationId() ).thenReturn( TEST_PUBLIC_PILOT_CORPORATION_ID );
		// Test
		final CharacterService characterService = new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		final PublicPilotV1 obtained = characterService.getPilotPublicData( TEST_PUBLIC_PILOT_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_ID, obtained.getPilotId() );
		Assertions.assertNotNull(  obtained.getPilotPublicData() );
		Assertions.assertNotNull(  obtained.getCorporationId() );
	}
	@Test
	public void getPilotV1Exception() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		// When
		Mockito.when( this.esiDataService.getCharactersCharacterId( Mockito.anyInt() ) ).thenReturn( null );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_PUBLIC_PILOT_ID );
		// Test
		final CharacterService characterService = new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		final NeoComRuntimeException error = Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			characterService.getPilotV1( credential );
		} );
		Assertions.assertEquals( "The pilot with identifier 91734031 is not found on the ESI repository.", error.getMessage() );
		Assertions.assertEquals( CHARACTER_SERVICE_ERRORCODE, error.getErrorCode() );
	}
}