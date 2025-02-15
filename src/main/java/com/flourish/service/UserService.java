package com.flourish.service;

import com.flourish.domain.User;

/**
 * Defines the contract for user-related operations (e.g., authentication).
 */
public interface UserService {

    /**
     * Authenticates a user.
     *
     * @param username the username
     * @param password the raw password
     * @return the authenticated User
     * @throws Exception if authentication fails
     */
    User authenticate(String username, String password) throws Exception;

}
