package com.flourish.views;

import com.flourish.domain.PlantDetails;
import com.flourish.domain.PlantIndex;
import com.flourish.domain.User;
import com.flourish.service.PlantDetailsService;
import com.flourish.service.PlantSearchService;
import com.flourish.service.UserPlantLibraryService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

/**
 * Provides a view for displaying all available plants with a search function.
 * Users can search, view descriptions, and add plants to their personal library.
 *
 * <p>Redirects to the login view if no authenticated user is found in the session.</p>
 *
 * @author
 *   Kenan Al Tal, Joar Eliasson
 * @version
 *   1.2.0
 * @since
 *   2025-03-14
 */
@PageTitle("All Plants")
@Route(value = "all-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class AllPlantsView extends Composite<VerticalLayout> {

    private final PlantSearchService plantSearchService;
    private final PlantDetailsService plantDetailsService;
    private final UserPlantLibraryService userPlantLibraryService;
    private Grid<PlantIndex> plantGrid;
    private final Long userId;

    /**
     * Constructs a new AllPlantsView, initializing service references, user data, and UI components.
     *
     * @param plantSearchService      service handling plant search queries
     * @param plantDetailsService     service retrieving detailed plant data
     * @param userPlantLibraryService service managing the user's plant library
     */
    public AllPlantsView(
            PlantSearchService plantSearchService,
            PlantDetailsService plantDetailsService,
            UserPlantLibraryService userPlantLibraryService
    ) {
        this.plantSearchService = plantSearchService;
        this.plantDetailsService = plantDetailsService;
        this.userPlantLibraryService = userPlantLibraryService;

        User user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            userId = null;
            return;
        }
        userId = user.getId();

        getContent().addClassName("all-plants-view");

        H2 title = new H2("All Plants");
        title.addClassName("all-plants-title");

        TextField searchField = new TextField("Search Plants");
        searchField.addClassName("all-plants-search");
        searchField.addValueChangeListener(event -> updatePlantList(event.getValue()));

        plantGrid = new Grid<>(PlantIndex.class, false);
        plantGrid.addClassName("all-plants-grid");
        plantGrid.addColumn(PlantIndex::getCommonName).setHeader("Common Name").setAutoWidth(true);
        plantGrid.addColumn(PlantIndex::getScientificName).setHeader("Scientific Name").setAutoWidth(true);
        plantGrid.setItemDetailsRenderer(new ComponentRenderer<>(plant -> {
            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.addClassName("all-plants-details-layout");
            Optional<PlantDetails> detailsOpt = plantDetailsService.getPlantDetailsByPlantIndex(plant);
            detailsOpt.ifPresentOrElse(
                    details -> detailsLayout.add("Description: " + details.getDescription()),
                    () -> detailsLayout.add("Description not available.")
            );
            Button addButton = new Button("Add to My Plants", e -> addToMyPlants(plant));
            addButton.addClassName("all-plants-add-button");
            detailsLayout.add(addButton);
            return detailsLayout;
        }));

        updatePlantList("");

        getContent().add(title, searchField, plantGrid);
    }

    /**
     * Updates the grid items based on the provided search query.
     *
     * @param query the text to filter plants by name or scientific name
     */
    private void updatePlantList(String query) {
        List<PlantIndex> plants = plantSearchService.search(query);
        plantGrid.setItems(plants);
    }

    /**
     * Adds the selected plant to the user's personal library and displays a notification of the outcome.
     *
     * @param plant the plant to add to the library
     */
    private void addToMyPlants(PlantIndex plant) {
        userPlantLibraryService.addPlantToLibrary(userId, plant).ifPresentOrElse(
                entry -> Notification.show(
                        plant.getCommonName() + " added to My Plants!",
                        3000,
                        Notification.Position.TOP_CENTER
                ),
                () -> Notification.show(
                        "Failed to add " + plant.getCommonName(),
                        3000,
                        Notification.Position.TOP_CENTER
                )
        );
    }
}
