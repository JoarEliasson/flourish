package com.flourish.service;

import com.flourish.domain.UserSettings;
import com.flourish.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing user-specific settings.
 *
 * <p>This service provides methods for retrieving, updating, and saving user settings. The settings include:
 * <ul>
 *   <li>language</li>
 *   <li>loginNotificationEnabled</li>
 *   <li>inAppNotificationEnabled</li>
 *   <li>emailNotificationEnabled</li>
 * </ul>
 * These methods can be used to view and update user preferences.</p>
 *
 * @see UserSettings
 * @see UserSettingsRepository
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    /**
     * Retrieves the user settings for the specified user.
     *
     * @param userId the user ID.
     * @return an Optional containing the UserSettings if found; otherwise, empty.
     */
    public Optional<UserSettings> getUserSettings(Long userId) {
        return userSettingsRepository.findById(userId);
    }

    /**
     * Saves or updates the user settings.
     *
     * @param settings the UserSettings object to save.
     * @return the saved UserSettings.
     */
    @Transactional
    public UserSettings saveUserSettings(UserSettings settings) {
        return userSettingsRepository.save(settings);
    }

    /**
     * Updates the language setting for the specified user.
     *
     * @param userId the user ID.
     * @param language the new language value.
     * @return an Optional containing the updated UserSettings if found; otherwise, empty.
     */
    @Transactional
    public Optional<UserSettings> updateLanguage(Long userId, String language) {
        Optional<UserSettings> opt = userSettingsRepository.findById(userId);
        if (opt.isPresent()) {
            UserSettings settings = opt.get();
            settings.setLanguage(language);
            userSettingsRepository.save(settings);
        }
        return opt;
    }

    /**
     * Updates the login notification setting for the specified user.
     *
     * @param userId the user ID.
     * @param enabled true to enable login notifications, false to disable.
     * @return an Optional containing the updated UserSettings if found; otherwise, empty.
     */
    @Transactional
    public Optional<UserSettings> updateLoginNotification(Long userId, boolean enabled) {
        Optional<UserSettings> opt = userSettingsRepository.findById(userId);
        if (opt.isPresent()) {
            UserSettings settings = opt.get();
            settings.setLoginNotificationEnabled(enabled);
            userSettingsRepository.save(settings);
        }
        return opt;
    }

    /**
     * Updates the in-app notification setting for the specified user.
     *
     * @param userId the user ID.
     * @param enabled true to enable in-app notifications, false to disable.
     * @return an Optional containing the updated UserSettings if found; otherwise, empty.
     */
    @Transactional
    public Optional<UserSettings> updateInAppNotification(Long userId, boolean enabled) {
        Optional<UserSettings> opt = userSettingsRepository.findById(userId);
        if (opt.isPresent()) {
            UserSettings settings = opt.get();
            settings.setInAppNotificationEnabled(enabled);
            userSettingsRepository.save(settings);
        }
        return opt;
    }

    /**
     * Updates the email notification setting for the specified user.
     *
     * @param userId the user ID.
     * @param enabled true to enable email notifications, false to disable.
     * @return an Optional containing the updated UserSettings if found; otherwise, empty.
     */
    @Transactional
    public Optional<UserSettings> updateEmailNotification(Long userId, boolean enabled) {
        Optional<UserSettings> opt = userSettingsRepository.findById(userId);
        if (opt.isPresent()) {
            UserSettings settings = opt.get();
            settings.setEmailNotificationEnabled(enabled);
            userSettingsRepository.save(settings);
        }
        return opt;
    }
}
