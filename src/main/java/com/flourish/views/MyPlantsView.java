package com.flourish.views;

import java.util.List;
import java.util.stream.Collectors;
import com.flourish.domain.PlantDetails;
import com.flourish.domain.User;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.views.components.AvatarItem;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;


/**
 * A view for displaying and managing the user's plant collection.
 *
 * <p>This class allows authenticated users to view, search, and delete plants
 * from their personal library.</p>
 *
 * @author
 *   Kenan Al Tal
 * @version
 *   1.0.0
 * @since
 *   2025-02-21
 */

@PageTitle("My Plants")
@Route(value = "my-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> {

    private final UserPlantLibraryService userPlantLibraryService;
    private final ListBox<Plant> plantList = new ListBox<>();
    private final User user;
    private Long userId;

    /**
     * A record representing a plant with an ID, name, and description.
     *
     * @param id The unique identifier of the plant.
     * @param name The common name of the plant.
     * @param description A short description of the plant.
     */
    public record Plant(long id, String name, String description) {}

    /**
     * Constructs the MyPlantsView and initializes the UI components.
     *
     * <p>The User id is saved using VaadinSession</p>
     *
     * @param userPlantLibraryService The service for managing user plant data.
     */
    public MyPlantsView(UserPlantLibraryService userPlantLibraryService) {
        this.userPlantLibraryService = userPlantLibraryService;

        user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        userId = user.getId();

        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("My Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(event -> updatePlantList(event.getValue()));

        plantList.setWidth("100%");
        plantList.getStyle().set("font-size", "18px").set("padding", "10px");

        loadUserPlants();

        Button deleteButton = new Button("Delete Selected Plant", event -> deleteSelectedPlant());
        deleteButton.getStyle().set("background-color", "#d32f2f").set("color", "white");

        HorizontalLayout controlsLayout = new HorizontalLayout(searchField, deleteButton);
        controlsLayout.setWidthFull();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        getContent().add(title, controlsLayout, plantList);
    }

    /**
     * Loads the plants associated with the user and populates the plant list.
     */
    private void loadUserPlants() {
        List<PlantDetails> userPlants = userPlantLibraryService.getAllPlantDetailsForUser(userId);
        List<Plant> plantData = userPlants.stream()
                .map(details -> new Plant(details.getId(), details.getCommonName(), details.getDescription()))
                .collect(Collectors.toList());

        plantList.setItems(plantData);
        plantList.setRenderer(new ComponentRenderer<>(plant -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(plant.name);
            avatarItem.setDescription(plant.description);
            avatarItem.setAvatar(new Avatar(plant.name));
            avatarItem.getStyle().set("font-size", "20px").set("padding", "10px");
            return avatarItem;
        }));
    }

    /**
     * Filters the plant list based on the search query.
     *
     * @warning OBSS!!!! Implementation is NOT tested.
     * !!!!!!!!!!!!!!!!!!!
     *
     * @param query The search query entered by the user.
     */
    private void updatePlantList(String query) {
        List<PlantDetails> filteredPlants = userPlantLibraryService.getAllPlantDetailsForUser(userId).stream()
                .filter(plant -> plant.getCommonName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        List<Plant> plantData = filteredPlants.stream()
                .map(details -> new Plant(details.getId(),details.getCommonName(), details.getDescription()))
                .collect(Collectors.toList());

        plantList.setItems(plantData);
    }

    /**
     * Deletes the selected plant from the user's library.
     *
     * @warning OBSS!!!! There is an issue with this implementation: The plants are not getting deleted,
     * the issue is with the plantId, The Id needed is the entry Id for the plant (ID in the user_plant_library table).
     * Check the getAllPlantDetailsForUser method in the userPlantLibraryService class.
     * !!!!!!!!!!!!!!!!!!!
     */
    private void deleteSelectedPlant() {
        Plant selectedPlant = plantList.getValue();
        if (selectedPlant == null) {
            Notification.show("Please select a plant to delete.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        userPlantLibraryService.removePlantFromLibrary(selectedPlant.id());

        Notification.show("Plant deleted successfully.", 3000, Notification.Position.TOP_CENTER);

        loadUserPlants();
    }
}