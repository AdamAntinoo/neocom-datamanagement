package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.PilotPreferencesEntity;

import static org.dimensinfin.eveonline.neocom.database.repositories.PilotPreferencesRepository.PREFERRED_MARKET_REGION_ID;
import static org.dimensinfin.eveonline.neocom.database.repositories.PilotPreferencesRepository.PREFERRED_MARKET_STATION_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.PilotPreferencesConstants.TEST_PILOT_PREFERENCE_PILOT_ID;

public class PilotPreferencesRepositoryTest {
	private final List<PilotPreferencesEntity> preferencesList = new ArrayList<>();
	private NeoComDatabaseService neoComDatabaseService;

	@BeforeEach
	public void beforeEach() {
		this.neoComDatabaseService = Mockito.mock( NeoComDatabaseService.class );
		final PilotPreferencesEntity prefMarketRegion = new PilotPreferencesEntity.Builder()
				.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
				.withPreferenceName( PREFERRED_MARKET_REGION_ID )
				.withPreferenceValue( (double) 10000012 )
				.build();
		this.preferencesList.add( prefMarketRegion );
		final PilotPreferencesEntity prefMarketStation = new PilotPreferencesEntity.Builder()
				.withPilotId( TEST_PILOT_PREFERENCE_PILOT_ID )
				.withPreferenceName( PREFERRED_MARKET_STATION_ID )
				.withPreferenceValue( (double) 60083760L )
				.build();
		this.preferencesList.add( prefMarketStation );
	}

	@Test
	public void constructorContract() {
		final PilotPreferencesRepository preferencesRepository = new PilotPreferencesRepository( this.neoComDatabaseService );
		Assertions.assertNotNull( preferencesRepository );
	}

	@Test
	public void findPilotPreferenceByPilot() throws SQLException {
		// Given
		final Integer pilotId = TEST_PILOT_PREFERENCE_PILOT_ID;
		final Dao<PilotPreferencesEntity, UUID> preferencesDao = Mockito.mock( Dao.class );
		// When
		Mockito.when( this.neoComDatabaseService.getPilotPreferencesDao() ).thenReturn( preferencesDao );
		Mockito.when( preferencesDao.queryForEq( Mockito.anyString(), Mockito.anyInt() ) ).thenReturn( this.preferencesList );
		// Test
		final PilotPreferencesRepository preferencesRepository = new PilotPreferencesRepository( this.neoComDatabaseService );
		final List<PilotPreferencesEntity> obtained = preferencesRepository.findPilotPreferenceByPilot( pilotId );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 2, obtained.size() );
		Assertions.assertEquals( (double) 10000012,
				preferencesRepository.accessMarketRegionId( pilotId ).getNumberValue(),
				0.01
		);
		Assertions.assertEquals( (double) 60083760L,
				preferencesRepository.accessMarketHubId( pilotId ).getNumberValue(),
				0.01
		);
	}
}