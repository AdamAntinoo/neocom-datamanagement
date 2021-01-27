package org.dimensinfin.eveonline.neocom.loyalty.converter;

import javax.validation.constraints.NotNull;

import org.dimensinfin.core.interfaces.Converter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetLoyaltyStoresCorporationIdOffers200Ok;
import org.dimensinfin.eveonline.neocom.loyalty.persistence.LoyaltyOfferEntity;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;

/**
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class GetLoyaltyStoresCorporationIdOfferToLoyaltyOfferEntityConverter implements
		Converter<GetLoyaltyStoresCorporationIdOffers200Ok, LoyaltyOfferEntity> {
	private final int corporationId;
	private final String corporationName;
	private final ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	public GetLoyaltyStoresCorporationIdOfferToLoyaltyOfferEntityConverter( @NotNull final Integer corporationId,
	                                                                        @NotNull final String corporationName,
	                                                                        @NotNull final ResourceFactory resourceFactory ) {
		this.corporationId = corporationId;
		this.corporationName = corporationName;
		this.resourceFactory = resourceFactory;
	}

	@Override
	public LoyaltyOfferEntity convert( final GetLoyaltyStoresCorporationIdOffers200Ok input ) {
		return new LoyaltyOfferEntity.Builder()
				.withId( input.getOfferId() )
				.withLoyaltyCorporation( corporationId, corporationName )
				.withType( this.resourceFactory.generateType4Id( input.getTypeId() ) )
				.withIskCost( input.getIskCost() )
				.withLpCost( input.getLpCost() )
				.withQuantity( input.getQuantity() )
				.build();
	}
}