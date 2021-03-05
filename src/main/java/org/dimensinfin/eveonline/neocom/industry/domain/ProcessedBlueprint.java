package org.dimensinfin.eveonline.neocom.industry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.market.MarketData;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;

public class ProcessedBlueprint implements Serializable {
	private static final long serialVersionUID = 1702676060995319018L;
	private int blueprintTypeId;
	private PricedResource blueprint;
	private PricedResource output;
	private MarketData outputMarketData;
	private List<PricedResource> bom = new ArrayList<>();

	// - C O N S T R U C T O R S
	private ProcessedBlueprint() {}

	// - G E T T E R S   &   S E T T E R S
	public PricedResource getBlueprint() {
		return this.blueprint;
	}

	public int getBlueprintTypeId() {
		return this.blueprintTypeId;
	}

	public List<PricedResource> getBom() {
		return this.bom;
	}

	public double getManufactureCost() {
		return this.bom.stream().mapToDouble( resource -> resource.getMarketPrice() ).sum();
	}

	public PricedResource getOutput() {
		return this.output;
	}

	public MarketData getOutputMarketData() {
		return this.outputMarketData;
	}

	// - B U I L D E R
	public static class Builder {
		private final ProcessedBlueprint onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new ProcessedBlueprint();
		}

		public ProcessedBlueprint build() {
			NeoObjects.requireNonNull( this.onConstruction.blueprint );
			NeoObjects.requireNonNull( this.onConstruction.output );
			NeoObjects.requireNonNull( this.onConstruction.outputMarketData );
			return this.onConstruction;
		}

		public ProcessedBlueprint.Builder withBOM( final List<PricedResource> resources ) {
			this.onConstruction.bom = NeoObjects.requireNonNull( resources );
			return this;
		}

		public ProcessedBlueprint.Builder withBlueprint( final PricedResource blueprint ) {
			this.onConstruction.blueprint = NeoObjects.requireNonNull( blueprint );
			this.onConstruction.blueprintTypeId = this.onConstruction.blueprint.getTypeId();
			return this;
		}

		public ProcessedBlueprint.Builder withOutput( final PricedResource output ) {
			this.onConstruction.output = NeoObjects.requireNonNull( output );
			return this;
		}

		public ProcessedBlueprint.Builder withOutputMarketData( final MarketData outputMarketData ) {
			this.onConstruction.outputMarketData = NeoObjects.requireNonNull( outputMarketData );
			return this;
		}
	}
}