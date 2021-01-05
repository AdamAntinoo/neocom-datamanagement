package org.dimensinfin.eveonline.neocom.miningextraction;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionDownloader;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;

public class MiningExtractionsProcessJobTest {
	private Credential credential;
	private ESIDataProvider esiDataProvider;
	private MiningRepository miningRepository;
	private LocationCatalogService locationCatalogService;
	private MiningExtractionDownloader miningExtractionDownloader;

	@BeforeEach
	public void beforeEach() {
		this.credential = Mockito.mock( Credential.class );
		this.esiDataProvider = Mockito.mock( ESIDataProvider.class );
		this.miningRepository = Mockito.mock( MiningRepository.class );
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.miningExtractionDownloader = Mockito.mock( MiningExtractionDownloader.class );
	}

	@Test
	public void buildComplete() {
		final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
				.withCredential( this.credential )
				.withEsiDataProvider( this.esiDataProvider )
				.withMiningRepository( this.miningRepository )
				.withLocationCatalogService( this.locationCatalogService )
				.withMiningExtractionsDownloader( this.miningExtractionDownloader )
				.build();
		Assertions.assertNotNull( miningExtractionsProcessJob );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( null )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( null )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( null )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( null )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( null )
					.build();
		} );
	}

	@Test
	public void buildMandatory() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withLocationCatalogService( this.locationCatalogService )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withMiningExtractionsDownloader( this.miningExtractionDownloader )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
					.withCredential( this.credential )
					.withEsiDataProvider( this.esiDataProvider )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.build();
		} );
	}

	@Test
	public void call() {
		// Given
		final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
				.withCredential( this.credential )
				.withEsiDataProvider( this.esiDataProvider )
				.withMiningRepository( this.miningRepository )
				.withLocationCatalogService( this.locationCatalogService )
				.withMiningExtractionsDownloader( this.miningExtractionDownloader )
				.build();
		// When
		Mockito.when( this.miningExtractionDownloader.downloadMiningExtractions() ).thenReturn( new ArrayList<>() );
		Assertions.assertTrue( miningExtractionsProcessJob.call() );
	}

	@Test
	public void getUniqueIdentifier() {
		Mockito.when( this.credential.getAccountId() ).thenReturn( 1234567 );
		final MiningExtractionsProcessJob miningExtractionsProcessJob = new MiningExtractionsProcessJob.Builder()
				.withCredential( this.credential )
				.withEsiDataProvider( this.esiDataProvider )
				.withMiningRepository( this.miningRepository )
				.withLocationCatalogService( this.locationCatalogService )
				.withMiningExtractionsDownloader( this.miningExtractionDownloader )
				.build();
		final long obtained = miningExtractionsProcessJob.getUniqueIdentifier();
		Assertions.assertEquals( -1153377134, obtained );
	}
}
