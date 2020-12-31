package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.DatabaseVersion;

public class DatabaseVersionRepositoryTest {
	@Test
	public void buildComplete() {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( dao )
				.build();
		Assert.assertNotNull( repository );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.build();
		Assert.assertNotNull( repository );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( null )
				.build();
		Assert.assertNotNull( repository );
	}

	@Test
	public void accessVersionSuccess() throws SQLException {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final List<DatabaseVersion> versionList = new ArrayList<>();
		versionList.add( new DatabaseVersion( 123 ) );
		Mockito.when( dao.queryForAll() ).thenReturn( versionList );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( dao )
				.build();

		final DatabaseVersion version = repository.accessVersion();
		Assert.assertNotNull( version );
		Assert.assertEquals( 123, version.getVersionNumber() );
	}

	@Test
	public void accessVersionNotFound() throws SQLException {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final List<DatabaseVersion> versionList = new ArrayList<>();
		Mockito.when( dao.queryForAll() ).thenReturn( versionList );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( dao )
				.build();

		final DatabaseVersion version = repository.accessVersion();
		Assert.assertNotNull( version );
		Assert.assertEquals( 0, version.getVersionNumber() );
	}

	@Test
	public void accessVersionException() throws SQLException {
		final Dao dao = Mockito.mock( Dao.class );
		final List<DatabaseVersion> versionList = new ArrayList<>();
		Mockito.when( dao.queryForAll() ).thenThrow( new SQLException() );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( dao )
				.build();

		final DatabaseVersion version = repository.accessVersion();
		Assert.assertNotNull( version );
		Assert.assertEquals( 0, version.getVersionNumber() );
	}

	@Test
	public void persist() throws SQLException {
		final Dao<DatabaseVersion,Integer> dao = Mockito.mock( Dao.class );
		final DatabaseVersion version = Mockito.mock( DatabaseVersion.class );
		Mockito.when( dao.createOrUpdate( Mockito.any( DatabaseVersion.class ) ) )
				.thenReturn( Mockito.any( Dao.CreateOrUpdateStatus.class ) );
		final DatabaseVersionRepository repository = new DatabaseVersionRepository.Builder()
				.withDatabaseVersionDao( dao )
				.build();

		repository.persist( version );
	}
}