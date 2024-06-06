package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.SpaceLocationConstants.TEST_ACCESIBLE_STRUCTURE_LOCATION_ID;

public class InstanceGenerator {
	public Credential getCredential() {
		return new Credential.Builder( 123456 )
				.withAccountName( "TEST CREDENTIAL" )
				.withCorporationId( 4321987 )
				.withAccessToken( "-TEST INVALID ACCESS TOKEN-" )
				.withRefreshToken( "-TEST INVALID ACCESS TOKEN-" )
				.withDataSource( "Tranquility" )
				.withScope( "SCOPE" )
				.build()
				.setAssetsCount( 89 )
				.setWalletBalance( 876567.54 )
				.setMiningResourcesEstimatedValue( 123456789.98 )
				.setRaceName( "TEST RACE" );
	}

	public GetUniverseGroupsGroupIdOk getEsiGroup() {
		return new GetUniverseGroupsGroupIdOk().groupId( 34 ).name( "Group Name" );
	}
	public GetUniverseCategoriesCategoryIdOk getEsiCategory(){
		return new GetUniverseCategoriesCategoryIdOk ().categoryId( 25 ).name( "Category Name" );
	}

	public LocationIdentifier getAccesibleStructureLocationIdentifier() {
		return new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_ACCESIBLE_STRUCTURE_LOCATION_ID )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.OTHER )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.build();
	}
}
