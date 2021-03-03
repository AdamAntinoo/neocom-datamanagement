package org.dimensinfin.eveonline.neocom.market.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.annotation.Cacheable;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.converter.GetMarketsRegionIdOrdersToMarketOrderConverter;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;
import org.dimensinfin.eveonline.neocom.service.IDataStore;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

/**
 * This service will have all the core and required common functionalities related to Market and market prices. Will help to locate the price for
 * an item of a region or to generate the Consolidated Market Data for another item related to a Market Hub.
 * Functionality added should be generic and really specific requirements should be implemented on the frontend that use the library and based on
 * this generic methods.
 *
 * To speed the access to data because this can be used repetitively from the clients we should provide a second cache level so that most of the
 * repetitive accesses to the same data during a request (that should be a short period of time) provide the same set of data even if this can
 * represent servicing data not updated up to the second. market data has a generic validity of 300 seconds that is much more than the duration for
 * a single request.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketService {
	public interface LowestSellOrderPassThrough {
		MarketOrder getLowestSellOrder( final Integer regionId, final Integer typeId );
	}

	public static final Integer PREDEFINED_MARKET_REGION_ID = 10000002; // This is the Jita region 'The Forge'
	public static final Long PREDEFINED_MARKET_HUB_STATION_ID = 60003760L; // This if the Jita 4-4 main trade hub at Caldari Navy Assembly Plant
	public static final Double MARKET_DEEP_RANGE = 1.05; // The width that will make market prices as 'equivalent'. Represents a 5%
	private static final Map<Integer, Long> regionMarketHubReferenceTable = new HashMap<>();

	static {
		regionMarketHubReferenceTable.put( 10000001, 60003760L );
		regionMarketHubReferenceTable.put( 10000002, 60003760L );
		regionMarketHubReferenceTable.put( 10000003, 60003760L );
		regionMarketHubReferenceTable.put( 10000004, 60003760L );
		regionMarketHubReferenceTable.put( 10000005, 60003760L );
		regionMarketHubReferenceTable.put( 10000006, 60003760L );
		regionMarketHubReferenceTable.put( 10000007, 60003760L );
		regionMarketHubReferenceTable.put( 10000008, 60003760L );
		regionMarketHubReferenceTable.put( 10000009, 60003760L );
		regionMarketHubReferenceTable.put( 10000010, 60003760L );
		regionMarketHubReferenceTable.put( 10000011, 60003760L );
		regionMarketHubReferenceTable.put( 10000012, 60003760L );
		regionMarketHubReferenceTable.put( 10000013, 60003760L );
		regionMarketHubReferenceTable.put( 10000014, 60003760L );
		regionMarketHubReferenceTable.put( 10000015, 60003760L );
		regionMarketHubReferenceTable.put( 10000016, 60003760L );
		regionMarketHubReferenceTable.put( 10000017, 60003760L );
		regionMarketHubReferenceTable.put( 10000018, 60003760L );
		regionMarketHubReferenceTable.put( 10000019, 60003760L );
		regionMarketHubReferenceTable.put( 10000020, 60008494L ); // Defaults to Amarr
		regionMarketHubReferenceTable.put( 10000021, 60003760L );
		regionMarketHubReferenceTable.put( 10000022, 60003760L );
		regionMarketHubReferenceTable.put( 10000023, 60003760L );
		regionMarketHubReferenceTable.put( 10000025, 60003760L );
		regionMarketHubReferenceTable.put( 10000027, 60003760L );
		regionMarketHubReferenceTable.put( 10000028, 60003760L );
		regionMarketHubReferenceTable.put( 10000029, 60003760L );
		regionMarketHubReferenceTable.put( 10000030, 60004588L ); // Defaults to Rens
		regionMarketHubReferenceTable.put( 10000031, 60003760L );
		regionMarketHubReferenceTable.put( 10000032, 60003760L );
		regionMarketHubReferenceTable.put( 10000033, 60003760L );
		regionMarketHubReferenceTable.put( 10000034, 60003760L );
		regionMarketHubReferenceTable.put( 10000035, 60003760L );
		regionMarketHubReferenceTable.put( 10000036, 60003760L );
		regionMarketHubReferenceTable.put( 10000037, 60003760L );
		regionMarketHubReferenceTable.put( 10000038, 60003760L );
		regionMarketHubReferenceTable.put( 10000039, 60003760L );
		regionMarketHubReferenceTable.put( 10000040, 60003760L );
		regionMarketHubReferenceTable.put( 10000041, 60003760L );
		regionMarketHubReferenceTable.put( 10000042, 60005686L ); // Defaults to Hek
		regionMarketHubReferenceTable.put( 10000043, 60008494L ); // Defaults to Amarr
		regionMarketHubReferenceTable.put( 10000044, 60003760L );
		regionMarketHubReferenceTable.put( 10000045, 60003760L );
		regionMarketHubReferenceTable.put( 10000046, 60003760L );
		regionMarketHubReferenceTable.put( 10000047, 60003760L );
		regionMarketHubReferenceTable.put( 10000048, 60003760L );
		regionMarketHubReferenceTable.put( 10000049, 60003760L );
		regionMarketHubReferenceTable.put( 10000050, 60003760L );
		regionMarketHubReferenceTable.put( 10000051, 60003760L );
		regionMarketHubReferenceTable.put( 10000052, 60003760L );
		regionMarketHubReferenceTable.put( 10000053, 60003760L );
		regionMarketHubReferenceTable.put( 10000054, 60003760L );
		regionMarketHubReferenceTable.put( 10000055, 60003760L );
		regionMarketHubReferenceTable.put( 10000056, 60003760L );
		regionMarketHubReferenceTable.put( 10000057, 60003760L );
		regionMarketHubReferenceTable.put( 10000058, 60003760L );
		regionMarketHubReferenceTable.put( 10000059, 60003760L );
		regionMarketHubReferenceTable.put( 10000060, 60003760L );
		regionMarketHubReferenceTable.put( 10000061, 60003760L );
		regionMarketHubReferenceTable.put( 10000062, 60003760L );
		regionMarketHubReferenceTable.put( 10000063, 60003760L );
		regionMarketHubReferenceTable.put( 10000064, 60003760L );
		regionMarketHubReferenceTable.put( 10000065, 60003760L );
		regionMarketHubReferenceTable.put( 10000066, 60003760L );
		regionMarketHubReferenceTable.put( 10000067, 60003760L );
		regionMarketHubReferenceTable.put( 10000068, 60003760L );
		regionMarketHubReferenceTable.put( 10000069, 60003760L );
		regionMarketHubReferenceTable.put( 10000070, 60003760L );
		regionMarketHubReferenceTable.put( 11000001, 60003760L );
		regionMarketHubReferenceTable.put( 11000002, 60003760L );
		regionMarketHubReferenceTable.put( 11000003, 60003760L );
		regionMarketHubReferenceTable.put( 11000004, 60003760L );
		regionMarketHubReferenceTable.put( 11000005, 60003760L );
		regionMarketHubReferenceTable.put( 11000006, 60003760L );
		regionMarketHubReferenceTable.put( 11000007, 60003760L );
		regionMarketHubReferenceTable.put( 11000008, 60003760L );
		regionMarketHubReferenceTable.put( 11000009, 60003760L );
		regionMarketHubReferenceTable.put( 11000010, 60003760L );
		regionMarketHubReferenceTable.put( 11000011, 60003760L );
		regionMarketHubReferenceTable.put( 11000012, 60003760L );
		regionMarketHubReferenceTable.put( 11000013, 60003760L );
		regionMarketHubReferenceTable.put( 11000014, 60003760L );
		regionMarketHubReferenceTable.put( 11000015, 60003760L );
		regionMarketHubReferenceTable.put( 11000016, 60003760L );
		regionMarketHubReferenceTable.put( 11000017, 60003760L );
		regionMarketHubReferenceTable.put( 11000018, 60003760L );
		regionMarketHubReferenceTable.put( 11000019, 60003760L );
		regionMarketHubReferenceTable.put( 11000020, 60003760L );
		regionMarketHubReferenceTable.put( 11000021, 60003760L );
		regionMarketHubReferenceTable.put( 11000022, 60003760L );
		regionMarketHubReferenceTable.put( 11000023, 60003760L );
		regionMarketHubReferenceTable.put( 11000024, 60003760L );
		regionMarketHubReferenceTable.put( 11000025, 60003760L );
		regionMarketHubReferenceTable.put( 11000026, 60003760L );
		regionMarketHubReferenceTable.put( 11000027, 60003760L );
		regionMarketHubReferenceTable.put( 11000028, 60003760L );
		regionMarketHubReferenceTable.put( 11000029, 60003760L );
		regionMarketHubReferenceTable.put( 11000030, 60003760L );
		regionMarketHubReferenceTable.put( 11000031, 60003760L );
		regionMarketHubReferenceTable.put( 11000032, 60003760L );
		regionMarketHubReferenceTable.put( 11000033, 60003760L );
		regionMarketHubReferenceTable.put( 12000001, 60003760L );
		regionMarketHubReferenceTable.put( 12000002, 60003760L );
		regionMarketHubReferenceTable.put( 12000003, 60003760L );
		regionMarketHubReferenceTable.put( 12000004, 60003760L );
		regionMarketHubReferenceTable.put( 12000005, 60003760L );
		regionMarketHubReferenceTable.put( 13000001, 60003760L );
	}

	private final IDataStore dataStore;
	private final LocationCatalogService locationCatalogService;
	private final ESIDataService esiDataService;

	// - C O N S T R U C T O R S
	@Inject
	public MarketService( @NotNull @Named(DMServicesDependenciesModule.IDATA_STORE) final IDataStore dataStore,
	                      @NotNull @Named(DMServicesDependenciesModule.LOCATION_CATALOG_SERVICE) final LocationCatalogService locationCatalogService,
	                      @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService ) {
		this.dataStore = dataStore;
		this.locationCatalogService = locationCatalogService;
		this.esiDataService = esiDataService;
	}

	/**
	 * Returns the value for the lowest sell order for the active orders on the main Market Hub for the selected region.
	 * Market data is segmented into regions and then each region has a predefined market Station Hub that is used to filter again the sell orders
	 * to just leave the ones for the selected station. If the client needs the absolute lowest price for the region will have to use the direct
	 * raw data.
	 *
	 * @param regionId the target region where tos search for the sell orders.
	 * @param typeId   the item type whose price should be searched.
	 * @return the market entry with the lowest sell price.
	 */
	public MarketOrder getLowestSellOrder( final Integer regionId, final Integer typeId ) {
		final AtomicDouble minPrice = new AtomicDouble( Double.MAX_VALUE );
		final long targetSystem = regionMarketHubReferenceTable.getOrDefault( regionId, PREDEFINED_MARKET_HUB_STATION_ID );
		GetMarketsRegionIdOrders200Ok targetOrder = null;
		for (final GetMarketsRegionIdOrders200Ok order : this.esiDataService.getUniverseMarketOrdersForId( regionId, typeId )
				.stream()
				.filter( order -> !order.getIsBuyOrder() )
				.filter( order -> order.getLocationId() == targetSystem )
				.collect( Collectors.toList() )) {
			if (order.getPrice() < minPrice.get()) {
				minPrice.set( order.getPrice() );
				targetOrder = order;
			}
		}
		if (null == targetOrder) return null;
		else return new GetMarketsRegionIdOrdersToMarketOrderConverter( this.locationCatalogService ).convert( targetOrder );
	}

	/**
	 * Get the sell price from the lowest sell order on the selected region and on the configured Market Hub for that region. If there are no
	 * orders then the sell price can be any so the value should be evaluated as zero.
	 *
	 * @param regionId the target region where tos search for the sell orders.
	 * @param typeId   the item type whose price should be searched.
	 * @return the sell price for market entry with the lowest sell price.
	 */
	@Cacheable
	public double getLowestSellPrice( final Integer regionId, final Integer typeId ) {
		final MarketOrder order = this.dataStore.accessLowestSellOrder( regionId, typeId,
				( rid, tid ) -> this.getLowestSellOrder( regionId, typeId )
		);
		if (null != order) return order.getPrice();
		else return 0.0;
	}

	/**
	 * Creates a new <code>MarketData</code> record to consolidate the available market data for the requested item at the requested region.
	 *
	 * @param regionId the target region where to search for the market data.
	 * @param typeId   the market item type id to retrieve the data.
	 * @return a new <code>MarketData</code> instance with the consolidated ESI market data.
	 */
	public MarketData getMarketConsolidatedByRegion4ItemId( final Integer regionId, final Integer typeId ) {
		final Station regionHub = this.getRegionMarketHub( regionId ); // Get the hub system to use in the filters.
		final List<GetMarketsRegionIdOrders200Ok> sellOrders = this.getMarketHubSellOrders4Id( regionHub, typeId );
		final List<GetMarketsRegionIdOrders200Ok> buyOrders = this.getMarketHubBuyOrders4Id( regionHub, typeId );
		return new MarketData.Builder()
				.withTypeId( typeId )
				.withRegionHub( regionHub )
				.withSellOrders( sellOrders )
				.withBestSellOrder( sellOrders.isEmpty() ?
						null :
						new GetMarketsRegionIdOrdersToMarketOrderConverter( this.locationCatalogService ).convert( sellOrders.get( 0 ) ) )
				.withBestBuyOrder( buyOrders.isEmpty() ?
						null :
						new GetMarketsRegionIdOrdersToMarketOrderConverter( this.locationCatalogService ).convert( buyOrders.get( 0 ) ) )
				.build();
	}

	public List<GetMarketsRegionIdOrders200Ok> getMarketHubBuyOrders4Id( final Station hub, final Integer itemId ) {
		return this.esiDataService.getUniverseMarketOrdersForId( hub.getRegionId(), itemId )
				.stream()
				.filter( GetMarketsRegionIdOrders200Ok::getIsBuyOrder )
				.filter( order -> order.getLocationId().equals( hub.getLocationId() ) ) // Filter only orders for the hub system
				.sorted( Comparator.comparingDouble( GetMarketsRegionIdOrders200Ok::getPrice ) )
				.collect( Collectors.toList() );
	}

	public List<GetMarketsRegionIdOrders200Ok> getMarketHubSellOrders4Id( final Station hub, final Integer itemId ) {
		final double priceLimit = this.getLowestSellPrice( hub.getRegionId(), itemId ) * MARKET_DEEP_RANGE;
		return this.esiDataService.getUniverseMarketOrdersForId( hub.getRegionId(), itemId )
				.stream()
				.filter( order -> !order.getIsBuyOrder() ) // Filter only SELL orders
				.filter( order -> order.getLocationId().equals( hub.getLocationId() ) ) // Filter only orders for the hub system
				.filter( order -> order.getPrice() <= priceLimit )
				.sorted( Comparator.comparingDouble( GetMarketsRegionIdOrders200Ok::getPrice ) )
				.collect( Collectors.toList() );
	}

	/**
	 * Searches on a predefined table for the match on the Region identifier. This reference table will store the preferred Market Hub for the
	 * selected region. If the region value of not found on the reference table then the spacial Jita market is selected as the region hub.
	 *
	 * There is no check on the type of location that should be reported. If the data table is wrong then there should be a runtime exception.
	 *
	 * @param regionId the target region to search for the market hub.
	 * @return the region's selected market hub predefined on the application. The returned value is a complete <code>Station</code> location record.
	 */
	public Station getRegionMarketHub( final int regionId ) {
		Long hit = regionMarketHubReferenceTable.get( regionId );
		if (null == hit) hit = PREDEFINED_MARKET_HUB_STATION_ID;
		final SpaceLocation location = this.locationCatalogService.searchLocation4Id( hit );
		return (Station) location;
	}
}