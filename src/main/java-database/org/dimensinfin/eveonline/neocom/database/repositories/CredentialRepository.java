package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;

public class CredentialRepository {
	protected static Logger logger = LoggerFactory.getLogger(CredentialRepository.class);

	protected Dao<Credential, String> credentialDao;

	public List<Credential> findAllByServer( final String esiServer ) {
		try {
			return this.credentialDao.queryForEq("dataSource", esiServer.toLowerCase());
		} catch (SQLException sqle) {
			logger.warn("W [CredentialRepository.findAllByServer]> Exception reading all Credentials. {}",
					sqle.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Credential> accessAllCredentials() {
		List<Credential> credentialList = new ArrayList<>();
		try {
			return this.credentialDao.queryForAll();
		} catch (SQLException sqle) {
			logger.error("EX [CredentialRepository.findAllByServer]> SQLException: {}", sqle.getMessage());
		}
		return credentialList;

	}

	public void persist( final Credential record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			this.credentialDao.createOrUpdate(record);
		}
	}

	public Credential findCredentialById( final String credentialId ) throws SQLException {
		return this.credentialDao.queryForId(credentialId);
	}

	// - B U I L D E R
	public static class Builder {
		protected CredentialRepository onConstruction;

		public Builder() {
			this.onConstruction = new CredentialRepository();
		}

		public Builder withCredentialDao( final Dao<Credential, String> credentialDao ) {
			Objects.requireNonNull( credentialDao );
			this.onConstruction.credentialDao = credentialDao;
			return this;
		}

		public CredentialRepository build() {
			Objects.requireNonNull(this.onConstruction.credentialDao);
			return this.onConstruction;
		}
	}
}
