package org.dimensinfin.eveonline.neocom.miningextraction.domain;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.industry.domain.Resource;

public class DailyExtractionResourcesContainer extends NeoComNode {
	private static final long serialVersionUID = 7532324977887962789L;
	private List<Resource> resources = new ArrayList<>();

	// - I C O L L A B O R A T I O N
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		return new ArrayList<>( this.resources );
	}

	// - B U I L D E R
	public static class Builder {
		private final DailyExtractionResourcesContainer onConstruction;

// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new DailyExtractionResourcesContainer();
		}

		public DailyExtractionResourcesContainer build() {
			return this.onConstruction;
		}

		public Builder withResourceList( final List<Resource> resources ) {
			this.onConstruction.resources = resources;
			return this;
		}
	}
}
