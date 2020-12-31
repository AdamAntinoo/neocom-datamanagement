package org.dimensinfin.eveonline.neocom.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

public class LocationCatalogServiceIT extends IntegrationEnvironmentDefinitionTCLocal {
	private static final int ESI_UNITTESTING_PORT = 6090;
	private static final long LOCATION_ID_STATION_4TEST = 60006526L;
	private static final long LOCATION_ID_STRUCTURE_4TEST = 1032555370327L;

	@Test
	public void buildComplete() {
		final IConfigurationService configurationProvider = Mockito.mock( IConfigurationService.class );
		final IFileSystem fileSystem = Mockito.mock( IFileSystem.class );
		final ESIUniverseDataProvider esiUniverseProvider = Mockito.mock( ESIUniverseDataProvider.class );
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final LocationCatalogService locationService = new LocationCatalogService.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystem )
				.withESIUniverseDataProvider( esiUniverseProvider )
				.withRetrofitFactory( retrofitFactory )
				.build();
		Assertions.assertNotNull( locationService );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
					.withConfigurationProvider( this.itConfigurationProvider )
					.withFileSystemAdapter( this.itFileSystemAdapter )
					.withESIUniverseDataProvider( null )
					.withRetrofitFactory( this.itRetrofitFactory )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
					.withConfigurationProvider( this.itConfigurationProvider )
					.withFileSystemAdapter( this.itFileSystemAdapter )
					.withRetrofitFactory( this.itRetrofitFactory )
					.build();
		} );
	}

	@Test
	public void callClean() {
		// Given
		final IConfigurationService configurationProvider = Mockito.mock( IConfigurationService.class );
		final IFileSystem fileSystem = Mockito.mock( IFileSystem.class );
		final ESIUniverseDataProvider esiUniverseProvider = Mockito.mock( ESIUniverseDataProvider.class );
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( configurationProvider )
				.withFileSystemAdapter( fileSystem )
				.withRetrofitFactory( itRetrofitFactory )
				.withESIUniverseDataProvider( esiUniverseProvider )
				.build();
		// Test
		final boolean obtained = locationCatalogService.call();
		final boolean expected = false;
		// Assertions
		Assertions.assertNotNull( locationCatalogService );
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void searchLocation4Id() throws IOException {
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();

		final SpaceLocation location = locationCatalogService.searchLocation4Id( LOCATION_ID_STATION_4TEST );
		Assertions.assertNotNull( location );
	}

	@Test
	public void searchStructure4Id() throws IOException {
		final LocationCatalogService locationCatalogService = new LocationCatalogService.Builder()
				.withConfigurationProvider( this.itConfigurationProvider )
				.withFileSystemAdapter( this.itFileSystemAdapter )
				.withESIUniverseDataProvider( this.itEsiUniverseDataProvider )
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();

		final SpaceLocation location = locationCatalogService.searchStructure4Id( LOCATION_ID_STRUCTURE_4TEST, credential4Test );
		Assertions.assertNotNull( location );
	}
}
