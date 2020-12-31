package org.dimensinfin.eveonline.neocom.infinity;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.infinity.service.SBConfigurationService;
import org.dimensinfin.eveonline.neocom.infinity.service.SBFileSystemAdapter;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

public class IntegrationNeoComServicesDependenciesModule extends AbstractModule {
	private static final String ENV_PROPERTIES_DIRECTORY = "PROPERTIES_DIRECTORY";
	private static final String ENV_APPLICATION_DIRECTORY = "APPLICATION_DIRECTORY";

	@Override
	protected void configure() {
		String propDirectory = System.getProperty( ENV_PROPERTIES_DIRECTORY );
		String appDirectory = System.getProperty( ENV_APPLICATION_DIRECTORY );
		if (null == propDirectory) propDirectory = "/src/integration/resources/properties";
		if (null == appDirectory) appDirectory = "appDir";
		bind( String.class )
				.annotatedWith( Names.named( "PropertiesDirectory" ) )
				.toInstance( propDirectory );
		bind( String.class )
				.annotatedWith( Names.named( "ApplicationDirectory" ) )
				.toInstance( appDirectory );
		bind( IConfigurationService.class )
				.annotatedWith( Names.named( "IConfigurationService" ) )
				.to( SBConfigurationService.class );
		bind( IFileSystem.class )
				.annotatedWith( Names.named( "IFileSystem" ) )
				.to( SBFileSystemAdapter.class );
		bind( RetrofitService.class )
				.annotatedWith( Names.named( "RetrofitService" ) )
				.to( RetrofitService.class );

		//		bind( StoreCacheManager.class )
		//				.annotatedWith( Names.named( "StoreCacheManager" ) )
		//				.to( StoreCacheManager.class );
		//		bind( RetrofitFactory.class )
		//				.annotatedWith( Names.named( "RetrofitFactory" ) )
		//				.to( RetrofitFactory.class );
		//				bind( LocationCatalogService.class )
		//						.annotatedWith( Names.named( "LocationCatalogService" ) )
		//						.to( LocationCatalogService.class );
	}
}