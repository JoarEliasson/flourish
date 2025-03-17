package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.domain.UserSettings;
import com.flourish.service.UserService;
import com.flourish.service.UserSettingsService;
import java.util.Optional;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Displays and edits user settings such as language and notifications.
 *
 * <p>Loads the user's existing settings, allows changes, and persists updates
 * via {@link UserSettingsService}.</p>
 *
 * <p>Requires an authenticated user, otherwise redirects to login.</p>
 *
 * <p>Available at the route "settings" with {@code MainLayout}.</p>
 *
 * @author
 *   Kenan Al Tal, Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-14
 */
@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("USER")
public class SettingsView extends VerticalLayout {

    private final UserSettingsService userSettingsService;
    private final User user;
    private final Long userId;
    private UserSettings userSettings;
    private UserService userService;

    private ComboBox<String> languageSelector;
    private Checkbox emailNotifications;
    private Checkbox notifications;
    private Checkbox loginNotifications;
    private Button saveChanges;
    private Button deleteAccountButton;


    /**
     * Constructs a new SettingsView and initializes components.
     *
     * @param userSettingsService the service managing user settings
     */
    @Autowired
    public SettingsView(UserSettingsService userSettingsService, UserService userService) {
        this.userSettingsService = userSettingsService;
        this.userService=userService;
        addClassName("settings-view");


        user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            userId = null;
            return;
        }
        userId = user.getId();

        createUI();
        loadUserSettings();
    }

    /**
     * Creates and configures the UI components.
     */
    private void createUI() {
        H2 title = new H2("Settings");
        title.addClassName("settings-title");

        languageSelector = new ComboBox<>("Language");
        languageSelector.setItems("English");
        languageSelector.addClassName("settings-language");

        emailNotifications = new Checkbox("Enable Email Notifications");
        emailNotifications.addClassName("settings-checkbox");

        notifications = new Checkbox("Enable In-App Notifications");
        notifications.addClassName("settings-checkbox");

        loginNotifications = new Checkbox("Enable Login Notifications");
        loginNotifications.addClassName("settings-checkbox");

        saveChanges = new Button("Save Changes", event -> saveUserSettings());
        saveChanges.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveChanges.addClassName("settings-save-button");

        deleteAccountButton = new Button("Delete Account", event -> deleteAccount());
        deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountButton.getStyle().set("margin-top", "20px");

        add(title, languageSelector, emailNotifications, notifications, loginNotifications, saveChanges,deleteAccountButton);
    }

    /**
     * Loads the user's current settings from the database and updates the UI.
     */
    private void loadUserSettings() {
        Optional<UserSettings> settingsOpt = userSettingsService.getUserSettings(userId);
        if (settingsOpt.isPresent()) {
            userSettings = settingsOpt.get();
            languageSelector.setValue(userSettings.getLanguage());
            emailNotifications.setValue(userSettings.isEmailNotificationEnabled());
            notifications.setValue(userSettings.isInAppNotificationEnabled());
            loginNotifications.setValue(userSettings.isLoginNotificationEnabled());
        } else {
            Notification.show("No settings found for this user.", 3000, Notification.Position.TOP_CENTER);
        }
    }

    /**
     * Saves the changes to user settings and displays a notification.
     */
    private void saveUserSettings() {
        if (userSettings != null) {
            userSettings.setLanguage(languageSelector.getValue());
            userSettings.setEmailNotificationEnabled(emailNotifications.getValue());
            userSettings.setInAppNotificationEnabled(notifications.getValue());
            userSettings.setLoginNotificationEnabled(loginNotifications.getValue());
            userSettingsService.saveUserSettings(userSettings);
            Notification.show("Settings saved successfully.", 3000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Deletes the user's account and clears the session.
     * @author Zahraa Alqassab
     * @since 2025-03-09
     */
    private void deleteAccount() {
        try {
            userService.deleteByEmail(user.getEmail());
            Notification.show("Your account has been deleted.", 3000, Notification.Position.TOP_CENTER);
            VaadinSession.getCurrent().setAttribute("user", null);
            UI.getCurrent().navigate("login");
        } catch (Exception e) {
            Notification.show("Error deleting account: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }
}