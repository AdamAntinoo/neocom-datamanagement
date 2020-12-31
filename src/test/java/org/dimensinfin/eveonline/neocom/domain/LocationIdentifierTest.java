package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class LocationIdentifierTest {
	private static final Long TEST_SPACE_IDENTIFIER = 43215L;
	private LocationIdentifier identifier4Test;

	@Test
	public void buildComplete() {
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_SPACE_IDENTIFIER )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assertions.assertNotNull( identifier );
	}

	@Test
	public void buildFailureA() {
		Assertions.assertThrows( NullPointerException.class, () -> {
					final LocationIdentifier identifier = new LocationIdentifier.Builder()
							.withSpaceIdentifier( null )
							.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
							.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
							.build();
				},
				"Expected LocationIdentifier.Builder() to throw null verification, but it didn't." );
		Assertions.assertThrows( NullPointerException.class, () -> {
					final LocationIdentifier identifier = new LocationIdentifier.Builder()
							.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
							.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
							.build();
				},
				"Expected LocationIdentifier.Builder() to throw null verification, but it didn't." );
	}

	@Test
	public void buildSuccessA() {
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_SPACE_IDENTIFIER )
				.withLocationFlag( null )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assertions.assertNotNull( identifier );
	}

	@Test
	public void buildSuccessB() {
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_SPACE_IDENTIFIER )
				.withLocationFlag( null )
				.withLocationType( null )
				.build();
		Assertions.assertNotNull( identifier );
	}

	@Test
	public void classifyLocationConstellation() {
		final Long spaceIdentifier = 20000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assertions.assertEquals( LocationIdentifierType.CONSTELLATION, identifier.getType() );
	}

	@Test
	public void classifyLocationOffice() {
		final Long spaceIdentifier = 100000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.CORPSAG1 )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.OTHER )
				.build();
		Assertions.assertEquals( LocationIdentifierType.OFFICE, identifier.getType() );
	}

	@Test
	public void classifyLocationRegion() {
		final Long spaceIdentifier = 10000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assertions.assertEquals( LocationIdentifierType.REGION, identifier.getType() );
	}

	@Test
	public void classifyLocationSolarSystem() {
		final Long spaceIdentifier = 30000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assertions.assertEquals( LocationIdentifierType.SOLAR_SYSTEM, identifier.getType() );
	}

	@Test
	public void classifyLocationStationA() {
		final Long spaceIdentifier = 60000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assertions.assertEquals( LocationIdentifierType.STATION, identifier.getType() );
	}

	@Test
	public void classifyLocationStationB() {
		final Long spaceIdentifier = 60000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assertions.assertEquals( LocationIdentifierType.STATION, identifier.getType() );
	}

	@Test
	public void classifyLocationStructure() {
		final Long spaceIdentifier = 100000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.OTHER )
				.build();
		Assertions.assertEquals( LocationIdentifierType.UNKNOWN, identifier.getType() );
	}

	@Test
	public void gettersContract() {
		Assertions.assertEquals( 43215L, this.identifier4Test.getSpaceIdentifier().longValue() );
		Assertions.assertEquals( LocationIdentifierType.REGION, this.identifier4Test.getType() );
		Assertions.assertEquals( EsiAssets200Ok.LocationFlagEnum.HANGAR, this.identifier4Test.getLocationFlag() );
	}

	@BeforeEach
	public void setUp() {
		this.identifier4Test = new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_SPACE_IDENTIFIER )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assertions.assertNotNull( this.identifier4Test );
	}

	@Test
	public void settersContract() {
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_SPACE_IDENTIFIER )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.OTHER )
				.build();
		identifier.setType( LocationIdentifierType.CONTAINER );
		Assertions.assertEquals( LocationIdentifierType.CONTAINER, identifier.getType() );
	}
}
