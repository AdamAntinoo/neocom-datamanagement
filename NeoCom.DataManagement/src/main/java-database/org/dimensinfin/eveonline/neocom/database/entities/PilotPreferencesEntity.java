package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "neocom.PilotPreferences")
public class PilotPreferencesEntity {
	@Id
	@NotNull(message = "PilotPreference unique UUID 'id' is a mandatory field and cannot be null.")
	@DatabaseField(id = true, index = true)
	private UUID id;
	@NotNull(message = "PilotPreference pilot identifier is a mandatory field and cannot be null.")
	@DatabaseField(index = true)
	private Integer pilotId;
	@NotNull(message = "PilotPreference name is a mandatory field and cannot be null.")
	@DatabaseField
	private String name;
	@DatabaseField
	private String stringValue;
	@DatabaseField
	private Double numberValue;

	// - C O N S T R U C T O R S
	private PilotPreferencesEntity() {}

	// - G E T T E R S   &   S E T T E R S
	public UUID getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Double getNumberValue() {
		return this.numberValue;
	}

	public Integer getPilotId() {
		return this.pilotId;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	// - B U I L D E R
	public static class Builder {
		private final PilotPreferencesEntity onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new PilotPreferencesEntity();
		}

		public PilotPreferencesEntity build() {
			this.onConstruction.id = UUID.randomUUID();
			Objects.requireNonNull( this.onConstruction.pilotId );
			Objects.requireNonNull( this.onConstruction.name );
			return this.onConstruction;
		}

		public PilotPreferencesEntity.Builder withPilotId( final int pilotId ) {
			this.onConstruction.pilotId = pilotId;
			return this;
		}

		public PilotPreferencesEntity.Builder withPreferenceName( final String name ) {
			if (null != name) this.onConstruction.name = name;
			return this;
		}

		public PilotPreferencesEntity.Builder withPreferenceValue( final Double value ) {
			if (null != value) this.onConstruction.numberValue = value;
			return this;
		}

		public PilotPreferencesEntity.Builder withPreferenceValue( final String value ) {
			if (null != value) this.onConstruction.stringValue = value;
			return this;
		}
	}
}