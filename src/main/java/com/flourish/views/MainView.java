package com.flourish.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * Main application view shown to authenticated users.
 *
 * <p>This view is displayed within the {@link MainLayout}, ensuring
 * a consistent header/navigation structure. Only "USER" roles
 * can access.</p>
 *
 * @see MainLayout
 */
@Route(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class MainView extends VerticalLayout {

    /**
     * Constructs a new MainView.
     */
    public MainView() {
        add(new H2("Welcome to the Flourish Application!"));
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
    }
}
