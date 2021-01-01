package org.dimensinfin.eveonline.neocom.planetary.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.planetary.PlanetaryResourceTierType;

public class PlanetaryResource extends Resource {
	// - C O N S T R U C T O R S
	@Deprecated
	public PlanetaryResource( final int typeId ) {
		super( typeId );
	}

	protected PlanetaryResource() {}

	// - G E T T E R S   &   S E T T E R S
	public PlanetaryResourceTierType getTier() {
		return PlanetaryResourceTierType.searchTierType4Group( this.getGroupName() );
	}

	// - C O R E
	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "resource", super.toString() )
				.append( "tier", this.getTier().name() )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final PlanetaryResource onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new PlanetaryResource();
		}

		public PlanetaryResource build() {
			Objects.requireNonNull( this.onConstruction.item );
			Objects.requireNonNull( this.onConstruction.group );
			Objects.requireNonNull( this.onConstruction.category );
			return this.onConstruction;
		}

		public PlanetaryResource.Builder withCategory( final GetUniverseCategoriesCategoryIdOk category ) {
			this.onConstruction.category = Objects.requireNonNull( category );
			return this;
		}

		public PlanetaryResource.Builder withGroup( final GetUniverseGroupsGroupIdOk group ) {
			this.onConstruction.group = Objects.requireNonNull( group );
			return this;
		}

		public PlanetaryResource.Builder withItemType( final GetUniverseTypesTypeIdOk item ) {
			this.onConstruction.item = Objects.requireNonNull( item );
			return this;
		}

		public PlanetaryResource.Builder withTypeId( final Integer typeId ) {
			this.onConstruction.typeId = Objects.requireNonNull( typeId );
			return this;
		}
	}
}
