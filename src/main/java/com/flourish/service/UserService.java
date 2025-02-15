package com.flourish.service;

import com.flourish.domain.User;

import java.util.Optional;

/**
 * Defines the operations related to user management such as
 * creating new users, fetching existing users, and handling
 * password reset tokens.
 *
 * <p>Following the SOLID principle of Single Responsibility, this
 * service encapsulates all user-related business logic.</p>
 *
 * <p>Following TDD, you can create tests that verify each
 * method independently.</p>
 *
 * @author
 *   Your Name
 * @version
 *   1.0.0
 * @since
 *   1.0.0
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
