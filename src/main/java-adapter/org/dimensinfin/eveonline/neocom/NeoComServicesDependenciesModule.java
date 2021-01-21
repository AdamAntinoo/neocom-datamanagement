package org.dimensinfin.eveonline.neocom;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

public class NeoComServicesDependenciesModule extends AbstractModule {

	@Override
	protected void configure() {
		//		bind( IConfigurationService.class )
		//				.annotatedWith( Names.named( "IConfigurationService" ) )
		//				.to( <tobedefinedbyenvironment>.class );
		//		bind( IFileSystem.class )
		//				.annotatedWith( Names.named( "IFileSystem" ) )
		//				.to( <tobedefinedbyenvironment>.class );
		bind( StoreCacheManager.class )
				.annotatedWith( Names.named( "StoreCacheManager" ) )
				.to( StoreCacheManager.class );
		bind( RetrofitService.class )
				.annotatedWith( Names.named( "RetrofitFactory" ) )
				.to( RetrofitService.class );
		bind( LocationCatalogService.class )
				.annotatedWith( Names.named( DMServicesDependenciesModule.LOCATION_CATALOG_SERVICE ) )
				.to( LocationCatalogService.class );
	}
}