package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

public class MiningExtractionEntityToMiningExtractionConverterTest {
	@Test
	public void convertSuccessToday() {
		// Given
		final MiningExtractionEntity miningExtractionEntity = Mockito.mock( MiningExtractionEntity.class );
		final LocationCatalogService locationCatalogService = Mockito.mock( LocationCatalogService.class );
		final SpaceSystemImplementation spaceLocation = Mockito.mock( SpaceSystemImplementation.class );
		final ResourceFactory resourceFactory = Mockito.mock( ResourceFactory.class );
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final LocalDate processingDate = LocalDate.now();
//		ResourceFactory.setSingleton( resourceFactory );
		// When
		Mockito.when( miningExtractionEntity.getOwnerId() ).thenReturn( 92223647 );
		Mockito.when( miningExtractionEntity.getTypeId() ).thenReturn( 17459 );
		Mockito.when( miningExtractionEntity.getSolarSystemId() ).thenReturn( 30001669 );
		Mockito.when( miningExtractionEntity.getQuantity() ).thenReturn( 15759L );
		Mockito.when( miningExtractionEntity.getExtractionHour() ).thenReturn( 21 );
		Mockito.when( miningExtractionEntity.getExtractionDateName() )
				.thenReturn( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) );
		Mockito.when( locationCatalogService.searchLocation4Id( Mockito.anyLong() ) ).thenReturn( spaceLocation );
		Mockito.when( resourceFactory.getItemById( Mockito.anyInt() ) ).thenReturn( neoItem );
		Mockito.when( neoItem.getTypeId() ).thenReturn( 17459 );
		Mockito.when( neoItem.getName() ).thenReturn( "Piroxeres" );
		Mockito.when( neoItem.getPrice() ).thenReturn( 432.56 );
		Mockito.when( neoItem.getVolume() ).thenReturn( 0.01 );
		Mockito.when( neoItem.getTypeIconURL() ).thenReturn( "http://17459.png" );
		Mockito.when( spaceLocation.getLocationId() ).thenReturn( 30001669L );
		Mockito.when( spaceLocation.getSolarSystemName() ).thenReturn( "Esescama" );
		// Test
		final MiningExtraction miningExtraction = new MiningExtractionEntityToMiningExtractionConverter( locationCatalogService , resourceFactory)
				.convert( miningExtractionEntity );
		final String calculatedId = LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) +
				":" +
				"21" +
				"-30001669-17459-92223647";
		// Assertions
		Assertions.assertNotNull( miningExtraction );
		Assertions.assertEquals( 15759, miningExtraction.getQuantity() );
		Assertions.assertEquals( 17459, miningExtraction.getTypeId() );
		Assertions.assertEquals( 92223647, miningExtraction.getOwnerId() );
		Assertions.assertEquals( calculatedId, miningExtraction.getId() );
		Assertions
				.assertEquals( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ), miningExtraction.getExtractionDateName() );
		Assertions.assertEquals( 21, miningExtraction.getExtractionHour() );
		Assertions.assertEquals( 30001669, miningExtraction.getLocationId() );
		Assertions.assertEquals( "Piroxeres", miningExtraction.getResourceName() );
		Assertions.assertEquals( "Esescama", miningExtraction.getSystemName() );
		Assertions.assertEquals( "http://17459.png", miningExtraction.getURLForItem() );
		Assertions.assertEquals( 0.01, miningExtraction.getVolume() );
		Assertions.assertEquals( 432.56, miningExtraction.getPrice() );
	}
}