package org.dimensinfin.eveonline.neocom.character.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdShipOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

/**
 * This is a full featured Pilot core data record. Adds some data fields that will require new accesses to the ESI backend and some of them will
 * also require authenticated endpoint calls.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class PilotV1 extends PublicPilotV1 {
	private static final long serialVersionUID = -7085010594597508212L;
	private long totalSkillpoints = 0;
	private double walletBalance = 0.0;
	private GetCharactersCharacterIdShipOk currentShip;
	private EsiType currentShipType;
	private SpaceLocation lastKnownLocation;

	// - C O N S T R U C T O R S
	private PilotV1() {}

	// - G E T T E R S   &   S E T T E R S
	public GetCharactersCharacterIdShipOk getCurrentShip() {
		return this.currentShip;
	}

	public EsiType getCurrentShipType() {
		return this.currentShipType;
	}

	public SpaceLocation getLastKnownLocation() {
		return this.lastKnownLocation;
	}

	public long getTotalSkillpoints() {
		return this.totalSkillpoints;
	}

	public double getWalletBalance() {
		return this.walletBalance;
	}

	// - B U I L D E R
	public static class Builder  {
		private PilotV1 onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = this.getActual();
		}

		public PilotV1 build() {
			Objects.requireNonNull( this.getActual().pilotId );
			Objects.requireNonNull( this.getActual().pilotPublicData );
			Objects.requireNonNull( this.getActual().corporation );
			Objects.requireNonNull( this.getActual().lastKnownLocation );
			Objects.requireNonNull( this.getActual().currentShip );
			Objects.requireNonNull( this.getActual().currentShipType );
			return getActual();
		}

		private PilotV1 getActual() {
			if (null == this.onConstruction)
				this.onConstruction = new PilotV1();
			return this.onConstruction;
		}

		private PilotV1.Builder getActualBuilder() {
			return this;
		}

		public PilotV1.Builder withCurrentShip( final GetCharactersCharacterIdShipOk currentShip ) {
			this.getActual().currentShip = Objects.requireNonNull( currentShip );
			return this;
		}

		public PilotV1.Builder withCurrentShipType( final EsiType currentShipType ) {
			this.getActual().currentShipType = Objects.requireNonNull( currentShipType );
			return this;
		}
		public PilotV1.Builder withAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
			if (null != ancestryData) this.getActual().ancestryData = ancestryData;
			return this;
		}

		public PilotV1.Builder withBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
			if (null != bloodlineData) this.getActual().bloodlineData = bloodlineData;
			return this.getActualBuilder();
		}

		public PilotV1.Builder withCorporation( final PublicCorporationV1 corporation ) {
			this.getActual().corporation = Objects.requireNonNull( corporation );
			return this.getActualBuilder();
		}

		public PilotV1.Builder withPilotId( final Integer pilotId ) {
			this.getActual().pilotId = Objects.requireNonNull( pilotId );
			return this.getActualBuilder();
		}

		public PilotV1.Builder withPilotPublicData( final GetCharactersCharacterIdOk pilotPublicData ) {
			this.getActual().pilotPublicData = Objects.requireNonNull( pilotPublicData );
			return this.getActualBuilder();
		}

		public PilotV1.Builder withRaceData( final GetUniverseRaces200Ok raceData ) {
			if (null != raceData) this.getActual().raceData = raceData;
			return this.getActualBuilder();
		}

		public PilotV1.Builder withLastKnownLocation( final SpaceLocation lastKnownLocation ) {
			this.getActual().lastKnownLocation = Objects.requireNonNull( lastKnownLocation );
			return this;
		}

		public PilotV1.Builder withTotalSkillPoints( final Long totalSkillpoints ) {
			if (null != totalSkillpoints)
				this.onConstruction.totalSkillpoints = totalSkillpoints;
			return this;
		}

		public PilotV1.Builder withWalletBalance( final Double walletBalance ) {
			if (null != walletBalance)
				this.onConstruction.walletBalance = walletBalance;
			return this;
		}
	}
}
