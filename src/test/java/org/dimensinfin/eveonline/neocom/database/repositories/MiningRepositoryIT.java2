package org.dimensinfin.eveonline.neocom.database.repositories;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionDownloader;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionPersistent;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

/**
 * Tests for repository classes will be executed under a integration environment. There should be a real database instance
 * implemented by the TestContainers platform or by an external docker instance prepared for integration.
 *
 * Test should manage the database to clear and insert test data to check all the exported api methods.
 *
 * Repository instances only depend on Dao components. So they should be provided from the integration environment.
 */
public class MiningRepositoryIT extends IntegrationEnvironmentDefinitionTCLocal {
	private static final String TEST_EXTRACTION_IDENTIFIER = "2018-06-01:24-30001669-17459-92223647";

	private static final Integer TEST_CHARACTER_ID = 2113197470;
	private MiningRepository miningRepository;
	private Dao<MiningExtractionEntity, String> miningExtractionDao;

	@Test
	public void accessMiningExtractionFindByIdFailure() throws SQLException {
		// Given
		final Dao localMiningExtractionDao = Mockito.mock( Dao.class );
		this.miningRepository = new MiningRepository.Builder()
				.withMiningExtractionDao( localMiningExtractionDao )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build();
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		// When
		Mockito.doThrow( new SQLException( "-TEST-EXCEPTION-MESSAGE-" ) )
				.when( localMiningExtractionDao ).queryForId( Mockito.anyString() );
		// Exceptions
		Assertions.assertThrows( NeoComRuntimeException.class, () -> {
			this.miningRepository.accessMiningExtractionFindById( "-UNEXISTENT-RECORD-" );
		} );
	}

	@Test
	public void accessMiningExtractionFindByIdNotFound() throws SQLException {
		// Given
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		// Assertions
		Assertions.assertNull( this.miningRepository.accessMiningExtractionFindById( "-UNEXISTENT-RECORD-" ) );
	}

	@Test
	public void accessMiningExtractionFindByIdSuccess() throws SQLException {
		// Given
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		miningExtractionPersistent.persistMiningExtractions( new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build()
				.downloadMiningExtractions() );
		// Test
		final MiningExtraction extraction = this.miningRepository.accessMiningExtractionFindById( TEST_EXTRACTION_IDENTIFIER );
		// Assertions
		Assertions.assertNotNull( extraction );
		Assertions.assertEquals( TEST_EXTRACTION_IDENTIFIER, extraction.getId() );
		Assertions.assertEquals( credential4Test.getAccountId().intValue(), extraction.getOwnerId() );
	}

	//	@Test
	public void accessMiningExtractions4Pilot() throws SQLException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
//		final List<MiningExtraction> miningRecords = this.miningRepository
//				.accessMiningExtractions4Pilot( credential );
//
//		Assertions.assertEquals( 2, miningRecords.size() );
	}

	//	@Test
	public void accessResources4Date() throws SQLException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
//		final List<MiningExtraction> miningRecords = this.miningRepository
//				.accessResources4Date( credential, LocalDate.now() );

//		Assertions.assertEquals( 2, miningRecords.size() );
	}

	//	@Test
	public void accessTodayMiningExtractions4Pilot() throws SQLException, IOException {
		this.insertTodayMiningExtractions();

		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 2113197470 );
		final List<MiningExtraction> miningRecords = this.miningRepository
				.accessTodayMiningExtractions4Pilot( credential );

		Assertions.assertEquals( 2, miningRecords.size() );
	}

	@BeforeEach
	public void beforeEach() throws SQLException {
		this.miningExtractionDao = this.itNeoComIntegrationDBAdapter.getMiningExtractionDao();
		this.miningRepository = new MiningRepository.Builder()
				.withMiningExtractionDao( this.miningExtractionDao )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build();
	}

	@Test
	public void buildComplete() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( miningDao )
				.withLocationCatalogService( locationService )
				.build();
		Assertions.assertNotNull( repository );
	}

	@Test
	public void buildFailure() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningRepository repository = new MiningRepository.Builder()
					.withMiningExtractionDao( null )
					.withLocationCatalogService( locationService )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningRepository repository = new MiningRepository.Builder()
					.withMiningExtractionDao( miningDao )
					.withLocationCatalogService( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningRepository repository = new MiningRepository.Builder()
					.withLocationCatalogService( locationService )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningRepository repository = new MiningRepository.Builder()
					.withMiningExtractionDao( miningDao )
					.build();
		} );
	}

	//	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final Dao miningDao = Mockito.mock( Dao.class );
		final LocationCatalogService locationService = Mockito.mock( LocationCatalogService.class );
		final MiningRepository repository = new MiningRepository.Builder()
				.withMiningExtractionDao( miningDao )
				.build();
	}

