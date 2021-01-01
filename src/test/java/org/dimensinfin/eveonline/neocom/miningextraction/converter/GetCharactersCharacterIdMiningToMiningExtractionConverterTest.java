package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceConstellationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

public class GetCharactersCharacterIdMiningToMiningExtractionConverterTest {
	private static final int DEFAULT_MINING_OWNER_IDENTIFIER = 92223647;

	@Test
	public void convertFailure() {
		// Given
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final ResourceFactory resourceFactory=Mockito.mock(ResourceFactory.class);
		final int ownerId = DEFAULT_MINING_OWNER_IDENTIFIER;
		final LocalDate processingDate = LocalDate.now();
		final GetCharactersCharacterIdMining200Ok mining200Ok = Mockito.mock( GetCharactersCharacterIdMining200Ok.class );
		final SpaceConstellationImplementation spaceLocation = Mockito.mock( SpaceConstellationImplementation.class );
		// When
		Mockito.when( mining200Ok.getTypeId() ).thenReturn( 17459 );
		Mockito.when( mining200Ok.getDate() ).thenReturn( new LocalDate( "2018-06-01" ) );
		Mockito.when( mining200Ok.getQuantity() ).thenReturn( 15759L );
		Mockito.when( mining200Ok.getSolarSystemId() ).thenReturn( 30001669 );
		Mockito.when( locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		Mockito.when( spaceLocation.getLocationId() ).thenReturn( 30001669L );
		// Exceptions
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
					final MiningExtraction miningExtraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
							locationCatalogService, resourceFactory, ownerId, processingDate )
							.convert( mining200Ok );
				},
				"Expected GetCharactersCharacterIdMiningToMiningExtractionConverter.convert() to NeoComRuntimeException null verification, but it didn't." );
	}

	@Test
	public void convertSuccessPastDate() {
		// Given
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
//		final ResourceFactory resourceFactory=Mockito.mock(ResourceFactory.class);
		final int ownerId = DEFAULT_MINING_OWNER_IDENTIFIER;
		final LocalDate processingDate = LocalDate.now();
		final GetCharactersCharacterIdMining200Ok mining200Ok = Mockito.mock( GetCharactersCharacterIdMining200Ok.class );
		final SpaceSystemImplementation spaceLocation = Mockito.mock( SpaceSystemImplementation.class );
		final ResourceFactory resourceFactory = Mockito.mock( ResourceFactory.class );
//		ResourceFactory.setSingleton( resourceFactory );
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		// When
		Mockito.when( mining200Ok.getTypeId() ).thenReturn( 17459 );
		Mockito.when( mining200Ok.getDate() ).thenReturn( new LocalDate( "2018-06-01" ) );
		Mockito.when( mining200Ok.getQuantity() ).thenReturn( 15759L );
		Mockito.when( mining200Ok.getSolarSystemId() ).thenReturn( 30001669 );
		Mockito.when( locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		Mockito.when( spaceLocation.getLocationId() ).thenReturn( 30001669L );
		Mockito.when( resourceFactory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getTypeId() ).thenReturn( 17459 );
		Mockito.when( neoItem.getName() ).thenReturn( "Piroxeres" );
		Mockito.when( neoItem.getPrice() ).thenReturn( 432.56 );
		Mockito.when( neoItem.getVolume() ).thenReturn( 0.01 );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "http://17459.png" );
		Mockito.when( spaceLocation.getSolarSystemName() ).thenReturn( "Esescama" );
		// Test
		final MiningExtraction miningExtraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
				locationCatalogService, resourceFactory, ownerId, processingDate )
				.convert( mining200Ok );
		// Assertions
		Assertions.assertNotNull( miningExtraction );
		Assertions.assertEquals( 15759, miningExtraction.getQuantity() );
		Assertions.assertEquals( 17459, miningExtraction.getTypeId() );
		Assertions.assertEquals( DEFAULT_MINING_OWNER_IDENTIFIER, miningExtraction.getOwnerId() );
		Assertions.assertEquals( "2018-06-01:24-30001669-17459-92223647", miningExtraction.getId() );
		Assertions.assertEquals( "2018-06-01", miningExtraction.getExtractionDateName() );
		Assertions.assertEquals( 24, miningExtraction.getExtractionHour() );
		Assertions.assertEquals( 30001669, miningExtraction.getLocationId() );
		Assertions.assertEquals( "Piroxeres", miningExtraction.getResourceName() );
		Assertions.assertEquals( "Esescama", miningExtraction.getSystemName() );
		Assertions.assertEquals( "http://17459.png", miningExtraction.getURLForItem() );
		Assertions.assertEquals( 0.01, miningExtraction.getVolume() );
		Assertions.assertEquals( 432.56, miningExtraction.getPrice() );
	}

	@Test
	public void convertSuccessToday() {
		// Given
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final int ownerId = DEFAULT_MINING_OWNER_IDENTIFIER;
		final LocalDate processingDate = LocalDate.now();
		final GetCharactersCharacterIdMining200Ok mining200Ok = Mockito.mock( GetCharactersCharacterIdMining200Ok.class );
		final SpaceSystemImplementation spaceLocation = Mockito.mock( SpaceSystemImplementation.class );
		final ResourceFactory resourceFactory = Mockito.mock( ResourceFactory.class );
//		ResourceFactory.setSingleton( resourceFactory );
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		// When
		Mockito.when( mining200Ok.getTypeId() ).thenReturn( 17459 );
		Mockito.when( mining200Ok.getDate() ).thenReturn( LocalDate.now() );
		Mockito.when( mining200Ok.getQuantity() ).thenReturn( 15759L );
		Mockito.when( mining200Ok.getSolarSystemId() ).thenReturn( 30001669 );
		Mockito.when( locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		Mockito.when( spaceLocation.getLocationId() ).thenReturn( 30001669L );
		Mockito.when( resourceFactory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getTypeId() ).thenReturn( 17459 );
		Mockito.when( neoItem.getName() ).thenReturn( "Piroxeres" );
		Mockito.when( neoItem.getPrice() ).thenReturn( 432.56 );
		Mockito.when( neoItem.getVolume() ).thenReturn( 0.01 );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "http://17459.png" );
		Mockito.when( spaceLocation.getSolarSystemName() ).thenReturn( "Esescama" );
		// Test
		final MiningExtraction miningExtraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
				locationCatalogService, resourceFactory, ownerId, processingDate )
				.convert( mining200Ok );
		final String calculatedId = LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) +
				":" +
				LocalDateTime.now().getHourOfDay() +
				"-30001669-17459-92223647";
		// Assertions
		Assertions.assertNotNull( miningExtraction );
		Assertions.assertEquals( 15759, miningExtraction.getQuantity() );
		Assertions.assertEquals( 17459, miningExtraction.getTypeId() );
		Assertions.assertEquals( DEFAULT_MINING_OWNER_IDENTIFIER, miningExtraction.getOwnerId() );
		Assertions.assertEquals( calculatedId, miningExtraction.getId() );
		Assertions.assertEquals( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ), miningExtraction.getExtractionDateName() );
		Assertions.assertEquals( LocalDateTime.now().getHourOfDay(), miningExtraction.getExtractionHour() );
		Assertions.assertEquals( 30001669, miningExtraction.getLocationId() );
		Assertions.assertEquals( "Piroxeres", miningExtraction.getResourceName() );
		Assertions.assertEquals( "Esescama", miningExtraction.getSystemName() );
		Assertions.assertEquals( "http://17459.png", miningExtraction.getURLForItem() );
		Assertions.assertEquals( 0.01, miningExtraction.getVolume() );
		Assertions.assertEquals( 432.56, miningExtraction.getPrice() );
	}
}