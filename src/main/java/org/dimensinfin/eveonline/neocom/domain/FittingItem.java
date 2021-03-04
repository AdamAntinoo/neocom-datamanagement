package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.utility.NeoObjects;

public class FittingItem extends NeoComNode {
	private static final long serialVersionUID = -8828866322219643759L;
	private CharacterscharacterIdfittingsItems fittingDefinition;
	private EsiType fittingType;

	// - C O N S T R U C T O R S
	private FittingItem() {}

	// - G E T T E R S   &   S E T T E R S
	public CharacterscharacterIdfittingsItems.FlagEnum getFlag() {return this.fittingDefinition.getFlag();}

	public Integer getQuantity() {return this.fittingDefinition.getQuantity();}

	public Integer getTypeId() {return this.fittingDefinition.getTypeId();}

	public String getTypeName() {
		return this.fittingType.getName();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( this.fittingDefinition ).append( this.fittingType ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof FittingItem)) return false;
		final FittingItem that = (FittingItem) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) ).append( this.fittingDefinition, that.fittingDefinition )
				.append( this.fittingType, that.fittingType ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "fittingType", this.fittingType )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final FittingItem onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new FittingItem();
		}

		public FittingItem build() {
			NeoObjects.requireNonNull( this.onConstruction.fittingDefinition );
			NeoObjects.requireNonNull( this.onConstruction.fittingType );
			return this.onConstruction;
		}

		public FittingItem.Builder withFittingData( final CharacterscharacterIdfittingsItems fittingItem ) {
			this.onConstruction.fittingDefinition = NeoObjects.requireNonNull( fittingItem );
			return this;
		}

		public FittingItem.Builder withType( final EsiType fittingType ) {
			this.onConstruction.fittingType = Objects.requireNonNull( fittingType );
			return this;
		}
	}
}
