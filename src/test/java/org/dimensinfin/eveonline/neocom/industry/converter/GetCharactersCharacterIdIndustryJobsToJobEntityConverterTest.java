package org.dimensinfin.eveonline.neocom.industry.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;

import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_ID;
import static org.dimensinfin.eveonline.neocom.service.ESIDataServiceTest.GetCharactersMockConstants.TEST_ESI_UNIVERSE_JOB_INSTALLER_ID;

public class GetCharactersCharacterIdIndustryJobsToJobEntityConverterTest {
	@Test
	public void constructorContract() {
		final String name = "Installers Name";
		final GetCharactersCharacterIdIndustryJobsToJobEntityConverter converter = new GetCharactersCharacterIdIndustryJobsToJobEntityConverter(
				name );
		Assertions.assertNotNull( converter );
	}

	@Test
	public void convert() {
		// Given
		final String name = "Installers Name";
		final GetCharactersCharacterIdIndustryJobs200Ok universeJob = new GetCharactersCharacterIdIndustryJobs200Ok();
		universeJob.setJobId( TEST_ESI_UNIVERSE_JOB_ID );
		universeJob.setInstallerId( TEST_ESI_UNIVERSE_JOB_INSTALLER_ID );
		// Test
		final GetCharactersCharacterIdIndustryJobsToJobEntityConverter converter = new GetCharactersCharacterIdIndustryJobsToJobEntityConverter(
				name );
		final JobEntity obtained = converter.convert( universeJob );
		// Assertions
		Assertions.assertEquals( TEST_ESI_UNIVERSE_JOB_ID, obtained.getJobId() );
		Assertions.assertEquals( TEST_ESI_UNIVERSE_JOB_INSTALLER_ID, obtained.getInstallerId() );
		Assertions.assertEquals( name, obtained.getInstallerName() );
	}
}