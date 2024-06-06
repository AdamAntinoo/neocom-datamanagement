package org.dimensinfin.eveonline.neocom.industry.domain;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.support.PojoTest;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ProcessedBlueprintConstants.TEST_PROCESSED_BLUEPRINT_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_LOCATION_ID;

public class ProcessedBlueprintTest {
	private static final String TEST_OUTPUT_NAME = "-TEST_OUTPUT_NAME-";
	private PricedResource blueprint;
	private PricedResource output;
	private SpaceLocation location;

	@BeforeEach
	public void beforeEach() {
		this.blueprint = Mockito.mock( PricedResource.class );
		this.output = Mockito.mock( PricedResource.class );
		this.location = Mockito.mock( SpaceLocation.class );
	}

	@Test
	public void buildContract() {
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		Assertions.assertNotNull( processedBlueprint );
	}

	@Test
	public void buildFailureMissingWidth() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withBlueprintItem( this.blueprint )
					.withLocation( this.location )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withLocation( this.location )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withBlueprintItem( this.blueprint )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withBlueprintItem( this.blueprint )
					.withLocation( this.location )
					.withBom( new ArrayList<>() )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( null )
					.withBlueprintItem( this.blueprint )
					.withLocation( this.location )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withBlueprintItem( null )
					.withLocation( this.location )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withBlueprintItem( this.blueprint )
					.withLocation( null )
					.withOutputItem( this.output )
					.withBom( new ArrayList<>() )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			ProcessedBlueprint.builder()
					.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
					.withBlueprintItem( this.blueprint )
					.withLocation( this.location )
					.withOutputItem( null )
					.withBom( new ArrayList<>() )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		// When
		Mockito.when( this.location.getLocationId() ).thenReturn( TEST_LOCATION_ID );
		// Test
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		// Assertions
		Assertions.assertEquals(
				TEST_LOCATION_ID + ":" + TEST_PROCESSED_BLUEPRINT_ID,
				processedBlueprint.getStorageUniqueId()
		);
	}

	public ProcessedBlueprint getProcessedBlueprint() {
		return ProcessedBlueprint.builder()
				.withTypeId( TEST_PROCESSED_BLUEPRINT_ID )
				.withBlueprintItem( this.blueprint )
				.withLocation( this.location )
				.withOutputItem( this.output )
				.withBom( new ArrayList<>() )
				.build();

	}

	@Test
	void getters_contract() {
		PojoTest.validateGetters( ProcessedBlueprint.class );
	}

	@Test
	@Disabled
	void toString_contract() {
		// Given
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		final String expected = "{\"typeId\":7765,\"blueprintItem\":\"Mock for PricedResource, hashCode: 1086323658\",\"location\":\"Mock for SpaceLocation, hashCode: 1391119179\",\"materialEfficiency\":0,\"timeEfficiency\":0,\"outputTypeId\":0,\"outputItem\":\"Mock for PricedResource, hashCode: 710220387\",\"manufactureCost\":null,\"outputCost\":null,\"bom\":[],\"index\":0.0}";
		// When
		final String obtained = processedBlueprint.toString();
		// Then
		Assertions.assertEquals( expected, obtained );
	}
}