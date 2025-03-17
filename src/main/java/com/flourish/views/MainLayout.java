package com.flourish.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

/**
 * A top-level layout that provides a common header and navigation bar.
 */
@RolesAllowed("USER")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        setPrimarySection(Section.NAVBAR);
        getElement().getStyle().set("background-color", "#e8f5e9");
    }

    private void createHeader() {
        H1 logo = new H1("Flourish");
        logo.getStyle()
                .set("color", "white")
                .set("font-size", "28px")
                .set("margin", "0");

        Avatar profileAvatar = new Avatar("USER");
        profileAvatar.setImage("images/DefualtImage.png");
        ContextMenu menu = new ContextMenu(profileAvatar);
        menu.setOpenOnClick(true);
        menu.addItem("Account Settings", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
        menu.addItem("Toggle Email Notifications", e -> {});
        menu.addItem("Log Out", e -> logout());

        // Navigation buttons
        HorizontalLayout navBar = new HorizontalLayout(
                logo,
                createNavButton("Home", ""),
                createNavButton("My Plants", "my-plants"),
                createNavButton("All Plants", "all-plants"),
                createNavButton("Settings", "settings"),
                createNavButton("Notifications", "notifications"),
                profileAvatar
        );
        navBar.setWidthFull();
        navBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navBar.setAlignItems(FlexComponent.Alignment.CENTER);
        navBar.getStyle()
                .set("background-color", "#66bb6a") // Match button background
                .set("padding", "10px");

        addToNavbar(navBar);
    }

    private Button createNavButton(String text, String route) {
        Button button = new Button(text, e -> getUI().ifPresent(ui -> ui.navigate(route)));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("background-color", "#66bb6a").set("color", "white").set("font-size", "18px");
        return button;
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        getUI().ifPresent(ui -> ui.getPage().setLocation("login"));
    }
}