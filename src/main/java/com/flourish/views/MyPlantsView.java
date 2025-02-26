package com.flourish.views;

import com.flourish.domain.PlantDetails;
import com.flourish.service.UserPlantLibraryService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.flourish.views.components.AvatarItem;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A view for My plants to view and edit the user's plants.
 *
 * @author
 *   Kenan Al Tal
 * @version
 *   1.1.0
 * @since
 *   2025-02-21
 */

@PageTitle("My Plants")
@Route(value = "my-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> {

    private final UserPlantLibraryService userPlantLibraryService;
    private final Long userId = 1L; // Temporary placeholder, should be dynamically assigned

    public record Plant(String name, String description) {}

    public MyPlantsView(UserPlantLibraryService userPlantLibraryService) {
        this.userPlantLibraryService = userPlantLibraryService;

        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("My Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        // Declare plantList first
        MultiSelectListBox<Plant> plantList = new MultiSelectListBox<>();
        plantList.setWidth("100%");
        plantList.getStyle().set("font-size", "18px").set("padding", "10px");

        // Now it's safe to use it in the listener
        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(event -> updatePlantList(plantList, event.getValue()));

        // Load the user's plants into the list
        loadUserPlants(plantList);

        getContent().add(title, searchField, plantList);
    }


    private void loadUserPlants(MultiSelectListBox<Plant> plantList) {
        List<PlantDetails> userPlants = userPlantLibraryService.getAllPlantDetailsForUser(userId);
        List<Plant> plantData = userPlants.stream()
                .map(details -> new Plant(details.getCommonName(), details.getDescription()))
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

    private void updatePlantList(MultiSelectListBox<Plant> plantList, String query) {
        List<PlantDetails> filteredPlants = userPlantLibraryService.getAllPlantDetailsForUser(userId).stream()
                .filter(plant -> plant.getCommonName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        List<Plant> plantData = filteredPlants.stream()
                .map(details -> new Plant(details.getCommonName(), details.getDescription()))
                .collect(Collectors.toList());

        plantList.setItems(plantData);
    }
}