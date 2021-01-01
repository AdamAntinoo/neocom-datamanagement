package org.dimensinfin.eveonline.neocom.service;

import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

public class ResourceFactory {
	private final ESIDataService esiDataService;

	// - C O N S T R U C T O R S
	@Inject
	public ResourceFactory( final @NotNull @Named("ESIDataService") ESIDataService esiDataService ) {this.esiDataService = esiDataService;}

	public Resource generateResource4Id( final int typeId, final int quantity ) {
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiItem4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		return new Resource.Builder()
				.withTypeId( typeId )
				.withItemType( item )
				.withGroup( group )
				.withCategory( this.esiDataService.searchItemCategory4Id( group.getCategoryId() ) )
				.build()
				.setQuantity( quantity );
	}

	public EsiType generateType4Id( final int typeId ) {
		final GetUniverseTypesTypeIdOk item = this.esiDataService.searchEsiItem4Id( typeId );
		final GetUniverseGroupsGroupIdOk group = this.esiDataService.searchItemGroup4Id( item.getGroupId() );
		return new EsiType.Builder()
				.withTypeId( typeId )
				.withItemType( item )
				.withGroup( group )
				.withCategory( this.esiDataService.searchItemCategory4Id( group.getCategoryId() ) )
				.build();
	}

	@Deprecated
	public NeoItem getItemById( final Integer itemId ) {
		return new NeoItem( itemId );
	}
}