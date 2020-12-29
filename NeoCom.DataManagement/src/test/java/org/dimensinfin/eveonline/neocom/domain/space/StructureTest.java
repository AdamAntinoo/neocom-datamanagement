package org.dimensinfin.eveonline.neocom.domain.space;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStructuresStructureIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CONSTELLATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CONSTELLATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CORPORATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CORPORATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_CORPORATION_OWNER_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_REGION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_REGION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_SECURITY_CLASS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_SOLAR_SYSTEM_SECURITY_STATUS;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STATION_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STRUCTURE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STRUCTURE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_STRUCTURE_TYPE_ID;

public class StructureTest {

	private GetUniverseRegionsRegionIdOk region;
	private GetUniverseConstellationsConstellationIdOk constellation;
	private GetUniverseSystemsSystemIdOk solarSystem;
	private long structureId;
	private GetUniverseStructuresStructureIdOk structure;

	@BeforeEach
	public void beforeEach() {
		// Given
		this.region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		this.constellation = Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		this.solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		this.structureId = TEST_STRUCTURE_ID;
		this.structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		// When
		Mockito.when( region.getRegionId() ).thenReturn( TEST_REGION_ID );
		Mockito.when( region.getName() ).thenReturn( TEST_REGION_NAME );
		Mockito.when( constellation.getConstellationId() ).thenReturn( TEST_CONSTELLATION_ID );
		Mockito.when( constellation.getName() ).thenReturn( TEST_CONSTELLATION_NAME );
		Mockito.when( solarSystem.getSystemId() ).thenReturn( TEST_SOLAR_SYSTEM_ID );
		Mockito.when( solarSystem.getName() ).thenReturn( TEST_SOLAR_SYSTEM_NAME );
		Mockito.when( solarSystem.getSecurityClass() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_CLASS );
		Mockito.when( solarSystem.getSecurityStatus() ).thenReturn( TEST_SOLAR_SYSTEM_SECURITY_STATUS );
		Mockito.when( structure.getName() ).thenReturn( TEST_STRUCTURE_NAME );
		Mockito.when( structure.getOwnerId() ).thenReturn( TEST_CORPORATION_OWNER_ID );
		Mockito.when( structure.getTypeId() ).thenReturn( TEST_STRUCTURE_TYPE_ID );
	}

	@Test
	public void buildContract() {
		// Test
		final SpaceLocation location = new Structure.Builder()
				.withRegion( this.region )
				.withConstellation( this.constellation )
				.withSolarSystem( this.solarSystem )
				.withStructure( this.structureId, this.structure )
				.withCorporation( TEST_CORPORATION_ID, TEST_CORPORATION_NAME )
				.build();
		Assertions.assertNotNull( location );
	}

