package org.dimensinfin.eveonline.neocom.industry.domain;

import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ProcessedBlueprintConstants.TEST_PROCESSED_BLUEPRINT_ID;

public class ProcessedBlueprintTest {
	private static final String TEST_OUTPUT_NAME = "-TEST_OUTPUT_NAME-";
	private PricedResource blueprint;
	private PricedResource output;
	private MarketData marketData;

	@BeforeEach
	public void beforeEach() {
		this.blueprint = Mockito.mock( PricedResource.class );
		this.output = Mockito.mock( PricedResource.class );
		this.marketData = Mockito.mock( MarketData.class );
	}

	@Test
	public void buildContract() {
		final ProcessedBlueprint processedBlueprint = new ProcessedBlueprint.Builder()
				.withBlueprint( this.blueprint )
				.withBOM( new ArrayList<>() )
				.withOutput( this.output )
				.withOutputMarketData( this.marketData )
				.build();
		Assertions.assertNotNull( processedBlueprint );
	}

	@Test
	public void buildFailureMissingWidth() {
		//		final EsiType output = Mockito.mock( EsiType.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBOM( new ArrayList<>() )
					.withOutput( this.output )
					.withOutputMarketData( this.marketData )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( this.blueprint )
					.withBOM( new ArrayList<>() )
					.withOutputMarketData( this.marketData )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( this.blueprint )
					.withBOM( new ArrayList<>() )
					.withOutput( this.output )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		//		final EsiType output = Mockito.mock( EsiType.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( null )
					.withBOM( new ArrayList<>() )
					.withOutput( this.output )
					.withOutputMarketData( this.marketData )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( this.blueprint )
					.withBOM( null )
					.withOutput( this.output )
					.withOutputMarketData( this.marketData )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( this.blueprint )
					.withBOM( new ArrayList<>() )
					.withOutput( null )
					.withOutputMarketData( this.marketData )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new ProcessedBlueprint.Builder()
					.withBlueprint( this.blueprint )
					.withBOM( new ArrayList<>() )
					.withOutput( this.output )
					.withOutputMarketData( null )
					.build();
		} );
	}

	@Test
	@Disabled
	public void getterContract() {
		// Given
		//		final EsiType output = Mockito.mock( EsiType.class );
		// When
		Mockito.when( this.output.getName() ).thenReturn( TEST_OUTPUT_NAME );
		// Test
		final ProcessedBlueprint processedBlueprint = new ProcessedBlueprint.Builder()
				.withBlueprint( this.blueprint )
				.withBOM( new ArrayList<>() )
				.withOutput( this.output )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_PROCESSED_BLUEPRINT_ID, processedBlueprint.getBlueprintTypeId() );
		Assertions.assertNotNull( processedBlueprint.getBom() );
		Assertions.assertEquals( TEST_OUTPUT_NAME, processedBlueprint.getOutput().getName() );
	}
}