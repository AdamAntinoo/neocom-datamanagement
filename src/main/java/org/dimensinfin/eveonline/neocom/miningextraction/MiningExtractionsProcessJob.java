package org.dimensinfin.eveonline.neocom.miningextraction;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionDownloader;
import org.dimensinfin.eveonline.neocom.miningextraction.service.MiningExtractionPersistent;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.service.scheduler.domain.Job;

public class MiningExtractionsProcessJob extends Job {
	private MiningRepository miningRepository;
	private Credential credential;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	private MiningExtractionDownloader miningExtractionDownloader;

	private MiningExtractionsProcessJob() {}

	// - J O B
	@Override
	public int getUniqueIdentifier() {
		return new HashCodeBuilder( 19, 137 )
				.appendSuper( super.hashCode() )
				.append( this.getClass().getSimpleName() )
				.append( this.credential.getAccountId() )
				.toHashCode();
	}

	/**
	 * Process the mining extractions data for a Credential.
	 *
	 * It will download and then persist the list of records from the ESI endpoint related to the mining ledger.
	 *
	 * @return true if the process completed successfully.
	 * @throws NeoComRuntimeException any exception intercepted during the process like io problems, network or database.
	 */
	@Override
	public Boolean call() throws NeoComRuntimeException {
		NeoComLogger.enter();
		try {
			new MiningExtractionPersistent.Builder()
					.withMiningRepository( this.miningRepository ).build()
					.persistMiningExtractions( this.miningExtractionDownloader.downloadMiningExtractions() );
		} catch (final RuntimeException rtex) {
			NeoComLogger.error( rtex );
			throw new NeoComRuntimeException( rtex );
		} finally {
			NeoComLogger.exit();
		}
		return true;
	}

	// - B U I L D E R
	public static class Builder extends Job.Builder<MiningExtractionsProcessJob, Builder> {
		private MiningExtractionsProcessJob onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionsProcessJob();
		}

		@Override
		protected MiningExtractionsProcessJob getActual() {
			if (null == this.onConstruction) this.onConstruction = new MiningExtractionsProcessJob();
			return this.onConstruction;
		}

		@Override
		protected Builder getActualBuilder() {
			return this;
		}

		public MiningExtractionsProcessJob build() {
			Objects.requireNonNull( this.onConstruction.credential );
			Objects.requireNonNull( this.onConstruction.esiDataProvider );
			Objects.requireNonNull( this.onConstruction.locationCatalogService );
			Objects.requireNonNull( this.onConstruction.miningRepository );
			Objects.requireNonNull( this.onConstruction.miningExtractionDownloader );
			return this.onConstruction;
		}

		public Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public Builder withMiningExtractionsDownloader( final MiningExtractionDownloader miningExtractionDownloader ) {
			Objects.requireNonNull( miningExtractionDownloader );
			this.onConstruction.miningExtractionDownloader = miningExtractionDownloader;
			return this;
		}

		public Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull( miningRepository );
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}
	}
}
