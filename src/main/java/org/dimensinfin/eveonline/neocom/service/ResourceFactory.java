package org.dimensinfin.eveonline.neocom.service;

import java.util.Optional;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.industry.domain.Resource;
import org.dimensinfin.eveonline.neocom.ports.IDataStorePort;

public class ResourceFactory {
	private final ESIDataService esiDataService;
	private final IDataStorePort dataStore;

	// - C O N S T R U C T O R S
	@Inject
	public ResourceFactory( final @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) ESIDataService esiDataService,
	                        final @NotNull @Named(DMServicesDependenciesModule.IDATA_STORE) IDataStorePort dataStore
	) {
		this.esiDataService = esiDataService;
		this.dataStore = dataStore;
	}

	public Resource generateResource4Id( final int typeId, final int quantity ) {
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiUniverseType4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		return new Resource.Builder()
				.withTypeId( typeId )
				.withItemType( item )
				.withGroup( group )
				.withCategory( this.esiDataService.searchItemCategory4Id( group.getCategoryId() ) )
				.build()
				.setQuantity( quantity );
	}

	/**
	 * This method is cached and the resulting data is stored on the Dta Store repository. The repository key to be used is the
	 * <code>ESI_TYPE_KEY_NAME</code>.
	 *
	 * If the item is not found on the cache then we should call the ESI Data Source to retrieve the elements that compose the <code>EsiType</code>.
	 *
	 * @param typeId the Eve unique type id to be searched/generated.
	 * @return a complete <code>EsiType</code>.
	 */
	public EsiType generateType4Id( final int typeId ) {
		// - Search for the type at the cache.
		final Optional<EsiType> cachedType = this.dataStore.accessEsiType4Id( typeId );
		if (cachedType.isPresent() )return cachedType.get();
		// - Not found on cache or stale.
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiUniverseType4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		final EsiType target = new EsiType.Builder()
				.withTypeId( typeId )
				.withItemType( item )
				.withGroup( group )
				.withCategory( this.esiDataService.searchItemCategory4Id( group.getCategoryId() ) )
				.build();
		return this.dataStore.storeEsiType4Id( target);
	}
}