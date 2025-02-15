package com.flourish.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

/**
 * A Vaadin view for signing into the application.
 *
 * <p>This view is shown for unauthorized users.
 * It leverages Vaadin's LoginForm and integrates with Spring Security
 * via the VaadinWebSecurity configuration in SecurityConfig.</p>
 *
 * <p>Uses a centered layout for responsiveness.</p>
 */
@Route("signin")
@PermitAll
@CssImport("./styles/views/signin/sign-in-view.css") // Optional custom CSS
public class SignInView extends FlexLayout {

    private final LoginForm loginForm = new LoginForm();

    /**
     * Constructs a new SignInView.
     */
    public SignInView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setFlexDirection(FlexDirection.COLUMN);

        H1 title = new H1("Please Sign In");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Flourish Application");
        i18n.getHeader().setDescription("Enter your credentials to continue");
        loginForm.setI18n(i18n);
        loginForm.setAction("login");

        Button registerButton = new Button("Register", e ->
                getUI().ifPresent(ui -> ui.navigate("register"))
        );

        Label registerLabel = new Label("Don't have an account?");
        VerticalLayout registerLayout = new VerticalLayout(registerLabel, registerButton);
        registerLayout.setAlignItems(Alignment.CENTER);

        add(title, loginForm, registerLayout);
    }
}
