package org.dimensinfin.eveonline.neocom.service;

import java.util.Objects;
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

	// - C O N S T R U C T O R S
	@Inject
	public ResourceFactory( final @NotNull @Named(DMServicesDependenciesModule.ESIDATA_SERVICE) ESIDataService esiDataService,
	                        final @NotNull @Named(DMServicesDependenciesModule.IDATA_STORE) IDataStorePort dataStore
	) {
		this.esiDataService = esiDataService;
	}

	public Resource generateResource4Id( final int typeId, final int quantity ) {
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiUniverseType4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		return new Resource.Builder()
				.withTypeId( typeId )
				.withItemType( Objects.requireNonNull( item, "ESI Type should not be null while creating Resource." ) )
				.withGroup( Objects.requireNonNull( group, "ESI Group should not be null while creating Resource." ) )
				.withCategory( Objects.requireNonNull(
								this.esiDataService.searchItemCategory4Id( group.getCategoryId() ),
								"ESI Category should not be null while creating Resource."
						)
				)
				.build()
				.setQuantity( quantity );
	}

	public EsiType generateType4Id( final int typeId ) {
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiUniverseType4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		return new EsiType.Builder()
				.withTypeId( typeId )
				.withItemType( Objects.requireNonNull( item, "ESI Type should not be null while creating EsiType." ) )
				.withGroup( Objects.requireNonNull( group, "ESI Group should not be null while creating EsiType." ) )
				.withCategory( Objects.requireNonNull(
								this.esiDataService.searchItemCategory4Id( group.getCategoryId() ),
								"ESI Category should not be null while creating EsiType."
						)
				)
				.build();
	}
}