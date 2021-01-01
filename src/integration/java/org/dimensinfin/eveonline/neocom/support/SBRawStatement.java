package org.dimensinfin.eveonline.neocom.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.database.core.ISDEStatement;
import org.dimensinfin.eveonline.neocom.database.core.RawStatement;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class SBRawStatement implements ISDEStatement {
    private PreparedStatement prepStmt;
    private ResultSet cursor;

    public SBRawStatement(final Connection privateConnection, final String query, final String[] parameters) throws
            SQLException {
        if (null != privateConnection) {
            this.prepStmt = privateConnection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                this.prepStmt.setString(i + 1, parameters[i]);
            }
            this.cursor = this.prepStmt.executeQuery();
        } else throw new SQLException("No valid connection to database to create statement. {}", query);
    }

    @Override
    public boolean moveToFirst() {
        try {
            return this.cursor.first();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean moveToLast() {
        try {
            return this.cursor.last();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean moveToNext() {
        try {
            return this.cursor.next();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return this.cursor.isFirst();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean isLast() {
        if ( null == this.cursor)return true;
        try {
            return this.cursor.isLast();
        } catch (SQLException sqle) {
            return true;
        }
    }

    @Override
    public String getString(int colindex) {
        try {
            return this.cursor.getString(colindex);
        } catch (SQLException sqle) {
            return "";
        }
    }

    @Override
    public short getShort(int colindex) {
        try {
            return this.cursor.getShort(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public int getInt(int colindex) {
        try {
            return this.cursor.getInt(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public long getLong(int colindex) {
        try {
            return this.cursor.getLong(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public float getFloat(int colindex) {
        try {
            return this.cursor.getFloat(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public double getDouble(int colindex) {
        try {
            return this.cursor.getDouble(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    public void close() {
        try {
            if (null != this.cursor) this.cursor.close();
            if (null != this.prepStmt) this.prepStmt.close();
        } catch (SQLException sqle) {
            NeoComLogger.error( sqle );
        }
    }
}
