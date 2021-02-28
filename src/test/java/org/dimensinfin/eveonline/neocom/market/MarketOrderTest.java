package org.dimensinfin.eveonline.neocom.market;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.Station;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

public class MarketOrderTest {
	@Test
	public void buildContract() {
		final GetMarketsRegionIdOrders200Ok orderData = Mockito.mock(GetMarketsRegionIdOrders200Ok.class);
		final Station station=Mockito.mock(Station.class);
		final MarketOrder marketOrder=new MarketOrder.Builder()
				.withOrderData( orderData )
				.withStation( station )
				.build();
		Assertions.assertNotNull(marketOrder);
	}
}