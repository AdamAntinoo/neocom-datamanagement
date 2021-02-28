package org.dimensinfin.eveonline.neocom.market.converter;

import javax.validation.constraints.NotNull;

import org.dimensinfin.core.interfaces.Converter;
import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;

public class GetMarketsRegionIdOrdersToMarketOrderConverter implements Converter<GetMarketsRegionIdOrders200Ok, MarketOrder> {
	private final LocationCatalogService locationCatalogService;

	// - C O N S T R U C T O R S
	public GetMarketsRegionIdOrdersToMarketOrderConverter( final @NotNull LocationCatalogService locationCatalogService ) {
		this.locationCatalogService = locationCatalogService;
	}

	@Override
	public MarketOrder convert( final GetMarketsRegionIdOrders200Ok input ) {
		return new MarketOrder.Builder()
				.withOrderData( input )
				.withStation( (Station) this.locationCatalogService.searchLocation4Id( input.getLocationId() ) )
				.build();
	}
}
