package org.dimensinfin.eveonline.neocom.service;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class ESIUniverseDataProviderIT {
	private static final Integer TEST_CHARACTER_IDENTIFIER = 92223647;
	private static final Integer TEST_CORPORATION_IDENTIFIER = 98384726;
	private static final Integer TEST_ALLIANCE_IDENTIFIER = 117383987;
	private static final Integer TEST_MARKET_REGION_ID = 10000043;
	private static final Integer TEST_MARKET_TYPE_ID = 11535;

	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private RetrofitService retrofitService;
	private IStoreCache storeCache;
	private GenericContainer<?> esiAuthenticationSimulator;
	private GenericContainer<?> esiDataSimulator;

	@Before
	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.configurationService = injector.getInstance( SBConfigurationService.class );
		this.fileSystem = injector.getInstance( SBFileSystemAdapter.class );
		this.retrofitService = injector.getInstance( RetrofitService.class );
		this.storeCache = injector.getInstance( MemoryStoreCacheService.class );
	}

	@Test
	public void getAlliancesAllianceId() {
		// Prepare
		this.beforeEach();
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final GetAlliancesAllianceIdOk obtained = universeDataProvider.getAlliancesAllianceId( TEST_ALLIANCE_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "AFK", obtained.getTicker() );
		Assertions.assertEquals( "Silent Infinity", obtained.getName() );
	}

	@Test
	public void getAlliancesAllianceIdIcons() {
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final GetAlliancesAllianceIdIconsOk obtained = universeDataProvider.getAlliancesAllianceIdIcons( TEST_ALLIANCE_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "https://images.evetech.net/Alliance/117383987_64.png", obtained.getPx64x64() );
	}


	@Test
	public void getCorporationsCorporationId() {
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final GetCorporationsCorporationIdOk obtained = universeDataProvider.getCorporationsCorporationId( TEST_CORPORATION_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "Industrias Machaque", obtained.getName() );
		Assertions.assertEquals( "IN.MA", obtained.getTicker() );
		Assertions.assertEquals( 92223647, obtained.getCeoId() );
	}

	@Test
	public void getCorporationsCorporationIdIcons() {
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final GetCorporationsCorporationIdIconsOk obtained = universeDataProvider.getCorporationsCorporationIdIcons( TEST_CORPORATION_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "https://images.evetech.net/Corporation/98384726_64.png", obtained.getPx64x64() );
	}

	@Test
	public void getUniverseMarketOrdersForId() {
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final List<GetMarketsRegionIdOrders200Ok> obtained = universeDataProvider
				.getUniverseMarketOrdersForId( TEST_MARKET_REGION_ID, TEST_MARKET_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
	}
}