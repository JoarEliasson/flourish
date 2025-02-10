package com.flourish.server.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Defines the contract for a database connection.
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public interface DatabaseConnection {
    /**
     * Retrieves the active database connection.
     *
     * @return an active {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    Connection getConnection() throws SQLException;

    /**
     * Closes the active database connection.
     *
     * @throws SQLException if an error occurs during closing the connection
     */
    void close() throws SQLException;
}
