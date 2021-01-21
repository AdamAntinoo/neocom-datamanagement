package org.dimensinfin.eveonline.neocom.miningextraction.domain;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.annotation.RequiresNetwork;
import org.dimensinfin.eveonline.neocom.domain.EsiType;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;

/**
 * This class represents a mining extraction to be used for rendering of for other data management operations. It is constructed from the persisted
 * esi record and has also access to location data and esi item information.
 *
 * @author Adam Antinoo (adamantinoo.git@gmail.com)
 * @since 0.14.0
 */
@JsonIgnoreProperties
public class MiningExtraction {
	private long quantity = 0L;
	private String extractionDateName;
	private int extractionHour = 24; // The hour of the day for this extraction delta or 24 if this is the date aggregated value.
	private Integer ownerId; // The credential identifier of the pilot's extraction.
	private EsiType resourceItem;
	private SpaceSystem solarSystemLocation;

	// - C O N S T R U C T O R S
	private MiningExtraction() {
		super();
	}

	// - G E T T E R S   &   S E T T E R S
	public String getExtractionDateName() {
		return this.extractionDateName;
	}

	public int getExtractionHour() {
		return this.extractionHour;
	}

	// -  G E T T E R S   &   S E T T E R S
	public String getId() {
		return String.format( "".concat( this.extractionDateName ).concat( ":" )
				.concat( Integer.toString( this.extractionHour ) ), "%d02" ).concat( "-" )
				.concat( this.getLocationId().toString() ).concat( "-" )
				.concat( this.getTypeId().toString() ).concat( "-" )
				.concat( this.ownerId.toString() );
	}

	public Long getLocationId() {return this.solarSystemLocation.getLocationId();}

	public long getOwnerId() {
		return this.ownerId;
	}

	public String getPreviousHourId() {
		return "".concat( this.extractionDateName ).concat( ":" )
				.concat( String.format( Integer.toString( this.extractionHour - 1 ), "%d02" ) ).concat( "-" )
				.concat( this.getLocationId().toString() ).concat( "-" )
				.concat( this.getTypeId().toString() ).concat( "-" )
				.concat( this.ownerId.toString() );
	}

//	public double getPrice() {
//		return this.resourceItem.getPrice();
//	}

	public Long getQuantity() {
		return this.quantity;
	}

	@RequiresNetwork
	public String getResourceName() {return this.resourceItem.getName();}

	public SpaceSystem getSolarSystemLocation() {
		return this.solarSystemLocation;
	}

	public String getSystemName() {return this.solarSystemLocation.getSolarSystemName();}

	public Integer getTypeId() {return this.resourceItem.getTypeId();}

	public String getURLForItem() {
		return this.resourceItem.getTypeIconURL();
	}

	public double getVolume() {
		return this.resourceItem.getVolume();
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( this.getId() )
				.append( this.quantity )
				.append( this.extractionDateName )
				.append( this.extractionHour )
				.append( this.ownerId )
				.append( this.getTypeId() )
				.append( this.getLocationId() )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final MiningExtraction that = (MiningExtraction) o;
		return new EqualsBuilder()
				.append( this.getId(), that.getId() )
				.append( this.quantity, that.quantity )
				.append( this.extractionHour, that.extractionHour )
				.append( this.ownerId, that.ownerId )
				.append( this.extractionDateName, that.extractionDateName )
				.append( this.getTypeId(), that.getTypeId() )
				.append( this.getLocationId(), that.getLocationId() )
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "quantity", this.quantity )
				.append( "extractionDateName", this.extractionDateName )
				.append( "extractionHour", this.extractionHour )
				.append( "ownerId", this.ownerId )
				.append( "id", getId() )
				.append( "typeId", getTypeId() )
				.append( "locationId", getLocationId() )
				.append( "resourceName", getResourceName() )
				.append( "systemName", getSystemName() )
				.append( "URLForItem", getURLForItem() )
				.append( "volume", getVolume() )
//				.append( "price", getPrice() )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private final MiningExtraction onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new MiningExtraction();
		}

		/**
		 * The unique and special extraction identifier is created at this point and using the current extraction time. This will
		 * exclude proper
		 * testing so there is special code to create special identifier when the <code>onConstruction.extractionHour</code> is
		 * set.
		 */
		public MiningExtraction build() {
			Objects.requireNonNull( this.onConstruction.resourceItem );
			Objects.requireNonNull( this.onConstruction.solarSystemLocation );
			Objects.requireNonNull( this.onConstruction.extractionDateName );
			return this.onConstruction;
		}

		public MiningExtraction.Builder withExtractionDate( final String extractionDate ) {
			Objects.requireNonNull( extractionDate );
			this.onConstruction.extractionDateName = extractionDate;
			return this;
		}

		public Builder withExtractionHour( final Integer extractionHour ) {
			Objects.requireNonNull( extractionHour );
			this.onConstruction.extractionHour = extractionHour;
			return this;
		}

		public MiningExtraction.Builder withNeoItem( final EsiType resourceItem ) {
			Objects.requireNonNull( resourceItem );
			this.onConstruction.resourceItem = resourceItem;
			return this;
		}

		public MiningExtraction.Builder withOwnerId( final Integer ownerId ) {
			Objects.requireNonNull( ownerId );
			this.onConstruction.ownerId = ownerId;
			return this;
		}

		public MiningExtraction.Builder withQuantity( final Long quantity ) {
			if (null == quantity) this.onConstruction.quantity = 0L;
			this.onConstruction.quantity = Objects.requireNonNull( quantity );
			return this;
		}

		public MiningExtraction.Builder withSpaceSystem( final SpaceSystem solarSystemLocation ) {
			Objects.requireNonNull( solarSystemLocation );
			this.onConstruction.solarSystemLocation = solarSystemLocation;
			return this;
		}
	}
}
