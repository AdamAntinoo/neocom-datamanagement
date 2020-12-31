package org.dimensinfin.eveonline.neocom.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;

@DatabaseTable(tableName = "Properties")
public class Property extends UpdatableEntity {
	private static final long serialVersionUID = 1209487969346789159L;
	public static final int LOCATION_ROLE_PROPERTY = 10;
	public static final int TASK_ACTION_PROPERTY = 20;

	@DatabaseField(index = true, generatedIdSequence = "property_id_seq")
	private long id = -2;
	@DatabaseField
	private String propertyType = PropertyTypes.UNDEFINED.name();
	@DatabaseField
	private String stringValue = "";
	@DatabaseField
	private double numericValue = 0.0;
	@DatabaseField
	private long targetId = -6;
	@DatabaseField
	private Long ownerId;

	// - C O N S T R U C T O R S
	private Property() {
	}

//	public Property( final PropertyTypes propertyType ) {
//		// Be sure the owner is reset to undefined when stored at the database.
//		this.resetOwner();
//		this.setPropertyType( propertyType );
////		try {
////			Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
////			// Try to create the pair. It fails then  it was already created.
////			propertyDao.create(this);
////		} catch (final SQLException sqle) {
////			this.store();
////		}
//	}

	// - M E T H O D - S E C T I O N ..........................................................................
//	public Property store() {
//		try {
//			Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
//			propertyDao.update(this);
//		} catch (final SQLException sqle) {
//		}
//		return this;
//	}

	public long getId() {
		return id;
	}

	public long getOwnerId() {
		return this.ownerId;
	}

	public double getNumericValue() {
		return numericValue;
	}

	public PropertyTypes getPropertyType() {
		return PropertyTypes.valueOf( this.propertyType );
	}

	public Property setNumericValue( final double numericValue ) {
		this.numericValue = numericValue;
		return this;
	}

	public String getStringValue() {
		return stringValue;
	}

	public Property setStringValue( final String stringValue ) {
		this.stringValue = stringValue;
		return this;
	}

	public long getTargetId() {
		return targetId;
	}

	public Property setTargetId( final long targetId ) {
		this.targetId = targetId;
		return this;
	}

//	public String getPropertyValue() {
//		return this.stringValue;
//	}

//	public void resetOwner() {
//		ownerId = -1;
//	}


//	public Property setOwnerId( final long ownerId ) {
//		this.ownerId = ownerId;
//		return this;
//	}

//	public Property setPropertyType( final PropertyTypes propertyType ) {
//		this.propertyType = propertyType.toString();
//		return this;
//	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer( "Property [" );
		buffer.append( stringValue ).append( " [" ).append( numericValue ).append( "] " );
		buffer.append( "Type:" ).append( propertyType ).append( " " );
		buffer.append( "]" );
		return buffer.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private Property onConstruction;

		public Builder() {
			this.onConstruction = new Property();
		}

		public Property.Builder withPropertyType( final PropertyTypes propertyType ) {
			this.onConstruction.propertyType = propertyType.name();
			return this;
		}

		public Property.Builder withOwnerId( final long owner ) {
			this.onConstruction.ownerId = owner;
			return this;
		}
		public Property.Builder withTargetId( final long targetId ) {
			this.onConstruction.targetId = targetId;
			return this;
		}

		public Property build() {
			return this.onConstruction;
		}
	}
}
