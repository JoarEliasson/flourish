package com.flourish.service;

import com.flourish.domain.User;

import java.util.Optional;

/**
 * Defines the operations related to user management such as
 * creating new users, fetching existing users, and handling
 * password reset tokens.
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
public interface UserService {

    /**
     * Creates a new user with the given data, encrypting the password
     * before storing.
     *
     * @param user A User entity. The password should be in plain-text form
     *             and will be encrypted within this method.
     * @return The saved User entity with an encrypted password and generated ID.
     */
    User createUser(User user);

    /**
     * Finds a User by their email address.
     *
     * @param email The email address to search by.
     * @return An Optional of User if found, otherwise Optional.empty().
     */
    Optional<User> findByEmail(String email);

    /**
     * Updates an existing user.
     * This method can be used for password reset, etc.
     *
     * @param user The user to update.
     * @return The updated user.
     */
    User updateUser(User user);

    /**
     * Finds a user by a password reset token.
     *
     * @param token The reset token.
     * @return An Optional of User if found, otherwise Optional.empty().
     */
    Optional<User> findByResetToken(String token);
}
