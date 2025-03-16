package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.security.UserSessionData;
import com.flourish.service.UserPlantLibraryService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

/**
 * Provides a landing page for authenticated users.
 * Displays a welcome title, a description, navigation buttons, and a feature image.
 *
 * <p>Ensures that the authenticated user's session data is initialized.
 * Redirects to the login view if the user is not logged in.</p>
 *
 * @author
 *   Joar Eliasson, Kenan Al Tal, Emil Åqvist
 * @version
 *   1.2.0
 * @since
 *   2025-03-14
 */
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class HomeView extends VerticalLayout {

    private final UserSessionData userSessionData;
    private final UserPlantLibraryService userPlantLibraryService;

    /**
     * Constructs a new HomeView with session data and plant library services.
     *
     * @param userSessionData the session data bean
     * @param userPlantLibraryService the service managing user plant library data
     */
    public HomeView(UserSessionData userSessionData, UserPlantLibraryService userPlantLibraryService) {
        this.userSessionData = userSessionData;
        this.userPlantLibraryService = userPlantLibraryService;
        addClassName("home-view");
        initUserSessionData();
        createLayout();
    }

    /**
     * Builds the layout, including a title, description, navigation buttons, and an image.
     */
    private void createLayout() {
        H1 title = new H1("Welcome to Flourish!");
        title.addClassName("home-title");

        Paragraph desc = new Paragraph("Your personal plant library!");
        desc.addClassName("home-desc");

        com.vaadin.flow.component.button.Button myPlantsButton =
                new com.vaadin.flow.component.button.Button("My Plants!", e -> getUI().ifPresent(ui -> ui.navigate("my-plants")));
        myPlantsButton.addClassName("home-nav-button");

        com.vaadin.flow.component.button.Button allPlantsButton =
                new com.vaadin.flow.component.button.Button("All Plants!", e -> getUI().ifPresent(ui -> ui.navigate("all-plants")));
        allPlantsButton.addClassName("home-nav-button");

        HorizontalLayout buttonLayout = new HorizontalLayout(myPlantsButton, allPlantsButton);
        buttonLayout.addClassName("home-button-layout");

        Image plantImage = new Image(
                "https://plus.unsplash.com/premium_photo-1675864663002-c330710c6ba0?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cGxhbnR8ZW58MHx8MHx8fDA%3D",
                "Plant Hero Image"
        );
        plantImage.addClassName("home-hero-image");

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(title, desc, buttonLayout, plantImage);
    }

    /**
     * Initializes user session data and redirects to the login view if the user is not authenticated.
     */
    private void initUserSessionData() {
        User user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }
        if (userSessionData.getUserId() == null) {
            userSessionData.setUserId(user.getId());
            userSessionData.setUsername(user.getEmail());
            userSessionData.setPlantLibraryEntries(
                    userPlantLibraryService.getAllLibraryEntriesForUser(user.getId())
            );
        }
    }
}
