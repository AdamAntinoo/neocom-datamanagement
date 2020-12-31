package org.dimensinfin.eveonline.neocom.miningextraction.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

public class MiningExtractionTest {
	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( MiningExtraction.class );
	}

	@Test
	public void buildComplete() {
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 24 )
				.withSpaceSystem( spaceSystem )
				.build();
		Assertions.assertNotNull( miningExtraction );
	}

	@Test
	public void equalsContract() {
		// Given
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		// When
		Mockito.when( neoItem.getTypeId() ).thenReturn( 321 );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "-URL-FOR-ITEM" );
		Mockito.when( spaceSystem.getLocationId() ).thenReturn( 3006754L );
		// Test
		final MiningExtraction miningExtractionA = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		final MiningExtraction miningExtractionB = new MiningExtraction.Builder()
				.withOwnerId( 43215 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 10005L )
				.withExtractionHour( 19 )
				.withSpaceSystem( spaceSystem )
				.build();
		final MiningExtraction miningExtractionC = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		// Assertions
		Assertions.assertTrue( miningExtractionA.equals( miningExtractionC ) );
		Assertions.assertFalse( miningExtractionA.equals( miningExtractionB ) );
		Assertions.assertFalse( miningExtractionC.equals( miningExtractionB ) );
	}

	@Test
	public void getPreviousHourId() {
		// Given
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		// Test
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		final String obtained = miningExtraction.getPreviousHourId();
		// Assertions
		Assertions.assertEquals( "2020-03-16:20-0-0-4321", miningExtraction.getId() );
		Assertions.assertEquals( "2020-03-16:19-0-0-4321", obtained );
	}

	@Test
	public void gettersContract() {
		// Given
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		// When
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "-URL-FOR-ITEM" );
		// Test
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		// Assertions
		Assertions.assertEquals( 4321, miningExtraction.getOwnerId() );
		Assertions.assertEquals( "-URL-FOR-ITEM", miningExtraction.getURLForItem() );
	}

	@Test
	public void hashCodeContract() {
		// Given
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		// When
		Mockito.when( neoItem.getTypeId() ).thenReturn( 321 );
		Mockito.when( neoItem.getURLForItem() ).thenReturn( "-URL-FOR-ITEM" );
		Mockito.when( spaceSystem.getLocationId() ).thenReturn( 3006754L );
		// Test
		final MiningExtraction miningExtractionA = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		final MiningExtraction miningExtractionB = new MiningExtraction.Builder()
				.withOwnerId( 43215 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 10005L )
				.withExtractionHour( 19 )
				.withSpaceSystem( spaceSystem )
				.build();
		final MiningExtraction miningExtractionC = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 20 )
				.withSpaceSystem( spaceSystem )
				.build();
		// Assertions
		Assertions.assertEquals( miningExtractionA.hashCode(), miningExtractionC.hashCode() );
		Assertions.assertFalse( miningExtractionA.hashCode() == miningExtractionB.hashCode() );
		Assertions.assertFalse( miningExtractionB.hashCode() == miningExtractionC.hashCode() );
	}

	@Test
	public void toStringContract() {
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( new LocalDate( "2020-03-08" ).toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 24 )
				.withSpaceSystem( spaceSystem )
				.build();
		final String expected = "{\"quantity\":1000,\"delta\":0,\"extractionDateName\":\"2020-03-08\",\"extractionHour\":24,\"ownerId\":4321,\"id\":\"2020-03-08:24-0-0-4321\",\"typeId\":0,\"locationId\":0,\"resourceName\":null,\"systemName\":null,\"URLForItem\":null,\"volume\":0.0,\"price\":0.0}";
		Assertions.assertEquals( expected, miningExtraction.toString() );
	}
}