//	@Test(expected = SQLException.class)
//	public void accessMiningExtractionFindByIdException() throws SQLException {
//		final Dao dao = Mockito.mock( Dao.class );
//		Mockito.when( dao.queryForId( ArgumentMatchers.any( String.class ) ) ).thenThrow( SQLException.class );
//		final MiningRepository repository = new MiningRepository.Builder()
//				.withMiningExtractionDao( dao )
//				.withLocationCatalogService( this.locationCatalogService )
//				.build();
//		final MiningExtraction miningRecord = repository.accessMiningExtractionFindById( "TEST-LOCATOR" );
//	}

	private void insertTodayMiningExtractions() throws SQLException {
//		final GetUniverseSystemsSystemIdOk solarSystemData = this.esiUniverseDataProvider
//				.getUniverseSystemById( 30002764 );
//		final GetUniverseConstellationsConstellationIdOk constellationData = this.esiUniverseDataProvider
//				.getUniverseConstellationById( 20000405 );
//		final GetUniverseRegionsRegionIdOk regionData = this.esiUniverseDataProvider
//				.getUniverseRegionById( 10000033 );
//		final MiningExtraction miningExtractionA = new MiningExtraction.Builder()
////				.withTypeId( 35 )
//				.withSpaceSystem( new SpaceSystemImplementation.Builder()
//						.withSolarSystem( solarSystemData )
//						.withConstellation( constellationData )
//						.withRegion( regionData )
//						.build() )
//				.withQuantity( 43215L )
//				.withOwnerId( TEST_CHARACTER_ID )
//				.withExtractionDate( "gg" )
//				.build();
//		final MiningExtraction miningExtractionB = new MiningExtraction.Builder()
////				.withTypeId( 34 )
//				.withSpaceSystem( new SpaceSystemImplementation.Builder()
//						.withSolarSystem( solarSystemData )
//						.withConstellation( constellationData )
//						.withRegion( regionData )
//						.build() )
//				.withQuantity( 12345L )
//				.withOwnerId( TEST_CHARACTER_ID )
//				.withExtractionDate( "ff" )
//				.build();
////		this.miningRepository.persist( miningExtractionA );
//		NeoComLogger.info( "Extraction id: {}", miningExtractionA.getId() );
//		this.expectedVerifiedId = miningExtractionA.getId();
////		this.miningRepository.persist( miningExtractionB );
//		NeoComLogger.info( "Extraction id: {}", miningExtractionB.getId() );
//		final List<MiningExtraction> result = this.miningExtractionDao.queryForAll();
//		Assert.assertEquals( 2, result.size() );
	}

	private void onCreate() throws SQLException {
		TableUtils.dropTable( connectionSource, MiningExtraction.class, true );
		TableUtils.createTableIfNotExists( connectionSource, MiningExtraction.class );
	}
//
//
//	@Test
//	public void persist() throws SQLException {
//		final MiningRepository repository = new MiningRepository.Builder()
//				.withMiningExtractionDao( dao )
//				.build();
//		Mockito.doAnswer( ( call ) -> {
//			final MiningExtraction parameter = call.getArgument( 0 );
//			Assert.assertNotNull( parameter );
//			return null;
//		} ).when( dao ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
////		repository.persist( miningExtraction );
//		Mockito.verify( dao, Mockito.times( 1 ) ).createOrUpdate( ArgumentMatchers.any( MiningExtraction.class ) );
//	}
}
