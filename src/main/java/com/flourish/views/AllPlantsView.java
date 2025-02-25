package com.flourish.views;

import com.flourish.service.PlantSearchService;
import com.flourish.domain.PlantIndex;
import com.flourish.views.components.AvatarItem;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;

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
    private final MultiSelectListBox<PlantIndex> plantList;

    public AllPlantsView(PlantSearchService plantSearchService) {
        this.plantSearchService = plantSearchService;

        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("All Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(event -> updatePlantList(event.getValue()));

        plantList = new MultiSelectListBox<>();
        plantList.setWidth("100%");
        plantList.getStyle().set("font-size", "18px").set("padding", "10px");

        Button addButton = new Button("Add to My Plants", event -> addSelectedPlants());
        addButton.getStyle().set("background-color", "#388e3c").set("color", "white").set("font-size", "16px");

        HorizontalLayout controls = new HorizontalLayout(searchField, addButton);
        controls.setWidth("100%");

        updatePlantList("");

        getContent().add(title, controls, plantList);
    }

    private void updatePlantList(String query) {
        List<PlantIndex> plants = plantSearchService.search(query);
        plantList.setItems(plants);
        plantList.setRenderer(new ComponentRenderer<>(plant -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(plant.getCommonName());
            avatarItem.setDescription(plant.getScientificName());
            avatarItem.setAvatar(new Avatar(plant.getCommonName()));
            avatarItem.getStyle().set("font-size", "20px").set("padding", "10px");
            return avatarItem;
        }));
    }

    private void addSelectedPlants() {
        List<PlantIndex> selectedPlants = plantList.getSelectedItems().stream().toList();
        // TODO: Add selected plant to my plants
    }
}