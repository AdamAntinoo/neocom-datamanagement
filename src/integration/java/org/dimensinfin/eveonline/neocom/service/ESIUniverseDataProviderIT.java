package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;

public class ESIUniverseDataProviderIT {
	private static final Integer TEST_CHARACTER_IDENTIFIER = 92223647;
	private static final Integer TEST_ALLIANCE_IDENTIFIER = 117383987;

	private IConfigurationService configurationService;
	private IFileSystem fileSystem;
	private RetrofitService retrofitService;
	private IStoreCache storeCache;
	private GenericContainer<?> esiAuthenticationSimulator;
	private GenericContainer<?> esiDataSimulator;

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
	public void getCharactersCharacterId() {
		// Prepare
		this.beforeEach();
		// Test
		final ESIUniverseDataProvider universeDataProvider = new ESIUniverseDataProvider.Builder()
				.withConfigurationProvider( this.configurationService )
				.withFileSystemAdapter( this.fileSystem )
				.withRetrofitFactory( this.retrofitService )
				.withStoreCacheManager( this.storeCache )
				.build();
		final GetCharactersCharacterIdOk obtained = universeDataProvider.getCharactersCharacterId( TEST_CHARACTER_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( "Beth Ripley", obtained.getName() );
	}
}