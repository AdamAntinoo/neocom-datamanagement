package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.database.core.ISDEStatement;
import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SDERepositoryConstants.TEST_SDE_REPOSITORY_TYPE_ID;

public class SDERepositoryTest {

	private ISDEDatabaseService sdeDatabaseService;
	private ResourceFactory resourceFactory;

	@Test
	public void accessBillOfMaterials() throws SQLException {
		// Given
		final ISDEStatement statement = Mockito.mock( ISDEStatement.class );
		final Resource resource = Mockito.mock( Resource.class );
		// When
		Mockito.when( this.sdeDatabaseService.constructStatement( Mockito.anyString(), Mockito.any( String[].class ) ) )
				.thenReturn( statement );
		Mockito.when( statement.getInt( Mockito.anyInt() ) ).thenReturn( TEST_SDE_REPOSITORY_TYPE_ID );
		Mockito.when( statement.moveToNext() )
				.thenReturn( true )
				.thenReturn( false );
		Mockito.when( this.resourceFactory.generateResource4Id( Mockito.anyInt(), Mockito.anyInt() ) ).thenReturn( resource );
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final List<Resource> obtained = sdeRepository.accessBillOfMaterials( TEST_SDE_REPOSITORY_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertEquals( 2, obtained.size() );
	}

	@Test
	public void accessSkillRequired() throws SQLException {
		// Given
		final ISDEStatement statement = Mockito.mock( ISDEStatement.class );
		final Resource resource = Mockito.mock( Resource.class );
		// When
		Mockito.when( this.sdeDatabaseService.constructStatement( Mockito.anyString(), Mockito.any( String[].class ) ) )
				.thenReturn( statement );
		Mockito.when( statement.getInt( Mockito.anyInt() ) ).thenReturn( TEST_SDE_REPOSITORY_TYPE_ID );
		Mockito.when( statement.moveToNext() )
				.thenReturn( true )
				.thenReturn( false );
		Mockito.when( this.resourceFactory.generateResource4Id( Mockito.anyInt(), Mockito.anyInt() ) ).thenReturn( resource );
		// Test
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		final List<Resource> obtained = sdeRepository.accessSkillRequired( TEST_SDE_REPOSITORY_TYPE_ID );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertTrue( obtained.size() > 0 );
		Assertions.assertEquals( 1, obtained.size() );
	}

	@BeforeEach
	public void beforeEach() {
		this.sdeDatabaseService = Mockito.mock( ISDEDatabaseService.class );
		this.resourceFactory = Mockito.mock( ResourceFactory.class );
	}

	@Test
	public void constructorContract() {
		final SDERepository sdeRepository = new SDERepository( this.sdeDatabaseService, this.resourceFactory );
		Assertions.assertNotNull( sdeRepository );
	}
}