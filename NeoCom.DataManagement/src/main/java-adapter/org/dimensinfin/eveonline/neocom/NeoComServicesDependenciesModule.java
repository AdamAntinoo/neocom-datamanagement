package org.dimensinfin.eveonline.neocom;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;

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
		bind( RetrofitFactory.class )
				.annotatedWith( Names.named( "RetrofitFactory" ) )
				.to( RetrofitFactory.class );
	}
}