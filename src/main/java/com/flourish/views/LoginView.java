package com.flourish.views;

import com.flourish.service.UserServiceImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.List;
import java.util.Map;

/**
 * Presents a login interface that authenticates users.
 * Redirects unauthenticated users who attempt to access restricted views.
 *
 * <p>Supports a forgot-password flow and a registration link for new users.</p>
 *
 * <p>Query parameters are used to determine if an error occurred.</p>
 *
 * <p>This view is accessible anonymously at the "login" route, hence {@link AnonymousAllowed}.</p>
 *
 * @author
 *   Joar Eliasson, Kenan Al Tal, Emil Åqvist, Christoffer Salsomonsson
 * @version
 *   1.2.0
 * @since
 *   2025-03-14
 */
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;
    private final UserServiceImpl userService;

    /**
     * Constructs a new LoginView with a Vaadin LoginForm.
     *
     * @param userService the user service implementation handling authentication logic
     */
    public LoginView(UserServiceImpl userService) {
        this.userService = userService;
        addClassName("login-view");

        loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.addClassName("login-form");

        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.addClassName("login-layout");

        LoginI18n i18n = LoginI18n.createDefault();
        if (i18n.getHeader() == null) {
            i18n.setHeader(new LoginI18n.Header());
        }
        i18n.getHeader().setTitle("Please Log In");
        i18n.getHeader().setDescription("Enter your credentials");
        i18n.getForm().setForgotPassword("Forgot password? Click here!");
        loginForm.setI18n(i18n);

        loginForm.addLoginListener(e -> validateLogin(e.getUsername(), e.getPassword()));
        loginForm.addForgotPasswordListener(e -> getUI().ifPresent(ui -> ui.navigate("forgot-password")));

        Button registerButton = new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate("register")));
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerButton.addClassName("login-register-button");

        H3 header = new H3("New to Flourish?");
        header.addClassName("login-register-header");

        loginLayout.add(loginForm, header, registerButton);
        add(loginLayout);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    /**
     * Validates the provided credentials for correct formatting.
     * Shows an error notification if they are invalid.
     *
     * @param username the provided username (email)
     * @param password the provided password
     */
    private void validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showErrorNotification("Email is required!");
        } else if (!username.contains("@")) {
            showErrorNotification("Invalid email format. Please enter a valid email!");
        } else if (password == null || password.trim().isEmpty()) {
            showErrorNotification("Password is required!");
        }
    }

    /**
     * Observes route navigation events. Displays an error if the route
     * includes an "error" parameter, indicating a failed login attempt.
     *
     * @param event the BeforeEnterEvent containing route information
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> queryParams = event.getLocation().getQueryParameters().getParameters();
        String query = queryParams.getOrDefault("error", List.of()).stream().findFirst().orElse(null);
        if (query != null) {
            loginForm.setError(true);
            showErrorNotification("Incorrect username or password");
        }
    }

    /**
     * Displays an error notification with the specified message.
     *
     * @param errorMessage the error message to show
     */
    private void showErrorNotification(String errorMessage) {
        Notification notification = Notification.show(errorMessage, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
