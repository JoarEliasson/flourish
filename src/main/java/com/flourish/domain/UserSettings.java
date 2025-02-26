package com.flourish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents user-specific settings.
 *
 * <p>This entity stores settings related to the user's preferences:
 * <ul>
 *   <li>language: the user's preferred language (e.g., "en", "sv").</li>
 *   <li>loginNotificationEnabled: if notifications upon login are enabled.</li>
 *   <li>inAppNotificationEnabled: if notifications while logged in are enabled.</li>
 *   <li>emailNotificationEnabled: if email notifications are enabled.</li>
 * </ul>
 * The primary key is the user’s ID. This design assumes a one-to-one relationship with a User entity.
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Entity
@Table(name = "user_settings")
public class UserSettings {

    /**
     * The user’s unique ID, used as the primary key.
     */
    @Id
    private Long userId;

    /**
     * The user's preferred language (e.g., "en", "sv").
     */
    @Column(name = "language", nullable = false)
    private String language;

    /**
     * Indicates whether notifications upon logging in are enabled.
     */
    @Column(name = "login_notification_enabled", nullable = false)
    private boolean loginNotificationEnabled;

    /**
     * Indicates whether in-app notifications while logged in are enabled.
     */
    @Column(name = "in_app_notification_enabled", nullable = false)
    private boolean inAppNotificationEnabled;

    /**
     * Indicates whether email notifications are enabled.
     */
    @Column(name = "email_notification_enabled", nullable = false)
    private boolean emailNotificationEnabled;

    /**
     * Default constructor required by JPA.
     */
    protected UserSettings() { }

    /**
     * Constructs a new UserSettings object.
     *
     * @param userId the user's unique ID.
     * @param language the user's preferred language.
     * @param loginNotificationEnabled true if login notifications are enabled.
     * @param inAppNotificationEnabled true if in-app notifications are enabled.
     * @param emailNotificationEnabled true if email notifications are enabled.
     */
    public UserSettings(Long userId, String language, boolean loginNotificationEnabled,
                        boolean inAppNotificationEnabled, boolean emailNotificationEnabled) {
        this.userId = userId;
        this.language = language;
        this.loginNotificationEnabled = loginNotificationEnabled;
        this.inAppNotificationEnabled = inAppNotificationEnabled;
        this.emailNotificationEnabled = emailNotificationEnabled;
    }

    public Long getUserId() {
        return userId;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isLoginNotificationEnabled() {
        return loginNotificationEnabled;
    }

    public boolean isInAppNotificationEnabled() {
        return inAppNotificationEnabled;
    }

    public boolean isEmailNotificationEnabled() {
        return emailNotificationEnabled;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLoginNotificationEnabled(boolean loginNotificationEnabled) {
        this.loginNotificationEnabled = loginNotificationEnabled;
    }

    public void setInAppNotificationEnabled(boolean inAppNotificationEnabled) {
        this.inAppNotificationEnabled = inAppNotificationEnabled;
    }

    public void setEmailNotificationEnabled(boolean emailNotificationEnabled) {
        this.emailNotificationEnabled = emailNotificationEnabled;
    }
}
