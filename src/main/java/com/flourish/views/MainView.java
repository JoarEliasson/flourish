package com.flourish.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;

/**
 * A protected main view for authenticated users with role USER.
 *
 * <p>Shown at "/main" using MainLayout. If an unauthenticated user
 * attempts to access it, they are redirected to "/login".</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Route(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class MainView extends VerticalLayout {

    /**
     * Constructs a new MainView with a welcome message.
     */
    public MainView() {
        add(new H1("Welcome to the Main View!"));
    }
}
