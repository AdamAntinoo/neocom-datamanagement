package org.dimensinfin.eveonline.neocom.character.domain;

import java.util.Objects;

import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

/**
 * Describes the data structures that can be accessed for any pilot identifier requested without needing any authenticated ESI endpoint.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.20.0
 */
public class PublicPilotV1 extends NeoComNode {
	public static final String PILOT_ICON_URL_PREFIX = "https://image.eveonline.com/Character/";
	public static final String PILOT_ICON_URL_SUFFIX = "_256.jpg";

	private static final long serialVersionUID = -3178716949754945800L;
	/**
	 * The unique game character identifier assigned at character creation that is going to be used to identify this Pilot.
	 */
	protected Integer pilotId;
	protected GetCharactersCharacterIdOk pilotPublicData;
	protected PublicCorporationV1 corporation;
	protected GetUniverseRaces200Ok raceData;
	protected GetUniverseAncestries200Ok ancestryData;
	protected GetUniverseBloodlines200Ok bloodlineData;

	// - C O N S T R U C T O R S
	protected PublicPilotV1() {}

	// - G E T T E R S   &   S E T T E R S
	public GetUniverseAncestries200Ok getAncestryData() {
		return this.ancestryData;
	}

	public DateTime getBirthday() {return this.pilotPublicData.getBirthday();}

	public GetUniverseBloodlines200Ok getBloodlineData() {
		return this.bloodlineData;
	}

	public Integer getCorporationId() {
		return this.corporation.getCorporationId();
	}

	public String getDescription() {return this.pilotPublicData.getDescription();}

	public Integer getFactionId() {return this.pilotPublicData.getFactionId();}

	public GetCharactersCharacterIdOk.GenderEnum getGender() {return this.pilotPublicData.getGender();}

	public String getName() {return this.pilotPublicData.getName();}

	public Integer getPilotId() {
		return this.pilotId;
	}

	public GetCharactersCharacterIdOk getPilotPublicData() {
		return this.pilotPublicData;
	}

	public GetUniverseRaces200Ok getRaceData() {
		return this.raceData;
	}

	public Float getSecurityStatus() {return this.pilotPublicData.getSecurityStatus();}

	public String getTitle() {return this.pilotPublicData.getTitle();}

	// - V I R T U A L S
	public String getUrl4Icon() {
		return PILOT_ICON_URL_PREFIX + this.pilotId + PILOT_ICON_URL_SUFFIX;
	}

	// - B U I L D E R
	public static class Builder<T extends PublicPilotV1, B extends PublicPilotV1.Builder> {
		private T onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
		}

		public PublicPilotV1 build() {
			return this.getActual();
		}

		public B withAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
			if (null != ancestryData) this.getActual().ancestryData = ancestryData;
			return this.getActualBuilder();
		}

		public B withBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
			if (null != bloodlineData) this.getActual().bloodlineData = bloodlineData;
			return this.getActualBuilder();
		}

		public B withCorporation( final PublicCorporationV1 corporation ) {
			this.getActual().corporation = Objects.requireNonNull( corporation );
			return this.getActualBuilder();
		}

		public B withPilotId( final Integer pilotId ) {
			this.getActual().pilotId = Objects.requireNonNull( pilotId );
			return this.getActualBuilder();
		}

		public B withPilotPublicData( final GetCharactersCharacterIdOk pilotPublicData ) {
			this.getActual().pilotPublicData = Objects.requireNonNull( pilotPublicData );
			return this.getActualBuilder();
		}

		public B withRaceData( final GetUniverseRaces200Ok raceData ) {
			if (null != raceData) this.getActual().raceData = raceData;
			return this.getActualBuilder();
		}

		protected T getActual() {
			if (null == this.onConstruction)
				this.onConstruction = (T) new PublicPilotV1();
			return this.onConstruction;
		}

		protected B getActualBuilder() {
			return (B) this;
		}
	}
}