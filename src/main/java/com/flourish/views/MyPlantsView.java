package com.flourish.views;

import java.util.List;
import java.util.Optional;

import com.flourish.domain.LibraryEntry;
import com.flourish.security.UserSessionData;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.views.components.WaterGauge;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;


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

    private final UserSessionData userSessionData;
    private final UserPlantLibraryService userPlantLibraryService;

    private final FlexLayout plantLayout = new FlexLayout();
    private Div selectedPlantDetails = new Div();
    private Plant currentlySelectedPlant = null;

    /**
     * A record representing a plant with an ID, name, and description.
     *
     * <p>This record is used to store plant data for display in the UI.</p>
     * <p>It is a simple data container with no behavior.</p>
     * @param libraryId         The library ID of the plant.
     * @param plantId           The plant ID of the plant.
     * @param name              The common name of the plant.
     * @param description       A short description of the plant.
     * @param watering
     * @param sunlight
     * @param type
     * @param edibleFruit
     * @param poisonousToHumans
     * @param poisonousToPets
     * @param medicinal
     */
    public record Plant(long libraryId, long plantId, String name, String description, String imageUrl, String watering, String sunlight,
                        String type, Boolean edibleFruit, Boolean poisonousToHumans, Boolean poisonousToPets,
                        Boolean medicinal) {}

    /**
     * Constructs the MyPlantsView and initializes the UI components.
     *
     * <p>The User id is saved using VaadinSession</p>
     *
     * @param userPlantLibraryService The service for managing user plant data.
     */
    @Autowired
    public MyPlantsView(UserPlantLibraryService userPlantLibraryService, UserSessionData userSessionData) {

        this.userPlantLibraryService = userPlantLibraryService;
        this.userSessionData = userSessionData;

        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("My Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        TextField searchField = new TextField("Search Plants");
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(event -> updatePlantList(event.getValue()));

        plantLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        plantLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        plantLayout.setAlignItems(FlexLayout.Alignment.START);
        plantLayout.setWidthFull();
        //plantList.setWidth("100%");
        //plantList.getStyle().set("font-size", "18px").set("padding", "10px");
        selectedPlantDetails.getStyle().set("display", "none");
        selectedPlantDetails.getStyle()
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)")
                .set("margin-bottom", "20px")
                .set("width", "80%");

        loadUserPlants();

        Button deleteButton = new Button("Delete Selected Plant", event -> deleteSelectedPlant());
        deleteButton.getStyle().set("background-color", "#d32f2f").set("color", "white");

        HorizontalLayout controlsLayout = new HorizontalLayout(searchField, deleteButton);
        controlsLayout.setWidthFull();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        getContent().add(title, selectedPlantDetails, controlsLayout, plantLayout);

    }

    /**
     * Loads the plants associated with the user and populates the plant list.
     */
    private void loadUserPlants() {
        plantLayout.removeAll();
        List<LibraryEntry> userLibraryEntries = userSessionData.getPlantLibraryEntries();

        List<Plant> userLibraryPlants = userLibraryEntries.stream()
                .map(entry -> new Plant(entry.getLibraryId(), entry.getPlantDetails().getId(), entry.getPlantDetails().getCommonName(),
                        entry.getPlantDetails().getDescription(), entry.getPlantDetails().getDefaultImageOriginalUrl(),
                        entry.getPlantDetails().getWatering(), entry.getPlantDetails().getSunlight(),
                        entry.getPlantDetails().getType(), entry.getPlantDetails().getEdibleFruit(),
                        entry.getPlantDetails().getPoisonousToHumans(), entry.getPlantDetails().getPoisonousToPets(),
                        entry.getPlantDetails().getMedicinal()))
                .toList();

        loadPlants(userLibraryPlants);

    }

    /**
     * Displays details for the selected plant along with a water gauge.
     *
     * <p>This method retrieves the UserPlantLibrary entry corresponding to the selected plant,
     * then calls the service to obtain the watering gauge percentage. A WaterGauge component is created,
     * its value is set, and it is added to the view along with plant image and name.</p>
     *
     * @param plant the selected plant record.
     */
    private void showPlantDetails(Plant plant) {

        Optional<LibraryEntry> userPlantOpt = userSessionData.getPlantLibraryEntries().stream()
                .filter(entry -> entry.getPlantDetails().getId() == plant.plantId())
                .findFirst();

        if (userPlantOpt.isEmpty()) {
            Notification.show("No library entry found for this plant.", 3000, Notification.Position.TOP_CENTER);
            return;
        }


        if (currentlySelectedPlant != null && currentlySelectedPlant.libraryId == plant.libraryId) {
            selectedPlantDetails.getStyle().set("display", "none");
            currentlySelectedPlant = null;
            return;
        }

        selectedPlantDetails.removeAll();
        selectedPlantDetails.getStyle().set("display", "block");

        Image plantImage = new Image(plant.imageUrl(), plant.name());
        plantImage.setWidth("300px");

        H3 plantName = new H3(plant.name());
        Paragraph description = new Paragraph(plant.description());

        Optional<Double> gaugePercentageOpt = userPlantLibraryService.getWateringGaugePercentage(userPlantOpt.get().getLibraryId());
        double gaugeValue = gaugePercentageOpt.orElse(0.0);

        WaterGauge waterGauge = new WaterGauge();
        waterGauge.setWaterLevel(gaugeValue);

        selectedPlantDetails.add(plantImage, plantName, description, waterGauge);
        currentlySelectedPlant = plant;
    }

    /**
     * Creates all the icons that are relevant for a plant, also adds tooltips to the icons.
     * @param plant
     * @return
     */
    public HorizontalLayout iconCreator(Plant plant){
        HorizontalLayout icons = new HorizontalLayout();

        Icon sunIcon = VaadinIcon.SUN_O.create();
        sunIcon.getStyle().set("color", "orange").set("font-size", "1.5rem").set("margin", "0");
        Tooltip sunTooltip = Tooltip.forComponent(sunIcon);
        sunTooltip.setText("Sunlight: " + plant.sunlight());

        Tooltip waterTooltip;
        Image waterIcon;
        if(plant.watering().equals("Frequent")){
            waterIcon = new Image("images/Rain.png", "Rain Icon");
        }else if(plant.watering().equals("Average")){
            waterIcon = new Image("images/AverageWater.png", "Rain Icon");
        }else {
            waterIcon = new Image("images/MinimalWater.png", "Rain Icon");
        }
        waterIcon.getStyle().set("color", "blue").set("font-size", "1.5rem").set("margin", "0");
        waterTooltip = Tooltip.forComponent(waterIcon);
        waterTooltip.setText("Watering: " + plant.watering());
        icons.add(sunIcon, waterIcon);

        if(plant.medicinal()) {
            Image medicinalIcon = new Image("images/Medicinal.png", "Medicinal Icon");
            medicinalIcon.getStyle().set("color", "red").set("font-size", "1.5rem").set("margin", "0");
            Tooltip medicinalTooltip = Tooltip.forComponent(medicinalIcon);
            medicinalTooltip.setText("Medicinal Plant");
            icons.add(medicinalIcon);
        }
        if(plant.edibleFruit()) {
            Icon fruitIcon = VaadinIcon.CUTLERY.create();
            fruitIcon.getStyle().set("color", "green").set("font-size", "1.5rem").set("margin", "0");
            Tooltip fruitTooltip = Tooltip.forComponent(fruitIcon);
            fruitTooltip.setText("Edible Fruit");
            icons.add(fruitIcon);
        }
        if(plant.poisonousToHumans() || plant.poisonousToPets()) {
            Image poisonIcon = new Image("images/Poison.png", "Poison Icon");
            poisonIcon.getStyle().set("color", "red").set("font-size", "1.5rem").set("margin", "0").set("height", "25px")
                    .set("width", "25px");
            Tooltip humanPoisonTooltip = Tooltip.forComponent(poisonIcon);
            if(plant.poisonousToPets()&& plant.poisonousToHumans()) {
                humanPoisonTooltip.setText("Poisonous to Humans and Pets");
            }else if(plant.poisonousToHumans()) {
                humanPoisonTooltip.setText("Poisonous to Humans");
            }else {
                humanPoisonTooltip.setText("Poisonous to Pets");
            }
            icons.add(poisonIcon);
        }

        return icons;
    }

    private void updatePlantList(String query) {
        List<Plant> userLibraryPlants = userSessionData.getPlantLibraryEntries().stream()
                .map(entry -> new Plant(entry.getLibraryId(), entry.getPlantDetails().getId(), entry.getPlantDetails().getCommonName(),
                        entry.getPlantDetails().getDescription(), entry.getPlantDetails().getDefaultImageOriginalUrl(),
                        entry.getPlantDetails().getWatering(), entry.getPlantDetails().getSunlight(),
                        entry.getPlantDetails().getType(), entry.getPlantDetails().getEdibleFruit(),
                        entry.getPlantDetails().getPoisonousToHumans(), entry.getPlantDetails().getPoisonousToPets(),
                        entry.getPlantDetails().getMedicinal()))
                .filter(plant -> plant.name().toLowerCase().contains(query.toLowerCase()))
                .toList();

        plantLayout.removeAll();
        loadPlants(userLibraryPlants);

    }

    private void deleteSelectedPlant() {
        if (currentlySelectedPlant != null) {
            userPlantLibraryService.removePlantFromLibrary(currentlySelectedPlant.libraryId);
            Notification.show("Plant deleted successfully.", 3000, Notification.Position.TOP_CENTER);
            loadUserPlants();
        } else {
            Notification.show("Please select a plant to delete.", 3000, Notification.Position.TOP_CENTER);

        }
    }

    private void loadPlants(List<Plant> plants) {
        for (Plant plant : plants) {
            Div plantDiv = new Div();
            plantDiv.getStyle().set("margin", "10px").set("width", "320px").set("height", "320px")
                    .set("display", "inline-block").set("border-radius", "8px")
                    .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)")
                    .set("background-color", "white").set("overflow", "hidden");

            Div imageContainer = new Div();
            imageContainer.getStyle().set("width", "100%").set("height", "200px").set("overflow", "hidden")
                    .set("border-top-left-radius", "8px")
                    .set("border-top-right-radius", "8px");

            Image plantImage = new Image(plant.imageUrl(), plant.name());
            plantImage.getStyle().set("width", "100%").set("height", "100%")
                    .set("object-fit", "cover").set("cursor", "pointer");

            Div infoUnderPicture = new Div();
            infoUnderPicture.getStyle().set("background-color", "white").set("padding", "10px")
                    .set("border-top", "1px solid #ccc")
                    .set("width", "100%").set("height", "120px").set("box-sizing", "border-box");

            Div nameContainer = new Div();
            nameContainer.getStyle().set("display", "flex").set("height", "60px")
                    .set("width", "100%").set("align-items", "center")
                    .set("justify-content", "center").set("flex-direction", "column")
                    .set("border-bottom", "1px solid #333");

            H4 plantName = new H4(plant.name());
            plantName.getStyle().set("margin", "0").set("font-size", "1rem");

            H4 typeOfPlant = new H4(plant.type());
            typeOfPlant.getStyle().set("margin", "0").set("font-size", "0.8rem")
                    .set("font-style", "italic").set("color", "limegreen");

            HorizontalLayout iconContainer = iconCreator(plant);
            iconContainer.getStyle().set("height", "60px").set("flex", "1").set("display", "flex").set("justify-content", "center").set("align-items", "center");

            plantImage.addClickListener(e -> showPlantDetails(plant));
            plantName.addClickListener(e -> showPlantDetails(plant));

            nameContainer.add(plantName, typeOfPlant);
            imageContainer.add(plantImage);
            infoUnderPicture.add(nameContainer);
            plantDiv.add(imageContainer, infoUnderPicture);
            plantLayout.add(plantDiv);
        }
    }
}
