package com.flourish.client.views;

import com.flourish.domain.model.User;
import com.flourish.domain.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * View for user sign-in (login).
 * <p>
 * Allows existing users to sign in by providing their username and password.
 * On successful authentication, the user is navigated to the main view.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private final UserService userService;

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;

    /**
     * Constructs the login view with the specified {@link UserService}.
     *
     * @param userService the user service used for authentication
     */
    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;
        initComponents();
    }

    /**
     * Initializes and arranges the UI components.
     */
    private void initComponents() {
        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        loginButton = new Button("Login", event -> processLogin());
        registerButton = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));

        add(usernameField, passwordField, loginButton, registerButton);
    }

    /**
     * Handles the login process.
     * <p>
     * Retrieves user input, invokes the {@link UserService#authenticate} method,
     * and navigates to the main view on successful login.
     * </p>
     */
    private void processLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        try {
            User user = userService.authenticate(username, password);
            Notification.show("Login successful, welcome " + user.getUsername(), 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("main"));
        } catch (Exception e) {
            Notification.show("Login failed: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Returns the username field.
     *
     * @return the username field
     */
    public TextField getUsernameField() {
        return usernameField;
    }

    /**
     * Returns the password field.
     *
     * @return the password field
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * Returns the login button.
     *
     * @return the login button
     */
    public Button getLoginButton() {
        return loginButton;
    }

}

