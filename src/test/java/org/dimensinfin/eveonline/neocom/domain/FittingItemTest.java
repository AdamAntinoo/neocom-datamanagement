package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.FittingItemConstants.TEST_FITTING_ITEM_FLAG;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.FittingItemConstants.TEST_FITTING_ITEM_QUANTITY;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.FittingItemConstants.TEST_FITTING_ITEM_TYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.FittingItemConstants.TEST_FITTING_ITEM_TYPE_NAME;

public class FittingItemTest {
	private CharacterscharacterIdfittingsItems fittingData;
	private EsiType esiType;

	@BeforeEach
	public void beforeEach() {
		this.fittingData = Mockito.mock( CharacterscharacterIdfittingsItems.class );
		this.esiType = Mockito.mock( EsiType.class );
	}

	@Test
	public void buildContract() {
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingData( this.fittingData )
				.withType( this.esiType )
				.build();
		Assertions.assertNotNull( fittingItem );
	}

	@Test
	public void buildFailureMissingWith() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new FittingItem.Builder()
					.withType( this.esiType )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new FittingItem.Builder()
					.withFittingData( this.fittingData )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new FittingItem.Builder()
					.withFittingData( null )
					.withType( this.esiType )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new FittingItem.Builder()
					.withFittingData( this.fittingData )
					.withType( null )
					.build();
		} );
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( FittingItem.class )
				.suppress( Warning.NONFINAL_FIELDS )
				.usingGetClass().verify();
	}

	@Test
	public void gettersContract() {
		// When
		Mockito.when( this.fittingData.getFlag() ).thenReturn( TEST_FITTING_ITEM_FLAG );
		Mockito.when( this.fittingData.getQuantity() ).thenReturn( TEST_FITTING_ITEM_QUANTITY );
		Mockito.when( this.fittingData.getTypeId() ).thenReturn( TEST_FITTING_ITEM_TYPE_ID );
		Mockito.when( this.esiType.getName() ).thenReturn( TEST_FITTING_ITEM_TYPE_NAME );
		// Test
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingData( this.fittingData )
				.withType( this.esiType )
				.build();
		// Assertions
		Assertions.assertEquals( TEST_FITTING_ITEM_FLAG, fittingItem.getFlag() );
		Assertions.assertEquals( TEST_FITTING_ITEM_QUANTITY, fittingItem.getQuantity() );
		Assertions.assertEquals( TEST_FITTING_ITEM_TYPE_ID, fittingItem.getTypeId() );
		Assertions.assertEquals( TEST_FITTING_ITEM_TYPE_NAME, fittingItem.getTypeName() );
	}

	//	@Test
	public void toStringContract() {
		// Test
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingData( this.fittingData )
				.withType( this.esiType )
				.build();
		final String expected = "";
		final String obtained = fittingItem.toString();
		// Assertions
		Assertions.assertEquals( expected, obtained );

	}
}
