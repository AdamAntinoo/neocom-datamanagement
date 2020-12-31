package org.dimensinfin.eveonline.neocom.asset.converter;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

import retrofit2.Converter;

public class GetCorporationLocationType2EsiAssets200OkLocationType implements Converter<GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum
		, EsiAssets200Ok.LocationTypeEnum> {
	@Override
	public EsiAssets200Ok.LocationTypeEnum convert( final GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum type ) {
		return EsiAssets200Ok.LocationTypeEnum.valueOf( type.toString().toUpperCase() );
	}
}
