package org.dimensinfin.eveonline.neocom.domain;


import java.util.concurrent.TimeUnit;

import com.annimon.stream.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pilot extends NeoComNode {
	private static final long PILOT_CACHE_TIME = TimeUnit.HOURS.toMillis( 12 );

	private Integer pilotId;
	private transient GetCharactersCharacterIdOk characterPublicData;
	private transient GetUniverseRaces200Ok raceData;
	private transient GetUniverseAncestries200Ok ancestryData;
	private transient GetUniverseBloodlines200Ok bloodlineData;

	// - C O N S T R U C T O R S
	private Pilot() {
	}

	// - G E T T E R S   &   S E T T E R S
	public int getPilotId() {
		return this.pilotId;
	}

	public GetUniverseRaces200Ok getRace() {
		return this.raceData;
	}

	public GetUniverseAncestries200Ok getAncestry() {
		return this.ancestryData;
	}

	public GetUniverseBloodlines200Ok getBloodline() {
		return this.bloodlineData;
	}

	public Pilot setCharacterPublicData( final GetCharactersCharacterIdOk characterPublicData ) {
		this.characterPublicData = characterPublicData;
		return this;
	}

	public Pilot setRaceData( final GetUniverseRaces200Ok raceData ) {
		this.raceData = raceData;
		return this;
	}

	public Pilot setAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
		this.ancestryData = ancestryData;
		return this;
	}

	public Pilot setBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
		this.bloodlineData = bloodlineData;
		return this;
	}

	// - V I R T U A L S
	public String getUrl4Icon() {
		return "https://image.eveonline.com/Character/" + this.pilotId + "_256.jpg";
	}

	// - D E L E G A T E D
	public int getCorporationId() {
		return this.characterPublicData.getCorporationId();
	}

	public DateTime getBirthday() {
		return this.characterPublicData.getBirthday();
	}

	public Integer getRaceId() {
		return this.characterPublicData.getRaceId();
	}

	public String getRaceName() {
		if (null != this.raceData)
			return this.raceData.getName();
		else return "-";
	}

	public Integer getAncestryId() {
		return this.characterPublicData.getAncestryId();
	}

	public Integer getBloodlineId() {
		return this.characterPublicData.getBloodlineId();
	}

	public String getDescription() {
		return this.characterPublicData.getDescription();
	}

	public String getGender() {
		return this.characterPublicData.getGender().name();
	}

	public String getName() {
		return this.characterPublicData.getName();
	}

	public Float getSecurityStatus() {
		return this.characterPublicData.getSecurityStatus();
	}

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Pilot pilot = (Pilot) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.pilotId, pilot.pilotId )
				.append( this.characterPublicData, pilot.characterPublicData )
				.append( this.raceData, pilot.raceData )
				.append( this.bloodlineData, pilot.bloodlineData )
				.append( this.ancestryData, pilot.ancestryData )
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.pilotId )
				.append( this.characterPublicData )
				.append( this.raceData )
				.append( this.bloodlineData )
				.append( this.ancestryData )
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "pilotId", pilotId )
				.append( "characterPublicData", characterPublicData )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private Pilot onConstruction;

		public Builder() {
			this.onConstruction = new Pilot();
		}

		public Pilot.Builder withPilotIdentifier( final Integer pilotIdentifier ) {
			Objects.requireNonNull( pilotIdentifier );
			this.onConstruction.pilotId = pilotIdentifier;
			return this;
		}

		public Pilot.Builder withCharacterPublicData( final GetCharactersCharacterIdOk characterPublicData ) {
			if (null != characterPublicData)
				this.onConstruction.characterPublicData = characterPublicData;
			return this;
		}

		public Pilot.Builder withRaceData( final GetUniverseRaces200Ok raceData ) {
			if (null != raceData)
				this.onConstruction.raceData = raceData;
			return this;
		}

		public Pilot.Builder withAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
			if (null != ancestryData)
				this.onConstruction.ancestryData = ancestryData;
			return this;
		}

		public Pilot.Builder withBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
			if (null != bloodlineData)
				this.onConstruction.bloodlineData = bloodlineData;
			return this;
		}

		public Pilot build() {
			Objects.requireNonNull( this.onConstruction.pilotId );
			Objects.requireNonNull( this.onConstruction.characterPublicData );
			return this.onConstruction;
		}
	}
}
