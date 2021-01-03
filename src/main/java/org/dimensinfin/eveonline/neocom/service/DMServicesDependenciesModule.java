package org.dimensinfin.eveonline.neocom.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class DMServicesDependenciesModule extends AbstractModule {
	@Override
	protected void configure() {
		bind( RetrofitService.class )
				.annotatedWith( Names.named( "RetrofitService" ) )
				.to( RetrofitService.class )
				.in( Singleton.class );
		bind( ESIDataService.class )
				.annotatedWith( Names.named( "ESIDataService" ) )
				.to( ESIDataService.class )
				.in( Singleton.class );
		bind( ResourceFactory.class )
				.annotatedWith( Names.named( "ResourceFactory" ) )
				.to( ResourceFactory.class )
				.in( Singleton.class );
	}
}