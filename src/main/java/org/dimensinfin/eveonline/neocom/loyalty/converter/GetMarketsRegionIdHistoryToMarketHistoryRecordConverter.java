package org.dimensinfin.eveonline.neocom.loyalty.converter;

import org.dimensinfin.core.interfaces.Converter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdHistory200Ok;
import org.dimensinfin.eveonline.neocom.loyalty.domain.MarketHistoryRecord;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class GetMarketsRegionIdHistoryToMarketHistoryRecordConverter implements Converter<GetMarketsRegionIdHistory200Ok, MarketHistoryRecord> {

	@Override
	public MarketHistoryRecord convert( final GetMarketsRegionIdHistory200Ok input ) {
		return new MarketHistoryRecord.Builder()
				.withRecordDate( input.getDate() )
				.withVolume( input.getVolume().intValue() )
				.build();
	}
}