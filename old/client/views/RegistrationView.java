package com.flourish.old.client.views;

import com.flourish.domain.model.User;
import com.flourish.domain.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * View for user registration.
 * <p>
 * Allows new users to register by providing their username, email, password,
 * and preferences for notifications and fun facts. Upon successful registration,
 * the user is prompted to sign in.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Route("register")
@PageTitle("Register")
public class RegistrationView extends VerticalLayout {

    private final UserService userService;

    private TextField usernameField;
    private EmailField emailField;
    private PasswordField passwordField;
    private Checkbox notificationCheckbox;
    private Checkbox funFactsCheckbox;
    private Button registerUserButton;
    private Button backToLoginButton;

    /**
     * Constructs the registration view with the specified {@link UserService}.
     *
     * @param userService the user service used to register new users
     */
    @Autowired
    public RegistrationView(UserService userService) {
        this.userService = userService;
        initComponents();
    }

    /**
     * Initializes and arranges the UI components.
     */
    private void initComponents() {
        usernameField = new TextField("Username");
        emailField = new EmailField("Email");
        passwordField = new PasswordField("Password");
        notificationCheckbox = new Checkbox("Enable Notifications");
        funFactsCheckbox = new Checkbox("Enable Fun Facts");

        registerUserButton = new Button("Register", event -> registerUser());
        backToLoginButton = new Button("Back to Login", event -> getUI().ifPresent(ui -> ui.navigate("login")));

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, emailField, passwordField, notificationCheckbox, funFactsCheckbox);

        add(formLayout, registerUserButton, backToLoginButton);
    }

    /**
     * Handles the user registration process.
     * <p>
     * Collects the input data, invokes the {@link UserService#register} method,
     * and shows a notification on success or failure. On successful registration,
     * the user is redirected to the login view.
     * </p>
     */
    private void registerUser() {
        String username = usernameField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();
        Boolean notifications = notificationCheckbox.getValue();
        Boolean funFacts = funFactsCheckbox.getValue();

        try {
            User user = userService.register(username, email, password, notifications, funFacts);
            Notification.show("Registration successful! Please log in.", 3000, Notification.Position.MIDDLE);
            // Navigate to login view after successful registration
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (Exception e) {
            Notification.show("Registration failed: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
}
