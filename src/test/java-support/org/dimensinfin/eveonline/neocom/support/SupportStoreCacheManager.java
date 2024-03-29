package org.dimensinfin.eveonline.neocom.support;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.service.RetrofitService;

public class SupportStoreCacheManager extends StoreCacheManager {
	private RetrofitService retrofitService;

	private SupportStoreCacheManager() {}

	// - C A C H E   E X P O R T E D   A P I
//	public Single<GetUniverseTypesTypeIdOk> accessItem( final Integer itemId ) {
//		return this.getUniverseTypeById( typeId )
//	}
//
//	public Single<GetUniverseGroupsGroupIdOk> accessGroup( final Integer groupId ) {
//		return this.itemGroupStore.get( groupId );
//	}
//
//	public Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final Integer categoryId ) {
//		return this.categoryStore.get( categoryId );
//	}
//
//	public Single<GetUniverseSystemsSystemIdOk> accessSolarSystem( final Integer solarSystemId ) {
//		return this.systemsStoreCache.get( solarSystemId );
//	}
	// - B U I L D E R
	public static class Builder {
		private SupportStoreCacheManager onConstruction;

		public Builder() {
			this.onConstruction = new SupportStoreCacheManager();
		}

		public SupportStoreCacheManager.Builder withRetrofitFactory( final RetrofitService retrofitService ) {
			Objects.requireNonNull( retrofitService );
			this.onConstruction.retrofitService = retrofitService;
			return this;
		}

		public SupportStoreCacheManager build() {
			return this.onConstruction;
		}
	}
}
