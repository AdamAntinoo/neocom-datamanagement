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
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdHistory200Ok;
import org.dimensinfin.eveonline.neocom.loyalty.domain.MarketHistoryRecord;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOfferEntity;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOffersRepository;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.logging.LogWrapper;

/**
 * Processes a set of loyalty shop offers corresponding to a NPC corporation.
 * The analysis will check the market history record to check for trade operations on a date range, the volume of traded operations and the
 * dependency on other market items (this should be improved on next releases).
 * The resulting data is recorded into a repository for later use.
 *
 * There are some attributes that can be configured like:
 * <ul>
 * <li>the number of days on the range period.</li>
 * <li>the volume of orders to make a date valid.</li>
 * <li>the number of dates inside a date range (expressed ad a percentage) to consider the item a secure sell.</li>
 * <li>the region to be used for the market operations.</li>
 * </ul>
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class LoyaltyService {
	private final ESIDataService esiDataService;
	private final LoyaltyOffersRepository loyaltyOffersRepository;
	private final ResourceFactory resourceFactory;
	private final MarketService marketService;

	// - C O N F I G U R A T I O N
	private int regionId = 10000002; // Defaults to The Forge (Jita).
	private int daysInRange = 15; // Number of days on the date range period back from the current date.
	private int dateCoveragePct = 70; // Percentage of the range days that should have market trades to validate an item.
	private int minTradeVolume = 8; // Min value on the volume of items traded to validate an order.
	private final int profitLevel = 1200;

	// - C O N S T R U C T O R S
	@Inject
	public LoyaltyService( @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService,
	                       @NotNull @Named(DMDatabaseDependenciesModule.LOYALTYOFFERS_REPOSITORY) final LoyaltyOffersRepository loyaltyOffersRepository,
	                       @NotNull @Named(DMServicesDependenciesModule.RESOURCE_FACTORY) final ResourceFactory resourceFactory,
	                       @NotNull @Named(DMServicesDependenciesModule.MARKET_SERVICE) final MarketService marketService ) {
		this.esiDataService = esiDataService;
		this.loyaltyOffersRepository = loyaltyOffersRepository;
		this.resourceFactory = resourceFactory;
		this.marketService = marketService;
	}

	// - G E T T E R S   &   S E T T E R S
	public int getProfitLevel() {
		return this.profitLevel;
	}

	public int getRegionId() {
		return this.regionId;
	}

	public LoyaltyService setRegionId( final int regionId ) {
		this.regionId = regionId;
		return this;
	}

	/**
	 * Scans a new fresh set of offers for the requested <code>corporationId</code>.
	 * Updates the loyalty repository with the new findings for profitable offers found for this corporation.
	 *
	 * @param corporationId the unique identifier for the loyalty store corporation that should be used as the offer source.
	 */
	public List<LoyaltyOfferEntity> processOffers( @NotNull final Integer corporationId ) {
		final String loyaltyCorporationName = Objects.requireNonNull( this.esiDataService.getCorporationsCorporationId( corporationId ) ).getName();
		return this.esiDataService.getLoyaltyStoresOffers( corporationId )
				.stream()
				.filter( offer -> offer.getRequiredItems().isEmpty() ) // Remove offers that have dependencies.
				.filter( this::offerIsGreen ) // Check for a Green on the sell state.
				.map( offer -> {
					final List<GetMarketsRegionIdHistory200Ok> marketHistoryEsi = Objects
							.requireNonNull( this.esiDataService.getMarketsHistoryForRegion(
									this.regionId, offer.getTypeId()
							) );
					if (!marketHistoryEsi.isEmpty()) LogWrapper.info( "Last market history record: " +
							marketHistoryEsi.get( marketHistoryEsi.size() - 1 ).toString()
					);
					return new LoyaltyOfferEntity.Builder()
							.withOfferId( offer.getOfferId() )
							.withLoyaltyCorporation( corporationId, loyaltyCorporationName )
							.withType( this.resourceFactory.generateType4Id( offer.getTypeId() ) )
							.withIskCost( offer.getIskCost() )
							.withLpCost( offer.getLpCost() )
							.withQuantity( offer.getQuantity() )
							.withMarketRegionId( this.regionId )
							// Use the current lowest market sell price instead the latest trade price
							.withPrice( this.marketService.getLowestSellPrice(
									this.regionId, offer.getTypeId() )
							)
							.build();
				} ) // Convert to an entity suitable to be persisted.
				.map( loyaltyOffer -> {
					LogWrapper.info( "Processing offer for type: " + loyaltyOffer.getTypeName() );
					try {
						LogWrapper.info( MessageFormat.format(
								"persisting Loyalty offer: Corporation {0} - {1}->LP Value: {2}",
								loyaltyCorporationName,
								loyaltyOffer.getTypeName(),
								loyaltyOffer.getLpValue()
						) );
						this.loyaltyOffersRepository.persist( loyaltyOffer );
					} catch (final SQLException sqle) {
						LogWrapper.error( sqle );
					}
					return loyaltyOffer;
				} )
				.collect( Collectors.toList() );
	}

	public LoyaltyService setDateCoveragePct( final int dateCoveragePct ) {
		this.dateCoveragePct = dateCoveragePct;
		return this;
	}

	public LoyaltyService setDaysInRange( final int daysInRange ) {
		this.daysInRange = daysInRange;
		return this;
	}

	public LoyaltyService setMinTradeVolume( final int minTradeVolume ) {
		this.minTradeVolume = minTradeVolume;
		return this;
	}

	/**
	 * Checks the market history orders for the selected item. If the number of dates in the last 15 days is greater that 11 then the state mnay be
	 * green. Check then the number of items traded on a date, if greater that 5 then count that date.
	 *
	 * If the final count is > 11 then the state is green.
	 */
	private boolean offerIsGreen( final GetLoyaltyStoresCorporationIdOffers200Ok offer ) {
		final List<GetMarketsRegionIdHistory200Ok> marketHistoryEsi = Objects.requireNonNull( this.esiDataService.getMarketsHistoryForRegion(
				this.regionId, offer.getTypeId()
		) );
		// Check that the date is on the 15 last days.
		final List<MarketHistoryRecord> marketHistory = marketHistoryEsi.stream()
				.map( historyRecord -> new MarketHistoryRecord.Builder()
						.withRecordDate( historyRecord.getDate() )
						.withDateRange( this.daysInRange )
						.withVolume( historyRecord.getVolume().intValue() )
						.build() )
				.filter( marketRecord -> marketRecord.isOnDateRange( LocalDate.now() ) ) // Check that the date is on the 15 last days.
				.filter( marketRecord -> marketRecord.getVolume() > this.minTradeVolume )
				.collect( Collectors.toList() );
		LogWrapper.info( MessageFormat.format( "Market history entries: {0} - filtered: {1}",
				marketHistoryEsi.size(),
				marketHistory.size() )
		);
		LogWrapper.info( "Green state: " + (marketHistory.size() > (this.dateCoveragePct * this.daysInRange) / 100) );
		return marketHistory.size() > (this.dateCoveragePct * this.daysInRange) / 100;
	}
}