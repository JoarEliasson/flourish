package com.flourish.service;

import com.flourish.domain.User;

import java.util.Optional;

/**
 * Defines operations related to user management, including creating new users,
 * fetching existing users, handling password reset tokens, and ensuring that a user-specific
 * settings row exists for each user.
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
public interface UserService {

    /**
     * Creates a new user with the given data.
     * This method encrypts the plain-text password before storing and creates a default row
     * in the user_settings table for the new user.
     *
     * @param user a User entity with a plain-text password.
     * @return the saved User entity with an encrypted password and generated ID.
     */
    User createUser(User user);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search by.
     * @return an Optional of User if found, otherwise Optional.empty().
     */
    Optional<User> findByEmail(String email);

    /**
     * Updates an existing user.
     * This method can be used for password reset, etc.
     *
     * @param user the user to update.
     * @return the updated user.
     */
    User updateUser(User user);

    /**
     * Finds a user by a password reset token.
     *
     * @param token the reset token.
     * @return an Optional of User if found, otherwise Optional.empty().
     */
    Optional<User> findByResetToken(String token);

    /**
     * Checks all users in the system to ensure each has a corresponding user_settings record.
     * For any user missing settings, a default settings record is created.
     */
    void ensureAllUsersHaveSettings();

    /**
     * Deletes a user by email.
     *
     * @param testUserEmail the email address of the user to delete.
     */
    void deleteByEmail(String testUserEmail);
}
