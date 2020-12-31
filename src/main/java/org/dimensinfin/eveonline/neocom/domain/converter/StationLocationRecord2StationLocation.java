package org.dimensinfin.eveonline.neocom.domain.converter;

public class StationLocationRecord2StationLocation {
	private StationLocationRecord2StationLocation() {}

	// - B U I L D E R
	public static class Builder {
		private StationLocationRecord2StationLocation onConstruction;

		public Builder() {
			this.onConstruction = new StationLocationRecord2StationLocation();
		}

		public StationLocationRecord2StationLocation build() {
			return this.onConstruction;
		}
	}
}
