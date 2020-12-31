package org.dimensinfin.eveonline.neocom.infinity.service;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.infinity.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

public class ESIDataServiceIT {
	private static final Integer TEST_INDUSTRY_JOBS_JOB_ID = 321654;

	private RetrofitService retrofitService;
	private LocationCatalogService locationCatalogService;

	@BeforeEach
	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.retrofitService = injector.getInstance( RetrofitService.class );
		this.locationCatalogService = injector.getInstance( LocationCatalogService.class );
	}

	@Test
	public void getCharactersCharacterIdIndustryJobsSuccess() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdIndustryJobs200Ok job = Mockito.mock( GetCharactersCharacterIdIndustryJobs200Ok.class );
		// When
//		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenReturn()
		Mockito.when( job.getJobId() ).thenReturn( TEST_INDUSTRY_JOBS_JOB_ID );
		// Test
		final ESIDataService esiDataService = new ESIDataService( this.retrofitService, this.locationCatalogService );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> obtained = esiDataService.getCharactersCharacterIdIndustryJobs( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertNotNull( obtained.get( 0 ) );
		Assertions.assertEquals( TEST_INDUSTRY_JOBS_JOB_ID, obtained.get( 0 ).getJobId() );
	}
}