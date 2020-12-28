package org.dimensinfin.eveonline.neocom.domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.asset.domain.EsiAssets200Ok;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

/**
 * The new NeoLocation uses a multipart identifier to be able to select between space locations, other spaces and also to reach
 * the definition of the structure hangars already defined on the flags.
 */
public class LocationIdentifier {
	private static final Map<EsiAssets200Ok.LocationFlagEnum, Integer> officeContainerLocationFlags =
			new EnumMap<>( EsiAssets200Ok.LocationFlagEnum.class );

	static {
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG1, 1 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG2, 2 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG3, 3 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG4, 4 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG5, 5 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG6, 6 );
		officeContainerLocationFlags.put( EsiAssets200Ok.LocationFlagEnum.CORPSAG7, 7 );
	}

	private Long spaceIdentifier;
	private EsiAssets200Ok.LocationFlagEnum locationFlag;
	private EsiAssets200Ok.LocationTypeEnum locationType = EsiAssets200Ok.LocationTypeEnum.OTHER;
	private LocationIdentifierType type = LocationIdentifierType.UNKNOWN;

	private LocationIdentifier() { }

	public Long getSpaceIdentifier() {
		return this.spaceIdentifier;
	}

	public EsiAssets200Ok.LocationFlagEnum getLocationFlag() {
		return this.locationFlag;
	}

	public LocationIdentifierType getType() {
		return this.type;
	}

	public LocationIdentifier setType( final LocationIdentifierType type ) {
		this.type = type;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "spaceIdentifier", this.spaceIdentifier )
				.append( "locationFlag", this.locationFlag )
				.append( "locationType", this.locationType )
				.append( "type", this.type )
				.toString();
	}

	private void classifyLocation() {
		if (this.spaceIdentifier < 20E6) {
			this.type = LocationIdentifierType.REGION;
			return;
		}
		if (this.spaceIdentifier < 30E6) {
			this.type = LocationIdentifierType.CONSTELLATION;
			return;
		}
		if (this.spaceIdentifier < 40E6) {
			this.type = LocationIdentifierType.SOLAR_SYSTEM;
			return;
		}
		if (this.spaceIdentifier < 61E6) {
			this.type = LocationIdentifierType.STATION;
			return;
		}
		if (null == this.locationType) this.locationType = EsiAssets200Ok.LocationTypeEnum.OTHER;
		if (this.locationType == EsiAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM)
			this.type = LocationIdentifierType.SOLAR_SYSTEM;
		if (this.locationType == EsiAssets200Ok.LocationTypeEnum.STATION)
			this.type = LocationIdentifierType.STATION;
		// Other types of locations. Use the id to extract the range
		if (officeContainerLocationFlags.containsKey( this.locationFlag ))
			this.type = LocationIdentifierType.OFFICE;
		if (this.locationType == EsiAssets200Ok.LocationTypeEnum.OTHER) {
			if (this.detectIfShipFittingSlot()) this.type = LocationIdentifierType.SHIP;
			if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.UNLOCKED)
				this.type = LocationIdentifierType.CONTAINER;
			if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.CARGO)
				this.type = LocationIdentifierType.SHIP;
		}
	}

	private boolean detectIfShipFittingSlot() {
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.DRONEBAY) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT0) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT1) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT2) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT3) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT4) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT5) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT6) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.HISLOT7) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT0) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT1) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT2) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT3) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT4) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT5) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT6) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.MEDSLOT7) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT0) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT1) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT2) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT3) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT4) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT5) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT6) return true;
		if (this.locationFlag == EsiAssets200Ok.LocationFlagEnum.LOSLOT7) return true;
		return false;
	}

	// - B U I L D E R
	public static class Builder {
		private LocationIdentifier onConstruction;

		public Builder() {
			this.onConstruction = new LocationIdentifier();
		}

		public LocationIdentifier build() {
			Objects.requireNonNull( this.onConstruction.spaceIdentifier );
			this.onConstruction.classifyLocation();
			return this.onConstruction;
		}

		public LocationIdentifier.Builder withLocationFlag( final EsiAssets200Ok.LocationFlagEnum locationFlag ) {
			this.onConstruction.locationFlag = locationFlag;
			return this;
		}

		public LocationIdentifier.Builder withLocationType( final EsiAssets200Ok.LocationTypeEnum locationType ) {
			this.onConstruction.locationType = locationType;
			return this;
		}

		public LocationIdentifier.Builder withSpaceIdentifier( final Long spaceIdentifier ) {
			Objects.requireNonNull( spaceIdentifier );
			this.onConstruction.spaceIdentifier = spaceIdentifier;
			return this;
		}
	}
}
