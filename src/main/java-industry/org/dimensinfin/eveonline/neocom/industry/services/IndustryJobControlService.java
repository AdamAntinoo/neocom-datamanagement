package org.dimensinfin.eveonline.neocom.industry.services;

import java.sql.SQLException;
import java.util.List;
import javax.validation.constraints.NotNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.industry.converter.GetCharactersCharacterIdIndustryJobsToJobEntityConverter;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobRepository;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.logging.LogWrapper;

public class IndustryJobControlService {
	private final ESIDataService esiDataService;
	private final JobRepository jobRepository;

	// - C O N S T R U C T O R S
	public IndustryJobControlService( final @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) ESIDataService esiDataService,
	                                  final @NotNull @Named("JobRepository") JobRepository jobRepository ) {
		this.esiDataService = esiDataService;
		this.jobRepository = jobRepository;
	}

	/**
	 * Reads the jobs from the ESI service and after transforming the jobs persists it on the Job repository.
	 * @param credential the pilot credential to use to read the industry job list.
	 * @return
	 */
	public List<JobEntity> getActiveIndustryJobs( final Credential credential ) {
		final GetCharactersCharacterIdIndustryJobsToJobEntityConverter jobConverter =
				new GetCharactersCharacterIdIndustryJobsToJobEntityConverter( credential.getAccountName() );
		return Stream.of( this.esiDataService.getCharactersCharacterIdIndustryJobs( credential ) )
				.map( job -> jobConverter.convert( job ) )
				.map( industryJob -> {
					try {
						this.jobRepository.persist( industryJob );
					} catch (final SQLException sqle) {
						LogWrapper.error( sqle );
					}
					return industryJob;
				} )
				.collect( Collectors.toList() );
	}
}