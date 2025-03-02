package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.UserService;
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
 * A view for new users to register an account.
 *
 * <p>Available at "/register". After successful registration,
 * we navigate to "/login?email=<theUserEmail>" to prefill
 * the login form with the newly created email.</p>
 *
 * <p>This view is fully accessible to anonymous users.</p>
 *
 * <p>Follows a typical pattern of collecting first/last name,
 * email, and password. Email is stored as the username in DB.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Route("register")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final UserService userService;

    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");

    /**
     * Constructs a new RegistrationView.
     *
     * @param userService The service to save new users.
     */
    public RegistrationView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H2("Register New Account"));

        FormLayout formLayout = new FormLayout(
                firstNameField, lastNameField, emailField, passwordField
        );
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        Button registerButton = new Button("Register", e -> handleRegister());
        formLayout.add(registerButton);
        add(formLayout);

        setWidth("400px");
    }

    /**
     * Handles the registration logic: creates a new user in DB,
     * takes the user to the login page.
     */
    private void handleRegister() {
        if (firstNameField.isEmpty() || lastNameField.isEmpty() ||
                emailField.isEmpty() || passwordField.isEmpty()) {
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
            getUI().ifPresent(ui -> {
                ui.navigate("login");
            });

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
