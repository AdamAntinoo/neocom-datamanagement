package org.dimensinfin.eveonline.neocom.asset.converter;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

import retrofit2.Converter;

public class GetCharactersCharacterIdAsset2EsiAssets200OkConverter implements Converter<GetCharactersCharacterIdAssets200Ok, EsiAssets200Ok> {
	@Override
	public EsiAssets200Ok convert( final GetCharactersCharacterIdAssets200Ok asset ) {
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
