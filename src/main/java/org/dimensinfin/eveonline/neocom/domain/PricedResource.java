package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.market.MarketData;

public class PricedResource extends Resource {
	private static final long serialVersionUID = 5026838820412104776L;
	private double price;
	private MarketData marketData;

	// - G E T T E R S   &   S E T T E R S
	public double getMarketPrice() {
		return this.marketData.getBestSellOrder().getPrice();
	}

	public MarketData getMarketData() {
		return this.marketData;
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
			this.onConstruction.category = Objects.requireNonNull( category );
			return this;
		}

		public PricedResource.Builder withGroup( final GetUniverseGroupsGroupIdOk group ) {
			this.onConstruction.group = Objects.requireNonNull( group );
			return this;
		}

		public PricedResource.Builder withItemType( final GetUniverseTypesTypeIdOk item ) {
			this.onConstruction.type = Objects.requireNonNull( item );
			this.onConstruction.typeId = this.onConstruction.type.getTypeId();
			return this;
		}

		public PricedResource.Builder withMarketData( final MarketData marketData ) {
			((PricedResource) this.onConstruction).marketData = Objects.requireNonNull( marketData );
			return this;
		}

		public PricedResource.Builder withPrice( final Double price ) {
			if (null != price) ((PricedResource) this.onConstruction).price = price;
			return this;
		}
	}
}
