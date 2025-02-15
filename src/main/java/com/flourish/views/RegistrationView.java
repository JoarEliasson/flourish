package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * A Vaadin view for registering new users.
 *
 * <p>Provides a responsive form layout, so fields
 * appear in one column on narrow screens and two
 * columns on wider screens.</p>
 */
@Route("register")
@PermitAll
public class RegistrationView extends VerticalLayout {

    private final UserService userService;

    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");

    /**
     * Constructs a new RegistrationView.
     *
     * @param userService The service handling user creation logic.
     */
    public RegistrationView(UserService userService) {
        this.userService = userService;

        // Center the form in the page
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        H1 heading = new H1("Register New Account");
        add(heading);

        // Create a form layout
        FormLayout formLayout = new FormLayout();
        // Add fields
        formLayout.add(firstNameField, lastNameField, emailField, passwordField);

        // Configure responsive steps
        formLayout.setResponsiveSteps(
                // min-width 0px: 1 column
                new FormLayout.ResponsiveStep("0", 1),
                // min-width 600px: 2 columns
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Add some recommended widths
        firstNameField.setWidthFull();
        lastNameField.setWidthFull();
        emailField.setWidthFull();
        passwordField.setWidthFull();

        // We can wrap the formLayout + button in a VerticalLayout
        VerticalLayout formWrapper = new VerticalLayout(formLayout);
        formWrapper.setWidth("400px"); // optional fixed max width
        formWrapper.setAlignItems(Alignment.STRETCH);

        Button registerButton = new Button("Register", event -> handleRegister());
        registerButton.setWidthFull();

        formWrapper.add(registerButton);
        add(formWrapper);

        // Link back to sign in
        Button signInButton = new Button("Back to Sign In",
                e -> getUI().ifPresent(ui -> ui.navigate("signin"))
        );
        add(new Paragraph("Already have an account?"), signInButton);
    }

    /**
     * Handles registration by collecting form data and creating a new User
     * via the UserService.
     */
    private void handleRegister() {
        if (firstNameField.isEmpty() || lastNameField.isEmpty() ||
                emailField.isEmpty() || passwordField.isEmpty()) {
            Notification.show("Please fill in all fields.");
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
            Notification.show("Registration successful. Please log in.");
            getUI().ifPresent(ui -> ui.navigate("signin"));
        } catch (Exception ex) {
            Notification.show("Registration failed: " + ex.getMessage());
        }
    }
}
