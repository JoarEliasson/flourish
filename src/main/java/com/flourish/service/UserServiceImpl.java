package com.flourish.service;

import com.flourish.domain.User;
import com.flourish.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implements the UserService interface, handling
 * user-related operations and business logic.
 *
 * <p>Uses a {@code UserRepository} for database operations
 * and a {@code PasswordEncoder} for password encryption.</p>
 *
 * <p>Adheres to the SOLID principle of Single Responsibility:
 * this class’s only job is to manage user business logic.</p>
 *
 * <p>Following TDD, tests can be written to verify the correctness
 * of each method with mock or in-memory DB approaches.</p>
 *
 * @see com.flourish.service.UserService
 * @see com.flourish.domain.User
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/">Spring Data JPA Reference</a>
 * @see <a href="https://vaadin.com/docs/latest/security/spring-security">Vaadin Spring Security</a>
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserServiceImpl.
     *
     * @param userRepository The repository for User entities.
     * @param passwordEncoder The password encoder to encrypt passwords.
     */
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        // Encrypt the password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUser(User user) {
        // If updating password, ensure it is already encoded
        // or re-encoded depending on your logic
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByResetToken(String token) {
        return Optional.ofNullable(userRepository.findByResetToken(token));
    }
}
