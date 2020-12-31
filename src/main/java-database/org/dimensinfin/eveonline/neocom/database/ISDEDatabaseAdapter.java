package org.dimensinfin.eveonline.neocom.database;

import java.sql.SQLException;

public interface ISDEDatabaseAdapter {
	RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;

	Integer getDatabaseVersion();
}
