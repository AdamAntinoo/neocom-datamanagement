package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.RawStatement;
import org.dimensinfin.eveonline.neocom.database.SBRawStatement;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class TestSDEDBAdapter implements ISDEDatabaseAdapter {
    protected static Logger logger = LoggerFactory.getLogger(TestSDEDBAdapter.class);
    private IFileSystem fileSystemAdapter;

    private String schema = "jdbc:sqlite";
    private String databasePath;
    private String databaseName;
    private int databaseVersion = 0;
    private Connection connectionSource = null;

    // - C O N S T R U C T O R S
    private TestSDEDBAdapter() {
        super();
    }

    protected String getConnectionDescriptor() {
        return schema + ":" + this.fileSystemAdapter.accessResource4Path(databasePath + databaseName);
    }

    protected Connection getSDEConnection() throws SQLException {
        if (null == this.connectionSource) this.openSDEDB();
        return this.connectionSource;
    }

    /**
     * Open a new pooled JDBC datasource connection list and stores its reference for use of the whole set of
     * services. Being a pooled connection it can create as many connections as required to do requests in
     * parallel to the database instance. This only is effective for MySql databases.
     * <p>
     * Check database definition before trying to open the database.
     */
    protected void openSDEDB() throws SQLException {
        logger.info(">> [SDESBDBAdapter.openSDEDB]");
        if (null == this.connectionSource) {
            this.createConnectionSource();
            logger.info("-- [SDESBDBAdapter.openSDEDB]> Opened database {} successfully with version {}.",
                    this.getConnectionDescriptor(),
                    this.databaseVersion);
        }
        logger.info("<< [SDESBDBAdapter.openSDEDB]");
    }

    private void createConnectionSource() throws SQLException {
        if (this.isDatabaseDefinitionValid()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException cnfe) {
                throw new SQLException("Cannot create connection. {}.", cnfe.getMessage());
            }
            this.connectionSource = DriverManager.getConnection(this.getConnectionDescriptor());
            this.connectionSource.setAutoCommit(false);
        } else throw new SQLException("Cannot create connection, database validation not passed.");
    }

    protected boolean isDatabaseDefinitionValid() {
        if ((null != this.databasePath) && (null != this.databaseName)) return true;
        return false;
    }

    /**
     * This is the specific SpringBoot implementation for the SDE database adaptation. We can create compatible
     * <code>RawStatements</code> that can isolate the generic database access code from the platform specific. This
     * statement uses the database connection to create a generic JDBC Java statement.
     */
    public RawStatement constructStatement(final String query, final String[] parameters) throws SQLException {
        return new SBRawStatement(this.getSDEConnection(), query, parameters);
    }

    // - B U I L D E R
    public static class Builder {
        private TestSDEDBAdapter onConstruction;

        public Builder() {
            this.onConstruction = new TestSDEDBAdapter();
        }

        public Builder withDatabasePath(final String databasePath) {
            this.onConstruction.databasePath = databasePath;
            return this;
        }

        public Builder withDatabaseName(final String databaseName) {
            this.onConstruction.databaseName = databaseName;
            return this;
        }

        public Builder withFileSystem(final IFileSystem fileSystem) {
            this.onConstruction.fileSystemAdapter = fileSystem;
            return this;
        }

        public TestSDEDBAdapter build() {
            Objects.requireNonNull(this.onConstruction.databasePath);
            Objects.requireNonNull(this.onConstruction.databaseName);
            Objects.requireNonNull(this.onConstruction.fileSystemAdapter);
            return this.onConstruction;
        }
    }
}
