package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.PilotPreferencesEntity;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.logging.LogWrapper;

public class PilotPreferencesRepository {
	private static final String PILOTPREFERENCES_PILOT_ID = "pilotId";
	protected final Dao<PilotPreferencesEntity, UUID> pilotPreferencesDao;

	// - C O N S T R U C T O R S
	@Inject
	private PilotPreferencesRepository( final @NotNull @Named("NeoComDatabaseService") NeoComDatabaseService neoComDatabaseService ) {
		try {
			this.pilotPreferencesDao = Objects.requireNonNull(neoComDatabaseService).getPilotPreferencesDao();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
			throw new NeoComRuntimeException(sqle);
		}
	}

	public List<PilotPreferencesEntity> findPilotPreferenceByPilot( final int pilotId ) throws SQLException {
		return this.pilotPreferencesDao.queryForEq( PILOTPREFERENCES_PILOT_ID, pilotId );
	}
	private Optional<PilotPreferencesEntity> accessPreferredMarketHubPreference( final Credential credential ) {
		return Optional.empty();
	}
}