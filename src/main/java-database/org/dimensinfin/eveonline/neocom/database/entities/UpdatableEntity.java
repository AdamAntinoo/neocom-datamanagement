package org.dimensinfin.eveonline.neocom.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;

public abstract class UpdatableEntity extends NeoComNode {
	@DatabaseField
	@JsonIgnore
	private DateTime creationTime;
	@DatabaseField
	@JsonIgnore
	private DateTime lastUpdateTime;

	protected UpdatableEntity() {
		this.creationTime = DateTime.now();
	}

	public DateTime getLastUpdateTime() {
		if (null == this.lastUpdateTime) this.lastUpdateTime = new DateTime(0);
		return this.lastUpdateTime;
	}

	/**
	 * Update the last time stamp to the current time. This should be called before any persistence update.
	 */
	public void timeStamp() {
		this.lastUpdateTime = DateTime.now();
	}
}
