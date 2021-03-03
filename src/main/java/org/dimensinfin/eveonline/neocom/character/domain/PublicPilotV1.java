package org.dimensinfin.eveonline.neocom.character.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 ).appendSuper( super.hashCode() ).append( this.pilotId ).append( this.pilotPublicData ).append(
				this.corporation )
				.append( this.raceData ).append( this.ancestryData ).append( this.bloodlineData ).toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof PublicPilotV1)) return false;
		final PublicPilotV1 that = (PublicPilotV1) o;
		return new EqualsBuilder().appendSuper( super.equals( o ) ).append( this.pilotId, that.pilotId )
				.append( this.pilotPublicData, that.pilotPublicData ).append( this.corporation, that.corporation ).append( this.raceData, that.raceData )
				.append( this.ancestryData, that.ancestryData ).append( this.bloodlineData, that.bloodlineData ).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "pilotId", this.pilotId )
				.append( "corporation", this.corporation )
				.toString();
	}

	// - B U I L D E R
	public static class Builder<T extends PublicPilotV1, B extends PublicPilotV1.Builder> {
		private T onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			super();
		}

		public T build() {
			Objects.requireNonNull( this.getActual() );
			Objects.requireNonNull( this.getActual().pilotId );
			Objects.requireNonNull( this.getActual().pilotPublicData );
			Objects.requireNonNull( this.getActual().corporation );
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

		private T getActual() {
			if (null == this.onConstruction)
				this.onConstruction = (T) new PublicPilotV1();
			return this.onConstruction;
		}

		private B getActualBuilder() {
			return (B) this;
		}
	}
}