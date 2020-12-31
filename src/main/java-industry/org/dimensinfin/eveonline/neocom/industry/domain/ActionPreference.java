package org.dimensinfin.eveonline.neocom.industry.domain;

public class ActionPreference {
	private ActionPreference() {}

	// - B U I L D E R
	public static class Builder {
		private final ActionPreference onConstruction;

		public Builder() {
			this.onConstruction = new ActionPreference();
		}

		public ActionPreference build() {
			return this.onConstruction;
		}
	}
}