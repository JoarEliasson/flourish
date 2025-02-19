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
 * Provides functionality to authenticate and register users by interacting with the database.
 * The registration process encrypts the raw password using BCrypt before storage.
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
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public User register(String username, String email, String password, Boolean notificationActivated, Boolean funFactsActivated) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already exists");
        }
        String encryptedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, email, encryptedPassword, notificationActivated, funFactsActivated);
        return userRepository.save(newUser);
    }
}
