package org.dimensinfin.eveonline.neocom.infinity;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class IntegrationNeoComServicesDependenciesModule extends AbstractModule {
	private static final String ENV_PROPERTIES_DIRECTORY = "PROPERTIES_DIRECTORY";
	private static final String ENV_APPLICATION_DIRECTORY = "APPLICATION_DIRECTORY";

	@Override
	protected void configure() {
		bind( String.class )
				.annotatedWith( Names.named( "PropertiesDirectory" ) )
				.toInstance( System.getProperty( ENV_PROPERTIES_DIRECTORY ) );
		bind( String.class )
				.annotatedWith( Names.named( "ApplicationDirectory" ) )
				.toInstance( System.getProperty( ENV_APPLICATION_DIRECTORY ) );
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
		//		bind( LocationCatalogService.class )
		//				.annotatedWith( Names.named( "LocationCatalogService" ) )
		//				.to( LocationCatalogService.class );
	}
}