package org.dimensinfin.eveonline.neocom.industry.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;

public class PricedResource extends Resource {
	private static final long serialVersionUID = 5026838820412104776L;
	private double price;
	private MarketData marketData;

	// - G E T T E R S   &   S E T T E R S
	public MarketData getMarketData() {
		return this.marketData;
	}

	public double getMarketPrice() {
		return this.marketData.getBestSellPrice();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( this.price ).append( this.marketData ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof PricedResource)) return false;
		final PricedResource that = (PricedResource) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) ).append( this.price, that.price )
				.append( this.marketData, that.marketData ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "typeId", this.typeId )
				.append( "industryGroup", this.industryGroup )
				.append( "price", this.price )
				.append( "marketData", this.marketData )
				.append( "quantity", this.quantity )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final PricedResource onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new PricedResource();
		}

		public PricedResource build() {
			return this.onConstruction;
		}

		public PricedResource.Builder withCategory( final GetUniverseCategoriesCategoryIdOk category ) {
			this.onConstruction.category = NeoObjects.requireNonNull( category, "Null Esi Category while constructing PricedResource" );
			return this;
		}

		public PricedResource.Builder withGroup( final GetUniverseGroupsGroupIdOk group ) {
			this.onConstruction.group = NeoObjects.requireNonNull( group );
			return this;
		}

		public PricedResource.Builder withItemType( final GetUniverseTypesTypeIdOk item ) {
			this.onConstruction.type = NeoObjects.requireNonNull( item );
			this.onConstruction.typeId = this.onConstruction.type.getTypeId();
			return this;
		}

		public PricedResource.Builder withMarketData( final MarketData marketData ) {
			this.onConstruction.marketData = NeoObjects.requireNonNull( marketData );
			return this;
		}

		public PricedResource.Builder withPrice( final Double price ) {
			if (null != price) this.onConstruction.price = price;
			return this;
		}

		public PricedResource.Builder withQuantity( final Integer quantity ) {
			if (null != quantity) this.onConstruction.quantity = quantity;
			return this;
		}
	}
}
