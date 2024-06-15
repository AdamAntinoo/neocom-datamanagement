package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_CATEGORY_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_CATEGORY_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_GROUP_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_ID;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_NAME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_ESITYPE_VOLUME;
import static org.dimensinfin.eveonline.neocom.support.TestDataConstants.EsiTypeConstants.TEST_GROUP_ID;
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

	public GetUniverseCategoriesCategoryIdOk getGetUniverseCategoriesCategoryIdOk() {
		return new GetUniverseCategoriesCategoryIdOk().categoryId( TEST_CATEGORY_ID ).name( TEST_ESITYPE_CATEGORY_NAME );
	}

	public GetUniverseGroupsGroupIdOk getGetUniverseGroupsGroupIdOk() {
		return new GetUniverseGroupsGroupIdOk().groupId( TEST_GROUP_ID ).name( TEST_ESITYPE_GROUP_NAME ).categoryId( TEST_CATEGORY_ID );
	}

	public GetUniverseTypesTypeIdOk getGetUniverseTypesTypeIdOk() {
		return new GetUniverseTypesTypeIdOk().typeId( TEST_ESITYPE_ID ).name( TEST_ESITYPE_NAME ).volume( TEST_ESITYPE_VOLUME );
	}

	public LocationIdentifier getAccesibleStructureLocationIdentifier() {
		return new LocationIdentifier.Builder()
				.withSpaceIdentifier( TEST_ACCESIBLE_STRUCTURE_LOCATION_ID )
				.withLocationType( EsiAssets200Ok.LocationTypeEnum.OTHER )
				.withLocationFlag( EsiAssets200Ok.LocationFlagEnum.HANGAR )
				.build();
	}

	public EsiType getEsiType() {
		return new EsiType.Builder()
				.withTypeId( 17464 )
				.withItemType( this.getGetUniverseTypesTypeIdOk() )
				.withCategory( this.getGetUniverseCategoriesCategoryIdOk() )
				.withGroup( this.getGetUniverseGroupsGroupIdOk() )
				.build();
	}

	public SpaceLocation getSpaceLocation() {
		return new SpaceSystemImplementation.Builder()
				.withRegion( new GetUniverseRegionsRegionIdOk().regionId( 10000006 ).name( "REGION" ) )
				.withConstellation(
						new GetUniverseConstellationsConstellationIdOk().regionId( 10000006 ).constellationId( 20000006 ).name( "CONSTELLATION" ) )
				.withSolarSystem( new GetUniverseSystemsSystemIdOk().constellationId( 20000006 ).systemId( 2000004 ).name( "SYSTEM" ) )
				.build();
	}
}
