package org.dimensinfin.eveonline.neocom.industry.persistence;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.NeoComDatabaseService;

public class JobRepositoryTest {
	@Test
	public void constructorContract() throws SQLException {
		// Given
		final NeoComDatabaseService neoComDatabaseService = Mockito.mock( NeoComDatabaseService.class );
		// Test
		final JobRepository jobRepository = new JobRepository( neoComDatabaseService );
		// Assertions
		Assertions.assertNotNull( jobRepository );
	}

	@Test
	public void persist() throws SQLException {
		// Given
		final NeoComDatabaseService neoComDatabaseService = Mockito.mock( NeoComDatabaseService.class );
		final Dao<JobEntity, String> industryJobDao = Mockito.mock( Dao.class );
		final Dao.CreateOrUpdateStatus status = Mockito.mock( Dao.CreateOrUpdateStatus.class );
		final JobEntity jobEntity = Mockito.mock( JobEntity.class );
		// When
		Mockito.when( neoComDatabaseService.getIndustryJobDao() ).thenReturn( industryJobDao );
		Mockito.when( industryJobDao.createOrUpdate( Mockito.any( JobEntity.class ) ) ).thenReturn( status );
		// Test
		final JobRepository jobRepository = new JobRepository( neoComDatabaseService );
		jobRepository.persist( jobEntity );
		// Assertions
		Mockito.verify( industryJobDao, Mockito.times( 1 ) ).createOrUpdate( Mockito.any( JobEntity.class ) );
	}
}