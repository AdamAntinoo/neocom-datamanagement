package org.dimensinfin.eveonline.neocom.character.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdLocationOk;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class GetCharactersCharacterIdLocationToSpaceLocationConverterTest {

	private LocationCatalogService locationCatalogService;
	private Credential credential;

	@BeforeEach
	public void beforeEach() {
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.credential = Mockito.mock( Credential.class );
	}

	@Test
	public void constructorContract() {
		final GetCharactersCharacterIdLocationToSpaceLocationConverter converter = new GetCharactersCharacterIdLocationToSpaceLocationConverter(
				this.locationCatalogService,
				this.credential
		);
		Assertions.assertNotNull( converter );
	}

	@Test
	public void convertStation() {
		final Integer TEST_LOCATION_STATION_ID = 432876;
		final SpaceLocation station = Mockito.mock( SpaceLocation.class );
		// Given
		final GetCharactersCharacterIdLocationOk location = Mockito.mock( GetCharactersCharacterIdLocationOk.class );
		// When
		Mockito.when( location.getStructureId() ).thenReturn( null );
		Mockito.when( location.getStationId() ).thenReturn( TEST_LOCATION_STATION_ID );
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) )
				.thenReturn( station );
		Mockito.when( station.getLocationId() ).thenReturn( (long) TEST_LOCATION_STATION_ID );
		Mockito.when( station.getLocationType() ).thenReturn( LocationIdentifierType.STATION );
		// Test
		final GetCharactersCharacterIdLocationToSpaceLocationConverter converter = new GetCharactersCharacterIdLocationToSpaceLocationConverter(
				this.locationCatalogService,
				this.credential
		);
		final SpaceLocation obtained = converter.convert( location );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals((long) TEST_LOCATION_STATION_ID, obtained.getLocationId() );
		Assertions.assertEquals( LocationIdentifierType.STATION, obtained.getLocationType() );
	}
	@Test
	public void convert() {
		final Integer TEST_LOCATION_ID = 432876;
		final SpaceLocation station = Mockito.mock( SpaceLocation.class );
		// Given
		final GetCharactersCharacterIdLocationOk location = Mockito.mock( GetCharactersCharacterIdLocationOk.class );
		// When
		Mockito.when( location.getStructureId() ).thenReturn( null );
		Mockito.when( location.getStationId() ).thenReturn( null );
		Mockito.when( this.locationCatalogService.searchLocation4Id( Mockito.anyLong() ) )
				.thenReturn( station );
		Mockito.when( station.getLocationId() ).thenReturn( (long) TEST_LOCATION_ID );
		Mockito.when( station.getLocationType() ).thenReturn( LocationIdentifierType.STATION );
		// Test
		final GetCharactersCharacterIdLocationToSpaceLocationConverter converter = new GetCharactersCharacterIdLocationToSpaceLocationConverter(
				this.locationCatalogService,
				this.credential
		);
		final SpaceLocation obtained = converter.convert( location );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals((long) TEST_LOCATION_ID, obtained.getLocationId() );
		Assertions.assertEquals( LocationIdentifierType.STATION, obtained.getLocationType() );
	}

	@Test
	public void convertStructure() {
		final Long TEST_LOCATION_STRUCTURE_ID = 432876L;
		final SpaceLocation structure = Mockito.mock( SpaceLocation.class );
		// Given
		final GetCharactersCharacterIdLocationOk location = Mockito.mock( GetCharactersCharacterIdLocationOk.class );
		// When
		Mockito.when( location.getStructureId() ).thenReturn( TEST_LOCATION_STRUCTURE_ID );
		Mockito.when( this.locationCatalogService.searchStructure4Id( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( structure );
		Mockito.when( structure.getLocationId() ).thenReturn( TEST_LOCATION_STRUCTURE_ID );
		Mockito.when( structure.getLocationType() ).thenReturn( LocationIdentifierType.STRUCTURE );
		// Test
		final GetCharactersCharacterIdLocationToSpaceLocationConverter converter = new GetCharactersCharacterIdLocationToSpaceLocationConverter(
				this.locationCatalogService,
				this.credential
		);
		final SpaceLocation obtained = converter.convert( location );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( TEST_LOCATION_STRUCTURE_ID, obtained.getLocationId() );
		Assertions.assertEquals( LocationIdentifierType.STRUCTURE, obtained.getLocationType() );
	}
}