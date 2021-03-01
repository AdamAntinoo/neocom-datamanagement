package org.dimensinfin.eveonline.neocom.database.repository;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.IntegrationNeoComServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.database.repositories.SDERepository;
import org.dimensinfin.eveonline.neocom.industry.domain.Resource;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.support.SBSDEDatabaseService;

public class SDERepositoryIT {
	private static final Integer TEST_BOM_TYPE_ID = 32873;
	private static final Integer TEST_BLUEPRINT_TYPE_ID = 31741;
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
		Assertions.assertFalse( obtained.isEmpty() );
		Assertions.assertEquals( 9, obtained.size() );
		Assertions.assertEquals( "Nocxium", obtained.get( 2 ).getName() );
	}

	@Test
	public void accessBillOfMaterialsFailure() throws SQLException {
		// Prepare
		this.beforeEach();
		// Given
		this.sdeDatabaseService = Mockito.mock( SBSDEDatabaseService.class );
		// When
		Mockito.when( this.sdeDatabaseService.constructStatement( Mockito.anyString(), Mockito.any( String[].class ) ) )
				.thenThrow( new SQLException( "-TEST-EXCEPTION-" ) );
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final List<Resource> obtained = sdeRepository.accessBillOfMaterials( TEST_BOM_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	@Test
	public void accessModule4Blueprint() {
		// Prepare
		this.beforeEach();
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final int obtained = sdeRepository.accessModule4Blueprint( TEST_BLUEPRINT_TYPE_ID );
		// Assertions
		Assertions.assertEquals( 31740, obtained );
	}

	@Test
	public void accessModule4BlueprintFailure() throws SQLException {
		// Prepare
		this.beforeEach();
		// Given
		this.sdeDatabaseService = Mockito.mock( SBSDEDatabaseService.class );
		// When
		Mockito.when( this.sdeDatabaseService.constructStatement( Mockito.anyString(), Mockito.any( String[].class ) ) )
				.thenThrow( new SQLException( "-TEST-EXCEPTION-" ) );
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final int obtained = sdeRepository.accessModule4Blueprint( TEST_BLUEPRINT_TYPE_ID );
		// Assertions
		Assertions.assertEquals( -1, obtained );
	}

	@Test
	public void accessSkillRequiredFailure() throws SQLException {
		// Prepare
		this.beforeEach();
		// Given
		this.sdeDatabaseService = Mockito.mock( SBSDEDatabaseService.class );
		// When
		Mockito.when( this.sdeDatabaseService.constructStatement( Mockito.anyString(), Mockito.any( String[].class ) ) )
				.thenThrow( new SQLException( "-TEST-EXCEPTION-" ) );
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final List<Resource> obtained = sdeRepository.accessSkillRequired( TEST_BLUEPRINT_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	public void beforeEach() {
		final Injector injector = Guice.createInjector( new IntegrationNeoComServicesDependenciesModule() );
		this.sdeDatabaseService = injector.getInstance( SBSDEDatabaseService.class );
		this.resourceFactory = injector.getInstance( ResourceFactory.class );
	}
}