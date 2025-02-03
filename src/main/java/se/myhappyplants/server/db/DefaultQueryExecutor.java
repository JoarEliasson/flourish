package se.myhappyplants.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Default implementation of the {@link QueryExecutor} interface.
 * <p>
 * This class handles executing SQL queries and managing transactions.
 * In the event of a connection failure, it will attempt to close and reopen the connection,
 * retrying the operation up to a maximum number of attempts.
 * </p>
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public class DefaultQueryExecutor implements QueryExecutor {

    private static final int MAX_RETRIES = 3;
    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a {@code DefaultQueryExecutor} with the provided {@link DatabaseConnection}.
     *
     * @param databaseConnection the database connection to use for executing queries
     */
    public DefaultQueryExecutor(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Executes an SQL update (INSERT, UPDATE, DELETE) using a retry mechanism.
     *
     * @param query the SQL update query to execute
     * @throws SQLException if the query fails after {@value MAX_RETRIES} attempts
     */
    @Override
    public void executeUpdate(String query) throws SQLException {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try (Statement statement = databaseConnection.getConnection().createStatement()) {
                statement.executeUpdate(query);
                return;
            } catch (SQLException e) {
                retries++;
                databaseConnection.close();
                if (retries >= MAX_RETRIES) {
                    throw new SQLException("Failed to execute update after " + MAX_RETRIES + " attempts", e);
                }
            }
        }
    }

    /**
     * Executes an SQL query that returns a {@link ResultSet}.
     * <p>
     * Note: Since the {@link Statement} is not closed here, the caller must close both the
     * {@code ResultSet} and its {@code Statement} when done.
     * </p>
     *
     * @param query the SQL query to execute
     * @return a {@link ResultSet} with the query results
     * @throws SQLException if the query fails after {@value MAX_RETRIES} attempts
     */
    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                Statement statement = databaseConnection.getConnection().createStatement();
                return statement.executeQuery(query);
            } catch (SQLException e) {
                retries++;
                databaseConnection.close();
                if (retries >= MAX_RETRIES) {
                    throw new SQLException("Failed to execute query after " + MAX_RETRIES + " attempts", e);
                }
            }
        }
        throw new SQLException("Failed to execute query: Unknown error");
    }

    /**
     * Begins a transaction by setting auto-commit to false.
     * <p>
     * A new {@link Statement} is returned for executing transactional queries.
     * </p>
     *
     * @return a {@link Statement} for executing queries within the transaction
     * @throws SQLException if beginning the transaction fails after {@value MAX_RETRIES} attempts
     */
    @Override
    public Statement beginTransaction() throws SQLException {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                Connection conn = databaseConnection.getConnection();
                conn.setAutoCommit(false);
                return conn.createStatement();
            } catch (SQLException e) {
                retries++;
                databaseConnection.close();
                if (retries >= MAX_RETRIES) {
                    throw new SQLException("Failed to begin transaction after " + MAX_RETRIES + " attempts", e);
                }
            }
        }
        throw new SQLException("Failed to begin transaction: Unknown error");
    }

    /**
     * Commits the current transaction and re-enables auto-commit mode.
     *
     * @throws SQLException if committing the transaction fails
     */
    @Override
    public void endTransaction() throws SQLException {
        Connection conn = databaseConnection.getConnection();
        conn.commit();
        conn.setAutoCommit(true);
    }

    /**
     * Rolls back the current transaction.
     *
     * @throws SQLException if rolling back the transaction fails
     */
    @Override
    public void rollbackTransaction() throws SQLException {
        Connection conn = databaseConnection.getConnection();
        conn.rollback();
    }
}
