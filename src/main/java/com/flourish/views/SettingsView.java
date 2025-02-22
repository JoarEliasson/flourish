package com.flourish.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("USER")

public class SettingsView  extends VerticalLayout{

    public SettingsView(){
        getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("Settings");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        Checkbox emailNotifications = new Checkbox("Enable Email Notifications");
        Checkbox darkMode = new Checkbox("Enable Dark Mode");
        PasswordField changePassword = new PasswordField("Change Password");

        H3 passwordTitle = new H3("Change Password");
        passwordTitle.getStyle().set("color", "#388e3c").set("font-size", "24px");

        PasswordField oldPassword = new PasswordField("Current Password");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmPassword = new PasswordField("Confirm New Password");

        oldPassword.setWidth("100%");
        newPassword.setWidth("100%");
        confirmPassword.setWidth("100%");

        Button saveChanges = new Button("Save Changes");
        saveChanges.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveChanges.getStyle().set("background-color", "#66bb6a").set("color", "white");

        Button deleteAccount = new Button("Delete Account");
        deleteAccount.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccount.getStyle().set("margin-top", "20px");

        add(title, emailNotifications, darkMode, passwordTitle, oldPassword,
                newPassword, confirmPassword, saveChanges, deleteAccount);
    }
}