package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Database entities are the data views that are stored on repositories. Usually they require transformation to and from the storage. This class
 * represents a mineral extraction delta.
 *
 * During mining the Esi system aggregates mineral extractions on records per system per date. So in the api results we found the complete
 * extractions for a resource and system for a past date and the mined resource per location of the current date.
 *
 * Two list of extractions at different times of the same date will keep past dates records equal while today extractions will reflect the
 * new aggregated extraction for a mineral per system so the difference with the first result will give the delta extractions between the two
 * record times.
 *
 * This class represents the database entity to store the ESI character's mining extractions. That data are records that are kept for 30 days and
 * contain the incremental values of what was mined on a data for a particular resource on a determinate solar system.
 * The records are stored on the database by creating an special unique identifier that is generated from the esi read data.
 *
 * The date is obtained from the esi record but the processing hour is set from the current creation time if the record is from today'ss date or
 * fixed to 24 if the record has a date different from today.
 *
 * Records can be read at any time and current date records values can increase if there is more mining done since the last esi data request. So
 * our system will record quantities by the hour and later calculate the deltas so the record will represent the estimated quantity mined on that
 * hour and not the aggregated quantity mined along the day.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.19.0
 */
@DatabaseTable(tableName = "neocom.MiningExtractions")
public class MiningExtractionEntity extends UpdatableEntity {
	public static final String EXTRACTION_DATE_FORMAT = "YYYY-MM-dd";
	private static final long serialVersionUID = -3786687847087826269L;
	// - F I E L D - S E C T I O N
	@Id
	@DatabaseField(id = true)
	@Column(name = "id", updatable = false, nullable = false)
	private String id = "YYYY-MM-DD:HH-SYSTEMID-TYPEID-OWNERID";
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	@Column(name = "typeId", nullable = false)
	private int typeId; // The eve type identifier for the resource being extracted
	@DatabaseField
	@Column(name = "solarSystemId", nullable = false)
	private int solarSystemId; // The solar system where the extraction is recorded.
	@DatabaseField
	@Column(name = "quantity", nullable = false)
	private long quantity = 0;
	@DatabaseField(dataType = DataType.STRING, canBeNull = false, index = true)
	@Column(name = "extractionDateName", nullable = false)
	private String extractionDateName;
	@DatabaseField
	private int extractionHour = 24; // The hour of the day for this extraction delta or 24 if this is the date aggregated value.
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true)
	@Column(name = "ownerId", nullable = false)
	private int ownerId; // The credential identifier of the pilot's extraction.

	private MiningExtractionEntity() {}

	// - G E T T E R S   &   S E T T E R S
	public String getId() {
		return this.id;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public Integer getSolarSystemId() {
		return this.solarSystemId;
	}

	public Long getQuantity() {
		return this.quantity;
	}

	public MiningExtractionEntity setQuantity( final long quantity ) {
		this.quantity = quantity;
		return this;
	}

	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public Integer getExtractionHour() {
		return this.extractionHour;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final MiningExtractionEntity that = (MiningExtractionEntity) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( this.typeId, that.typeId )
				.append( this.solarSystemId, that.solarSystemId )
				.append( this.quantity, that.quantity )
				.append( this.extractionHour, that.extractionHour )
				.append( this.ownerId, that.ownerId )
				.append( this.id, that.id )
				.append( this.extractionDateName, that.extractionDateName )
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( this.id )
				.append( this.typeId )
				.append( this.solarSystemId )
				.append( this.quantity )
				.append( this.extractionDateName )
				.append( this.extractionHour )
				.append( this.ownerId )
				.toHashCode();
	}

	// - B U I L D E R
	public static class Builder {
		private MiningExtractionEntity onConstruction;

		public Builder() {
			this.onConstruction = new MiningExtractionEntity();
		}

		public MiningExtractionEntity build() {
			return this.onConstruction;
		}

		public MiningExtractionEntity.Builder withExtractionDateName( final String extractionDateName ) {
			Objects.requireNonNull( extractionDateName );
			this.onConstruction.extractionDateName = extractionDateName;
			return this;
		}

		public MiningExtractionEntity.Builder withExtractionHour( final Integer extractionHour ) {
			Objects.requireNonNull( extractionHour );
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

		public MiningExtractionEntity.Builder withId( final String id ) {
			Objects.requireNonNull( id );
			this.onConstruction.id = id;
			return this;
		}

		public MiningExtractionEntity.Builder withOwnerId( final Integer ownerId ) {
			Objects.requireNonNull( ownerId );
			this.onConstruction.ownerId = ownerId;
			return this;
		}

		public MiningExtractionEntity.Builder withQuantity( final Long quantity ) {
			Objects.requireNonNull( quantity );
			this.onConstruction.quantity = quantity;
			return this;
		}

		public MiningExtractionEntity.Builder withSolarSystemId( final Integer solarSystemId ) {
			Objects.requireNonNull( solarSystemId );
			this.onConstruction.solarSystemId = solarSystemId;
			return this;
		}

		public MiningExtractionEntity.Builder withTypeId( final Integer typeId ) {
			Objects.requireNonNull( typeId );
			this.onConstruction.typeId = typeId;
			return this;
		}
	}
}
