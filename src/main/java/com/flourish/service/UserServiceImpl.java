package com.flourish.service;

import com.flourish.domain.User;
import com.flourish.domain.UserSettings;
import com.flourish.repository.UserRepository;
import com.flourish.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implements the UserService interface, handling user-related operations
 * and ensuring that each user has a corresponding settings record.
 *
 * <p>This class uses a UserRepository for user data and a PasswordEncoder for password encryption.
 * It also creates or synchronizes default user settings by calling a helper method that
 * creates a settings record (using default values from application.properties) if one is missing.</p>
 *
 * @see com.flourish.domain.User
 * @see com.flourish.domain.UserSettings
 * @see com.flourish.repository.UserRepository
 * @see com.flourish.repository.UserSettingsRepository
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.settings.default.language}")
    private String defaultLanguage;

    @Value("${user.settings.default.loginNotificationEnabled}")
    private boolean defaultLoginNotificationEnabled;

    @Value("${user.settings.default.inAppNotificationEnabled}")
    private boolean defaultInAppNotificationEnabled;

    @Value("${user.settings.default.emailNotificationEnabled}")
    private boolean defaultEmailNotificationEnabled;

    @Value("${user.default.user.image}")
    private String defaultUserImageUrl;


    public UserServiceImpl(UserRepository userRepository,
                           UserSettingsRepository userSettingsRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user by encrypting the password and saving the user entity.
     * After saving, a default user settings record is created.
     *
     * @param user a User entity with a plain-text password.
     * @return the saved User entity.
     */
    @Override
    @Transactional
    public User createUser(User user) {
        if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            user.setProfileImageUrl(defaultUserImageUrl);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        createDefaultSettingsIfNotExists(savedUser.getId());
        return savedUser;
    }
    /**
     * Checks if the provided email matches a basic email format.
     *
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public String getUserImageUrl(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getProfileImageUrl();
        }
        return null;
    }

    /**
     * Finds a User by email.
     *
     * @param email the email address.
     * @return an Optional containing the User if found; otherwise, Optional.empty().
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    /**
     * Updates an existing user.
     *
     * @param user the user to update.
     * @return the updated User.
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds a User by a password reset token.
     *
     * @param token the reset token.
     * @return an Optional containing the User if found; otherwise, Optional.empty().
     */
    @Override
    public Optional<User> findByResetToken(String token) {
        return Optional.ofNullable(userRepository.findByResetToken(token));
    }

    /**
     * Checks all users in the system and creates a default settings record for any user missing one.
     */
    @Override
    @Transactional
    public void ensureAllUsersHaveSettings() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            createDefaultSettingsIfNotExists(user.getId());
        }
    }

    /**
     * Creates a default UserSettings record for the given userId if one does not already exist.
     *
     * @param userId the user ID.
     */
    @Transactional
    protected void createDefaultSettingsIfNotExists(Long userId) {
        if (!userSettingsRepository.existsById(userId)) {
            UserSettings settings = new UserSettings(userId,
                    defaultLanguage,
                    defaultLoginNotificationEnabled,
                    defaultInAppNotificationEnabled,
                    defaultEmailNotificationEnabled);
            userSettingsRepository.save(settings);
        }
    }

    /**
     * Deletes a user by email.
     * <p>This method deletes the user entity and the corresponding user settings record.</p>
     *
     * @param email the email address.
     */
    @Override
    @Transactional
    public void deleteByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            userRepository.delete(user);
            Long userId = user.getId();
            userSettingsRepository.deleteById(userId);
        }
    }

    /**
     * Deletes a user by ID.
     * <p>This method deletes the user entity and the corresponding user settings record.</p>
     *
     * @param userId the user ID.
     */
    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
        userSettingsRepository.deleteById(userId);
    }
}
