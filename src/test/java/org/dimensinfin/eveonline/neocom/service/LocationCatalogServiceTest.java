package org.dimensinfin.eveonline.neocom.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;
import org.dimensinfin.eveonline.neocom.support.InstanceGenerator;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_ID;

public class LocationCatalogServiceTest {
	private ILocationFactoryPort locationFactory;

	@BeforeEach
	public void beforeEach() {
		this.locationFactory = Mockito.mock( ILocationFactoryPort.class );
	}

	@Test
	public void constructorContract() {
		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		Assertions.assertNotNull( locationCatalogService );
	}

	@Test
	void lookupLocation4Id_universe_location_not_cached() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.of( spaceLocation ) );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpLocation4LocationId( Mockito.anyLong() );
	}

	@Test
	void lookupLocation4Id_structure_location_not_cached() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID + 100000000 );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.locationFactory.buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( Optional.of( spaceLocation ) );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) );
	}

	@Test
	void lookupLocation4Id_universe_exception_during_generation() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.empty() );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertFalse( sut.isPresent() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpLocation4LocationId( Mockito.anyLong() );
	}

	@Test
	void lookupLocation4Id_structure_exception_during_generation() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID + 100000000 );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.locationFactory.buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( Optional.empty() );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertFalse( sut.isPresent() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) );
	}

	private LocationCatalogService getLocationCatalogService() {
		return new LocationCatalogService( this.locationFactory );
	}
}