package org.dimensinfin.eveonline.neocom.domain.space;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.CONSTELLATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.CONSTELLATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.CORPORATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.REGION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.REGION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.SOLAR_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.SOLAR_SYSTEM_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.SOLAR_SYSTEM_SECURITY_CLASS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.SOLAR_SYSTEM_SECURITY_STATUS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.STATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.STATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.STRUCTURE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.STRUCTURE_NAME;

public class StructureTest {
	@Test
	public void buildContract() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final long structureId = STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( region.getName() ).thenReturn( REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( structure.getName() ).thenReturn( STRUCTURE_NAME );
		// Test
		final SpaceLocation location = new Structure.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStructure( structureId, structure )
				.withCorporation( CORPORATION_ID, CORPORATION_NAME )
				.build();
		Assert.assertNotNull( location );
	}

	@Test
	public void buildFailureMissing() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final long structureId = STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		final int corporationId = CORPORATION_ID;
		final String corporationName = CORPORATION_NAME;
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final long structureId = STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		final int corporationId = CORPORATION_ID;
		final String corporationName = CORPORATION_NAME;
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( null )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( null )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( null )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( null, structure )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, null )
					.withCorporation( corporationId, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( null, corporationName )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final SpaceLocation location = new Structure.Builder()
					.withRegion( region )
					.withConstellation( constellation )
					.withSolarSystem( solarSystem )
					.withStructure( structureId, structure )
					.withCorporation( corporationId, null )
					.build();
		} );
	}

	@Test
	public void buildFromStructure() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final long structureId = STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( region.getName() ).thenReturn( REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( structure.getName() ).thenReturn( STRUCTURE_NAME );
		// Test
		final Structure structureOrigin = new Structure.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStructure( structureId, structure )
				.withCorporation( CORPORATION_ID, CORPORATION_NAME )
				.build();
		final Structure target = new Structure.Builder().fromStructure( structureOrigin );
		// Assertions
		Assert.assertNotNull( target );
		Assertions.assertEquals( REGION_ID, target.getRegionId() );
		Assertions.assertEquals( REGION_NAME, ((SpaceRegion) target).getRegionName() );
		Assertions.assertEquals( CONSTELLATION_ID, ((SpaceConstellation) target).getConstellationId() );
		Assertions.assertEquals( CONSTELLATION_NAME, ((SpaceConstellation) target).getConstellationName() );
		Assertions.assertEquals( SOLAR_SYSTEM_ID, ((SpaceSystem) target).getSolarSystemId() );
		Assertions.assertEquals( SOLAR_SYSTEM_NAME, ((SpaceSystem) target).getSolarSystemName() );
		Assertions.assertEquals( SOLAR_SYSTEM_SECURITY_CLASS, ((SpaceSystem) target).getSecurityClass() );
		Assertions.assertEquals( SOLAR_SYSTEM_SECURITY_STATUS, ((SpaceSystem) target).getSecurityStatus() );
		Assertions.assertEquals( STRUCTURE_ID, ((Station) target).getStationId() );
		Assertions.assertEquals( STRUCTURE_NAME, ((Station) target).getStationName() );
		Assertions.assertEquals( STRUCTURE_ID, ((Structure) target).getStructureId() );
		Assertions.assertEquals( STRUCTURE_NAME, ((Structure) target).getStructureName() );
	}

	@Test
	public void getLocationIdConstellation() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.build();
		final long expected = (long) CONSTELLATION_ID;
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
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.build();
		final long expected = (long) REGION_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.REGION, location.getLocationType() );
	}

	@Test
	public void getLocationIdSolarSystem() {
		// Given
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( SOLAR_SYSTEM_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.build();
		final long expected = (long) SOLAR_SYSTEM_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.SOLAR_SYSTEM, location.getLocationType() );
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
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( SOLAR_SYSTEM_ID );
		Mockito.when( station.getStationId() ).thenReturn( STATION_ID );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		final long expected = (long) STATION_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.STATION, location.getLocationType() );
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
		Mockito.when( region.getRegionId() ).thenReturn( REGION_ID );
		Mockito.when( region.getName() ).thenReturn( REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( station.getStationId() ).thenReturn( STATION_ID );
		Mockito.when( station.getName() ).thenReturn( STATION_NAME );
		// Test
		final SpaceLocation location = new SpaceLocationImplementation.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStation( station )
				.build();
		Assert.assertNotNull( location );

		// Assertions
		Assertions.assertEquals( REGION_ID, ((SpaceRegion) location).getRegionId() );
		Assertions.assertEquals( REGION_NAME, ((SpaceRegion) location).getRegionName() );
		Assertions.assertEquals( CONSTELLATION_ID, ((SpaceConstellation) location).getConstellationId() );
		Assertions.assertEquals( CONSTELLATION_NAME, ((SpaceConstellation) location).getConstellationName() );
		Assertions.assertEquals( SOLAR_SYSTEM_ID, ((SpaceSystem) location).getSolarSystemId() );
		Assertions.assertEquals( SOLAR_SYSTEM_NAME, ((SpaceSystem) location).getSolarSystemName() );
		Assertions.assertEquals( SOLAR_SYSTEM_SECURITY_CLASS, ((SpaceSystem) location).getSecurityClass() );
		Assertions.assertEquals( SOLAR_SYSTEM_SECURITY_STATUS, ((SpaceSystem) location).getSecurityStatus() );
		Assertions.assertEquals( STATION_ID, ((Station) location).getStationId() );
		Assertions.assertEquals( STATION_NAME, ((Station) location).getStationName() );
	}

}