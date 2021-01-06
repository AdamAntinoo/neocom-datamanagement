package org.dimensinfin.eveonline.neocom.database.repository;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.database.repositories.SDERepository;
import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.support.SBSDEDatabaseService;

public class SDERepositoryIT {
	private static final Integer TEST_BOM_TYPE_ID = 32873;
	private ISDEDatabaseService sdeDatabaseService;
	private ResourceFactory resourceFactory;

	@Test
	public void accessBillOfMaterials() {
		// Prepare
		this.beforeEach();
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final List<Resource> obtained = sdeRepository.accessBillOfMaterials( TEST_BOM_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertEquals( 9, obtained.size() );
		Assertions.assertEquals( "Nocxium", obtained.get( 2 ).getName() );
	}

	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.sdeDatabaseService = injector.getInstance( SBSDEDatabaseService.class );
		this.resourceFactory = injector.getInstance( ResourceFactory.class );
	}
}