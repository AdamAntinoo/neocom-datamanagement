package org.dimensinfin.eveonline.neocom.loyalty.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.database.DMDatabaseDependenciesModule;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetLoyaltyStoresCorporationIdOffers200Ok;
import org.dimensinfin.eveonline.neocom.loyalty.converter.GetLoyaltyStoresCorporationIdOfferToLoyaltyOfferEntityConverter;
import org.dimensinfin.eveonline.neocom.loyalty.converter.GetMarketsRegionIdHistoryToMarketHistoryRecordConverter;
import org.dimensinfin.eveonline.neocom.loyalty.domain.MarketHistoryRecord;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.logging.LogWrapper;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class LoyaltyService {
	private final ESIDataService esiDataService;
	private final LoyaltyOffersRepository loyaltyOffersRepository;
	private final ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	@Inject
	public LoyaltyService( @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService,
	                       @NotNull @Named(DMDatabaseDependenciesModule.LOYALTYOFFERS_REPOSITORY) final LoyaltyOffersRepository loyaltyOffersRepository,
	                       @NotNull @Named(DMServicesDependenciesModule.RESOURCE_FACTORY) final ResourceFactory resourceFactory ) {
		this.esiDataService = esiDataService;
		this.loyaltyOffersRepository = loyaltyOffersRepository;
		this.resourceFactory = resourceFactory;
	}

	/**
	 * Scans a new fresh set of offers for the requested <code>corporationId</code>.
	 * Updates the loyalty repository with the new findings for profitable offers found for this corporation.
	 *
	 * @param corporationId the unique identifier for the loyalty store corporation that should be used as the offer source.
	 */
	public void processOffers( @NotNull final Integer corporationId, @NotNull final Integer regionId ) {
		final String corporationName = Objects.requireNonNull( this.esiDataService.getCorporationsCorporationId( corporationId ) ).getName();
		// Get the complete list of offers.
		final GetLoyaltyStoresCorporationIdOfferToLoyaltyOfferEntityConverter offerConverter =
				new GetLoyaltyStoresCorporationIdOfferToLoyaltyOfferEntityConverter( corporationId, corporationName, resourceFactory );
		this.esiDataService.getLoyaltyStoresOffers( corporationId )
				.stream()
				.filter( offer -> !offer.getRequiredItems().isEmpty() ) // Remove offers that have dependencies.
				.filter( offer -> this.offerIsGreen( offer, regionId ) ) // Check for a Green on the sell state.
				.map( offerConverter::convert ) // Convert to an entity suitable to be persisted.
				.forEach( loyaltyOffer -> {
					try {
						LogWrapper.info( MessageFormat.format(
								"persisting Loyalty offer: Corporation {0} - {1}->LP Value: {3}",
								corporationName,
								loyaltyOffer.getTypeName(),
								loyaltyOffer.getLpValue()
						) );
						this.loyaltyOffersRepository.persist( loyaltyOffer );
					} catch (final SQLException sqle) {
						LogWrapper.error( sqle );
					}
				} );
	}

	/**
	 * Checks the market history orders for the selected item. If the number of dates in the last 15 days is greater that 11 then the state mnay be
	 * green. Check then the number of items traded on a date, if greater that 5 then count that date.
	 *
	 * If the final count is > 11 then the state is green.
	 */
	private boolean offerIsGreen( final GetLoyaltyStoresCorporationIdOffers200Ok offer, final int regionId ) {
		final GetMarketsRegionIdHistoryToMarketHistoryRecordConverter converter = new GetMarketsRegionIdHistoryToMarketHistoryRecordConverter();
		final List<MarketHistoryRecord> marketHistory = Objects.requireNonNull( this.esiDataService.getMarketsHistoryForRegion(
				regionId, offer.getTypeId()
		) ).stream()
				.map( converter::convert )
				.filter( marketRecord -> marketRecord.isOnDateRange( LocalDate.now() ) ) // Check that the date is on the 15 last days.
				.filter( marketRecord -> marketRecord.getVolume() > 5 )
				.collect( Collectors.toList() );
		return marketHistory.size() > 11;
	}
}