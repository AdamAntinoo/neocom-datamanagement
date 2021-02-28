package org.dimensinfin.eveonline.neocom.service;

import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

public interface IDataStore {
	MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId, final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod);
}
