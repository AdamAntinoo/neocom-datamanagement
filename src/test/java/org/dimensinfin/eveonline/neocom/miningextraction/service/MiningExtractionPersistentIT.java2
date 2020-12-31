package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;
import org.dimensinfin.eveonline.neocom.support.SupportMiningRepository;

public class MiningExtractionPersistentIT extends IntegrationEnvironmentDefinitionTCLocal {
	private SupportMiningRepository miningRepository;

	@BeforeEach
	public void beforeEach() throws SQLException {
		this.miningRepository = new SupportMiningRepository.Builder()
				.withLocationCatalogService( this.itLocationCatalogService )
				.withMiningExtractionDao( this.itNeoComIntegrationDBAdapter.getMiningExtractionDao() )
				.withConnection4Transaction( this.itNeoComIntegrationDBAdapter.getConnectionSource() )
				.build();
		this.miningRepository.deleteAll();
	}

	@Test
	public void buildComplete() {
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		Assertions.assertNotNull( miningExtractionPersistent );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
					.withMiningRepository( null ).build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
					.build();
		} );
	}

	@Test
	public void persistMiningExtractionsPreviousRecords() throws SQLException {
		// Given
		final List<MiningExtraction> extractionsSerieA = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build()
				.downloadMiningExtractions();
		Assertions.assertEquals( 13, extractionsSerieA.size() );
		final Credential credential = Mockito.mock( Credential.class );
		Mockito.when( credential.getAccountId() ).thenReturn( 93813310 );
		Mockito.when( credential.getDataSource() ).thenReturn( "tranquility" );
		Mockito.when( credential.setMiningResourcesEstimatedValue( Mockito.anyDouble() ) ).thenReturn( credential4Test );
		final List<MiningExtraction> extractionsSerieB = new MiningExtractionDownloader.Builder()
				.withCredential( credential )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build()
				.downloadMiningExtractions();
		Assertions.assertEquals( 5, extractionsSerieB.size() );
		// Replace the series B hour to be different from the series A
		final List<MiningExtraction> extractionsSerieC = new ArrayList<>();
		for (MiningExtraction extraction : extractionsSerieB) {
			extractionsSerieC.add( new MiningExtraction.Builder()
					.withOwnerId( credential4Test.getAccountId() )
					.withNeoItem( NeoItemFactory.getSingleton().getItemById( extraction.getTypeId() ) )
					.withExtractionDate( extraction.getExtractionDateName() )
					.withExtractionHour( extraction.getExtractionHour() - 1 )
					.withSpaceSystem( extraction.getSolarSystemLocation() )
					.withQuantity( extraction.getQuantity() )
					.build() );
		}
		// Test
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		miningExtractionPersistent.persistMiningExtractions( extractionsSerieC );
		// Assertions
		List<MiningExtraction> obtained = this.miningRepository.accessMiningExtractions4Pilot( credential4Test );
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 5, obtained.size() );
		miningExtractionPersistent.persistMiningExtractions( extractionsSerieA );
		obtained = this.miningRepository.accessMiningExtractions4Pilot( credential4Test );
		Assertions.assertEquals( 5 + 13, obtained.size() );
		// Get only today's records from database.
		obtained = this.miningRepository.accessTodayMiningExtractions4Pilot( credential4Test );
		Assertions.assertEquals( 5 + 5, obtained.size() );
	}

	@Test
	public void persistMiningExtractionsSuccess() throws SQLException {
		// Given
		final List<MiningExtraction> extractions = new MiningExtractionDownloader.Builder()
				.withCredential( credential4Test )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.itLocationCatalogService )
				.build()
				.downloadMiningExtractions();
		Assertions.assertEquals( 13, extractions.size() );
		// Test
		final MiningExtractionPersistent miningExtractionPersistent = new MiningExtractionPersistent.Builder()
				.withMiningRepository( this.miningRepository ).build();
		miningExtractionPersistent.persistMiningExtractions( extractions );
		// Assertions
		final List<MiningExtraction> obtained = this.miningRepository.accessMiningExtractions4Pilot( credential4Test );
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 13, obtained.size() );
	}
}
