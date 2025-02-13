package com.flourish.domain.service;

import com.flourish.domain.model.User;

/**
 * Service interface for user-related operations.
 * <p>
 * This interface defines the contract for user authentication and potentially
 * other user management functionalities.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public interface UserService {

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username the username of the user
     * @param password the raw (unencrypted) password of the user
     * @return the authenticated {@link User} object if credentials are valid
     * @throws Exception if authentication fails due to invalid credentials or other issues
     */
    User authenticate(String username, String password) throws Exception;

    // Additional methods (e.g., registration, password reset) can be defined here.
}
