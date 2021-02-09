package org.dimensinfin.eveonline.neocom.market.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class MarketService {
	public static final Double MARKET_DEEP_RANGE = 1.05;
	private final ESIDataService esiDataService;

	// - C O N S T R U C T O R S
	@Inject
	public MarketService( @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) final ESIDataService esiDataService ) {
		this.esiDataService = esiDataService;
	}

	public double getLowestSellPrice( final List<GetMarketsRegionIdOrders200Ok> orders, final int targetSystem ) {
		double minPrice = Double.MAX_VALUE;
		for (final GetMarketsRegionIdOrders200Ok order : orders) {
			if ((Boolean.TRUE.equals( order.getIsBuyOrder() )) || (order.getSystemId() != targetSystem)) continue;
			if (order.getPrice() < minPrice) minPrice = order.getPrice();
		}
		return minPrice;
	}

	public List<GetMarketsRegionIdOrders200Ok> getMarketHubSellOrders4Id( final int regionId, final Integer itemId ) {
		final Station regionHub = this.esiDataService.getRegionMarketHub( regionId );
		final List<GetMarketsRegionIdOrders200Ok> orders = this.esiDataService.getUniverseMarketOrdersForId( regionId, itemId );
		final double priceLimit = this.getLowestSellPrice( orders, regionHub.getSolarSystemId() ) * MARKET_DEEP_RANGE;
		return orders
				.stream()
				.filter( order -> !order.getIsBuyOrder() ) // Filter only SELL orders
				.filter( order -> order.getSystemId().equals( regionHub.getSolarSystemId() ) ) // Filter only orders for the hub system
				.filter( order -> order.getPrice() <= priceLimit )
				.sorted( Comparator.comparingDouble( GetMarketsRegionIdOrders200Ok::getPrice ) )
				.collect( Collectors.toList() );
	}
}