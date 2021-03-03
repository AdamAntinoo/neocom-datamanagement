package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.PilotPreferencesEntity;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

public class PilotPreferencesRepository {
	public static final String PREFERRED_MARKET_REGION_ID = "-PREFERRED_MARKET_REGION_ID-";
	public static final String PREFERRED_MARKET_STATION_ID = "-PREFERRED_MARKET_STATION_ID-";
	private static final String PILOTPREFERENCES_PILOT_ID = "pilotId";
	protected final Dao<PilotPreferencesEntity, UUID> pilotPreferencesDao;

	// - C O N S T R U C T O R S
	@Inject
	public PilotPreferencesRepository( @NotNull @Named(DMServicesDependenciesModule.INEOCOM_DATABASE_SERVICE) final NeoComDatabaseService neoComDatabaseService ) {
		try {
			this.pilotPreferencesDao = Objects.requireNonNull( neoComDatabaseService ).getPilotPreferencesDao();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
			throw new NeoComRuntimeException( sqle );
		}
	}

	public PilotPreferencesEntity accessMarketHubId( final int pilotId ) {
		final List<PilotPreferencesEntity> preferences = this.findPilotPreferenceByPilot( pilotId )
				.stream()
				.filter( pref -> pref.getName().equals( PREFERRED_MARKET_STATION_ID ) )
				.collect( Collectors.toList() );
		if (preferences.isEmpty()) return new PilotPreferencesEntity.Builder()
				.withPilotId( pilotId )
				.withPreferenceName( PREFERRED_MARKET_STATION_ID )
				.withPreferenceValue( (double) MarketService.PREDEFINED_MARKET_HUB_STATION_ID )
				.build();
		else return preferences.get( 0 );

	}

	public PilotPreferencesEntity accessMarketRegionId( final int pilotId ) {
		final List<PilotPreferencesEntity> preferences = this.findPilotPreferenceByPilot( pilotId )
				.stream()
				.filter( pref -> pref.getName().equals( PREFERRED_MARKET_REGION_ID ) )
				.collect( Collectors.toList() );
		if (preferences.isEmpty()) return new PilotPreferencesEntity.Builder()
				.withPilotId( pilotId )
				.withPreferenceName( PREFERRED_MARKET_REGION_ID )
				.withPreferenceValue( (double) MarketService.PREDEFINED_MARKET_REGION_ID )
				.build();
		else return preferences.get( 0 );
	}

	public List<PilotPreferencesEntity> findPilotPreferenceByPilot( final int pilotId ) {
		try {
			return this.pilotPreferencesDao.queryForEq( PILOTPREFERENCES_PILOT_ID, pilotId );
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
			return new ArrayList<>();
		}
	}
}