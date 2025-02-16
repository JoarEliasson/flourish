package com.flourish.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * The initial landing page of the Flourish application,
 * displayed at the root route ("/").
 *
 * <p>Provides buttons for users to navigate to either
 * the login page or the registration page. This view
 * does not require authentication.</p>
 *
 * @author
 *   Your Name
 * @version
 *   1.0.0
 * @since
 *   1.0.0
 */
@Route("start")
@AnonymousAllowed
public class LandingView extends VerticalLayout {

    /**
     * Constructs a new LandingView with two navigation buttons:
     * "Go to Login" and "Go to Register".
     */
    public LandingView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H2("Welcome to Flourish!"));

        Button loginButton = new Button("Go to Login", e ->
                getUI().ifPresent(ui -> ui.navigate("login"))
        );
        Button registerButton = new Button("Go to Register", e ->
                getUI().ifPresent(ui -> ui.navigate("register"))
        );

        add(loginButton, registerButton);
    }
}
