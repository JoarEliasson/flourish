package com.flourish.views;

import com.flourish.domain.UserSettings;
import com.flourish.service.UserSettingsService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * A view for Settings to view and edit the user's settings.
 *
 * @author
 *   Kenan Al Tal
 * @version
 *   1.1.0
 * @since
 *   2025-02-21
 */

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("USER")
public class SettingsView extends VerticalLayout {

    private final UserSettingsService userSettingsService;
    private final Long loggedInUserId = 1L; // Temporary placeholder, late replaced with the actual ID

    private ComboBox<String> languageSelector;
    private Checkbox emailNotifications;
    private Checkbox notifications;
    private Checkbox loginNotifications;
    private Button saveChanges;

    private UserSettings userSettings;

    @Autowired
    public SettingsView(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;

        setupUI();
        loadUserSettings();
    }

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

        add(title, languageSelector, emailNotifications, notifications, loginNotifications, saveChanges);
    }

    private void loadUserSettings() {
        Optional<UserSettings> settingsOpt = userSettingsService.getUserSettings(loggedInUserId);
            userSettings = settingsOpt.get();
            languageSelector.setValue(userSettings.getLanguage());
            emailNotifications.setValue(userSettings.isEmailNotificationEnabled());
            notifications.setValue(userSettings.isInAppNotificationEnabled());
            loginNotifications.setValue(userSettings.isLoginNotificationEnabled());

    }

    private void saveUserSettings() {
        userSettings.setLanguage(languageSelector.getValue());
        userSettings.setEmailNotificationEnabled(emailNotifications.getValue());
        userSettings.setInAppNotificationEnabled(notifications.getValue());
        userSettings.setLoginNotificationEnabled(loginNotifications.getValue());

        userSettingsService.saveUserSettings(userSettings);
        Notification.show("Settings saved successfully", 3000, Notification.Position.MIDDLE);
    }
}