package com.flourish.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.flourish.views.components.AvatarItem;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("My Plants")
@Route("")
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> {

    // Define Plant record at the class level
    public record Plant(String name, String description) {
    }

    public MyPlantsView() {
        // Set page background color to light green
        getContent().getStyle()
                .set("background-color", "#e8f5e9") // Light green shade
                .set("padding", "20px");

        H1 title = new H1("Flowrish");
        title.getStyle().set("color", "#2e7d32") // Dark green text
                .set("font-size", "36px");

        H2 subtitle = new H2("My Plants");
        subtitle.getStyle().set("color", "#388e3c") // Green shade
                .set("font-size", "28px");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);

        // Creating styled buttons
        Button myPlantsButton = createStyledButton("My Plants");
        Button allPlantsButton = createStyledButton("All Plants");
        Button settingsButton = createStyledButton("Settings");
        Button notificationsButton = createStyledButton("Notifications");

        buttonLayout.add(myPlantsButton, allPlantsButton, settingsButton, notificationsButton);

        // Search field
        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");

        // List box to display plant information
        MultiSelectListBox<Plant> plantList = new MultiSelectListBox<>();
        plantList.setWidth("100%");
        plantList.getStyle().set("font-size", "18px")
                .set("padding", "10px");

        setPlantSampleData(plantList);

        getContent().add(title, subtitle, buttonLayout, searchField, plantList);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("background-color", "#66bb6a") // Light green
                .set("color", "white")
                .set("font-size", "18px");
        return button;
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
            avatarItem.setAvatar(new Avatar(plant.name)); // Placeholder avatar
            avatarItem.getStyle().set("font-size", "20px")
                    .set("padding", "10px");
            return avatarItem;
        }));
    }
}
