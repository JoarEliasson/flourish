package com.flourish.views;

import java.util.List;
import java.util.stream.Collectors;
import com.flourish.domain.PlantDetails;
import com.flourish.domain.User;
import com.flourish.service.UserPlantLibraryService;
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
    //private final ListBox<Plant> plantList = new ListBox<>();
    private final FlexLayout plantLayout = new FlexLayout();
    private Div selectedPlantDetails = new Div();
    private Plant currentlySelectedPlant = null;
    private final User user;
    private Long userId;

    /**
     * A record representing a plant with an ID, name, and description.
     *
     * @param id                The unique identifier of the plant.
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
    public record Plant(long id, String name, String description, String imageUrl, String watering, String sunlight,
                        String type, Boolean edibleFruit, Boolean poisonousToHumans, Boolean poisonousToPets,
                        Boolean medicinal) {}

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


        List<PlantDetails> userPlants = userPlantLibraryService.getAllPlantDetailsForUser(userId);
        List<Plant> plantData = userPlants.stream()
                .map(details -> new Plant(details.getId(), details.getCommonName(), details.getDescription(), details.getDefaultImageMediumUrl()
                , details.getWatering(), details.getSunlight(), details.getType(),details.getEdibleFruit(), details.getPoisonousToHumans(),
                        details.getPoisonousToPets(),details.getMedicinal()))
                .collect(Collectors.toList());

        for(Plant plant: plantData){

            Div plantDiv = new Div();
            plantDiv.getStyle().set("margin", "10px").set("width","320px").set("height","320px");
            plantDiv.getStyle().set("display", "inline-block").set("border-radius", "8px").set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");
            plantDiv.getStyle().set("background-color","white").set("overflow", "hidden");

            Div imageContainer = new Div();
            imageContainer.getStyle().set("width", "100%").set("height", "200px").set("overflow", "hidden")
                    .set("border-top-left-radius", "8px")
                    .set("border-top-right-radius", "8px");

            Image plantImage = new Image(plant.imageUrl(), plant.name());
            plantImage.getStyle().set("width","100%").set("height","100%").set("object-fit", "cover").set("cursor", "pointer");

            Div infoUnderPicture = new Div();
            infoUnderPicture.getStyle().set("background-color", "white").set("padding", "10px").set("border-top", "1px solid #ccc");
            infoUnderPicture.getStyle().set("width","100%").set("height","120px").set("box-sizing", "border-box");

            Div nameContainer = new Div();
            nameContainer.getStyle().set("display", "flex").set("height", "60px").set("width", "100%").set("align-items", "center")
                    .set("justify-content", "center").set("box-seizing", "border-box").set("border-bottom", "1px solid #333")
                    .set("flex-direction", "column");

            H4 plantName = new H4(plant.name());
            plantName.getStyle().set("margin", "0");
            plantName.getStyle().set("font-size", "1rem");

            H5 typeOfPlant = new H5(plant.type());
            typeOfPlant.getStyle().set("margin", "0").set("font-size", "0.8rem").set("font-style", "italic")
                    .set("color","limegreen");;

            HorizontalLayout iconContainer = iconCreater(plant);
            iconContainer.getStyle().set("height", "60px").set("flex", "1").set("display", "flex").set("justify-content", "center").set("align-items", "center");

            plantImage.addClickListener(e -> showPlantDetails(plant));
            plantName.addClickListener(e -> showPlantDetails(plant));

            nameContainer.add(plantName, typeOfPlant);
            imageContainer.add(plantImage);
            infoUnderPicture.add(nameContainer,iconContainer);
            plantDiv.add(imageContainer, infoUnderPicture);
            plantLayout.add(plantDiv);
        }


        /*plantList.setItems(plantData);
        plantList.setRenderer(new ComponentRenderer<>(plant -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(plant.name);
            avatarItem.setDescription(plant.description);
            avatarItem.setAvatar(new Avatar(plant.name));
            avatarItem.getStyle().set("font-size", "20px").set("padding", "10px");
            return avatarItem;
        }));

         */


    }

    /**
     * Displays the details of the selected plant, the selectedPlantDetails is a hidden div that will be shown when a plant is selected.
     * @param plant
     */
    private void showPlantDetails(Plant plant) {
        if(currentlySelectedPlant != null && currentlySelectedPlant.id() == plant.id()) {
            selectedPlantDetails.getStyle().set("display", "none");
            currentlySelectedPlant = null;
        }else {
            selectedPlantDetails.removeAll();
            selectedPlantDetails.getStyle().set("display", "block");

            Image plantImage = new Image(plant.imageUrl(), plant.name());
            plantImage.setWidth("300px");

            H3 plantName = new H3(plant.name());
            Paragraph description = new Paragraph(plant.description());

            selectedPlantDetails.add(plantImage, plantName, description);
            currentlySelectedPlant = plant;
        }
    }

    /**
     * Creates all the icons that are relevant for a plant, also adds tooltips to the icons.
     * @param plant
     * @return
     */
    public HorizontalLayout iconCreater(Plant plant){
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

        plantLayout.removeAll();
        for(PlantDetails plant: filteredPlants){
            Image plantImage = new Image(plant.getDefaultImageOriginalUrl(), "Plant Image");
            plantImage.setWidth("100%");
            Div plantDiv = new Div();

            plantDiv.add(plantImage, new H3(plant.getCommonName()), new Paragraph(plant.getDescription()));
            plantLayout.add(plantDiv);
        }

        /*List<Plant> plantData = filteredPlants.stream()
                .map(details -> new Plant(details.getId(),details.getCommonName(), details.getDescription()))
                .collect(Collectors.toList());

        plantList.setItems(plantData);

         */
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

        if (currentlySelectedPlant == null) {
            Notification.show("Please select a plant to delete.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        userPlantLibraryService.removePlantFromLibrary(currentlySelectedPlant.id());

        Notification.show("Plant deleted successfully.", 3000, Notification.Position.TOP_CENTER);

        loadUserPlants();


    }
}
