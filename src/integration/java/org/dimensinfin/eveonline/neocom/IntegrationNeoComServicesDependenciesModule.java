package org.dimensinfin.eveonline.neocom;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.IStoreCache;
import org.dimensinfin.eveonline.neocom.service.MemoryStoreCacheService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;
import org.dimensinfin.eveonline.neocom.support.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.support.SBFileSystemAdapter;
import org.dimensinfin.eveonline.neocom.support.SBSDEDatabaseService;

public class IntegrationNeoComServicesDependenciesModule extends AbstractModule {
	private static final String ENV_PROPERTIES_DIRECTORY = "PROPERTIES_DIRECTORY";
	private static final String ENV_APPLICATION_DIRECTORY = "APPLICATION_DIRECTORY";
	private static final String SDE_DATABASE = "SDE_DATABASE_PATH";

	@Override
	protected void configure() {
		String propDirectory = System.getProperty( ENV_PROPERTIES_DIRECTORY );
		String appDirectory = System.getProperty( ENV_APPLICATION_DIRECTORY );
		String sdeDatabasePath = System.getProperty( SDE_DATABASE );
		if (null == propDirectory) propDirectory = "/src/integration/resources/properties";
		if (null == appDirectory) appDirectory = "appDir";
		if (null == sdeDatabasePath) sdeDatabasePath = "/src/integration/resources/sde.db";
		bind( String.class )
				.annotatedWith( Names.named( "PropertiesDirectory" ) )
				.toInstance( propDirectory );
		bind( String.class )
				.annotatedWith( Names.named( "ApplicationDirectory" ) )
				.toInstance( appDirectory );
		bind( String.class )
				.annotatedWith( Names.named( "SDEDatabasePath" ) )
				.toInstance( sdeDatabasePath );

		bind( IConfigurationService.class )
				.annotatedWith( Names.named( "IConfigurationService" ) )
				.to( SBConfigurationService.class );
		bind( IFileSystem.class )
				.annotatedWith( Names.named( "IFileSystem" ) )
				.to( SBFileSystemAdapter.class );
		bind( RetrofitService.class )
				.annotatedWith( Names.named( "RetrofitService" ) )
				.to( RetrofitService.class );
		bind( ISDEDatabaseService.class )
				.annotatedWith( Names.named( "ISDEDatabaseService" ) )
				.to( SBSDEDatabaseService.class );
		bind( IStoreCache.class )
				.annotatedWith( Names.named( "IStoreCache" ) )
				.to( MemoryStoreCacheService.class );
		bind( ESIDataService.class )
				.annotatedWith( Names.named( "ESIDataService" ) )
				.to( ESIDataService.class );
		bind( ResourceFactory.class )
				.annotatedWith( Names.named( "ResourceFactory" ) )
				.to( ResourceFactory.class );
	}
}