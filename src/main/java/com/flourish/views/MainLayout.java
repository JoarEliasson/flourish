package com.flourish.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

/**
 * A top-level layout that provides a common header and navigation drawer.
 *
 * <p>This layout can wrap other views via the <code>@Route</code> annotation.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
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
        DrawerToggle toggle = new DrawerToggle();
        Span title = new Span("Flourish");
        H1 logo = new H1("Flourish");
        logo.getStyle()
                .set("font-size", "1.5em")
                .set("margin", "0");

        Avatar profileAvatar = new Avatar("USER");
        ContextMenu menu = new ContextMenu(profileAvatar);
        menu.setOpenOnClick(true);

        menu.addItem("Account Settings", e -> Notification.show("Settings Clicked"));
        menu.addItem("Toggle Email Notifications",e -> Notification.show("Email Notifications Toggled"));
        menu.addItem("Log Out", e -> logout());

        HorizontalLayout header = new HorizontalLayout(toggle,title,profileAvatar);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
      //  header.setAlignItems(FlexComponent.JustifyContentMode.BETWEEN);
        header.setPadding(true);

        addToNavbar(header);
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        getUI().ifPresent(ui ->
                ui.getPage().setLocation("login"));
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
