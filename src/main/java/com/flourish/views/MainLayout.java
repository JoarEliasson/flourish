package com.flourish.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

/**
 * A top-level layout that provides a common header and navigation drawer.
 *
 * <p>This layout can wrap other views via the <code>@Route</code> annotation.</p>
 *
 * @author
 *   Your Name
 * @version
 *   1.0.0
 * @since
 *   1.0.0
 */
@RolesAllowed("USER")
public class MainLayout extends AppLayout {

    /**
     * Constructs the MainLayout, setting up a header
     * and a basic navigation drawer.
     */
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    /**
     * Creates the header portion of the layout.
     */
    private void createHeader() {
        H1 logo = new H1("Flourish");
        logo.getStyle()
                .set("font-size", "1.5em")
                .set("margin", "0");

        addToNavbar(logo);
    }

    /**
     * Creates the navigation drawer.
     */
    private void createDrawer() {
        Nav nav = new Nav();
        nav.add(new Span("Main menu item here..."));
        addToDrawer(nav);
    }
}
