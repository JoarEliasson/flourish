package se.myhappyplants.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Repository for handling password reset tokens in the "PasswordReset" table,
 * using the same pattern as PlantRepository (string-based queries).
 *
 * @author Joar Eliasson
 * @since 2025-02-05
 */
public class PasswordResetTokenRepository {

    private final QueryExecutor queryExecutor;

    /**
     * Constructs a PasswordResetTokenRepository with the specified QueryExecutor.
     *
     * @param queryExecutor the QueryExecutor to use for database operations.
     */
    public PasswordResetTokenRepository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * Inserts a new token row into the "PasswordReset" table.
     *
     * @param userId    The ID of the user requesting a reset.
     * @param token     The unique token string.
     * @param expiresAt The LocalDateTime when the token expires.
     */
    public void createToken(int userId, String token, LocalDateTime expiresAt) {
        String escapedToken = escapeString(token);

        String expiresAtStr = Timestamp.valueOf(expiresAt).toString();

        String query = "INSERT INTO PasswordReset (user_id, token, expires_at, used) VALUES ("
                + userId + ", '"
                + escapedToken + "', '"
                + expiresAtStr + "', 0)";

        try {
            queryExecutor.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a reset token entry by the token value.
     *
     * @param token The token string to look up.
     * @return A {@link ResetToken} object if found; otherwise, null.
     */
    public ResetToken findByToken(String token) {
        String escapedToken = escapeString(token);

        String query = "SELECT id, user_id, token, expires_at, used FROM PasswordReset WHERE token = '"
                + escapedToken + "'";

        try (ResultSet rs = queryExecutor.executeQuery(query)) {
            if (rs.next()) {
                ResetToken resetToken = new ResetToken();
                resetToken.setId(rs.getInt("id"));
                resetToken.setUserId(rs.getInt("user_id"));
                resetToken.setToken(rs.getString("token"));
                resetToken.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                resetToken.setUsed(rs.getBoolean("used"));
                return resetToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Marks the token as used (set used=1) so it can't be reused.
     *
     * @param token The token string to update.
     */
    public void markTokenUsed(String token) {
        String escapedToken = escapeString(token);

        String query = "UPDATE PasswordReset SET used = 1 WHERE token = '"
                + escapedToken + "'";

        try {
            queryExecutor.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to escape single quotes in a string to help prevent SQL injection
     * when building queries via string concatenation.
     *
     * @param input the input string to escape.
     * @return the escaped string.
     */
    private String escapeString(String input) {
        return input == null ? null : input.replace("'", "''");
    }
}
