package org.dimensinfin.eveonline.neocom.service;

import java.util.List;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.industry.domain.ProcessedBlueprint;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

public interface IDataStore {
	GetUniverseTypesTypeIdOk accessEsiItem4Id( final int typeId, final ESIDataService.EsiItemPassThrough esiItemPassThrough );

	MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId, final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod );

	List<ProcessedBlueprint> accessProcessedBlueprints( final Integer pilotId );

	void updateProcessedBlueprint( final Integer pilotId, final ProcessedBlueprint blueprint );
}
