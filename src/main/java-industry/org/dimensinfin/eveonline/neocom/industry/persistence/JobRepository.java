package org.dimensinfin.eveonline.neocom.industry.persistence;

import java.sql.SQLException;
import java.text.MessageFormat;
import javax.validation.constraints.NotNull;

import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.logging.LogWrapper;

public class JobRepository {
	private final Dao<JobEntity, String> industryJobDao;

	// - C O N S T R U C T O R S
	public JobRepository( final @NotNull @Named(DMServicesDependenciesModule.NEOCOM_DATABASE_SERVICE) NeoComDatabaseService neoComDatabaseService ) throws SQLException {
		this.industryJobDao = neoComDatabaseService.getIndustryJobDao();
	}

	public void persist( final JobEntity jobEntity ) throws SQLException {
		if (null != jobEntity) {
			jobEntity.timeStamp();
			this.industryJobDao.createOrUpdate( jobEntity );
			LogWrapper.info( MessageFormat.format( "Write/Update job. Id: {0} - owner {1}",
					jobEntity.getJobId(),
					jobEntity.getInstallerName() )
			);
		}
	}
}