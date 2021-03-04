package org.dimensinfin.eveonline.neocom.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.database.core.ISDEStatement;
import org.dimensinfin.logging.LogWrapper;

public class SBRawStatement implements ISDEStatement {
	private final PreparedStatement prepStmt;
	private final ResultSet cursor;

// - C O N S T R U C T O R S
	public SBRawStatement( final Connection privateConnection, final String query, final String[] parameters ) throws
			SQLException {
		if (null != privateConnection) {
			this.prepStmt = privateConnection.prepareStatement( query );
			for (int i = 0; i < parameters.length; i++) {
				this.prepStmt.setString( i + 1, parameters[i] );
			}
			this.cursor = this.prepStmt.executeQuery();
		} else throw new SQLException( "No valid connection to database to create statement. {}", query );
	}

// - G E T T E R S   &   S E T T E R S
	@Override
	public boolean isFirst() {
		try {
			return this.cursor.isFirst();
		} catch (final SQLException sqle) {
			return false;
		}
	}

	@Override
	public boolean isLast() {
		if (null == this.cursor) return true;
		try {
			return this.cursor.isLast();
		} catch (final SQLException sqle) {
			return true;
		}
	}

	@Override
    public void close() {
		try {
			if (null != this.cursor) this.cursor.close();
			if (null != this.prepStmt) this.prepStmt.close();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		}
	}

	@Override
	public double getDouble( final int colindex ) {
		try {
			return this.cursor.getDouble( colindex );
		} catch (final SQLException sqle) {
			return 0;
		}
	}

	@Override
	public float getFloat( final int colindex ) {
		try {
			return this.cursor.getFloat( colindex );
		} catch (final SQLException sqle) {
			return 0;
		}
	}

	@Override
	public int getInt( final int colindex ) {
		try {
			return this.cursor.getInt( colindex );
		} catch (final SQLException sqle) {
			return 0;
		}
	}

	@Override
	public long getLong( final int colindex ) {
		try {
			return this.cursor.getLong( colindex );
		} catch (final SQLException sqle) {
			return 0;
		}
	}

	@Override
	public short getShort( final int colindex ) {
		try {
			return this.cursor.getShort( colindex );
		} catch (final SQLException sqle) {
			return 0;
		}
	}

	@Override
	public String getString( final int colindex ) {
		try {
			return this.cursor.getString( colindex );
		} catch (final SQLException sqle) {
			return "";
		}
	}

	@Override
	public boolean moveToFirst() {
		try {
			return this.cursor.first();
		} catch (final SQLException sqle) {
			return false;
		}
	}

	@Override
	public boolean moveToLast() {
		try {
			return this.cursor.last();
		} catch (final SQLException sqle) {
			return false;
		}
	}

	@Override
	public boolean moveToNext() {
		try {
			return this.cursor.next();
		} catch (final SQLException sqle) {
			return false;
		}
	}
}
