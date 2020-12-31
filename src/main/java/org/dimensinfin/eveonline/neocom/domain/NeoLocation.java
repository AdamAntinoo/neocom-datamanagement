package org.dimensinfin.eveonline.neocom.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * This new development will try to use the principle of single responsibility. The Locations will be classified into spaces
 * and have at least two variants, related to space location and to station locations.
 */
public class NeoLocation implements Serializable {
	public enum NeoLocationType{
		UNKNOWN, UNREACHEABLE, SPACE;
	}
	private static final long serialVersionUID = 469762593747237911L;
	private LocationIdentifier identifier;
	private NeoLocationType type=NeoLocationType.UNKNOWN;

	private NeoLocation() {
	}

	// - B U I L D E R
	public static class Builder {
		private NeoLocation onConstruction;

		public Builder() {
			this.onConstruction = new NeoLocation();
		}

		public NeoLocation.Builder withLocationIdentifier( final LocationIdentifier identifier ) {
			Objects.requireNonNull( identifier );
			this.onConstruction.identifier = identifier;
			return this;
		}

		public NeoLocation build() {
			Objects.requireNonNull( this.onConstruction.identifier );
			return this.onConstruction;
		}
	}
}