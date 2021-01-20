package org.dimensinfin.eveonline.neocom.industry.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobRepository;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_ID;
import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_INSTALLER_ID;

public class IndustryJobControlServiceTest {
	@Test
	public void constructorContract() {
		final ESIDataService esiDataService = Mockito.mock( ESIDataService.class );
		final JobRepository jobRepository = Mockito.mock( JobRepository.class );
		final IndustryJobControlService industryJobControlService = new IndustryJobControlService( esiDataService, jobRepository );
		Assertions.assertNotNull( industryJobControlService );
	}

	@Test
	public void getActiveIndustryJobs() {
		// Given
		final ESIDataService esiDataService = Mockito.mock( ESIDataService.class );
		final JobRepository jobRepository = Mockito.mock( JobRepository.class );
		final Credential credential = Mockito.mock( Credential.class );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> universeJobs = new ArrayList<>();
		final GetCharactersCharacterIdIndustryJobs200Ok universeJob1 = new GetCharactersCharacterIdIndustryJobs200Ok();
		universeJob1.setJobId( TEST_ESI_UNIVERSE_JOB_ID );
		universeJob1.setInstallerId( TEST_ESI_UNIVERSE_JOB_INSTALLER_ID );
		universeJobs.add( universeJob1 );
		// When
		Mockito.when( esiDataService.getCharactersCharacterIdIndustryJobs( credential ) ).thenReturn( universeJobs );
		// Test
		final IndustryJobControlService industryJobControlService = new IndustryJobControlService( esiDataService, jobRepository );
		final List<JobEntity> obtained = industryJobControlService.getActiveIndustryJobs( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertFalse( obtained.isEmpty() );
	}
}