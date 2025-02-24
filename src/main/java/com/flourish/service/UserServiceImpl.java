package com.flourish.service;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
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
 * @see com.flourish.service.UserService
 * @see com.flourish.domain.User
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/">Spring Data JPA Reference</a>
 * @see <a href="https://vaadin.com/docs/latest/security/spring-security">Vaadin Spring Security</a>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
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
