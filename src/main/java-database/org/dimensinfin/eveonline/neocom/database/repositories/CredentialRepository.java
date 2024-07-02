package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

public class CredentialRepository {
	protected Dao<Credential, String> credentialDao;

	// - C O N S T R U C T O R S
	@Inject
	public CredentialRepository( @NotNull @Named(DMServicesDependenciesModule.INEOCOM_DATABASE_SERVICE) final NeoComDatabaseService neoComDatabaseService ) {
		try {
			this.credentialDao = Objects.requireNonNull( neoComDatabaseService ).getCredentialDao();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
			throw new NeoComRuntimeException( sqle );
		}
	}

	public List<Credential> accessAllCredentials() {
		final List<Credential> credentialList = new ArrayList<>();
		try {
			return this.credentialDao.queryForAll();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		}
		return credentialList;

	}

	public List<Credential> findAllByServer( final String esiServer ) {
		try {
			return this.credentialDao.queryForEq( "dataSource", esiServer.toLowerCase() );
		} catch (final SQLException sqle) {
			LogWrapper.error( new SQLException( MessageFormat.format(
					"Exception reading all Credentials. {0} - {1}",
					esiServer,
					sqle.getMessage() )
			) );
			return new ArrayList<>();
		}
	}

	public Credential findCredentialById( final String credentialId ) throws SQLException {
		return this.credentialDao.queryForId( credentialId );
	}

	public Credential persist( final @NotNull Credential record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.credentialDao.createOrUpdate( record );
		}
		return record;
	}
}
