package org.dimensinfin.eveonline.neocom.service;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

import io.reactivex.Single;

public interface IStoreCache {
	Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final int categoryId );

	Single<GetUniverseGroupsGroupIdOk> accessGroup( final int groupId );

	Single<GetUniverseTypesTypeIdOk> accessType( final int typeId );
}
