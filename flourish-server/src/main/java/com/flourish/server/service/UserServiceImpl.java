package com.flourish.server.service;

import com.flourish.domain.model.User;
import com.flourish.domain.service.UserService;
import com.flourish.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface.
 * <p>
 * Handles user authentication by verifying that the provided raw password matches
 * the encrypted password stored in the database.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a {@code UserServiceImpl} with the specified {@code UserRepository} and {@code PasswordEncoder}.
     *
     * @param userRepository  the repository used for accessing user data
     * @param passwordEncoder the encoder used for password hashing and verification
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user by comparing the provided raw password with the stored encrypted password.
     *
     * @param username the username of the user
     * @param password the raw (unencrypted) password provided for authentication
     * @return the authenticated {@link User} if the credentials are valid
     * @throws Exception if authentication fails due to invalid credentials or if the user is not found
     */
    @Override
    public User authenticate(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Invalid username or password"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new Exception("Invalid username or password");
        }
    }
}
