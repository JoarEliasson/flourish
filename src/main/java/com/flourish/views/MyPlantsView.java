package com.flourish.views;

//import com.vaadin.flow.component.map.Map;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;

import com.flourish.domain.LibraryEntry;
import com.flourish.security.UserSessionData;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.views.components.WaterGauge;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A view for displaying and managing the user's plant collection.
 *
 * <ul>
 *   <li>Displays a list of plants from the user's library, each with a delete icon and characteristic icons.</li>
 *   <li>Provides search functionality to filter the user’s library.</li>
 *   <li>Supports a details panel showing water gauge and a "Mark as Watered" button.</li>
 *   <li>Displays a "Find New Plants" button when the library is empty.</li>
 *   <li>Uses BeforeEnterObserver to refresh user data on every navigation to this view.</li>
 * </ul>
 *
 * ------------------------------------
 * @MartinFrick
 * Added hastags and filter functionality
 * ------------------------------------
 *
 * @author
 *   Kenan Al Tal, Joar Eliasson, Martin Frick
 * @version
 *   1.1.1
 * @since
 *   2025-03-09
 */
@PageTitle("My Plants")
@Route(value = "my-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserSessionData userSessionData;
    private final UserPlantLibraryService userPlantLibraryService;
    private final FlexLayout plantLayout = new FlexLayout();
    private final Div emptyLibraryNotice = new Div();
    private final Div selectedPlantDetails = new Div();
    private final TextField searchField = new TextField("Search Plants");
    private final TextField plantDetailsHashtagField = new TextField("Hashtag field");
    private final Set<String> selectedHashtags = new HashSet<>();
    private Button resetFilterButton = new Button();
    private final Div mainViewHashtagFilterField = new Div();

    /**
     * A record representing a plant with necessary details for the UI.
     *
     * @param libraryId         The ID of the user's library entry (used forremoval, watering).
     * @param plantId           The ID of the actual plant details record.
     * @param name              The plant's common name.
     * @param description       A short textual description (shown in detail view).
     * @param imageUrl          The URL of the plant’s image.
     * @param watering          Watering requirement string (e.g., Frequent, Average, Minimum).
     * @param sunlight          Sunlight requirement string.
     * @param type              The plant type/category.
     * @param edibleFruit       True if the plant produces edible fruit.
     * @param poisonousToHumans True if the plant is poisonous to humans.
     * @param poisonousToPets   True if the plant is poisonous to pets.
     * @param medicinal         True if the plant is medicinal.
     * @param hashtags          List of hashtags associated with the library entry.
     */
    public record Plant(long libraryId,
                       long plantId,
                       String name,
                       String description,
                       String imageUrl,
                       String watering,
                       String sunlight,
                       String type,
                       Boolean edibleFruit,
                       Boolean poisonousToHumans,
                       Boolean poisonousToPets,
                       Boolean medicinal,
                       List<String> hashtags
    )
    {}

    /**
     * Constructs the MyPlantsView and initializes UI components.
     */
    @Autowired
    public MyPlantsView(UserPlantLibraryService userPlantLibraryService,
                        UserSessionData userSessionData) {
        this.userPlantLibraryService = userPlantLibraryService;
        this.userSessionData = userSessionData;

        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        getContent().addClassName("my-plants-view");
        plantLayout.addClassName("my-plants-layout");
        selectedPlantDetails.addClassName("my-plants-details");
        emptyLibraryNotice.addClassName("my-plants-empty-notice");
        searchField.addClassName("my-plants-search");

        H2 title = new H2("My Plants");
        title.addClassName("my-plants-title");

        searchField.addValueChangeListener(e -> filterPlantList(e.getValue()));
        plantDetailsHashtagField.setVisible(false);

        plantLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        plantLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        plantLayout.setAlignItems(FlexLayout.Alignment.START);
        plantLayout.setFlexWrap(FlexWrap.WRAP);
        plantLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        plantLayout.setAlignItems(FlexComponent.Alignment.START);
        plantLayout.getStyle()
                .set("display", "flex")
                .set("gap", "10px")
                .set("align-items", "stretch");
        plantLayout.setWidthFull();

        selectedPlantDetails.setVisible(false);

        emptyLibraryNotice.setVisible(false);

        HorizontalLayout topBar = new HorizontalLayout(title, searchField);
        topBar.setWidthFull();
        topBar.setFlexGrow(1, searchField);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        //--------------------------------------------------------------------
        mainViewHashtagFilterField.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "5px")
                .set("align-items", "start")
                .set("width", "200px")
                .set("height", "400px") // Fixed height to prevent jumping
                .set("overflow-y", "auto")
                .set("overflow-x", "auto")
                .set("background-color", "#e8e8e8")
                .set("padding", "10px")
                .set("border-radius", "5px")
                .set("border", "1px solid #ccc");


        resetFilterButton.addClickListener(btClick -> resetFilter());
        resetFilterButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resetFilterButton.getStyle()
                .set("min-height", "40px")
                .set("width", "100%")
                .set("background-color", "red")
                .set("color", "white")
                .set("opacity", "0")
                .set("visibility", "hidden");

        VerticalLayout rightBar = new VerticalLayout(resetFilterButton, mainViewHashtagFilterField);
        rightBar.setWidth("250px");
        rightBar.getStyle().set("flex-shrink", "0");
        rightBar.getStyle().set("align-items", "start");
        rightBar.getStyle().set("min-height", "200px");

        plantLayout.setWidth("100%");
/*
        rightBar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setFlexGrow(1, plantLayout);
        getContent().add(new HorizontalLayout(plantLayout, rightBar));*/


        HorizontalLayout mainLayout = new HorizontalLayout(plantLayout, rightBar);
        mainLayout.setWidthFull();
        mainLayout.setFlexGrow(1, plantLayout);
        mainLayout.setSpacing(true);


        //--------------------------------------------------------------------

        getContent().add(topBar, selectedPlantDetails, mainLayout, emptyLibraryNotice);
    }

    /**
     * Called each time the user navigates to this view, ensuring that
     * the user's library data and UI are refreshed.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        refreshPlantList();
    }

    /**
     * Reloads the user's library entries from the service and updates the UI.
     */
    private void refreshPlantList() {
        List<LibraryEntry> entries = userPlantLibraryService.getAllLibraryEntriesForUser(userSessionData.getUserId());
        userSessionData.setPlantLibraryEntries(entries);
        List<Plant> mappedPlants = mapLibraryEntriesToPlants(entries);

        populateRightBarHashtags();

        String currentQuery = searchField.getValue() != null ? searchField.getValue() : "";
        List<Plant> filtered = mappedPlants.stream()
                .filter(p -> p.name().toLowerCase().contains(currentQuery.toLowerCase()))
                .collect(Collectors.toList());

        updatePlantLayout(filtered);

//        populateAvailableFilters();

    }

    private void filterByHashtag(String hashtag) {

        System.out.println("Existing hashtags coming in: " + selectedHashtags);

        if (selectedHashtags.contains(hashtag)) {
            selectedHashtags.remove(hashtag);
        } else {
            selectedHashtags.add(hashtag);
        }

        List<LibraryEntry> entries = userSessionData.getPlantLibraryEntries();
        List<Plant> filteredPlants = mapLibraryEntriesToPlants(entries).stream()
                .filter(p -> selectedHashtags.isEmpty() || p.hashtags().stream().anyMatch(selectedHashtags::contains))
                .collect(Collectors.toList());

        updatePlantLayout(filteredPlants);

        resetFilterButton.setVisible(!selectedHashtags.isEmpty());

        System.out.println("Existing hashtags going out: " + selectedHashtags);

    }

    /**
     * Filters the current library data using the specified search query.
     */
    private void filterPlantList(String query) {
        List<LibraryEntry> entries = userSessionData.getPlantLibraryEntries();
        List<Plant> filteredPlants = mapLibraryEntriesToPlants(entries).stream()
                .filter(p -> p.name().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        updatePlantLayout(filteredPlants);

    }

    /**
     * Maps the user's library entries to {@link Plant} records for display.
     *
     * @param entries the user's library entries
     * @return a list of Plant records derived from the entries
     */
    private List<Plant> mapLibraryEntriesToPlants(List<LibraryEntry> entries) {
        return entries.stream()
                .map(entry -> new Plant(
                        entry.getUserPlantLibrary().getId(),
                        entry.getPlantDetails().getId(),
                        entry.getPlantDetails().getCommonName(),
                        entry.getPlantDetails().getDescription(),
                        entry.getPlantDetails().getDefaultImageOriginalUrl(),
                        entry.getPlantDetails().getWatering(),
                        entry.getPlantDetails().getSunlight(),
                        entry.getPlantDetails().getType(),
                        entry.getPlantDetails().getEdibleFruit(),
                        entry.getPlantDetails().getPoisonousToHumans(),
                        entry.getPlantDetails().getPoisonousToPets(),
                        entry.getPlantDetails().getMedicinal(),
                        entry.getUserPlantLibrary().getHashtags()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Updates the plant layout and hides/shows the empty-library notice as appropriate.
     *
     * @param plants the list of plants to display
     */
    private void updatePlantLayout(List<Plant> plants) {
        plantLayout.removeAll();
        selectedPlantDetails.setVisible(false);
        if (plants.isEmpty()) {
            showEmptyLibraryNotice();
        } else {
            emptyLibraryNotice.setVisible(false);
            plants.forEach(plant -> plantLayout.add(createPlantCard(plant)));
        }
    }

    /**
     * Shows a notice indicating that the library is empty, with a button to add new plants.
     */
    private void showEmptyLibraryNotice() {
        emptyLibraryNotice.removeAll();
        emptyLibraryNotice.setText("No plants in your library yet. Start adding plants now!");
        Button goToAllPlantsButton = new Button("Find New Plants", event -> UI.getCurrent().navigate("all-plants"));
        goToAllPlantsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        emptyLibraryNotice.add(goToAllPlantsButton);
        emptyLibraryNotice.setVisible(true);
    }

/**
* Creates a card displaying plant information, including an image, icons, and a deletion icon.
     *
     * @param plant the plant record used to populate the card
     * @return a card component for the specified plant
     */
    private Div createPlantCard(Plant plant) {
        Div plantDiv = new Div();
        plantDiv.getStyle()
                .set("max-width", "320px")  // Prevents stretching
                .set("width", "320px")
                .set("height", "400px")  // Ensures consistent height
                .set("overflow", "hidden")  // Prevents expanding
                .set("display", "inline-block")  // Ensures it's in a grid layout
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");
        plantDiv.addClassName("my-plants-card");

        Div imageContainer = new Div();
        imageContainer.addClassName("my-plants-card-image-container");

        Image plantImage = new Image(plant.imageUrl(), plant.name());
        plantImage.getStyle()
                .set("width", "100%")  // Ensures it fits within its container
                .set("max-width", "300px")  // Prevents it from becoming gigantic
                .set("max-height", "200px")  // Limits height
                .set("object-fit", "cover")  // Ensures proper aspect ratio
                .set("cursor", "pointer");
        plantImage.addClickListener(e -> showPlantDetails(plant));
        imageContainer.add(plantImage);

        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.addClassName("my-plants-card-vaadin-icon");
        deleteIcon.addClickListener(e -> confirmPlantDeletion(plant));

        Div infoContainer = new Div();
        infoContainer.addClassName("my-plants-card-info");

        H4 plantName = new H4(plant.name());
        plantName.addClickListener(e -> showPlantDetails(plant));

        Div hashtagContainer = createHashtagComponent(plant);
        hashtagContainer.getStyle().set("margin-bottom", "10px");

        HorizontalLayout iconLayout = iconCreator(plant);

        LibraryEntry lib = userSessionData.getPlantLibraryEntryById(plant.libraryId());
        Div nextWateringLabel = new Div();
        nextWateringLabel.setText("Next Watering: " + lib.getNextWatering().format(DATE_FORMAT));

        infoContainer.add(plantName, iconLayout, nextWateringLabel);
        plantDiv.add(imageContainer, infoContainer, deleteIcon, hashtagContainer);


        return plantDiv;
    }

    /**
     * Creates a row of icons representing plant attributes such as sunlight, watering, or poison status.
     *
     * @param plant the plant record whose properties will be displayed as icons
     * @return a HorizontalLayout containing characteristic icons
     */
    public HorizontalLayout iconCreator(Plant plant) {
        HorizontalLayout icons = new HorizontalLayout();

        Icon sunIcon = VaadinIcon.SUN_O.create();
        Tooltip.forComponent(sunIcon).setText("Sunlight: " + plant.sunlight());

        Image waterIcon;
        if ("Frequent".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image("images/Rain.png", "Rain Icon");
        } else if ("Average".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image("images/AverageWater.png", "Water Icon");
        } else {
            waterIcon = new Image("images/MinimalWater.png", "Water Icon");
        }
        Tooltip.forComponent(waterIcon).setText("Watering: " + plant.watering());

        icons.add(sunIcon, waterIcon);

        if (Boolean.TRUE.equals(plant.medicinal())) {
            Image medicinalIcon = new Image("images/Medicinal.png", "Medicinal Icon");
            Tooltip.forComponent(medicinalIcon).setText("Medicinal Plant");
            icons.add(medicinalIcon);
        }
        if (Boolean.TRUE.equals(plant.edibleFruit())) {
            Icon fruitIcon = VaadinIcon.CUTLERY.create();
            Tooltip.forComponent(fruitIcon).setText("Edible Fruit");
            icons.add(fruitIcon);
        }
        if (Boolean.TRUE.equals(plant.poisonousToHumans()) || Boolean.TRUE.equals(plant.poisonousToPets())) {
            Image poisonIcon = new Image("images/Poison.png", "Poison Icon");
            String tooltipText = (Boolean.TRUE.equals(plant.poisonousToHumans()) && Boolean.TRUE.equals(plant.poisonousToPets()))
                    ? "Poisonous to Humans and Pets"
                    : Boolean.TRUE.equals(plant.poisonousToHumans()) ? "Poisonous to Humans" : "Poisonous to Pets";
            Tooltip.forComponent(poisonIcon).setText(tooltipText);
            icons.add(poisonIcon);
        }
        return icons;
    }

    /**
     * Opens a confirmation dialog before removing the specified plant from the user's library.
     *
     * @param plant the plant to be removed
     */
    private void confirmPlantDeletion(Plant plant) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirm Deletion");
        confirmDialog.setText("Are you sure you want to remove \"" + plant.name() + "\"?");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Delete");
        confirmDialog.addConfirmListener(event -> {
            userPlantLibraryService.removePlantFromLibrary(plant.libraryId());
            refreshPlantList();
            Notification.show("Plant deleted successfully.", 3000, Notification.Position.TOP_CENTER);
        });
        confirmDialog.open();
    }

    /**
     * Shows a detail panel with the plant’s image, description,
     * a water gauge, and a "Mark as Watered" button.
     */
    private void showPlantDetails(Plant plant) {
        System.out.println("Showing details for plant: " + plant.name());

        System.out.println("looking at User: "+userSessionData.getUserId());
        System.out.println("looking plantId: "+plant.plantId());


        selectedPlantDetails.getStyle()
                .set("max-width", "600px")  // Limit width
                .set("width", "80%")  // Prevents overflow
                .set("margin", "auto")  // Centers the element
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");


        LibraryEntry libEntry = userSessionData.getPlantLibraryEntryById(plant.libraryId());

        if (libEntry == null) {
            Notification.show("No library entry found for this plant.", 3000, Notification.Position.TOP_CENTER);
            return;
        }


        if (selectedPlantDetails.isVisible() && selectedPlantDetails.getElement().getProperty("data-plant-id", "").equals("" + plant.plantId())){
            selectedPlantDetails.setVisible(false);
            return;
        }

        selectedPlantDetails.removeAll();
        selectedPlantDetails.getElement().setProperty("data-plant-id", "" + plant.plantId());

        Image bigPlantImage = new Image(plant.imageUrl(), plant.name());
        bigPlantImage.setWidth("300px");

        H3 title = new H3(plant.name());
        Paragraph description = new Paragraph(
                plant.description() != null ? plant.description() : "(No Description)"
        );

        WaterGauge gauge = new WaterGauge();
        Optional<Double> gaugeValueOpt = userPlantLibraryService.getWateringGaugePercentage(libEntry.getLibraryId());
        gauge.setWaterLevel(gaugeValueOpt.orElse(0.0));
        gauge.setWateringDates(libEntry.getLastWatered(), libEntry.getNextWatering());

        Button waterButton = new Button("Mark as Watered",e ->
                userPlantLibraryService.waterPlant(libEntry.getLibraryId()).ifPresent(updatedEntry -> {
                    double updatedValue = userPlantLibraryService
                            .getWateringGaugePercentage(updatedEntry.getId())
                            .orElse(0.0);
                    gauge.setWaterLevel(updatedValue);
                    gauge.setWateringDates(updatedEntry.getLastWatered(), updatedEntry.getNextWatering());
                    Notification.show("Plant marked as watered.", 3000, Notification.Position.TOP_CENTER);
                })
        );
        waterButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button closeButton = new Button("Close", e -> {refreshPlantList();});
        closeButton.addClassName("close-button");
        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        HorizontalLayout actionBar = new HorizontalLayout(waterButton, closeButton);

        //---------------
        // Hashtags display
        List<String> fetchedHashtags = userPlantLibraryService.readHashtags(userSessionData.getUserId(), plant.libraryId());

        Div hashtagsDiv = new Div();
        hashtagsDiv.getStyle().set("margin-top", "10px");
        plantDetailsUpdateHashtagDisplay(hashtagsDiv, fetchedHashtags);

        // Add hashtag field and button
        TextField addHashtagField = new TextField();
        addHashtagField.setPlaceholder("Add hashtag");
        Button addHashtagButton = new Button("Add", e -> {
            String newHashtag = addHashtagField.getValue().trim();
            if (!newHashtag.isEmpty()) {
                boolean success = userPlantLibraryService.addHashtag(userSessionData.getUserId(), plant.libraryId(), newHashtag);
                if (success) {
                    List<String> updatedHashtags = userPlantLibraryService.readHashtags(userSessionData.getUserId(), plant.libraryId());

                    UI.getCurrent().access(() -> {
                        plantDetailsUpdateHashtagDisplay(hashtagsDiv, updatedHashtags);
                        addHashtagField.clear();
                        populateRightBarHashtags();
                    });

                } else {
                    Notification.show("Hashtag already exists.", 3000, Notification.Position.TOP_CENTER);
                }
            }
        });

        // Remove hashtag field and button
        TextField removeHashtagField = new TextField();
        removeHashtagField.setPlaceholder("Remove hashtag");
        Button removeHashtagButton = new Button("Remove", e -> {
            String hashtagToRemove = removeHashtagField.getValue().trim();
            if (!hashtagToRemove.isEmpty()) {
                boolean success = userPlantLibraryService.removeHashtag(userSessionData.getUserId(), plant.libraryId(), hashtagToRemove);
                if (success) {
                    List<String> updatedHashtags = userPlantLibraryService.readHashtags(userSessionData.getUserId(), plant.libraryId());

                    UI.getCurrent().access(() -> {
                        plantDetailsUpdateHashtagDisplay(hashtagsDiv, updatedHashtags);
                        removeHashtagField.clear();
                        populateRightBarHashtags();
                    });

                } else {
                    Notification.show("Hashtag not found.", 3000, Notification.Position.TOP_CENTER);
                }
            }
        });
        //--------
        HorizontalLayout hashtagActions = new HorizontalLayout(addHashtagField, addHashtagButton, removeHashtagField, removeHashtagButton);
        hashtagActions.setSpacing(true);

        selectedPlantDetails.add(bigPlantImage, title, description, hashtagsDiv, hashtagActions, actionBar, gauge);
        selectedPlantDetails.getStyle().remove("display");
        selectedPlantDetails.setVisible(true);
//        selectedPlantDetails.getStyle().set("pointer-events", "auto");
    }

    private void plantDetailsUpdateHashtagDisplay(Div hashtagsDiv, List<String> hashtags) {
        
        hashtagsDiv.removeAll(); // Clear

        System.out.println("Hashtags coming in: "+hashtags);

        if (hashtags.isEmpty()) {
            hashtagsDiv.setText("No hashtags yet.");
        } else {
            hashtags.forEach(hashtag -> {
                Button hashtagButton = new Button("#" + hashtag, e -> toggleHashtagSelection(hashtag));
                hashtagButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                if (selectedHashtags.contains(hashtag)) {
                    hashtagButton.addClassName("selected");
                }
                hashtagsDiv.add(hashtagButton);
            });
        }
    }

    private void toggleHashtagSelection(String hashtag) {
        if (!selectedHashtags.isEmpty()) {
            resetFilterButton.getStyle().set("visibility", "visible");
        } else {
            resetFilterButton.getStyle().set("visibility", "hidden");
        }

        filterByHashtag(hashtag);


        //Too keep the space the button occupies empty when it goes invisible.
        if (!selectedHashtags.isEmpty()) {
            resetFilterButton.getStyle().set("opacity", "1").set("pointer-events", "auto");
        } else {
            resetFilterButton.getStyle().set("opacity", "0").set("pointer-events", "none");
        }
    }

    private void resetFilter(){

        selectedHashtags.clear();

        resetFilterButton.getStyle().set("visibility", "hidden");

        refreshPlantList();
        populateRightBarHashtags();
    }

    private Div createHashtagComponent(Plant plant) {
        Div hashtagContainer = new Div();
        hashtagContainer.getStyle().set("margin-top", "5px");

        if (plant.hashtags().isEmpty()) {
            hashtagContainer.setText("No hashtags");
            return hashtagContainer;
        }

        for (String hashtag : plant.hashtags()) {
            Span tag = new Span("#" + hashtag);
            tag.getStyle()
                    .set("color", "blue")
                    .set("cursor", "pointer")
                    .set("margin-right", "5px")
                    .set("text-decoration", "underline");
            tag.addClickListener(e -> filterByHashtag(hashtag));
            hashtagContainer.add(tag);
        }

        return hashtagContainer;
    }

    private void populateRightBarHashtags() {
        mainViewHashtagFilterField.removeAll();

        //
        Set<String> allHashtags = userSessionData.getPlantLibraryEntries().stream()
                .flatMap(entry -> entry.getUserPlantLibrary().getHashtags().stream())
                .collect(Collectors.toSet());

        if (allHashtags.isEmpty()) {
            Div spacer = new Div();
            spacer.setText("No available filters.");
            spacer.getStyle().set("min-height", "20px");
            mainViewHashtagFilterField.add(spacer);
//            mainViewHashtagFilterField.setText("No available filters.");
            return;
        }

        allHashtags.forEach(hashtag -> {
            Span tag = new Span("#" + hashtag);
            tag.getStyle()
                    .set("color", "blue")
                    .set("cursor", "pointer")
                    .set("margin-right", "5px")
                    .set("text-decoration", "underline");

            tag.addClickListener(e -> filterByHashtag(hashtag));
            mainViewHashtagFilterField.add(tag);
        });
    }



   /* public void refreshUserSessionData() {
        List<LibraryEntry> updatedEntries = userPlantLibraryService.getAllLibraryEntriesForUser(userSessionData.getUserId());
        userSessionData.setPlantLibraryEntries(updatedEntries);
    }*/

}
