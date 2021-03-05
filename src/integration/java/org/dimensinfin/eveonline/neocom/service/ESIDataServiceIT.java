package org.dimensinfin.eveonline.neocom.service;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.support.IntegrationRedisDataStoreImplementation;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CredentialConstants.TEST_CREDENTIAL_SCOPE;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.CredentialConstants.TEST_CREDENTIAL_UNIQUE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ESIDataServiceConstants.TEST_ALLIANCE_IDENTIFIER;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ESIDataServiceConstants.TEST_CHARACTER_IDENTIFIER;

public class ESIDataServiceIT {
	private static final int ESI_AUTHENTICATION_UNITTESTING_PORT = 53100;
	private static final int ESI_DATA_UNITTESTING_PORT = 53200;
	private static final String ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME = "/esioauth-simulation";
	private static final String ESI_DATA_SIMULATOR_SERVICE_NAME = "/esidata-simulation";
	private static final String ESI_DATA_SIMULATOR_PATH = "/home/adam/Development/NeoCom0.20/NeoCom-DataManagement/NeoCom" +
			".DataManagement/src/integration/resources" +
			ESI_DATA_SIMULATOR_SERVICE_NAME;
	private static final Integer TEST_INDUSTRY_JOBS_JOB_ID = 446529008;
	private static final Integer TEST_CREDENTIAL_ID = 92223647;

	private SBConfigurationService configurationService; // Use the class to get access to the method to update properties.
	private IFileSystem fileSystem;
	private IStoreCache storeCache;
	private RetrofitService retrofitService;
	private LocationCatalogService locationCatalogService;
	private IDataStore dataStore;
	private ESIDataService esiDataService;

	private GenericContainer<?> esiAuthenticationSimulator;
	private GenericContainer<?> esiDataSimulator;

	/**
	 * WARNING: Internally the Test Containers will expose an aleatory port that should be configured onto the properties after they are read from
	 * the sources.
	 */
	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.configurationService = injector.getInstance( SBConfigurationService.class );
		this.fileSystem = injector.getInstance( SBFileSystemAdapter.class );
		this.retrofitService = injector.getInstance( RetrofitService.class );
		this.dataStore = injector.getInstance( IntegrationRedisDataStoreImplementation.class );
		this.storeCache = injector.getInstance( MemoryStoreCacheService.class );
		this.locationCatalogService = new LocationCatalogService( this.retrofitService );
		this.esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.retrofitService,
				this.locationCatalogService,
				this.storeCache,
				this.dataStore );
	}

	@Test
	public void getAlliancesAllianceId() {
		// Prepare
		this.beforeEach();
		final GetAlliancesAllianceIdOk obtained = this.esiDataService.getAlliancesAllianceId( TEST_ALLIANCE_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "AFK", obtained.getTicker() );
		Assertions.assertEquals( "Silent Infinity", obtained.getName() );
	}

	@Test
	public void getAlliancesAllianceIdIcons() {
		// Prepare
		this.beforeEach();
		final GetAlliancesAllianceIdIconsOk obtained = this.esiDataService.getAlliancesAllianceIdIcons( TEST_ALLIANCE_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "https://images.evetech.net/Alliance/117383987_64.png", obtained.getPx64x64() );
	}

	@Test
	public void getAlliancesAllianceIdNotFound() {
	}

	@Test
	public void getCharactersCharacterId() {
		// Prepare
		this.beforeEach();
		// Test
		final GetCharactersCharacterIdOk obtained = this.esiDataService.getCharactersCharacterId( TEST_CHARACTER_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "Zach Zender", obtained.getName() );
	}

	//	@Test
	public void getCharactersCharacterIdIndustryJobsSuccess() {
		// Prepare
		//		this.prepareMocks();
		this.beforeEach();
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
		//		final ESIDataService esiDataService = new ESIDataService( this.configurationService,
		//				this.fileSystem,
		//				this.storeCache,
		//				this.retrofitService,
		//				this.locationCatalogService );
		final List<GetCharactersCharacterIdIndustryJobs200Ok> obtained = this.esiDataService.getCharactersCharacterIdIndustryJobs( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertNotNull( obtained.get( 0 ) );
		Assertions.assertEquals( TEST_INDUSTRY_JOBS_JOB_ID, obtained.get( 0 ).getJobId() );
	}

	public void prepareMocks() {
		this.esiAuthenticationSimulator = new GenericContainer<>( "apimastery/apisimulator" )
				.withExposedPorts( ESI_AUTHENTICATION_UNITTESTING_PORT )
				.withFileSystemBind( ESI_DATA_SIMULATOR_PATH, ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME, BindMode.READ_WRITE )
				.withCommand( "bin/apisimulator start " + ESI_AUTHENTICATION_SIMULATOR_SERVICE_NAME + " -p " + ESI_AUTHENTICATION_UNITTESTING_PORT );
		this.esiAuthenticationSimulator.start();
		this.esiDataSimulator = new GenericContainer<>( "apimastery/apisimulator" )
				.withExposedPorts( ESI_DATA_UNITTESTING_PORT )
				.withFileSystemBind( ESI_DATA_SIMULATOR_PATH, ESI_DATA_SIMULATOR_SERVICE_NAME, BindMode.READ_WRITE )
				.withCommand( "bin/apisimulator start " + ESI_DATA_SIMULATOR_SERVICE_NAME + " -p " + ESI_DATA_UNITTESTING_PORT );
		this.esiDataSimulator.start();
	}
}