	@Test
	public void buildFailureMissing() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final GetUniverseConstellationsConstellationIdOk constellation =
				Mockito.mock( GetUniverseConstellationsConstellationIdOk.class );
		final GetUniverseSystemsSystemIdOk solarSystem = Mockito.mock( GetUniverseSystemsSystemIdOk.class );
		final long structureId = TEST_STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		final int corporationId = TEST_CORPORATION_ID;
		final String corporationName = TEST_CORPORATION_NAME;
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
		final long structureId = TEST_STRUCTURE_ID;
		final GetUniverseStructuresStructureIdOk structure = Mockito.mock( GetUniverseStructuresStructureIdOk.class );
		final int corporationId = TEST_CORPORATION_ID;
		final String corporationName = TEST_CORPORATION_NAME;
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
		// Test
		final Structure structureOrigin = new Structure.Builder()
				.withRegion( region )
				.withConstellation( constellation )
				.withSolarSystem( solarSystem )
				.withStructure( structureId, structure )
				.withCorporation( TEST_CORPORATION_ID, TEST_CORPORATION_NAME )
				.build();
		final Structure target = new Structure.Builder().fromStructure( structureOrigin );
		// Assertions
		Assertions.assertNotNull( target );
		Assertions.assertEquals( TEST_REGION_ID, target.getRegionId() );
		Assertions.assertEquals( TEST_REGION_NAME, target.getRegionName() );
		Assertions.assertEquals( TEST_CONSTELLATION_ID, target.getConstellationId() );
		Assertions.assertEquals( TEST_CONSTELLATION_NAME, target.getConstellationName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_ID, target.getSolarSystemId() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_NAME, target.getSolarSystemName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_CLASS, target.getSecurityClass() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_STATUS, target.getSecurityStatus() );
		Assertions.assertEquals( TEST_STRUCTURE_ID, Math.toIntExact( target.getStationId() ) );
		Assertions.assertEquals( TEST_STRUCTURE_NAME, target.getStationName() );
		Assertions.assertEquals( TEST_STRUCTURE_ID, target.getStructureId() );
		Assertions.assertEquals( TEST_STRUCTURE_NAME, target.getStructureName() );
		Assertions.assertEquals( TEST_CORPORATION_ID, target.getCorporationId() );
		Assertions.assertEquals( TEST_CORPORATION_NAME, target.getCorporationName() );
		Assertions.assertEquals( TEST_CORPORATION_OWNER_ID, target.getOwnerId() );
		Assertions.assertEquals( TEST_STRUCTURE_TYPE_ID, target.getStructureTypeId() );
	}

	@Test
	public void getLocationIdStructure() {
		// Test
		final Structure location = new Structure.Builder()
				.withRegion( this.region )
				.withConstellation( this.constellation )
				.withSolarSystem( this.solarSystem )
				.withStructure( TEST_STRUCTURE_ID, structure )
				.withCorporation( TEST_CORPORATION_ID, TEST_CORPORATION_NAME )
				.build();
		final long expected = TEST_STRUCTURE_ID;
		final long obtained = location.getLocationId();
		// Assertions
		Assertions.assertEquals( expected, obtained );
		Assertions.assertEquals( LocationIdentifierType.STRUCTURE, location.getLocationType() );
	}

	@Test
	public void toStringContract() {
		// Test
		final Structure location = new Structure.Builder()
				.withRegion( this.region )
				.withConstellation( this.constellation )
				.withSolarSystem( this.solarSystem )
				.withStructure( TEST_STRUCTURE_ID, structure )
				.withCorporation( TEST_CORPORATION_ID, TEST_CORPORATION_NAME )
				.build();
		final String expected = "{\"structureId\":60008494,\"structureName\":\"Amarr VIII (Oris) - Emperor Family Academy\",\"ownerId\":98384726,\"structureTypeId\":60008494,\"corporationId\":98384726,\"corporationName\":\"Industrias Machaque\",\"spaceLocation\":\"{\\\"regionId\\\":1000043,\\\"regionName\\\":\\\"Domain\\\",\\\"constellationId\\\":20000322,\\\"constellationName\\\":\\\"Throne Worlds\\\",\\\"solarSystemId\\\":30002187,\\\"solarSystemName\\\":\\\"Amarr\\\",\\\"stationId\\\":60008494,\\\"stationName\\\":\\\"Amarr VIII (Oris) - Emperor Family Academy\\\",\\\"securityClass\\\":\\\"E1\\\",\\\"securityStatus\\\":0.5623334}\"}";
		final String obtained = location.toString();
		// Assertions
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void getterContract() {
		// Test
		final Structure location = new Structure.Builder()
				.withRegion( this.region )
				.withConstellation( this.constellation )
				.withSolarSystem( this.solarSystem )
				.withStructure( TEST_STRUCTURE_ID, structure )
				.withCorporation( TEST_CORPORATION_ID, TEST_CORPORATION_NAME )
				.build();
		Assertions.assertNotNull( location );

		// Assertions
		Assertions.assertEquals( TEST_REGION_ID, location.getRegionId() );
		Assertions.assertEquals( TEST_REGION_NAME, location.getRegionName() );
		Assertions.assertEquals( TEST_CONSTELLATION_ID, location.getConstellationId() );
		Assertions.assertEquals( TEST_CONSTELLATION_NAME, location.getConstellationName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_ID, location.getSolarSystemId() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_NAME, location.getSolarSystemName() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_CLASS, location.getSecurityClass() );
		Assertions.assertEquals( TEST_SOLAR_SYSTEM_SECURITY_STATUS, location.getSecurityStatus() );
		Assertions.assertEquals( TEST_STATION_ID, Math.toIntExact( location.getStationId() ) );
		Assertions.assertEquals( TEST_STATION_NAME, location.getStationName() );
		Assertions.assertEquals( TEST_CORPORATION_ID, location.getCorporationId() );
		Assertions.assertEquals( TEST_CORPORATION_NAME, location.getCorporationName() );
		Assertions.assertEquals( TEST_CORPORATION_OWNER_ID, location.getOwnerId() );
		Assertions.assertEquals( TEST_STRUCTURE_TYPE_ID, location.getStructureTypeId() );
		Assertions.assertEquals( LocationIdentifierType.STRUCTURE, location.getLocationType() );
	}
}