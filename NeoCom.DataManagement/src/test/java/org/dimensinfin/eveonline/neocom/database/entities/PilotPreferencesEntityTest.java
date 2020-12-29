package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotPreferencesConstants.TEST_PILOT_PREFERENCE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotPreferencesConstants.TEST_PILOT_PREFERENCE_NUMERIC_VALUE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotPreferencesConstants.TEST_PILOT_PREFERENCE_PILOT_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotPreferencesConstants.TEST_PILOT_PREFERENCE_STRING_VALUE;

public class PilotPreferencesEntityTest {
	@Test
	public void buildContract() {
		final PilotPreferencesEntity entity = new PilotPreferencesEntity.Builder()
				.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
				.withPreferenceName( TEST_PILOT_PREFERENCE_NAME )
				.withPreferenceValue( TEST_PILOT_PREFERENCE_STRING_VALUE )
				.withPreferenceValue( TEST_PILOT_PREFERENCE_NUMERIC_VALUE )
				.build();
		Assertions.assertNotNull( entity );
	}

	@Test
	public void buildFailureMissing() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotPreferencesEntity.Builder()
					.withPreferenceName( TEST_PILOT_PREFERENCE_NAME )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_STRING_VALUE )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_NUMERIC_VALUE )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotPreferencesEntity.Builder()
					.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_STRING_VALUE )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_NUMERIC_VALUE )
					.build();
		} );
	}

	@Test
	public void buildFailureNull() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			new PilotPreferencesEntity.Builder()
					.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
					.withPreferenceName( null )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_STRING_VALUE )
					.withPreferenceValue( TEST_PILOT_PREFERENCE_NUMERIC_VALUE )
					.build();
		} );
	}

	@Test
	public void getterContract() {
		final PilotPreferencesEntity entity = new PilotPreferencesEntity.Builder()
				.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
				.withPreferenceName( TEST_PILOT_PREFERENCE_NAME )
				.withPreferenceValue( TEST_PILOT_PREFERENCE_STRING_VALUE )
				.withPreferenceValue( TEST_PILOT_PREFERENCE_NUMERIC_VALUE )
				.build();
		// Assertions
		Assertions.assertNotNull( entity );
		Assertions.assertNotNull( entity.getId() );
		Assertions.assertEquals( TEST_PILOT_PREFERENCE_PILOT_ID, entity.getPilotId() );
		Assertions.assertEquals( TEST_PILOT_PREFERENCE_NAME, entity.getName() );
		Assertions.assertEquals( TEST_PILOT_PREFERENCE_STRING_VALUE, entity.getStringValue() );
		Assertions.assertEquals( TEST_PILOT_PREFERENCE_NUMERIC_VALUE, entity.getNumberValue() );
	}
}