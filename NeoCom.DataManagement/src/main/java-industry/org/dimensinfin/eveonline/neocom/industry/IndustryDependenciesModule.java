package org.dimensinfin.eveonline.neocom.industry;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.dimensinfin.eveonline.neocom.industry.services.IndustrySession;

public class IndustryDependenciesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind( IndustrySession.class )
				.annotatedWith( Names.named( "IndustrySession" ) )
				.to( IndustrySession.class );
		bind( IndustryProcessorService.class )
				.annotatedWith( Names.named( "IndustryProcessorService" ) )
				.to( IndustryProcessorService.class );
	}
}