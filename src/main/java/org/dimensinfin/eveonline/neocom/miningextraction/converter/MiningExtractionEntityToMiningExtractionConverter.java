package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import javax.validation.constraints.NotNull;

import org.dimensinfin.eveonline.neocom.service.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

import retrofit2.Converter;

public class MiningExtractionEntityToMiningExtractionConverter implements Converter<MiningExtractionEntity, MiningExtraction> {
	private final LocationCatalogService locationCatalogService;
	private final ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	public MiningExtractionEntityToMiningExtractionConverter( final @NotNull LocationCatalogService locationCatalogService,
	                                                          final @NotNull ResourceFactory resourceFactory ) {
		this.locationCatalogService = locationCatalogService;
		this.resourceFactory = resourceFactory;
	}

	@Override
	public MiningExtraction convert( final MiningExtractionEntity value ) {
		final SpaceLocation spaceLocation = this.locationCatalogService.searchLocation4Id( value.getSolarSystemId().longValue() );
//		if (spaceLocation instanceof SpaceSystemImplementation)
			return new MiningExtraction.Builder()
					.withOwnerId( value.getOwnerId() )
					.withNeoItem( this.resourceFactory.getItemById( value.getTypeId() ) )
					.withSpaceSystem( (SpaceSystem) spaceLocation )
					.withQuantity( value.getQuantity() )
					.withExtractionDate( value.getExtractionDateName() )
					.withExtractionHour( value.getExtractionHour() )
					.build();
//		else throw new NeoComRuntimeException( ErrorInfoCatalog.LOCATION_NOT_THE_CORRECT_TYPE.getErrorMessage() );
	}
}