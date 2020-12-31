package org.dimensinfin.eveonline.neocom.asset.converter;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

import retrofit2.Converter;

public class GetCorporationsLocationFlag2EsiAssets200OkLocationFlag implements Converter<GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum
		, EsiAssets200Ok.LocationFlagEnum> {
	@Override
	public EsiAssets200Ok.LocationFlagEnum convert( final GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum flag ) {
		return EsiAssets200Ok.LocationFlagEnum.valueOf( flag.toString().toUpperCase() );
	}
}
