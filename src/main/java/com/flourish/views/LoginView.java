package com.flourish.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * A Vaadin view that serves as the login page.
 *
 * <p>Route-based security is handled by VaadinWebSecurity:
 * If a user is not authenticated and tries to access a secured route,
 * they're redirected here.</p>
 *
 * <p>This route is "login", matching setLoginView in SecurityConfig.</p>
 */
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    /**
     * Constructs a new LoginView with a Vaadin LoginForm.
     */
    public LoginView() {
        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setWidth("400px");
        loginLayout.setPadding(true);
        loginLayout.setSpacing(true);
        loginLayout.setAlignItems(Alignment.CENTER);

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        if (i18n.getHeader() == null) {
            i18n.setHeader(new LoginI18n.Header());
        }
        i18n.getHeader().setTitle("Please Log In");
        i18n.getHeader().setDescription("Enter your credentials");
        i18n.getForm().setForgotPassword("NYTT LÖSENORD TACK");
        loginForm.setI18n(i18n);

        loginForm.addForgotPasswordListener(e -> getUI().ifPresent(ui -> ui.navigate("forgotpassword")));

        Button registerButton = new Button("Register", e ->
                getUI().ifPresent(ui -> ui.navigate("register"))
        );

        H3 header = new H3("New to Flourish?");

        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        registerButton.getStyle().set("cursor", "pointer");
        loginLayout.add(loginForm, header, registerButton);
        add(loginLayout);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }
}
