package org.dimensinfin.eveonline.neocom.infinity.service;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.infinity.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CredentialConstants.TEST_CREDENTIAL_SCOPE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CredentialConstants.TEST_CREDENTIAL_UNIQUE_ID;

public class ESIDataServiceIT {
	private static final Integer TEST_INDUSTRY_JOBS_JOB_ID = 321654;
	private static final int ESI_AUTHENTICATION_UNITTESTING_PORT = 5310;
	private static final int ESI_DATA_UNITTESTING_PORT = 5320;
	private static final String ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME = "/esioauth-simulation";
	private static final String ESI_DATA_SIMULATOR_SERVICE_NAME = "/esidata-simulation";
	private static final String ESI_DATA_SIMULATOR_PATH = "/home/adam/Development/NeoCom0.20/NeoCom-DataManagement/NeoCom" +
			".DataManagement/src/integration/resources" +
			ESI_DATA_SIMULATOR_SERVICE_NAME;
	private static final Integer TEST_CREDENTIAL_ID = 92223647;

	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private RetrofitService retrofitService;
	private GenericContainer<?> esiAuthenticationSimulator;
	private GenericContainer<?> esiDataSimulator;

	@BeforeEach
	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.configurationService = injector.getInstance( SBConfigurationService.class );
		this.fileSystem = injector.getInstance( SBFileSystemAdapter.class );
		this.retrofitService = injector.getInstance( RetrofitService.class );

		this.esiAuthenticationSimulator = new GenericContainer<>( "apimastery/apisimulator" )
				.withExposedPorts( ESI_AUTHENTICATION_UNITTESTING_PORT )
				.withFileSystemBind( ESI_DATA_SIMULATOR_PATH, ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME, BindMode.READ_WRITE )
				.withCommand( "bin/apisimulator start " + ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME + " -p " + ESI_AUTHENTICATION_UNITTESTING_PORT );
		esiAuthenticationSimulator.start();
		this.esiDataSimulator = new GenericContainer<>( "apimastery/apisimulator" )
				.withExposedPorts( ESI_DATA_UNITTESTING_PORT )
				.withFileSystemBind( ESI_DATA_SIMULATOR_PATH, ESI_DATA_SIMULATOR_SERVICE_NAME, BindMode.READ_WRITE )
				.withCommand( "bin/apisimulator start " + ESI_DATA_SIMULATOR_SERVICE_NAME + " -p " + ESI_DATA_UNITTESTING_PORT );
		esiDataSimulator.start();
	}

	@Test
	public void getCharactersCharacterIdIndustryJobsSuccess() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final GetCharactersCharacterIdIndustryJobs200Ok job = Mockito.mock( GetCharactersCharacterIdIndustryJobs200Ok.class );
		// When
		Mockito.when( credential.getUniqueCredential() ).thenReturn( TEST_CREDENTIAL_UNIQUE_ID );
		Mockito.when( credential.getAccountId() ).thenReturn( TEST_CREDENTIAL_ID );
		Mockito.when( credential.getDataSource() ).thenReturn( DEFAULT_ESI_SERVER );
		Mockito.when( credential.getScope() ).thenReturn( TEST_CREDENTIAL_SCOPE );
		//		Mockito.when( this.retrofitFactory.accessUniverseConnector() ).thenReturn()
		Mockito.when( job.getJobId() ).thenReturn( TEST_INDUSTRY_JOBS_JOB_ID );
		// Test
		final ESIDataService esiDataService = new ESIDataService( this.configurationService, this.fileSystem, this.retrofitService );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> obtained = esiDataService.getCharactersCharacterIdIndustryJobs( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertNotNull( obtained.get( 0 ) );
		Assertions.assertEquals( TEST_INDUSTRY_JOBS_JOB_ID, obtained.get( 0 ).getJobId() );
	}
}