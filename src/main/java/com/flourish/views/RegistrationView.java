package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.flourish.service.UserServiceImpl;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Map;


/**
 * Provides a registration form for new users. Accessible at "/register."
 * Navigates to "/login" upon successful registration.
 *
 * <p>Collects first name, last name, email, and password. Email is used as the username.</p>
 *
 * <p>This view is available to anonymous users.</p>
 *
 * <p>Example usage:
 * <pre>{@code ui.navigate("register");}</pre>
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-14
 */
@Route("register")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final UserServiceImpl userService;
    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");

    /**
     * Constructs a RegistrationView for new user sign-ups.
     *
     * @param userService the service to create and save user records
     */
    public RegistrationView(UserServiceImpl userService) {
        this.userService = userService;
        addClassName("registration-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 header = new H2("Register New Account");
        header.addClassName("registration-header");

        FormLayout formLayout = new FormLayout(
                firstNameField,
                lastNameField,
                emailField,
                passwordField
        );
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.addClassName("registration-form");

        Button registerButton = new Button("Register", event -> handleRegister());
        registerButton.addClassName("registration-button");
        formLayout.add(registerButton);

        add(header, formLayout);
    }

    /**
     * Handles user registration by creating a new record and storing it in the database.
     * Redirects to the login view if successful.
     */
    private void handleRegister() {
        if (firstNameField.isEmpty() || lastNameField.isEmpty()
                || emailField.isEmpty() || passwordField.isEmpty()) {
            Notification.show("Please fill all fields.");
            return;
        }
        User newUser = new User(
                firstNameField.getValue(),
                lastNameField.getValue(),
                emailField.getValue(),
                passwordField.getValue(),
                "USER"
        );
        try {
            userService.createUser(newUser);
            Notification.show("Registration successful!");
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (Exception ex) {
            Notification.show("Registration failed: " + ex.getMessage());
        }
    }

    /**
     *@author Zahraa Alqassab
     **/
    public void triggerRegistration(){
        handleRegister();
    }

    /**
     * Gets the first name input field.
     *@author Zahraa Alqassab
     * @return the text field where the user enters their first name.
     */
    public TextField getFirstNameField() {
        return firstNameField;
    }

    /**
     * Gets the last name input field.
     *@author Zahraa Alqassab
     * @return the text field where the user enters their last name.
     */
    public TextField getLastNameField() {
        return lastNameField;
    }

    /**
     * Gets the email input field.
     *@author Zahraa Alqassab
     * @return the email field where the user enters their email address.
     */
    public EmailField getEmailField() {
        return emailField;
    }

    /**
     * Gets the password input field.
     * @author Zahraa Alqassab
     * @return the password field where the user enters their password.
     */
    public PasswordField getPasswordField() {
        return passwordField;
    }
}
