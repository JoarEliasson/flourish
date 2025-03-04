package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.flourish.domain.UserSettings;
import com.flourish.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

/**
 * Unit tests for {@link UserSettingsService}, achieving complete method, line,
 * and branch coverage by testing each service method in present vs. absent settings scenarios.
 * <p>
 * Covers:
 * <ul>
 *   <li>{@code getUserSettings(Long)}</li>
 *   <li>{@code saveUserSettings(UserSettings)}</li>
 *   <li>{@code updateLanguage(Long, String)}</li>
 *   <li>{@code updateLoginNotification(Long, boolean)}</li>
 *   <li>{@code updateInAppNotification(Long, boolean)}</li>
 *   <li>{@code updateEmailNotification(Long, boolean)}</li>
 * </ul>
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-01
 */
@ActiveProfiles("test")
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository userSettingsRepository;

    private UserSettingsService userSettingsService;

    /**
     * Initializes mock dependencies and instantiates {@link UserSettingsService}.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userSettingsService = new UserSettingsService(userSettingsRepository);
    }

    /**
     * Confirms that {@code getUserSettings(Long)} returns an Optional containing
     * the user settings if found, otherwise an empty Optional.
     */
    @Test
    @DisplayName("getUserSettings: present vs. absent")
    void testGetUserSettings() {
        UserSettings existingSettings = new UserSettings(1L, "en", true, false, true);
        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(existingSettings));
        Optional<UserSettings> found = userSettingsService.getUserSettings(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getUserId());

        when(userSettingsRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<UserSettings> notFound = userSettingsService.getUserSettings(2L);
        assertFalse(notFound.isPresent());
    }

    /**
     * Verifies that {@code saveUserSettings(UserSettings)} saves and returns
     * the provided settings object.
     */
    @Test
    @DisplayName("saveUserSettings: normal save call")
    void testSaveUserSettings() {
        UserSettings inputSettings = new UserSettings(10L, "fr", true, true, false);
        UserSettings savedSettings = new UserSettings(10L, "fr", true, true, false);

        when(userSettingsRepository.save(inputSettings)).thenReturn(savedSettings);
        UserSettings result = userSettingsService.saveUserSettings(inputSettings);

        verify(userSettingsRepository).save(inputSettings);
        assertEquals("fr", result.getLanguage());
    }

    /**
     * Checks {@code updateLanguage(Long, String)} with user present vs. absent.
     * If found, updates the language; if not, returns empty.
     */
    @Test
    @DisplayName("updateLanguage: user present vs. absent")
    void testUpdateLanguage() {
        UserSettings existingSettings = new UserSettings(5L, "en", true, false, true);
        when(userSettingsRepository.findById(5L)).thenReturn(Optional.of(existingSettings));

        Optional<UserSettings> updatedOpt = userSettingsService.updateLanguage(5L, "de");
        assertTrue(updatedOpt.isPresent());
        assertEquals("de", updatedOpt.get().getLanguage());
        verify(userSettingsRepository).save(existingSettings);

        when(userSettingsRepository.findById(6L)).thenReturn(Optional.empty());
        Optional<UserSettings> notFoundOpt = userSettingsService.updateLanguage(6L, "it");
        assertFalse(notFoundOpt.isPresent());
    }

    /**
     * Checks {@code updateLoginNotification(Long, boolean)} with user present vs. absent.
     * If found, updates the login notification setting; if not, returns empty.
     */
    @Test
    @DisplayName("updateLoginNotification: user present vs. absent")
    void testUpdateLoginNotification() {
        UserSettings existingSettings = new UserSettings(7L, "en", false, false, false);
        when(userSettingsRepository.findById(7L)).thenReturn(Optional.of(existingSettings));

        Optional<UserSettings> updatedOpt = userSettingsService.updateLoginNotification(7L, true);
        assertTrue(updatedOpt.isPresent());
        assertTrue(updatedOpt.get().isLoginNotificationEnabled());
        verify(userSettingsRepository).save(existingSettings);

        when(userSettingsRepository.findById(8L)).thenReturn(Optional.empty());
        Optional<UserSettings> notFoundOpt = userSettingsService.updateLoginNotification(8L, false);
        assertFalse(notFoundOpt.isPresent());
    }

    /**
     * Checks {@code updateInAppNotification(Long, boolean)} with user present vs. absent.
     * If found, updates the in-app notification setting; if not, returns empty.
     */
    @Test
    @DisplayName("updateInAppNotification: user present vs. absent")
    void testUpdateInAppNotification() {
        UserSettings existingSettings = new UserSettings(15L, "en", true, false, true);
        when(userSettingsRepository.findById(15L)).thenReturn(Optional.of(existingSettings));

        Optional<UserSettings> updatedOpt = userSettingsService.updateInAppNotification(15L, true);
        assertTrue(updatedOpt.isPresent());
        assertTrue(updatedOpt.get().isInAppNotificationEnabled());
        verify(userSettingsRepository).save(existingSettings);

        when(userSettingsRepository.findById(16L)).thenReturn(Optional.empty());
        Optional<UserSettings> notFoundOpt = userSettingsService.updateInAppNotification(16L, false);
        assertFalse(notFoundOpt.isPresent());
    }

    /**
     * Checks {@code updateEmailNotification(Long, boolean)} with user present vs. absent.
     * If found, updates the email notification setting; if not, returns empty.
     */
    @Test
    @DisplayName("updateEmailNotification: user present vs. absent")
    void testUpdateEmailNotification() {
        UserSettings existingSettings = new UserSettings(20L, "en", false, false, false);
        when(userSettingsRepository.findById(20L)).thenReturn(Optional.of(existingSettings));

        Optional<UserSettings> updatedOpt = userSettingsService.updateEmailNotification(20L, true);
        assertTrue(updatedOpt.isPresent());
        assertTrue(updatedOpt.get().isEmailNotificationEnabled());
        verify(userSettingsRepository).save(existingSettings);

        when(userSettingsRepository.findById(21L)).thenReturn(Optional.empty());
        Optional<UserSettings> notFoundOpt = userSettingsService.updateEmailNotification(21L, false);
        assertFalse(notFoundOpt.isPresent());
    }
}
