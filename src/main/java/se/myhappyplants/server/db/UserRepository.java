package se.myhappyplants.server.db;

import org.mindrot.jbcrypt.BCrypt;
import se.myhappyplants.shared.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Repository class responsible for user-related database operations.
 * <p>
 * Provides methods to save users, validate login credentials, retrieve user details,
 * update preferences, and delete user accounts in a transactional manner.
 * </p>
 */
public class UserRepository {

    private final QueryExecutor queryExecutor;

    /**
     * Constructs a UserRepository with the specified QueryExecutor.
     *
     * @param queryExecutor the QueryExecutor to use for database operations.
     */
    public UserRepository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * Saves a new user into the database with a hashed password.
     *
     * @param user the User object to be saved.
     * @return true if the user was saved successfully; false otherwise.
     */
    public boolean saveUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        String username = escapeString(user.getUsername());
        String email = escapeString(user.getEmail());
        String query = "INSERT INTO Users (username, email, password, notification_activated, fun_facts_activated) " +
                "VALUES ('" + username + "', '" + email + "', '" + hashedPassword + "', 1, 1);";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the provided email and password match a user in the database.
     *
     * @param email    the user's email.
     * @param password the user's password.
     * @return true if the credentials are valid; false otherwise.
     */
    public boolean checkLogin(String email, String password) {
        String safeEmail = escapeString(email);
        String query = "SELECT password FROM Users WHERE email = '" + safeEmail + "';";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves user details for the specified email.
     *
     * @param email the user's email.
     * @return a User object with details, or null if not found.
     */
    public User getUserDetails(String email) {
        String safeEmail = escapeString(email);
        String query = "SELECT id, username, notification_activated, fun_facts_activated FROM Users " +
                "WHERE email = '" + safeEmail + "';";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                boolean notificationActivated = resultSet.getBoolean("notification_activated");
                boolean funFactsActivated = resultSet.getBoolean("fun_facts_activated");
                return new User(id, email, username, notificationActivated, funFactsActivated);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a user account and all associated plants in a transactional manner.
     *
     * @param email    the user's email.
     * @param password the user's password.
     * @return true if the account was successfully deleted; false otherwise.
     */
    public boolean deleteAccount(String email, String password) {
        if (!checkLogin(email, password)) {
            return false;
        }
        String safeEmail = escapeString(email);
        String selectQuery = "SELECT id FROM Users WHERE email = '" + safeEmail + "';";
        Statement transactionStmt = null;
        try {
            transactionStmt = queryExecutor.beginTransaction();
            ResultSet resultSet = transactionStmt.executeQuery(selectQuery);
            if (!resultSet.next()) {
                throw new SQLException("User not found");
            }
            int userId = resultSet.getInt("id");

            String deletePlantsQuery = "DELETE FROM Plants WHERE user_id = " + userId + ";";
            transactionStmt.executeUpdate(deletePlantsQuery);

            String deleteUserQuery = "DELETE FROM Users WHERE id = " + userId + ";";
            transactionStmt.executeUpdate(deleteUserQuery);

            queryExecutor.endTransaction();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                queryExecutor.rollbackTransaction();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Updates the notification setting for the specified user.
     *
     * @param user          the user whose notification setting is to be updated.
     * @param notifications the new notification setting.
     * @return true if the update was successful; false otherwise.
     */
    public boolean changeNotifications(User user, boolean notifications) {
        String safeEmail = escapeString(user.getEmail());
        int notificationsValue = notifications ? 1 : 0;
        String query = "UPDATE Users SET notification_activated = " + notificationsValue +
                " WHERE email = '" + safeEmail + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the fun facts setting for the specified user.
     *
     * @param user            the user whose fun facts setting is to be updated.
     * @param funFactsEnabled the new fun facts setting.
     * @return true if the update was successful; false otherwise.
     */
    public boolean changeFunFacts(User user, boolean funFactsEnabled) {
        String safeEmail = escapeString(user.getEmail());
        int funFactsValue = funFactsEnabled ? 1 : 0;
        String query = "UPDATE Users SET fun_facts_activated = " + funFactsValue +
                " WHERE email = '" + safeEmail + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Escapes single quotes in a string to help prevent SQL injection.
     *
     * @param input the input string.
     * @return the escaped string.
     */
    private String escapeString(String input) {
        return input == null ? null : input.replace("'", "''");
    }
}
