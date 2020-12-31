package org.dimensinfin.eveonline.neocom.asset.converter;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

import retrofit2.Converter;

public class GetCorporationsCorporationAsset2EsiAssets200OkConverter implements Converter<GetCorporationsCorporationIdAssets200Ok, EsiAssets200Ok> {
	@Override
	public EsiAssets200Ok convert( final GetCorporationsCorporationIdAssets200Ok asset ) {
		return new EsiAssets200Ok.Builder()
				.withTypeId( asset.getTypeId() )
				.withItemId( asset.getItemId() )
				.withQuantity( asset.getQuantity() )
				.withIsBlueprintCopy( asset.getIsBlueprintCopy() )
				.withIsSingleton( asset.getIsSingleton() )
				.withLocationId( asset.getLocationId() )
				.withLocationFlag( asset.getLocationFlag() )
				.withLocationType( asset.getLocationType() )
				.build();
	}
}
