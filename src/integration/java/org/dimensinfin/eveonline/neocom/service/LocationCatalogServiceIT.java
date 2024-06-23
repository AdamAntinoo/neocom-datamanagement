package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;
import org.dimensinfin.eveonline.neocom.ports.ILocationFactoryPort;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.support.IntegrationRedisDataStoreImplementation;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.ESIDataServiceConstants.TEST_ALLIANCE_IDENTIFIER;

public class LocationCatalogServiceIT {
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
	private IDataStorePort dataStore;
	private ESIDataService esiDataService;
	private ILocationFactoryPort locationFactory;

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
		this.locationFactory = Mockito.mock( ILocationFactoryPort.class );
		this.locationCatalogService = new LocationCatalogService( this.locationFactory);
		this.esiDataService = new ESIDataService(
				this.configurationService,
				this.fileSystem,
				this.retrofitService,
				this.locationCatalogService,
				this.storeCache,
				this.dataStore );
	}
	@Test
	public void when_location_notcached_is_requested_get_location_generated() {
		// Prepare
		this.beforeEach();
		final GetAlliancesAllianceIdOk obtained = this.esiDataService.getAlliancesAllianceId( TEST_ALLIANCE_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "AFK", obtained.getTicker() );
		Assertions.assertEquals( "Silent Infinity", obtained.getName() );
	}
}
