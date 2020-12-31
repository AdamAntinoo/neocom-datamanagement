package org.dimensinfin.eveonline.neocom.domain.space;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CONSTELLATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CONSTELLATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_REGION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_REGION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_SECURITY_CLASS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_SECURITY_STATUS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_NAME;

public class SpaceLocationImplementationTest {
	@Test
	public void buildContract() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final GetUniverseStationsStationIdOk station = Mockito.mock( GetUniverseStationsStationIdOk.class );
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		Assert.assertNotNull( location );
	}

	@Test
	public void buildFailure() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new SpaceLocationImplementation.Builder()
					.withRegion( null )
					.withConstellation( null )
					.withSolarSystem( null )
					.withStation( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new SpaceLocationImplementation.Builder()
					.build();
		} );
	}

	@Test
	public void buildFromSpaceLocation() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final GetUniverseStationsStationIdOk station = Mockito.mock( GetUniverseStationsStationIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( region.getName() ).thenReturn( TEST_REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( TEST_CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( TEST_SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( TEST_SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( station.getStationId() ).thenReturn( TEST_STATION_ID );
		Mockito.when( station.getName() ).thenReturn( TEST_STATION_NAME );
		// Test
		final SpaceLocation locationOrigin = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		final SpaceLocation location = new SpaceLocationImplementation.Builder().fromSpaceLocation( locationOrigin );
		// Assertions
		Assert.assertNotNull( location );
		Assertions.assertEquals( TEST_REGION_ID, ((SpaceRegion) location).getRegionId() );
		Assertions.assertEquals( TEST_REGION_NAME, ((SpaceRegion) location).getRegionName() );
		Assertions.assertEquals( TEST_CONSTELLATION_ID, ((SpaceConstellation) location).getConstellationId() );
		Assertions.assertEquals( TEST_CONSTELLATION_NAME, ((SpaceConstellation) location).getConstellationName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_ID, ((SpaceSystem) location).getSolarSystemId() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_NAME, ((SpaceSystem) location).getSolarSystemName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_CLASS, ((SpaceSystem) location).getSecurityClass() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_STATUS, ((SpaceSystem) location).getSecurityStatus() );
		Assertions.assertEquals( TEST_STATION_ID, ((Station) location).getStationId().intValue() );
		Assertions.assertEquals( TEST_STATION_NAME, ((Station) location).getStationName() );
	}

	@Test
	public void getLocationIdConstellation() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.build();
		final long expected = (long) TEST_CONSTELLATION_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.CONSTELLATION, location.getLocationType() );
	}

	@Test
	public void getLocationIdRegion() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.build();
		final long expected = (long) TEST_REGION_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.REGION, location.getLocationType() );
	}

	@Test
	public void getLocationIdStation() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final GetUniverseStationsStationIdOk station = Mockito.mock( GetUniverseStationsStationIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( TEST_SOLAR_SYSTEM_ID );
		Mockito.when( station.getStationId() ).thenReturn( TEST_STATION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		final long expected = (long) TEST_STATION_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.STATION, location.getLocationType() );
	}

	@Test
	public void getLocationIdSolarSystem() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( TEST_SOLAR_SYSTEM_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.build();
		final long expected = (long) TEST_SOLAR_SYSTEM_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.SOLAR_SYSTEM, location.getLocationType() );
	}

	@Test
	public void getterContract() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final GetUniverseStationsStationIdOk station = Mockito.mock( GetUniverseStationsStationIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( region.getName() ).thenReturn( TEST_REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( TEST_CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( TEST_SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( TEST_SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( station.getStationId() ).thenReturn( TEST_STATION_ID );
		Mockito.when( station.getName() ).thenReturn( TEST_STATION_NAME );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		Assert.assertNotNull( location );

		// Assertions
		Assertions.assertEquals( TEST_REGION_ID, ((SpaceRegion) location).getRegionId() );
		Assertions.assertEquals( TEST_REGION_NAME, ((SpaceRegion) location).getRegionName() );
		Assertions.assertEquals( TEST_CONSTELLATION_ID, ((SpaceConstellation) location).getConstellationId() );
		Assertions.assertEquals( TEST_CONSTELLATION_NAME, ((SpaceConstellation) location).getConstellationName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_ID, ((SpaceSystem) location).getSolarSystemId() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_NAME, ((SpaceSystem) location).getSolarSystemName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_CLASS, ((SpaceSystem) location).getSecurityClass() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_STATUS, ((SpaceSystem) location).getSecurityStatus() );
		Assertions.assertEquals( TEST_STATION_ID, ((Station) location).getStationId().intValue() );
		Assertions.assertEquals( TEST_STATION_NAME, ((Station) location).getStationName() );
	}
}