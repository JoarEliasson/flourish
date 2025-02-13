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

@Route("login")
@PageTitle("Login")
/**
 * The login view of the Flourish application.
 * <p>
 * This view allows users to authenticate by providing their username and password.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public class LoginView extends VerticalLayout {

    private final UserService userService;

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;
        initComponents();
    }

    private void initComponents() {
        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        loginButton = new Button("Login", event -> processLogin());

        add(usernameField, passwordField, loginButton);
    }

    private void processLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        try {
            User user = userService.authenticate(username, password);
            Notification.show("Login successful, welcome " + user.getUsername());
            getUI().ifPresent(ui -> ui.navigate(MainView.class));
        } catch (Exception e) {
            Notification.show("Login failed: " + e.getMessage());
        }
    }
}
