package org.dimensinfin.eveonline.neocom.miningextraction.service;

import java.util.List;
import java.util.Objects;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import org.joda.time.LocalDate;

import org.dimensinfin.annotation.LogEnterExit;
import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.miningextraction.converter.GetCharactersCharacterIdMiningToMiningExtractionConverter;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class MiningExtractionDownloader {
	private Credential credential;
	private ESIDataProvider esiDataProvider;
	private LocationCatalogService locationCatalogService;
	private ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	private MiningExtractionDownloader() {}

	/**
	 * Mining actions are small records that register the ore collected by a Pilot or the Moon mining done by a Corporation. The time
	 * base is 10 minutes and I suppose that those records are aggregated during a day. The data is a list of entries, each one
	 * declaring the quantity of one ore mined on a date and related to a single star system.
	 *
	 * The processing algorithm should add the capturing hour of day to the search index and update with the current value or create a
	 * new record when the index matches. The <b>id</b> of the record is changed to an string having the next fields:
	 * YEAR/MONTH/DAY:HOUR-TYPEID-SYSTEMID-OWNERID.
	 * Once one record is found instead storing on the database a single record per day/item/system/owner we store this same data for
	 * each hour until the date changes and then we stop processing entries until we have new current mining extractions.
	 */
	@TimeElapsed
	@LogEnterExit
	public List<MiningExtraction> downloadMiningExtractions() {
		// Get to the Network and download the data from the ESI api.
		NeoComLogger.info( "Starting download of credential {} mining extractions...", this.credential.getAccountId() + "" );
		return Stream.of( Objects.requireNonNull( this.esiDataProvider.getCharactersCharacterIdMining( credential ) ) )
				.map( ( extractionOk ) -> {
					final MiningExtraction extraction = new GetCharactersCharacterIdMiningToMiningExtractionConverter(
							this.locationCatalogService,
							this.resourceFactory,
							this.credential.getAccountId(),
							LocalDate.now() )
							.convert( extractionOk );
					final String recordId = extraction.getId();
					NeoComLogger.info( "Generating record identifier: {}.", recordId );
					return extraction;
				} )
				.collect( Collectors.toList() );
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionDownloader onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MiningExtractionDownloader();
		}

		public MiningExtractionDownloader build() {
			return this.onConstruction;
		}

		public MiningExtractionDownloader.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public MiningExtractionDownloader.Builder withEsiDataProvider( final ESIDataProvider esiDataProvider ) {
			Objects.requireNonNull( esiDataProvider );
			this.onConstruction.esiDataProvider = esiDataProvider;
			return this;
		}

		public MiningExtractionDownloader.Builder withLocationCatalogService( final LocationCatalogService locationCatalogService ) {
			Objects.requireNonNull( locationCatalogService );
			this.onConstruction.locationCatalogService = locationCatalogService;
			return this;
		}

		public MiningExtractionDownloader.Builder withResourceFactory( final ResourceFactory resourceFactory ) {
			Objects.requireNonNull( resourceFactory );
			this.onConstruction.resourceFactory = resourceFactory;
			return this;
		}
	}
}