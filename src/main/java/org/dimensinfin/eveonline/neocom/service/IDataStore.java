package org.dimensinfin.eveonline.neocom.service;

import java.util.List;
import java.util.Optional;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.industry.domain.ProcessedBlueprint;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;

public interface IDataStore {
	String REDIS_SEPARATOR=":";
	GetUniverseTypesTypeIdOk accessEsiItem4Id( final int typeId, final ESIDataService.EsiItemPassThrough esiItemPassThrough );

	MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId, final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod );

	List<ProcessedBlueprint> accessProcessedBlueprints( final Integer pilotId );

	ProcessedBlueprint accessProcessedBlueprintsByUID( final Integer pilotId, final String blueprintUID );

	String generateBlueprintCostIndexUniqueId( final Integer pilotId );

	void updateProcessedBlueprint( final Integer pilotId, final ProcessedBlueprint blueprint );

	Optional<SpaceLocation> accessLocation( final String locationCacheId );

	void updateLocation(final  String locationCacheId, final SpaceLocation location );
}
