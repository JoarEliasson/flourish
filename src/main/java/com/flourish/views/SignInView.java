package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * A single Vaadin view handling both sign-in and registration.
 *
 * <p>We toggle entire layouts (signInLayout and registerLayout)
 * when switching tabs so the user can see the correct content.</p>
 */
@Route("signin")
@PermitAll
@CssImport("./styles/views/signin/sign-in-view.css") // optional styling
public class SignInView extends FlexLayout {

    private final LoginForm loginForm = new LoginForm();
    private VerticalLayout signInLayout;

    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private VerticalLayout registerLayout;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs a new SignInView.
     *
     * @param userService          For creating new users.
     * @param authenticationManager For programmatic login after registration.
     */
    public SignInView(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;

        setSizeFull();
        setFlexDirection(FlexDirection.COLUMN);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Tab signInTab = new Tab("Sign In");
        Tab registerTab = new Tab("Register");
        Tabs tabs = new Tabs(signInTab, registerTab);

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == signInTab) {
                showSignInForm();
            } else {
                showRegisterForm();
            }
        });

        signInLayout = createSignInLayout();
        registerLayout = createRegisterLayout();

        signInLayout.setVisible(true);
        registerLayout.setVisible(false);

        HorizontalLayout formsContainer = new HorizontalLayout(signInLayout, registerLayout);
        formsContainer.setSizeUndefined();

        add(new H2("Welcome to Flourish"), tabs, formsContainer);
    }

    /**
     * Creates the layout with the Vaadin LoginForm for sign-in.
     *
     * @return The sign-in layout
     */
    private VerticalLayout createSignInLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Sign In");
        i18n.getHeader().setDescription("Enter your credentials");
        loginForm.setI18n(i18n);
        loginForm.setAction("login");

        layout.add(loginForm);
        return layout;
    }

    /**
     * Creates the layout with fields to register a new user.
     *
     * @return The registration layout
     */
    private VerticalLayout createRegisterLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        FormLayout form = new FormLayout();
        form.add(firstNameField, lastNameField, emailField, passwordField);

        Button registerButton = new Button("Register", e -> handleRegister());
        form.add(registerButton);

        layout.add(form);
        return layout;
    }

    /**
     * Displays the sign-in form, hides the register form.
     */
    private void showSignInForm() {
        signInLayout.setVisible(true);
        registerLayout.setVisible(false);
    }

    /**
     * Displays the register form, hides the sign-in form.
     */
    private void showRegisterForm() {
        signInLayout.setVisible(false);
        registerLayout.setVisible(true);
    }

    /**
     * Creates a new user, then programmatically logs them in.
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

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(newUser.getEmail(), passwordField.getValue());
            Authentication authResult = authenticationManager.authenticate(authRequest);
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .setAuthentication(authResult);

            Notification.show("Registration successful. You're now logged in!");
            getUI().ifPresent(ui -> ui.navigate(""));
        } catch (Exception ex) {
            Notification.show("Registration failed: " + ex.getMessage());
        }
    }
}
