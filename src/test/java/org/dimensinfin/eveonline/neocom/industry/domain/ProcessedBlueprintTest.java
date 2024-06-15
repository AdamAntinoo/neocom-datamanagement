package org.dimensinfin.eveonline.neocom.industry.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.support.InstanceGenerator;
import org.dimensinfin.eveonline.neocom.support.PojoTest;
import org.dimensinfin.logging.LogWrapper;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ProcessedBlueprintConstants.TEST_PROCESSED_BLUEPRINT_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_LOCATION_ID;

public class ProcessedBlueprintTest {
	private static final String TEST_OUTPUT_NAME = "-TEST_OUTPUT_NAME-";
	private EsiType blueprint;
	private EsiType output;
	private SpaceLocation location;

	@BeforeEach
	public void beforeEach() {
		this.blueprint = new InstanceGenerator().getEsiType();
		this.output = new InstanceGenerator().getEsiType();
		this.location = new InstanceGenerator().getSpaceLocation();
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

	@Disabled
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

	@Disabled
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

	@Test
	void when_instanceIsSerialized_using_standardSerializer() throws IOException {
		// Given
		final Charset codepage = StandardCharsets.UTF_8;
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream( fileOutputStream );
		// When
		objectOutputStream.writeObject( processedBlueprint );
		final String outputText = fileOutputStream.toString( codepage );
		LogWrapper.info( outputText );
	}

	@Test
	void when_instanceIsSerialized_using_gson() throws IOException {
		// Given
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		final StringWriter writer = new StringWriter();

		//		final Charset codepage = StandardCharsets.UTF_8;
		//		ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
		//		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		// When
		gson.toJson( processedBlueprint, writer );
		// Then
		final String sut = writer.toString();
		LogWrapper.info( sut );
	}

	@Test
	void when_instanceIsSerialized_using_jackson() throws IOException {
		// Given
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
		final ProcessedBlueprint processedBlueprint = this.getProcessedBlueprint();
		// When
		final String sut = mapper.writeValueAsString( processedBlueprint );
		// Then
		LogWrapper.info( sut );
	}
}