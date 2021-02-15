package org.dimensinfin.eveonline.neocom.service.scheduler;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.asset.service.AssetDownloadProcessorJob;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.AssetRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.miningextraction.MiningExtractionsProcessJob;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionDownloader;
import org.dimensinfin.eveonline.neocom.service.scheduler.conf.ISchedulerConfiguration;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;
import org.dimensinfin.logging.LogWrapper;

public class CredentialJobGeneratorJob extends Job {
	private static final String CRON_SCHEDULE_ASSETS = "-to-be-defined-";
	private static final String CRON_SCHEDULE_MINING_EXTRACTIONS = "-to-be-defined-";
	private IConfigurationService configurationService;
	private CredentialRepository credentialRepository;
	private ISchedulerConfiguration schedulerConfiguration;
	private AssetRepository assetRepository;
	private MiningRepository miningRepository;
	private ESIDataService esiDataProvider;
	private LocationCatalogService locationCatalogService;

	private CredentialJobGeneratorJob() {
		this.setSchedule( "0/5 - *" );
	}

	// - J O B
	@Override
	public int getUniqueIdentifier() {
		return new HashCodeBuilder( 19, 137 )
				.appendSuper( super.hashCode() )
				.append( this.getClass().getSimpleName() )
				.toHashCode();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		return super.equals( o );
	}

	/**
	 * Generate the list of jobs that should be run by every Credential, possible depending on type and state. Check also the property flags for
	 * features not allowed to be scheduled.
	 * The list of jobs generated is then registered on the scheduler for time schedule.
	 *
	 * @return true if the process completed successfully
	 * @throws RuntimeException if there any exception during processing this is thrown to report to scheduler.
	 */
	@Override
	public Boolean call() {
		LogWrapper.enter();
		// Read the list of Credentials and process them.
		for (Credential credential : this.credentialRepository.accessAllCredentials()) {
			if (Boolean.TRUE.equals( this.schedulerConfiguration.getAllowedToRun() )) {
				if (Boolean.TRUE.equals( this.schedulerConfiguration.getAllowedMiningExtractions() ))
					JobScheduler.getJobScheduler()
							.registerJob( new MiningExtractionsProcessJob.Builder()
									.withCredential( credential )
									.withEsiDataProvider( this.esiDataProvider )
									.withLocationCatalogService( this.locationCatalogService )
									.withMiningRepository( this.miningRepository )
									.withMiningExtractionsDownloader( new MiningExtractionDownloader.Builder()
											.withCredential( credential )
											.withEsiDataProvider( this.esiDataProvider )
											.withLocationCatalogService( this.locationCatalogService )
											.build() )
									.addCronSchedule( this.configurationService.getResourceString(
											CRON_SCHEDULE_MINING_EXTRACTIONS, "* - *" ) )
									.build() );
				if (Boolean.TRUE.equals( this.schedulerConfiguration.getAllowedAssets() ))
					JobScheduler.getJobScheduler()
							.registerJob(
									new AssetDownloadProcessorJob.Builder()
											.withCredential( credential )
											.withAssetRepository( this.assetRepository )
											.withEsiDataProvider( this.esiDataProvider )
											.withLocationCatalogService( this.locationCatalogService )
											.addCronSchedule( CRON_SCHEDULE_ASSETS )
											.build() );
			}
		}
		LogWrapper.exit();
		return true;
	}

	// - B U I L D E R
	public static class Builder extends Job.Builder<CredentialJobGeneratorJob, Builder> {
		private CredentialJobGeneratorJob onConstruction;

		public Builder() {
			this.onConstruction = new CredentialJobGeneratorJob();
		}

		@Override
		protected CredentialJobGeneratorJob getActual() {
			if (null == this.onConstruction) this.onConstruction = new CredentialJobGeneratorJob();
			return this.onConstruction;
		}

		@Override
		protected Builder getActualBuilder() {
			return this;
		}

		@Override
		public CredentialJobGeneratorJob build() {
			super.build();
			Objects.requireNonNull( this.onConstruction.configurationService );
			Objects.requireNonNull( this.onConstruction.assetRepository );
			Objects.requireNonNull( this.onConstruction.credentialRepository );
			Objects.requireNonNull( this.onConstruction.miningRepository );
			Objects.requireNonNull( this.onConstruction.esiDataProvider );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.schedulerConfiguration );
			return this.onConstruction;
		}

		public CredentialJobGeneratorJob.Builder withAssetRepository( final AssetRepository assetRepository ) {
			Objects.requireNonNull( assetRepository );
			this.getActual().assetRepository = assetRepository;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withConfigurationService( final IConfigurationService configurationService ) {
			Objects.requireNonNull( configurationService );
			this.onConstruction.configurationService = configurationService;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withCredentialRepository( final CredentialRepository credentialRepository ) {
			Objects.requireNonNull( credentialRepository );
			this.onConstruction.credentialRepository = credentialRepository;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withEsiDataProvider( final ESIDataService esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull( miningRepository );
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}

		public CredentialJobGeneratorJob.Builder withSchedulerConfiguration( final ISchedulerConfiguration schedulerConfiguration ) {
			Objects.requireNonNull( schedulerConfiguration );
			this.onConstruction.schedulerConfiguration = schedulerConfiguration;
			return this;
		}
	}
}
