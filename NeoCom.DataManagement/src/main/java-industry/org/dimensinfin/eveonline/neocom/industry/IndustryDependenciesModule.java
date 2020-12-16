package org.dimensinfin.eveonline.neocom.industry;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.industry.persistence.JobEntity;
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
		bind( new TypeLiteral<Dao<JobEntity, String>>() {
		} )
				.annotatedWith( Names.named( "DaoJobEntity" ) )
				.to( DaoJobEntity.class );
	}
}