package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;

import retrofit2.Converter;

public class MiningExtractionToMiningExtractionEntityConverter implements Converter<MiningExtraction, MiningExtractionEntity> {
	@Override
	public MiningExtractionEntity convert( final MiningExtraction value ) {
		return new MiningExtractionEntity.Builder()
				.withId( value.getId() )
				.withTypeId( value.getTypeId() )
				.withOwnerId( (int) value.getOwnerId() )
				.withExtractionDateName( value.getExtractionDateName() )
				.withExtractionHour( value.getExtractionHour() )
				.withQuantity( value.getQuantity() )
				.withSolarSystemId( value.getLocationId().intValue() )
				.build();
	}
}
