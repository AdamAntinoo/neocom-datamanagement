package org.dimensinfin.eveonline.neocom.domain.container;

import java.util.Objects;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.ExpandableContainer;

/**
 * This class of containers have two faces. By one side the type of the elements contained. This is a generic with the generic
 * Container interface. At the same time the container is expandable so it should implement the IExpandable interface to export
 * the methods to manage the expansion state.
 *
 * The second side is the way the MVC render system sees of the instance. It is not a generic container but a faceted one that
 * internally has a model instance that is the '<b>visible</b>' interface of the assembly. So this way I can have a
 * <code>SpaceRegion</code>
 * faceted container that behaves as a Region but that internally has a set of other containers like Stations or Structures or
 * even SolarSystem contents.
 *
 * The <b>F</b> stands for the faceted type of the assembly and the <b>C</b> for the contents type.
 */
public class FacetedExpandableContainer<F extends ICollaboration, C extends ICollaboration> extends ExpandableContainer<C> {
	private F facet;

	protected FacetedExpandableContainer() {}

	public F getFacet() {
		return this.facet;
	}

	// - B U I L D E R
	public static class Builder<F extends ICollaboration, C extends ICollaboration> {
		private FacetedExpandableContainer onConstruction;

		public Builder() {
			this.onConstruction = new FacetedExpandableContainer<F, C>();
		}

		public FacetedExpandableContainer.Builder withFacet( final F facet ) {
			Objects.requireNonNull( facet );
			this.onConstruction.facet = facet;
			return this;
		}

		public FacetedExpandableContainer build() {
			Objects.requireNonNull( this.onConstruction.facet );
			return this.onConstruction;
		}
	}
}
