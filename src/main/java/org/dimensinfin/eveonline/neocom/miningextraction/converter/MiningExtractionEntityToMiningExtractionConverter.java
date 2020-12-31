package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;

import retrofit2.Converter;

public class MiningExtractionEntityToMiningExtractionConverter implements Converter<MiningExtractionEntity, MiningExtraction> {
	private LocationCatalogService locationCatalogService;

	public MiningExtractionEntityToMiningExtractionConverter( final LocationCatalogService locationCatalogService ) {
		this.locationCatalogService = locationCatalogService;
	}

	@Override
	public MiningExtraction convert( final MiningExtractionEntity value ) {
		final SpaceLocation spaceLocation = this.locationCatalogService.searchLocation4Id( value.getSolarSystemId().longValue() );
		if (spaceLocation instanceof SpaceSystemImplementation)
			return new MiningExtraction.Builder()
					.withOwnerId( value.getOwnerId() )
					.withNeoItem( NeoItemFactory.getSingleton().getItemById( value.getTypeId() ) )
					.withSpaceSystem( (SpaceSystem) spaceLocation )
					.withQuantity( value.getQuantity() )
					.withExtractionDate( value.getExtractionDateName() )
					.withExtractionHour( value.getExtractionHour() )
					.build();
		else throw new NeoComRuntimeException( ErrorInfoCatalog.LOCATION_NOT_THE_CORRECT_TYPE.getErrorMessage() );
	}
}