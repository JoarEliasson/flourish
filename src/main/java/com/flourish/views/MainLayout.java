package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.PlantNotificationService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

/**
 * A top-level layout that provides a common header and navigation bar.
 */
@RolesAllowed("USER")
public class MainLayout extends AppLayout {
    private final PlantNotificationService notificationService;

    public MainLayout(PlantNotificationService notificationService) {
        this.notificationService= notificationService;
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
        ContextMenu menu = new ContextMenu(profileAvatar);
        menu.setOpenOnClick(true);
        menu.addItem("Account Settings", e -> {});
        menu.addItem("Toggle Email Notifications", e -> {});
        menu.addItem("Log Out", e -> logout());


        User user = (User) VaadinSession.getCurrent().getAttribute("user");
        int notificationCount = 0;
        if (user != null) {
            notificationCount = notificationService.generateNotificationsForUser(user.getId()).size();
        }
        Button notificationsButton = createNavButton("Notifications", "notifications");
        HorizontalLayout notificationsLayout = new HorizontalLayout(notificationsButton);
        if (notificationCount > 0) {
            Span badge = new Span(String.valueOf(notificationCount));
            badge.getStyle()
                    .set("color", "white")
                    .set("background-color", "red")
                    .set("border-radius", "50%")
                    .set("padding", "0 6px")
                    .set("font-weight", "bold")
                    .set("margin-left", "5px");
            notificationsLayout.add(badge);
        }

        // Navigation buttons
        HorizontalLayout navBar = new HorizontalLayout(
                logo,
                createNavButton("Home", ""),
                createNavButton("My Plants", "my-plants"),
                createNavButton("All Plants", "all-plants"),
                createNavButton("Settings", "settings"),
               // createNavButton("Notifications", "notifications"),
                notificationsLayout,
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