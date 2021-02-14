package org.dimensinfin.eveonline.neocom.character.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.character.domain.PilotV1;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdSkillsOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

import static org.dimensinfin.eveonline.neocom.exception.ErrorCodesData.CHARACTER_SERVICE_ERRORCODE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotConstants.TEST_PUBLIC_PILOT_ID;

public class CharacterServiceTest {

	private ESIDataService esiDataService;
	private LocationCatalogService locationCatalogService;
	private ResourceFactory resourceFactory;

	@BeforeEach
	public void beforeEach() {
		this.esiDataService = Mockito.mock(ESIDataService.class);
		this.locationCatalogService = Mockito.mock(LocationCatalogService.class);
		this.resourceFactory = Mockito.mock(ResourceFactory.class);
	}

	@Test
	public void getPilotV1() {
		// Given
		final Credential credential=Mockito.mock(Credential.class);
		final GetCharactersCharacterIdOk esiCharacterData =Mockito.mock(GetCharactersCharacterIdOk.class);
		final GetCharactersCharacterIdSkillsOk skillsRecord=Mockito.mock(GetCharactersCharacterIdSkillsOk.class);
		// When
		Mockito.when( this.esiDataService.getCharactersCharacterId(Mockito.anyInt()) ).thenReturn( esiCharacterData );
		Mockito.when( this.esiDataService.getCharactersCharacterIdSkills(Mockito.any(Credential.class)) )
				.thenReturn( skillsRecord );
		// Test
		final CharacterService characterService=new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		final PilotV1 obtained = characterService.getPilotV1( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_PUBLIC_PILOT_ID, obtained.getPilotId() );
	}
	@Test
	public void getPilotV1Exception() {
		// Given
		final Credential credential=Mockito.mock(Credential.class);
		// When
		Mockito.when( this.esiDataService.getCharactersCharacterId(Mockito.anyInt()) ).thenReturn( null );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_PUBLIC_PILOT_ID );
		// Test
		final CharacterService characterService=new CharacterService(
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
	@Test
	public void constructorContract() {
		final CharacterService characterService=new CharacterService(
				this.esiDataService,
				this.locationCatalogService,
				this.resourceFactory
		);
		Assertions.assertNotNull(characterService);
	}
}