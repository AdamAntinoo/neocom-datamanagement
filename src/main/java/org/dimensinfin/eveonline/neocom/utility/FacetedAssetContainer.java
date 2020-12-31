package org.dimensinfin.eveonline.neocom.utility;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.asset.domain.LocationAssetContainer;

public class FacetedAssetContainer<F> extends LocationAssetContainer {
	private F facet;

	private FacetedAssetContainer() {
		super();
	}

	public F getFacet() {
		return this.facet;
	}

	// - B U I L D E R
	public static class Builder<F> {
		private FacetedAssetContainer<F> onConstruction;

		public Builder() {
			this.onConstruction = new FacetedAssetContainer<F>();
		}

		public FacetedAssetContainer.Builder<F> withFacet( final F facet ) {
			Objects.requireNonNull( facet );
			this.onConstruction.facet = facet;
			return this;
		}

		public FacetedAssetContainer<F> build() {
			Objects.requireNonNull( this.onConstruction.facet );
			return this.onConstruction;
		}
	}
}