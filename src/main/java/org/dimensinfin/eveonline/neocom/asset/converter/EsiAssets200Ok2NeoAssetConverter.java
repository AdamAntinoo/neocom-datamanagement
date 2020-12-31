package org.dimensinfin.eveonline.neocom.asset.converter;

import java.util.HashSet;

import org.dimensinfin.eveonline.neocom.asset.domain.AssetTypes;
import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;

import retrofit2.Converter;

public class EsiAssets200Ok2NeoAssetConverter implements Converter<EsiAssets200Ok, NeoAsset> {
	private static final HashSet<Integer> containerIds = new HashSet<>( 25 );

	static {
		containerIds.add( 11488 );
		containerIds.add( 11489 );
		containerIds.add( 11490 );
		containerIds.add( 17363 );
		containerIds.add( 17364 );
		containerIds.add( 17365 );
		containerIds.add( 17366 );
		containerIds.add( 17367 );
		containerIds.add( 17368 );
		containerIds.add( 2263 );
		containerIds.add( 23 );
		containerIds.add( 24445 );
		containerIds.add( 28570 );
		containerIds.add( 3293 );
		containerIds.add( 3296 );
		containerIds.add( 3297 );
		containerIds.add( 33003 );
		containerIds.add( 33005 );
		containerIds.add( 33007 );
		containerIds.add( 33009 );
		containerIds.add( 33011 );
		containerIds.add( 3465 );
		containerIds.add( 3466 );
		containerIds.add( 3467 );
		containerIds.add( 3468 );
		containerIds.add( 41567 );
		containerIds.add( 60 ); // Asset Safety Wrap
	}

	@Override
	public NeoAsset convert( final EsiAssets200Ok esiAsset ) {
		final NeoAsset newAsset = new NeoAsset();
		newAsset.setAssetId( esiAsset.getItemId() );
		newAsset.setAssetDelegate( esiAsset );
		newAsset.setItemDelegate( new NeoItem( esiAsset.getTypeId() ) );
		if (newAsset.getCategoryName().equalsIgnoreCase( AssetTypes.SHIP.getTypeName() ))
			newAsset.setShipFlag( true );
		newAsset.setBlueprintFlag( this.checkIfBlueprint( newAsset ) );
		if (esiAsset.getLocationId() > 61E6) // The asset is contained into another asset. Set the parent.
			newAsset.setParentContainerId( esiAsset.getLocationId() );

		// Now calculate the public part of the location. The definitive data should be calculated outside.
		newAsset.setLocationId( new LocationIdentifier.Builder()
				.withSpaceIdentifier( esiAsset.getLocationId() )
				.withLocationFlag( esiAsset.getLocationFlag() )
				.withLocationType( esiAsset.getLocationType() )
				.build() );
		newAsset.setContainerFlag( this.checkIfContainer( newAsset ) ); // Container detection requires the location identifier.
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
		if (containerIds.contains( asset.getTypeId() )) return true;
		if (asset.isOffice()) return true;
		if (asset.getName().contains( "Container" )) return true;
		if (asset.getName().contains( "Wrap" )) return true;
		if (null != asset.getLocationId())
			return asset.getLocationId().getLocationFlag().equals( EsiAssets200Ok.LocationFlagEnum.IMPOUNDED );
		return false;
	}
}
