package org.dimensinfin.eveonline.neocom.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This is a singleton row table. The unique ID is fixed to a predefined value because the database can only can
 * have a version so there is no need to allow the creation of more records.
 */
@Entity(name = "Version")
@DatabaseTable(tableName = "Version")
public class DatabaseVersion {
	private static final String DATABASE_SINGLETON_RECORD_ID = "-DATABASE_SINGLETON_RECORD_ID-";
	@DatabaseField(id = true)
	@Column(name = "id", updatable = false, nullable = false)
	public String id = DATABASE_SINGLETON_RECORD_ID;
	@DatabaseField
	@Column(name = "versionNumber", updatable = true, nullable = false)
	public Integer versionNumber;

	// - C O N S T R U C T O R S
	private DatabaseVersion() { }

	public DatabaseVersion( final int newVersion ) {
		this();
		this.versionNumber = newVersion;
	}

	public int getVersionNumber() {
		return this.versionNumber;
	}

	public DatabaseVersion setVersionNumber( final Integer versionNumber ) {
		this.versionNumber = versionNumber;
		return this;
	}

	// - C O R E
	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "versionNumber", versionNumber )
				.toString();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (!(o instanceof DatabaseVersion)) return false;
		final DatabaseVersion that = (DatabaseVersion) o;
		return new EqualsBuilder()
				.append( this.id, that.id )
				.append( this.versionNumber, that.versionNumber )
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.append( this.id )
				.append( this.versionNumber )
				.toHashCode();
	}
}
