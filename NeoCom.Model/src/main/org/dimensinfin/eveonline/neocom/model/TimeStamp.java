//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.joda.time.Instant;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "TimeStamp")
public class TimeStamp {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger								= Logger.getLogger("TimeStamp");

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true)
	private String				reference							= "-REF-";
	@DatabaseField
	private long					timeStamp							= -1;
	@DatabaseField
	private String				dateTimeUserReference	= "-";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TimeStamp() {
	}

	public TimeStamp(final String reference, final Instant instant) {
		this.reference = reference;
		timeStamp = instant.getMillis();
		dateTimeUserReference = instant.toString();
		try {
			Dao<TimeStamp, String> timeStampDao = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO();
			// Try to create the pair. It fails then  it was already created.
			timeStampDao.create(this);
		} catch (final SQLException sqle) {
			TimeStamp.logger.info("WR [TimeStamp.<init>]>Timestamp exists. Update values.");
			this.setDirty(true);
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getDateTimeUserReference() {
		return dateTimeUserReference;
	}

	public String getReference() {
		return reference;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setDirty(final boolean state) {
		if (state) {
			try {
				Dao<TimeStamp, String> timeStampDao = ModelAppConnector.getSingleton().getDBConnector().getTimeStampDAO();
				timeStampDao.update(this);
			} catch (final SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("TimeStamp [");
		buffer.append(reference).append("-").append(dateTimeUserReference);
		buffer.append("]");
		return buffer.toString();
	}

	public void updateTimeStamp(final Instant instant) {
		timeStamp = instant.getMillis();
		dateTimeUserReference = instant.toString();
		this.setDirty(true);
	}
}

// - UNUSED CODE ............................................................................................
