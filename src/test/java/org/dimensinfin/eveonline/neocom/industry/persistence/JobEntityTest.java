package org.dimensinfin.eveonline.neocom.industry.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.industry.converter.GetCharactersCharacterIdIndustryJobsToJobEntityConverter;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiUniverseTestDataConstants.TEST_ESI_UNIVERSE_JOB_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiUniverseTestDataConstants.TEST_ESI_UNIVERSE_JOB_INSTALLER_ID;

public class JobEntityTest {
	@Test
	public void constructorContract() {
		// Empty
		JobEntity jobEntity = new JobEntity();
		Assertions.assertNotNull( jobEntity );
		// Mapped
		final GetCharactersCharacterIdIndustryJobs200Ok universeJob = Mockito.mock( GetCharactersCharacterIdIndustryJobs200Ok.class );
		jobEntity = new GetCharactersCharacterIdIndustryJobsToJobEntityConverter( "InstallerName" ).convert( universeJob );
		Assertions.assertNotNull( jobEntity );
	}

	@Test
	public void getterContrac() {
		// Given
		final String name = "InstallerName";
		final GetCharactersCharacterIdIndustryJobs200Ok universeJob = new GetCharactersCharacterIdIndustryJobs200Ok();
		universeJob.setJobId( TEST_ESI_UNIVERSE_JOB_ID );
		universeJob.setInstallerId( TEST_ESI_UNIVERSE_JOB_INSTALLER_ID );
		// Test
		final JobEntity jobEntity = new GetCharactersCharacterIdIndustryJobsToJobEntityConverter( name ).convert( universeJob );
		//Assertions
		Assertions.assertEquals( TEST_ESI_UNIVERSE_JOB_ID, jobEntity.getJobId() );
		Assertions.assertEquals( TEST_ESI_UNIVERSE_JOB_INSTALLER_ID, jobEntity.getInstallerId() );
		Assertions.assertEquals( name, jobEntity.getInstallerName() );
	}
}