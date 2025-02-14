package com.flourish.domain.service;

import com.flourish.domain.model.User;

/**
 * Service interface for user-related operations.
 * <p>
 * This interface defines the contract for user authentication and registration.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public interface UserService {

    /**
     * Authenticates a user by comparing the provided raw password with the stored encrypted password.
     *
     * @param username the username of the user
     * @param password the raw password provided for authentication
     * @return the authenticated {@link User} if credentials are valid
     * @throws Exception if authentication fails due to invalid credentials or other issues
     */
    User authenticate(String username, String password) throws Exception;

    /**
     * Registers a new user with the provided details.
     *
     * @param username              the desired username
     * @param email                 the user's email address
     * @param password              the raw password (to be encrypted before storage)
     * @param notificationActivated whether notifications are activated
     * @param funFactsActivated     whether fun facts are activated
     * @return the newly registered {@link User}
     * @throws Exception if registration fails (e.g., if username or email already exists)
     */
    User register(String username, String email, String password,
                  Boolean notificationActivated, Boolean funFactsActivated) throws Exception;

}
