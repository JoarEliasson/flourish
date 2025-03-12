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
 * A view for Settings to view and edit the user's settings.
 *
 * <p>This class allows authenticated users to view and update their settings,
 * such as language preference and notification preferences.</p>
 *
 * @author
 *   Kenan Al Tal
 * @version
 *   1.0.0
 * @since
 *   2025-02-21
 */
@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("USER")
public class SettingsView extends VerticalLayout {

    private final UserSettingsService userSettingsService;
    private final User user;
    private Long userId;
    private UserSettings userSettings;
    private UserService userService; // Added UserService for account deletion


    private ComboBox<String> languageSelector;
    private Checkbox emailNotifications;
    private Checkbox notifications;
    private Checkbox loginNotifications;
    private Button saveChanges;
    private Button deleteAccountButton;



    /**
     * Constructs the SettingsView and initializes the UI components.
     *
     * <p>The User id is saved using VaadinSession</p>
     *
     * @param userSettingsService The service for handling user settings.
     */
    @Autowired
    public SettingsView(UserSettingsService userSettingsService, UserService userService) {
        this.userSettingsService = userSettingsService;
        this.userService=userService;

        user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        userId = user.getId();

        setupUI();
        loadUserSettings();
    }

    /**
     * Sets up the UI components for the settings page.
     */
    private void setupUI() {
        getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("Settings");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        languageSelector = new ComboBox<>("Language");
        languageSelector.setItems("English");
        languageSelector.setWidth("250px");

        emailNotifications = new Checkbox("Enable Email Notifications");
        notifications = new Checkbox("Enable In-App Notifications");
        loginNotifications = new Checkbox("Enable Login Notifications");

        saveChanges = new Button("Save Changes", event -> saveUserSettings());
        saveChanges.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveChanges.getStyle().set("background-color", "#66bb6a").set("color", "white");

        deleteAccountButton = new Button("Delete Account", event -> deleteAccount());
        deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountButton.getStyle().set("margin-top", "20px");

        add(title, languageSelector, emailNotifications, notifications, loginNotifications, saveChanges,deleteAccountButton);
    }

    /**
     * Loads the current user's settings (saved in the database) and updates the UI components accordingly.
     */
    private void loadUserSettings() {
        Optional<UserSettings> settingsOpt = userSettingsService.getUserSettings(userId);
            userSettings = settingsOpt.get();
            languageSelector.setValue(userSettings.getLanguage());
            emailNotifications.setValue(userSettings.isEmailNotificationEnabled());
            notifications.setValue(userSettings.isInAppNotificationEnabled());
            loginNotifications.setValue(userSettings.isLoginNotificationEnabled());
    }

    /**
     * Saves the user's updated settings to the database
     */
    private void saveUserSettings() {
        userSettings.setLanguage(languageSelector.getValue());
        userSettings.setEmailNotificationEnabled(emailNotifications.getValue());
        userSettings.setInAppNotificationEnabled(notifications.getValue());
        userSettings.setLoginNotificationEnabled(loginNotifications.getValue());
        userSettingsService.saveUserSettings(userSettings);

        Notification.show("Settings saved successfully", 3000, Notification.Position.MIDDLE);
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