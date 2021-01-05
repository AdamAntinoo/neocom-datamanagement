package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.service.scheduler.conf.ISchedulerConfiguration;

public class CredentialJobGeneratorJobTest {
	private AssetRepository assetRepository;
	private CredentialRepository credentialRepository;
	private IConfigurationService configurationService;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	private MiningRepository miningRepository;
	private ISchedulerConfiguration schedulerConfiguration;

	@BeforeEach
	public void beforeEach() {
		this.assetRepository = Mockito.mock( AssetRepository.class );
		this.credentialRepository = Mockito.mock( CredentialRepository.class );
		this.miningRepository = Mockito.mock( MiningRepository.class );
		this.configurationService = Mockito.mock( IConfigurationService.class );
		this.esiDataProvider = Mockito.mock( ESIDataProvider.class );
		this.locationCatalogService = Mockito.mock( LocationCatalogService.class );
		this.schedulerConfiguration = Mockito.mock( ISchedulerConfiguration.class );
	}

	@Test
	public void buildComplete() {
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		Assertions.assertNotNull( credentialJobGeneratorJob );
	}

	@Test
	public void buildFailure() {
		Assertions.assertThrows( NullPointerException.class, () -> {
			final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
					.withConfigurationService( this.configurationService )
					.withAssetRepository( this.assetRepository )
					.withCredentialRepository( this.credentialRepository )
					.withMiningRepository( null )
					.withEsiDataProvider( this.esiDataProvider )
					.withLocationCatalogService( this.locationCatalogService )
					.withSchedulerConfiguration( this.schedulerConfiguration )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
					.withConfigurationService( this.configurationService )
					.withAssetRepository( this.assetRepository )
					.withCredentialRepository( this.credentialRepository )
					.withMiningRepository( this.miningRepository )
					.withEsiDataProvider( this.esiDataProvider )
					.withLocationCatalogService( this.locationCatalogService )
					.withSchedulerConfiguration( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
					.withConfigurationService( this.configurationService )
					.withCredentialRepository( this.credentialRepository )
					.withMiningRepository( this.miningRepository )
					.withEsiDataProvider( this.esiDataProvider )
					.withLocationCatalogService( this.locationCatalogService )
					.withSchedulerConfiguration( this.schedulerConfiguration )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
					.withConfigurationService( this.configurationService )
					.withAssetRepository( this.assetRepository )
					.withCredentialRepository( this.credentialRepository )
					.withMiningRepository( this.miningRepository )
					.withLocationCatalogService( this.locationCatalogService )
					.withSchedulerConfiguration( this.schedulerConfiguration )
					.build();
		} );
	}

	@Test
	public void callAllowedAll() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final List<Credential> credentialList = new ArrayList<>();
		credentialList.add( credential );
		JobScheduler.getJobScheduler().clear();
		// When
		Mockito.when( this.credentialRepository.accessAllCredentials() ).thenReturn( credentialList );
		Mockito.when( this.schedulerConfiguration.getAllowedToRun() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedMiningExtractions() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedAssets() ).thenReturn( true );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "* - *" );
		// Test
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		// Assertions
		Assertions.assertTrue( credentialJobGeneratorJob.call() );
		Assertions.assertEquals( 2, JobScheduler.getJobScheduler().getJobCount() );
	}
	@Test
	public void callAllowedMining() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final List<Credential> credentialList = new ArrayList<>();
		credentialList.add( credential );
		JobScheduler.getJobScheduler().clear();
		// When
		Mockito.when( this.credentialRepository.accessAllCredentials() ).thenReturn( credentialList );
		Mockito.when( this.schedulerConfiguration.getAllowedToRun() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedMiningExtractions() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedAssets() ).thenReturn( false );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "* - *" );
		// Test
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		// Assertions
		Assertions.assertTrue( credentialJobGeneratorJob.call() );
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().getJobCount() );
	}
	@Test
	public void callAllowedAsssets() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final List<Credential> credentialList = new ArrayList<>();
		credentialList.add( credential );
		JobScheduler.getJobScheduler().clear();
		// When
		Mockito.when( this.credentialRepository.accessAllCredentials() ).thenReturn( credentialList );
		Mockito.when( this.schedulerConfiguration.getAllowedToRun() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedMiningExtractions() ).thenReturn( false );
		Mockito.when( this.schedulerConfiguration.getAllowedAssets() ).thenReturn( true );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "* - *" );
		// Test
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		// Assertions
		Assertions.assertTrue( credentialJobGeneratorJob.call() );
		Assertions.assertEquals( 1, JobScheduler.getJobScheduler().getJobCount() );
	}
	@Test
	public void callNotAllowed() {
		// Given
		final Credential credential = Mockito.mock( Credential.class );
		final List<Credential> credentialList = new ArrayList<>();
		credentialList.add( credential );
		JobScheduler.getJobScheduler().clear();
		// When
		Mockito.when( this.credentialRepository.accessAllCredentials() ).thenReturn( credentialList );
		Mockito.when( this.schedulerConfiguration.getAllowedToRun() ).thenReturn( false );
		Mockito.when( this.schedulerConfiguration.getAllowedMiningExtractions() ).thenReturn( true );
		Mockito.when( this.schedulerConfiguration.getAllowedAssets() ).thenReturn( true );
		Mockito.when( this.configurationService.getResourceString( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( "* - *" );
		// Test
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		// Assertions
		Assertions.assertTrue( credentialJobGeneratorJob.call() );
		Assertions.assertEquals( 0, JobScheduler.getJobScheduler().getJobCount() );
	}

	@Test
	public void getUniqueIdentifier() {
		final CredentialJobGeneratorJob credentialJobGeneratorJob = new CredentialJobGeneratorJob.Builder()
				.withConfigurationService( this.configurationService )
				.withAssetRepository( this.assetRepository )
				.withCredentialRepository( this.credentialRepository )
				.withMiningRepository( this.miningRepository )
				.withEsiDataProvider( this.esiDataProvider )
				.withLocationCatalogService( this.locationCatalogService )
				.withSchedulerConfiguration( this.schedulerConfiguration )
				.build();
		Assertions.assertEquals( -1288800699, credentialJobGeneratorJob.getUniqueIdentifier() );
	}
}
