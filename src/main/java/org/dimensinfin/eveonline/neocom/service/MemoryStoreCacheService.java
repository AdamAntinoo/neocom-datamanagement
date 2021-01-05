package org.dimensinfin.eveonline.neocom.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.logging.LogWrapper;

import io.reactivex.Single;
import retrofit2.Response;

public class MemoryStoreCacheService implements IStoreCache {
	private final RetrofitService retrofitService;

	// - C A C H E S
	private Map<Integer, GetUniverseTypesTypeIdOk> esiTypeStore = new HashMap<>();
	private Map<Integer, GetUniverseGroupsGroupIdOk> typeGroupStore = new HashMap<>();
	private Map<Integer, GetUniverseCategoriesCategoryIdOk> typeCategoryStore = new HashMap<>();

	// - C O N S T R U C T O R S
	@Inject
	private MemoryStoreCacheService( final @NotNull @Named(DMServicesDependenciesModule.RETROFIT_SERVICE) RetrofitService retrofitService ) {
		this.retrofitService = retrofitService;
	}

	@Override
	public Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final int categoryId ) {
		if (!this.typeCategoryStore.containsKey( categoryId ))
			this.typeCategoryStore.put( categoryId, this.downloadCategory( categoryId ) );
		return Single.just( this.typeCategoryStore.get( categoryId ) );
	}

	@Override
	public Single<GetUniverseGroupsGroupIdOk> accessGroup( final int groupId ) {
		if (!this.typeGroupStore.containsKey( groupId ))
			this.typeGroupStore.put( groupId, this.downloadGroup( groupId ) );
		return Single.just( this.typeGroupStore.get( groupId ) );
	}

	@Override
	public Single<GetUniverseTypesTypeIdOk> accessType( final int typeId ) {
		if (!this.esiTypeStore.containsKey( typeId ))
			this.esiTypeStore.put( typeId, this.downloadEsiType( typeId ) );
		return Single.just( this.esiTypeStore.get( typeId ) );
	}

	public GetUniverseTypesTypeIdOk downloadEsiType( final int typeId ) {
		LogWrapper.enter( MessageFormat.format( "Type Id: {0}", typeId ) );
		try {
			final Response<GetUniverseTypesTypeIdOk> esiTypeResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseTypesTypeId( typeId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (esiTypeResponse.isSuccessful()) return esiTypeResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	private GetUniverseCategoriesCategoryIdOk downloadCategory( final int categoryId ) {
		LogWrapper.enter( MessageFormat.format( "Category Id: {0}", categoryId ) );
		try {
			final Response<GetUniverseCategoriesCategoryIdOk> categoryResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseCategoriesCategoryId( categoryId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (categoryResponse.isSuccessful()) return categoryResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}

	private GetUniverseGroupsGroupIdOk downloadGroup( final int groupId ) {
		LogWrapper.enter( MessageFormat.format( "Group Id: {0}", groupId ) );
		try {
			final Response<GetUniverseGroupsGroupIdOk> groupResponse = this.retrofitService
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseGroupsGroupId( groupId,
							ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE,
							ESIDataProvider.DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (groupResponse.isSuccessful()) return groupResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			LogWrapper.error( ioe );
		} finally {
			LogWrapper.exit();
		}
		return null;
	}
}