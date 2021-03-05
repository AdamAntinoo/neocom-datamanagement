package org.dimensinfin.eveonline.neocom;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.database.DMDatabaseDependenciesModule;
import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.loyalty.service.LoyaltyService;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.IDataStore;
import org.dimensinfin.eveonline.neocom.service.IStoreCache;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.service.MemoryStoreCacheService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;
import org.dimensinfin.eveonline.neocom.support.IntegrationNeoComDatabaseService;
import org.dimensinfin.eveonline.neocom.support.IntegrationRedisDataStoreImplementation;
import org.dimensinfin.eveonline.neocom.support.MarketServiceReconfigurer;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;
import org.dimensinfin.eveonline.neocom.support.SBSDEDatabaseService;

public class IntegrationNeoComServicesDependenciesModule extends AbstractModule {
	private static final String ENV_PROPERTIES_DIRECTORY = "PROPERTIES_DIRECTORY";
	private static final String ENV_APPLICATION_DIRECTORY = "APPLICATION_DIRECTORY";
	private static final String SDE_DATABASE = "SDE_DATABASE_PATH";
	private static final String NEOCOM_DATABASE = "NEOCOM_DATABASE_PATH";

	@Override
	protected void configure() {
		String propDirectory = System.getProperty( ENV_PROPERTIES_DIRECTORY );
		String appDirectory = System.getProperty( ENV_APPLICATION_DIRECTORY );
		String sdeDatabasePath = System.getProperty( SDE_DATABASE );
		String neocomDatabasePath = System.getProperty( NEOCOM_DATABASE );
		final String redisDatabaseUrl = "redis://localhost:6379";
		if (null == propDirectory) propDirectory = "/src/integration/resources/properties";
		if (null == appDirectory) appDirectory = "appDir";
		if (null == sdeDatabasePath) sdeDatabasePath = "/src/integration/resources/sde.db";
		if (null == neocomDatabasePath) neocomDatabasePath = "jdbc:sqlite:neocom.db";
		this.bind( String.class )
				.annotatedWith( Names.named( "PropertiesDirectory" ) )
				.toInstance( propDirectory );
		this.bind( String.class )
				.annotatedWith( Names.named( "ApplicationDirectory" ) )
				.toInstance( appDirectory );
		this.bind( String.class )
				.annotatedWith( Names.named( "SDEDatabasePath" ) )
				.toInstance( sdeDatabasePath );
		this.bind( String.class )
				.annotatedWith( Names.named( "NeoComDatabasePath" ) )
				.toInstance( neocomDatabasePath );
		this.bind( String.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.REDIS_DATABASE_URL ) )
				.toInstance( redisDatabaseUrl );

		this.bind( IConfigurationService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.ICONFIGURATION_SERVICE ) )
				.to( SBConfigurationService.class )
				.in( Singleton.class );
		this.bind( IFileSystem.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.IFILE_SYSTEM ) )
				.to( SBFileSystemAdapter.class )
				.in( Singleton.class );
		this.bind( IStoreCache.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.ISTORE_CACHE ) )
				.to( MemoryStoreCacheService.class )
				.in( Singleton.class );
		this.bind( RetrofitService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.RETROFIT_SERVICE ) )
				.to( RetrofitService.class )
				.in( Singleton.class );
		this.bind( LocationCatalogService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.LOCATION_CATALOG_SERVICE ) )
				.to( LocationCatalogService.class )
				.in( Singleton.class );
		this.bind( ESIDataService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.ESIDATA_SERVICE ) )
				.to( ESIDataService.class )
				.in( Singleton.class );
		this.bind( ResourceFactory.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.RESOURCE_FACTORY ) )
				.to( ResourceFactory.class )
				.in( Singleton.class );
		this.bind( ISDEDatabaseService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.ISDE_DATABASE_SERVICE ) )
				.to( SBSDEDatabaseService.class )
				.in( Singleton.class );
		this.bind( NeoComDatabaseService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.INEOCOM_DATABASE_SERVICE ) )
				.to( IntegrationNeoComDatabaseService.class )
				.in( Singleton.class );
		this.bind( LoyaltyOffersRepository.class )
				.annotatedWith( Names.named( DMDatabaseDependenciesModule.LOYALTYOFFERS_REPOSITORY ) )
				.to( LoyaltyOffersRepository.class )
				.in( Singleton.class );
		this.bind( LoyaltyService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.LOYALTY_SERVICE ) )
				.to( LoyaltyService.class )
				.in( Singleton.class );
		this.bind( MarketService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.MARKET_SERVICE ) )
				.to( MarketService.class )
				.in( Singleton.class );

		this.bind( IDataStore.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.IDATA_STORE ) )
				.to( IntegrationRedisDataStoreImplementation.class )
				.in( Singleton.class );

		this.bind( MarketServiceReconfigurer.class )
				.annotatedWith( Names.named( "Reconfigurer" ) )
				.to( MarketServiceReconfigurer.class )
				.in( Singleton.class );
	}
}