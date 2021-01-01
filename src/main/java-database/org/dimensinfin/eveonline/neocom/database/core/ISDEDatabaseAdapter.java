package org.dimensinfin.eveonline.neocom.database.core;

import java.sql.SQLException;

public interface ISDEDatabaseAdapter {
	String getDatabaseVersion();

	ISDEStatement constructStatement( final String query, final String[] parameters ) throws SQLException;
}
