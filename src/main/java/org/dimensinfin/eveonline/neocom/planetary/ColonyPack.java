package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.UpdatableNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ColonyPack extends UpdatableNode {
	private static final long serialVersionUID = 1789734971700494969L;
	private static final long COLONY_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);
	private static final int CPU_20KMLINK = 22;
	private static final int POWER_20KMLINK = 16;

	private Integer pilotIdentifier; // Use this identifier to identify the owner of the planet when clicking on the item.
	private transient GetCharactersCharacterIdPlanetsPlanetIdOk planetFacilities; // The raw data for the colony structures
	private transient GetCharactersCharacterIdPlanets200Ok planet; // Some colony and planet data
	private transient GetUniversePlanetsPlanetIdOk planetData; // Planet location and generic planet data
	private List<IPlanetaryFacility> facilities; // NeoCom instances for the colony facilities with all extended data
	private ICommandCenterFacility commandCenter; // The colony Command center to be used to center the colony installations.
	private Float totalVolume = -1.0F;
	private Double totalValue = -1.0;

	// - C O N S T R U C T O R S
	private ColonyPack() {}

	// - G E T T E R S   &   S E T T E R S
	public String getName() {
		if (null != this.planetData)
			return this.planetData.getName();
		else return "-";
	}

	public Integer getPlanetId() {
		return planet.getPlanetId();
	}

	public GetCharactersCharacterIdPlanets200Ok.PlanetTypeEnum getPlanetType() {
		return this.planet.getPlanetType();
	}

	public int getInstallationsCount() {
		return this.facilities.size();
	}

	public Integer getUpgradeLevel() {
		return this.planet.getUpgradeLevel();
	}

	public int getPilotIdentifier() {
		return pilotIdentifier;
	}

	public List<IPlanetaryFacility> getFacilities() {
		return this.facilities;
	}

	public Float getTotalVolume() {
		return totalVolume;
	}

	public ColonyPack setTotalVolume( final Float totalVolume ) {
		this.totalVolume = totalVolume;
		return this;
	}

	public Double getTotalValue() {
		return totalValue;
	}

	public ColonyPack setTotalValue( final Double totalvalue ) {
		this.totalValue = totalvalue;
		return this;
	}

	/**
	 * Add the market values for all the resources on any of the colony facilities with storage.
	 */
	public Double getResourcesMarketValue() {
		Double resourcesMarketValue = 0.0;
		for (IPlanetaryFacility facility : this.getFacilities()) {
			final PlanetaryFacilityType facilityType = facility.getFacilityType();
			if ((facilityType == PlanetaryFacilityType.COMMAND_CENTER) ||
					(facilityType == PlanetaryFacilityType.STORAGE) ||
					(facilityType == PlanetaryFacilityType.LAUNCHPAD)) {
				resourcesMarketValue += ((IPlanetaryStorage) facility).getTotalValue();
			}
		}
		return resourcesMarketValue;
	}

	public Float getResourcesVolume() {
		Float resourcesVolume = 0.0F;
		for (IPlanetaryFacility facility : this.getFacilities()) {
			final PlanetaryFacilityType facilityType = facility.getFacilityType();
			if ((facilityType == PlanetaryFacilityType.COMMAND_CENTER) ||
					(facilityType == PlanetaryFacilityType.STORAGE) ||
					(facilityType == PlanetaryFacilityType.LAUNCHPAD)) {
				resourcesVolume += ((IPlanetaryStorage) facility).getTotalVolume();
			}
		}
		return resourcesVolume;
	}

	// - I U P D A T A B L E
	@Override
	public boolean needsRefresh() {
		if (this.getLastUpdateTime().plus(COLONY_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	// - I C O L L A B O R A T I O N

	/**
	 * Generate the list of facilities as the collaboration to the model list. Colonies will collaborate with the planet
	 * components that are the list of facilities.
	 * To be able to integrate this list into the application I should adapt the ESI data to the MVC model so a converter is required.
	 *
	 * @param variation an string to differentiate the environment. This will allow to generate different contents on different environments.
	 * @return the list of planet facilities.
	 */
	@Override
	public List<ICollaboration> collaborate2Model( final String variation ) {
		return new ArrayList<>();
	}

	// - C O R E
	@Override
	public int compareTo( final Object o ) {
		if (o instanceof ColonyPack)
			return this.pilotIdentifier - ((ColonyPack) o).pilotIdentifier;
		return 0;
	}

	protected void initialise() {
		this.commandCenter = this.accessCommandCenter();
		this.commandCenter.setUpgradeLevel(this.planet.getUpgradeLevel());
		this.commandCenter.setCpuInUse(this.calculateUsedCpu());
		this.commandCenter.setPowerInUse(this.calculateUsedPower());
		for (IPlanetaryFacility facility : this.facilities) {
			facility.setCommandCenterPosition(this.commandCenter.getGeoPosition());
		}
	}

	protected ICommandCenterFacility accessCommandCenter() {
		for (IPlanetaryFacility facility : this.facilities) {
			if (facility.getFacilityType() == PlanetaryFacilityType.COMMAND_CENTER)
				return (ICommandCenterFacility) facility;
		}
		throw new NeoComRuntimeException("There is no command center facility for this planet. Unable to process initialisation.");
	}

	protected int calculateUsedCpu() {
		int usedCpu = 0;
		if (null != this.planetFacilities) usedCpu += this.planetFacilities.getLinks().size() * CPU_20KMLINK;
		for (IPlanetaryFacility facility : this.facilities) {
			usedCpu += facility.getCpuUsage();
		}
		return usedCpu;
	}

	protected int calculateUsedPower() {
		int usedPower = 0;
		if (null != this.planetFacilities) usedPower += this.planetFacilities.getLinks().size() * POWER_20KMLINK;
		for (IPlanetaryFacility facility : this.facilities) {
			usedPower += facility.getPowerUsage();
		}
		return usedPower;
	}

	public Float getStorageInUse() {
		Float storage = 0.0F;
		for (IPlanetaryFacility facility : this.facilities) {
			if (facility instanceof IPlanetaryStorage)
				storage += ((IPlanetaryStorage) facility).getTotalVolume();
		}
		return storage;
	}

	public Float getStorageCapacity() {
		Float storage = 0.0F;
		for (IPlanetaryFacility facility : this.facilities) {
			if (facility instanceof IPlanetaryStorage)
				storage += ((IPlanetaryStorage) facility).getStorageCapacity();
		}
		return storage;
	}

	// - B U I L D E R
	public static class Builder {
		private ColonyPack onConstruction;

		public Builder() {
			this.onConstruction = new ColonyPack();
		}

		public Builder withPilotIdentifier( final int pilotIdentifier ) {
			this.onConstruction.pilotIdentifier = pilotIdentifier;
			return this;
		}

		public Builder withPlanetFacilities( final GetCharactersCharacterIdPlanetsPlanetIdOk planetFacilities ) {
			this.onConstruction.planetFacilities = planetFacilities;
			return this;
		}

		public Builder withColony( final GetCharactersCharacterIdPlanets200Ok colony ) {
			this.onConstruction.planet = colony;
			return this;
		}

		public Builder withPlanetData( final GetUniversePlanetsPlanetIdOk planetData ) {
			this.onConstruction.planetData = planetData;
			return this;
		}

		public Builder withFacilities( final List<IPlanetaryFacility> facilities ) {
			this.onConstruction.facilities = facilities;
			return this;
		}

		public ColonyPack build() {
			Objects.requireNonNull(this.onConstruction.pilotIdentifier);
//			Objects.requireNonNull(this.onConstruction.planetFacilities);
			Objects.requireNonNull(this.onConstruction.planet);
			Objects.requireNonNull(this.onConstruction.planetData);
			Objects.requireNonNull(this.onConstruction.facilities);
			if (this.onConstruction.facilities.size() < 1)
				throw new NeoComRuntimeException("There are no facilities on this planet.");
			// Do other initialisations like calculating the power and cpu used.
			this.onConstruction.initialise();
			return this.onConstruction;
		}
	}
}
