package org.dimensinfin.eveonline.neocom.asset.converter;

import java.util.HashMap;
import java.util.Map;

import org.dimensinfin.eveonline.neocom.asset.domain.AssetTypes;
import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

import retrofit2.Converter;

@Deprecated
public class GetCharactersCharacterIdAsset2NeoAssetConverter implements Converter<GetCharactersCharacterIdAssets200Ok, NeoAsset> {
	private static final Map<Integer, Boolean> containerTypes = new HashMap<>();

	static {
		containerTypes.put( 11488, true );
		containerTypes.put( 11489, true );
		containerTypes.put( 11490, true );
		containerTypes.put( 17363, true );
		containerTypes.put( 17364, true );
		containerTypes.put( 17365, true );
		containerTypes.put( 17366, true );
		containerTypes.put( 17367, true );
		containerTypes.put( 17368, true );
		containerTypes.put( 2263, true );
		containerTypes.put( 23, true );
		containerTypes.put( 24445, true );
		containerTypes.put( 28570, true );
		containerTypes.put( 3293, true );
		containerTypes.put( 3296, true );
		containerTypes.put( 3297, true );
		containerTypes.put( 33003, true );
		containerTypes.put( 33005, true );
		containerTypes.put( 33007, true );
		containerTypes.put( 33009, true );
		containerTypes.put( 33011, true );
		containerTypes.put( 3465, true );
		containerTypes.put( 3466, true );
		containerTypes.put( 3467, true );
		containerTypes.put( 3468, true );
		containerTypes.put( 41567, true );
		containerTypes.put( 60, true ); // Asset Safety Wrap

	}

	@Override
	public NeoAsset convert( final GetCharactersCharacterIdAssets200Ok esiAssetOk ) {
		final EsiAssets200Ok esiAsset = new GetCharactersCharacterIdAsset2EsiAssets200OkConverter().convert( esiAssetOk );
		final NeoAsset newAsset = new NeoAsset();
		newAsset.setAssetId( esiAsset.getItemId() );
		newAsset.setAssetDelegate( esiAsset );
		newAsset.setItemDelegate( new NeoItem( esiAsset.getTypeId() ) );
		if (newAsset.getCategoryName().equalsIgnoreCase( AssetTypes.SHIP.getTypeName() ))
			newAsset.setShipFlag( true );
		newAsset.setBlueprintFlag( this.checkIfBlueprint( newAsset ) );
		newAsset.setContainerFlag( this.checkIfContainer( newAsset ) );
		if (esiAsset.getLocationId() > 61E6) // The asset is contained into another asset. Set the parent.
			newAsset.setParentContainerId( esiAsset.getLocationId() );

		// Now calculate the public part of the location. The definitive data should be calculated outside.
		newAsset.setLocationId( new LocationIdentifier.Builder()
				.withSpaceIdentifier( esiAsset.getLocationId() )
				.withLocationFlag( esiAsset.getLocationFlag() )
				.withLocationType( esiAsset.getLocationType() )
				.build() );
		return newAsset;
	}

	/**
	 * One asset is a blueprint if the category is blueprint. Then if the blueprint is original or a copy it comes from the
	 * asset delegate field 'getIsBlueprintCopy'.
	 *
	 * @param asset the asset to check.
	 * @return true if the asset is a blueprint of any type.
	 */
	private boolean checkIfBlueprint( final NeoAsset asset ) {
		return asset.getCategoryName().equalsIgnoreCase( AssetTypes.BLUEPRINT.getTypeName() );
	}

	/**
	 * There are many types of containers. Use all the identified catalog of asset types to calculate the response.
	 *
	 * @param asset the asset to check.
	 * @return true if the asset is able to contain other assets, ships included.
	 */
	private boolean checkIfContainer( final NeoAsset asset ) {
		if (asset.isBlueprint()) return false;
		if (asset.isShip()) return true;
		// Use a list of types to set what is a container
		if (containerTypes.containsKey( asset.getTypeId() ) return true;
		if (asset.getName().contains( "Container" )) return true;
		return asset.getName().contains( "Wrap" );
	}
}
