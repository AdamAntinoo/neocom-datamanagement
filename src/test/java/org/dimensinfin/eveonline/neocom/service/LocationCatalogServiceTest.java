package org.dimensinfin.eveonline.neocom.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;
import org.dimensinfin.eveonline.neocom.support.InstanceGenerator;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_ACCESIBLE_STRUCTURE_LOCATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_ID;

public class LocationCatalogServiceTest {
	private RetrofitService retrofitService;
	private ILocationFactoryPort locationFactory;
	private IDataStorePort dataStore;

	@BeforeEach
	public void beforeEach() {
		this.retrofitService = Mockito.mock( RetrofitService.class );
		this.locationFactory = Mockito.mock( ILocationFactoryPort.class );
		this.dataStore = Mockito.mock( IDataStorePort.class );
	}

	@Test
	public void constructorContract() {
		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		Assertions.assertNotNull( locationCatalogService );
	}

	@Test
	public void cleanLocationsCache_when_the_cache_has_items() {
		// Given
		final Long locationIdentifier = TEST_ACCESIBLE_STRUCTURE_LOCATION_ID;
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.of( spaceLocation ) );

		locationCatalogService.searchLocation4Id( locationIdentifier );
		final int sut = locationCatalogService.cleanLocationsCache();
		// Then
		Assertions.assertTrue( sut > 0 );
	}

	@Test
	void getUniqueIdentifier() {
		Assertions.assertTrue( this.getLocationCatalogService().getUniqueIdentifier() > 0 );
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
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.empty() );
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpLocation4LocationId( Mockito.anyLong() );
		Mockito.verify( this.dataStore, Mockito.times( 1 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );
	}

	@Test
	void lookupLocation4Id_universe_location_cached() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 0 ) ).buildUpLocation4LocationId( Mockito.anyLong() );
		Mockito.verify( this.dataStore, Mockito.times( 0 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );
	}

	@Test
	void lookupLocation4Id_structure_location_notcached() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID + 100000000 );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.empty() );
		Mockito.when( this.locationFactory.buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) );
		Mockito.verify( this.dataStore, Mockito.times( 1 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );
	}

	@Test
	void lookupLocation4Id_structure_location_cached() {
		// Given
		final Long locationIdentifier = Long.valueOf( TEST_STATION_ID + 100000000 );
		final Credential credential = new InstanceGenerator().getCredential();
		final SpaceLocation spaceLocation = Mockito.mock( SpaceLocation.class );

		final LocationCatalogService locationCatalogService = this.getLocationCatalogService();
		// When
		Mockito.when( spaceLocation.getLocationType() ).thenReturn( LocationIdentifierType.REGION );
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.locationFactory.buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( Optional.of( spaceLocation ) );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertEquals( LocationIdentifierType.REGION, sut.get().getLocationType() );
		Mockito.verify( this.locationFactory, Mockito.times( 0 ) ).buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) );
		Mockito.verify( this.dataStore, Mockito.times( 0 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );
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
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.empty() );
		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) )
				.thenReturn( Optional.empty() );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertFalse( sut.isPresent() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpLocation4LocationId( Mockito.anyLong() );
		Mockito.verify( this.dataStore, Mockito.times( 0 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );

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
		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.empty() );
		Mockito.when( this.locationFactory.buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) ) )
				.thenReturn( Optional.empty() );
		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
				.thenReturn( spaceLocation );

		final Optional<SpaceLocation> sut = locationCatalogService.lookupLocation4Id( locationIdentifier, credential );
		// Then
		Assertions.assertNotNull( sut );
		Assertions.assertFalse( sut.isPresent() );
		Mockito.verify( this.locationFactory, Mockito.times( 1 ) ).buildUpStructure4Pilot( Mockito.anyLong(), Mockito.any( Credential.class ) );
		Mockito.verify( this.dataStore, Mockito.times( 0 ) ).updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) );

	}

	//	@Test
	//	void lookupLocation4Id_datastore_fails_to_cache_location() {
	//		// Given
	//		final Long locationIdentifier = 60006907L;
	//		final Credential credential = new InstanceGenerator().getCredential();
	//		final SpaceLocation testLocation = Mockito.mock( SpaceLocation.class );
	//
	//		final LocationCatalogService locationService = this.getLocationCatalogService();
	//		// When
	//		Mockito.when( this.dataStore.accessLocation( Mockito.anyString() ) ).thenReturn( Optional.empty() );
	//		Mockito.when( this.locationFactory.buildUpLocation4LocationId( Mockito.anyLong() ) ).thenReturn( Optional.of( testLocation ) );
	//		Mockito.when( this.dataStore.updateLocation( Mockito.anyString(), Mockito.any( SpaceLocation.class ) ) )
	//				.thenReturn( null );
	//
	//		final Optional<SpaceLocation> sut = locationService.lookupLocation4Id( locationIdentifier, credential );
	//		// Then
	//		Assertions.assertFalse( sut.isPresent() );
	//
	//	}
	//
	//	private Retrofit getNewUniverseConnector( final OkHttpClient client ) {
	//		return new Retrofit.Builder()
	//				.baseUrl( TEST_RETROFIT_BASE_URL )
	//				.addConverterFactory( GSON_CONVERTER_FACTORY )
	//				.client( client )
	//				.build();
	//	}
	//
	//	private Retrofit getNewAuthenticatedConnector( final OkHttpClient client ) {
	//		return new Retrofit.Builder()
	//				.baseUrl( TEST_RETROFIT_BASE_URL )
	//				.addConverterFactory( GSON_CONVERTER_FACTORY )
	//				.client( client )
	//				.build();
	//	}

	private LocationCatalogService getLocationCatalogService() {
		return new LocationCatalogService( this.retrofitService, this.dataStore, this.locationFactory );
	}
}