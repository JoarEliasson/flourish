package com.flourish.server.db;

import com.flourish.server.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementation of the DatabaseConnection interface for MySQL.
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public class MySQLDatabaseConnection implements DatabaseConnection {

    private static MySQLDatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    /**
     * Private constructor to prevent direct instantiation.
     * Loads the DB configuration from DBConfig and creates the connection.
     *
     * @throws SQLException if a database access error occurs
     */
    private MySQLDatabaseConnection() throws SQLException {
        this.username = DBConfig.USER;
        this.password = DBConfig.PASSWORD;
        this.url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false",
                DBConfig.HOST, DBConfig.PORT, DBConfig.DATABASE);

        this.connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Provides the singleton instance of MySQLDatabaseConnection.
     *
     * @return the singleton instance
     * @throws SQLException if a database access error occurs
     */
    public static synchronized MySQLDatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new MySQLDatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active database connection.
     *
     * @return an active {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Closes the active database connection.
     *
     * @throws SQLException if an error occurs while closing the connection
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
