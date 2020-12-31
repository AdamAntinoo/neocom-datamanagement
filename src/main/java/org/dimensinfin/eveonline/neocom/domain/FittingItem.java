package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

public class FittingItem extends NeoComNode {
	private transient CharacterscharacterIdfittingsItems fittingDefinition;
	private transient NeoItem itemDetails;

	// - C O N S T R U C T O R S
	private FittingItem() {}

	// - G E T T E R S   &   S E T T E R S
	public CharacterscharacterIdfittingsItems.FlagEnum getFlag() {return fittingDefinition.getFlag();}

	public Integer getQuantity() {return this.fittingDefinition.getQuantity();}

	public Integer getTypeId() {return this.fittingDefinition.getTypeId();}

	@RequiresNetwork
	public String getTypeName() {
		Objects.requireNonNull( this.itemDetails );
		return this.itemDetails.getName();
	}

	private void downloadItem() {
		Objects.requireNonNull( this.fittingDefinition );
		this.itemDetails = new NeoItem( this.fittingDefinition.getTypeId() );
		Objects.requireNonNull( this.itemDetails );
	}

	// - B U I L D E R
	public static class Builder {
		private FittingItem onConstruction;

		public Builder() {
			this.onConstruction = new FittingItem();
		}

		public FittingItem build() {
			this.onConstruction.downloadItem();
			return this.onConstruction;
		}

		public FittingItem.Builder withFittingItem( final CharacterscharacterIdfittingsItems fittingItem ) {
			Objects.requireNonNull( fittingItem );
			this.onConstruction.fittingDefinition = fittingItem;
			return this;
		}
	}
}
