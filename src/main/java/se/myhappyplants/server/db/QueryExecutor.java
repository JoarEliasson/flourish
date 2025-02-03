package se.myhappyplants.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Defines the contract for executing SQL queries.
 * Provides methods for executing update and query statements,
 * as well as for managing transactions.
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public interface QueryExecutor {

    /**
     * Executes an SQL update query (such as INSERT, UPDATE, or DELETE).
     *
     * @param query the SQL update query to execute
     * @throws SQLException if a database access error occurs
     */
    void executeUpdate(String query) throws SQLException;

    /**
     * Executes an SQL query that returns a {@link ResultSet}.
     * <p>
     * <b>Note:</b> The caller is responsible for closing the returned {@code ResultSet}
     * (and its underlying {@code Statement}) to release database resources.
     * </p>
     *
     * @param query the SQL query to execute
     * @return a {@link ResultSet} containing the data produced by the query
     * @throws SQLException if a database access error occurs
     */
    ResultSet executeQuery(String query) throws SQLException;

    /**
     * Begins a new transaction by disabling auto-commit mode.
     *
     * @return a {@link Statement} that can be used to execute queries in the transaction
     * @throws SQLException if a database access error occurs
     */
    Statement beginTransaction() throws SQLException;

    /**
     * Commits the current transaction and re-enables auto-commit mode.
     *
     * @throws SQLException if a database access error occurs
     */
    void endTransaction() throws SQLException;

    /**
     * Rolls back the current transaction.
     *
     * @throws SQLException if a database access error occurs
     */
    void rollbackTransaction() throws SQLException;
}
