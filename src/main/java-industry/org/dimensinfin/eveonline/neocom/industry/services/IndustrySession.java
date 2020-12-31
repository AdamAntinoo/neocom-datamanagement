package org.dimensinfin.eveonline.neocom.industry.services;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.domain.Pilot;
import org.dimensinfin.eveonline.neocom.industry.domain.ActionPreference;

/**
 * This class will collect all the data or services that would be used by the Industry Transformation Processes. Applying the Principle Of Single
 * Responsibility I will separate configuration from execution so it can be easily tested and replaced on other environments if required.
 *
 * The Industry Session accepts the Pilot credential data, the Corporation identifier to access corporation preferences and the list of Actions
 * that apply to the next transformation processes. With the separation from the container functionality from the action to obtain that
 * configuration the class can be mocked and the configuration can be controlled.
 */
public class IndustrySession {
	private Pilot pilot;
	private List<ActionPreference> actionPreferences = new ArrayList<>();

	private IndustrySession() {}

	// - B U I L D E R
	public static class Builder {
		private final IndustrySession onConstruction;

		public Builder() {
			this.onConstruction = new IndustrySession();
		}

		public IndustrySession build() {
			return this.onConstruction;
		}
	}
}