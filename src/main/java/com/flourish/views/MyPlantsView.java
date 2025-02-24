package com.flourish.views;

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

    public record Plant(String name, String description) {}

    public MyPlantsView() {
        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("My Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");

        MultiSelectListBox<Plant> plantList = new MultiSelectListBox<>();
        plantList.setWidth("100%");
        plantList.getStyle().set("font-size", "18px").set("padding", "10px");

        setPlantSampleData(plantList);

        getContent().add(title, searchField, plantList);
    }

    private void setPlantSampleData(MultiSelectListBox<Plant> plantList) {
        List<Plant> data = List.of(
                new Plant("Aloe Vera", "Healing plant, easy to maintain"),
                new Plant("Snake Plant", "Air purifier, requires little sunlight"),
                new Plant("Monstera", "Tropical, large green leaves"),
                new Plant("Pothos", "Fast-growing vine, low maintenance")
        );

        plantList.setItems(data);
        plantList.setRenderer(new ComponentRenderer<>(plant -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(plant.name);
            avatarItem.setDescription(plant.description);
            avatarItem.setAvatar(new Avatar(plant.name));
            avatarItem.getStyle().set("font-size", "20px").set("padding", "10px");
            return avatarItem;
        }));
    }
}