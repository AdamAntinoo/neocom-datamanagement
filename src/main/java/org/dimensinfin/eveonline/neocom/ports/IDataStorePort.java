package org.dimensinfin.eveonline.neocom.ports;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.industry.domain.ProcessedBlueprint;
import org.dimensinfin.eveonline.neocom.market.MarketOrder;
import org.dimensinfin.eveonline.neocom.market.service.MarketService;
import org.dimensinfin.eveonline.neocom.service.ESIDataService;

public interface IDataStorePort {

	Optional<EsiType> accessEsiType4Id( final int typeId );

	EsiType storeEsiType4Id( final EsiType target );

	GetUniverseTypesTypeIdOk accessEsiUniverseItem4Id( final int typeId, final ESIDataService.EsiItemPassThrough esiItemPassThrough );

	MarketOrder accessLowestSellOrder( final Integer regionId, final Integer typeId, final MarketService.LowestSellOrderPassThrough lowestSellOrderReloadMethod );

	List<ProcessedBlueprint> accessProcessedBlueprints( final Integer pilotId );

	Optional<ProcessedBlueprint> accessProcessedBlueprintsByUID( final Integer pilotId, final String blueprintUID );

	String generateBlueprintCostIndexUniqueId( final Integer pilotId );

	void updateProcessedBlueprint( final Integer pilotId, final ProcessedBlueprint blueprint );

	Optional<SpaceLocation> accessLocation( final String locationCacheId );

	SpaceLocation updateLocation( final String locationCacheId, final SpaceLocation location );

	Optional<EsiType> accessType4Id( int typeId, final @NotNull Function<Integer, EsiType> generatorEsiType );

	Optional<SpaceLocation> accessLocation4Id( final @NotNull Long locationId, final @NotNull Credential credential );

}
