package com.flourish.views;

import com.flourish.domain.PlantDetails;
import com.flourish.domain.User;
import com.flourish.service.PlantDetailsService;
import com.flourish.service.PlantSearchService;
import com.flourish.domain.PlantIndex;
import com.flourish.service.UserPlantLibraryService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A view for displaying all available plants with a search function.
 *
 * @author
 *   Kenan Al Tal
 * @version
 *   1.0.0
 * @since
 *   2025-02-25
 */

@PageTitle("All Plants")
@Route(value = "all-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class AllPlantsView extends Composite<VerticalLayout> {

    private final PlantSearchService plantSearchService;
    private final PlantDetailsService plantDetailsService;
    private final UserPlantLibraryService userPlantLibraryService;
    private Grid<PlantIndex> plantGrid;
    private final User user;
    private Long userId;


    public AllPlantsView(PlantSearchService plantSearchService, PlantDetailsService plantDetailsService, UserPlantLibraryService userPlantLibraryService) {

        this.plantSearchService = plantSearchService;
        this.plantDetailsService = plantDetailsService;
        this.userPlantLibraryService = userPlantLibraryService;

        user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        userId = user.getId();



        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("All Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(event -> updatePlantList(event.getValue()));

        plantGrid = new Grid<>(PlantIndex.class, false);
        plantGrid.setWidth("100%");
        plantGrid.addColumn(PlantIndex::getCommonName).setHeader("Common Name").setAutoWidth(true);
        plantGrid.addColumn(PlantIndex::getScientificName).setHeader("Scientific Name").setAutoWidth(true);

        plantGrid.setItemDetailsRenderer(new ComponentRenderer<>(plant -> {
            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.setPadding(false);
            detailsLayout.setSpacing(false);

            Optional<PlantDetails> detailsOpt = plantDetailsService.getPlantDetailsByPlantIndex(plant);
            detailsOpt.ifPresentOrElse(details -> {
                detailsLayout.add("Description: " + details.getDescription());
            }, () -> {
                detailsLayout.add("Description not available.");
            });

            Button addButton = new Button("Add to My Plants", event -> addToMyPlants(plant));
            detailsLayout.add(addButton);


            return detailsLayout;
        }));

        updatePlantList("");

        getContent().add(title, searchField, plantGrid);
    }

    private void updatePlantList(String query) {
        List<PlantIndex> plants = plantSearchService.search(query);
        plantGrid.setItems(plants);
    }

    private void addToMyPlants(PlantIndex plant) {
        userPlantLibraryService.addPlantToLibrary(userId, plant)
                .ifPresentOrElse(
                        entry -> Notification.show(plant.getCommonName() + " added to My Plants!", 3000, Notification.Position.TOP_CENTER),
                        () -> Notification.show("Failed to add " + plant.getCommonName(), 3000, Notification.Position.TOP_CENTER)
                );
    }
}
