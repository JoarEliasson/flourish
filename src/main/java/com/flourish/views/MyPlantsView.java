package com.flourish.views;

import com.flourish.domain.LibraryEntry;
import com.flourish.security.UserSessionData;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.views.components.WaterGauge;
import com.flourish.views.components.VerticalWaterGauge;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
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

import java.util.List;
import java.util.Optional;
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
 * @author
 *   Kenan Al Tal, Joar Eliasson
 * @version
 *   1.1.1
 * @since
 *   2025-03-09
 */
@PageTitle("My Plants")
@Route(value = "my-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final UserSessionData userSessionData;
    private final UserPlantLibraryService userPlantLibraryService;

    // A layout that holds all plant "cards".
    private final FlexLayout plantLayout = new FlexLayout();

    // A Div for showing "no plants" + a button to add new plants.
    private final Div emptyLibraryNotice = new Div();

    // A panel that shows detailed info + water gauge for the selected plant.
    private final Div selectedPlantDetails = new Div();

    // A search field for filtering user's plants by name.
    private final TextField searchField = new TextField("Search Plants");

    /**
     * A record representing a plant with necessary details for the UI.
     *
     * @param libraryId         The ID of the user's library entry (used for removal, watering).
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
                        Boolean medicinal) {}

    /**
     * Constructs the MyPlantsView and initializes UI components.
     */
    @Autowired
    public MyPlantsView(UserPlantLibraryService userPlantLibraryService,
                        UserSessionData userSessionData) {
        this.userPlantLibraryService = userPlantLibraryService;
        this.userSessionData = userSessionData;

        // Ensure user is logged in.
        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            Notification.show("You must be logged in to view your plants.", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
            return;
        }

        // --- View styling ---
        getContent().getStyle().set("background-color", "#e8f5e9").set("padding", "20px");

        H2 title = new H2("My Plants");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");

        // --- Search field ---
        searchField.setWidth("100%");
        searchField.getStyle().set("font-size", "18px");
        searchField.addValueChangeListener(e -> filterPlantList(e.getValue()));

        // --- Plant layout for displaying card-like elements ---
        plantLayout.setFlexWrap(FlexWrap.WRAP);
        plantLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        plantLayout.setAlignItems(FlexLayout.Alignment.START);
        plantLayout.setWidthFull();

        // --- Selected plant details panel (hidden by default) ---
        selectedPlantDetails.getStyle()
                .set("display", "none")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)")
                .set("margin-bottom", "20px")
                .set("width", "80%");

        // --- Empty library notice & button to AllPlantsView ---
        emptyLibraryNotice.getStyle()
                .set("text-align", "center")
                .set("margin-top", "20px");
        emptyLibraryNotice.setVisible(false);

        // --- Layout the top portion (title + search) ---
        HorizontalLayout topBar = new HorizontalLayout(title, searchField);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        getContent().add(topBar, selectedPlantDetails, plantLayout, emptyLibraryNotice);
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
        // Update session data:
        userSessionData.setPlantLibraryEntries(entries);

        // Map library entries to our UI-focused Plant record.
        List<Plant> allPlants = mapLibraryEntriesToPlants(entries);

        // Filter them by current search text, if any, to remain consistent.
        String currentQuery = searchField.getValue() != null ? searchField.getValue() : "";
        List<Plant> filtered = allPlants.stream()
                .filter(p -> p.name().toLowerCase().contains(currentQuery.toLowerCase()))
                .collect(Collectors.toList());

        // Rebuild the layout with filtered plants
        updatePlantLayout(filtered);
    }

    /**
     * Filters the current library data using the specified search query.
     */
    private void filterPlantList(String query) {
        // Use the plants in userSessionData to generate the filtered list.
        List<LibraryEntry> sessionEntries = userSessionData.getPlantLibraryEntries();
        List<Plant> filteredPlants = mapLibraryEntriesToPlants(sessionEntries).stream()
                .filter(p -> p.name().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        updatePlantLayout(filteredPlants);
    }

    /**
     * Maps LibraryEntry objects to the local Plant record for easy UI handling.
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
                        entry.getPlantDetails().getMedicinal()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Rebuilds the layout (plant cards + empty notice) based on the provided list of plants.
     */
    private void updatePlantLayout(List<Plant> plants) {
        plantLayout.removeAll();
        selectedPlantDetails.setVisible(false);

        if (plants.isEmpty()) {
            // Show an empty library notice, with a button to go add more plants.
            showEmptyLibraryNotice();
        } else {
            emptyLibraryNotice.setVisible(false);
            for (Plant plant : plants) {
                Div card = createPlantCard(plant);
                plantLayout.add(card);
            }
        }
    }

    /**
     * Displays a "no plants" message plus a button to navigate to AllPlantsView.
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
     * Creates the card (Div) for a single plant, including an image,
     * plant name, characteristic icons, and a trash icon for deletion.
     */
    private Div createPlantCard(Plant plant) {
        Div plantDiv = new Div();
        plantDiv.getStyle().set("position", "relative")
                .set("margin", "10px")
                .set("width", "320px")
                .set("height", "340px") // slightly taller to accommodate icons
                .set("display", "inline-block")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)")
                .set("background-color", "white")
                .set("overflow", "hidden");

        // -- Image container --
        Div imageContainer = new Div();
        imageContainer.getStyle()
                .set("width", "100%")
                .set("height", "200px")
                .set("overflow", "hidden");

        Image plantImage = new Image(plant.imageUrl(), plant.name());
        plantImage.getStyle()
                .set("width", "100%")
                .set("height", "100%")
                .set("object-fit", "cover")
                .set("cursor", "pointer");

        plantImage.addClickListener(e -> showPlantDetails(plant));
        imageContainer.add(plantImage);

        // -- Delete icon --
        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.getStyle()
                .set("position", "absolute")
                .set("top", "5px")
                .set("right", "5px")
                .set("cursor", "pointer")
                .set("color", "red")
                .set("font-size", "1.2rem");
        deleteIcon.addClickListener(e -> confirmPlantDeletion(plant));

        // -- Info container (name + icons) --
        Div infoContainer = new Div();
        infoContainer.getStyle().set("padding", "10px");

        H4 plantName = new H4(plant.name());
        plantName.getStyle().set("margin", "0").set("font-size", "1rem").set("cursor", "pointer");
        plantName.addClickListener(e -> showPlantDetails(plant));

        // Add characteristic icons to a row
        HorizontalLayout iconLayout = iconCreator(plant);
        iconLayout.getStyle().set("margin-top", "6px");

        infoContainer.add(plantName, iconLayout);
        plantDiv.add(imageContainer, infoContainer, deleteIcon);

        return plantDiv;
    }

    /**
     * Creates a horizontal layout containing icons that represent various plant characteristics.
     *
     * @param plant the plant record.
     * @return a HorizontalLayout with characteristic icons and tooltips.
     */
    public HorizontalLayout iconCreator(Plant plant) {
        HorizontalLayout icons = new HorizontalLayout();

        // Sunlight icon
        Icon sunIcon = VaadinIcon.SUN_O.create();
        sunIcon.getStyle().set("color", "orange").set("font-size", "1.5rem").set("margin", "0");
        Tooltip.forComponent(sunIcon).setText("Sunlight: " + plant.sunlight());

        // Watering icon (image variant based on frequency)
        Image waterIcon;
        if ("Frequent".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image("images/Rain.png", "Rain Icon");
        } else if ("Average".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image("images/AverageWater.png", "Water Icon");
        } else {
            waterIcon = new Image("images/MinimalWater.png", "Water Icon");
        }
        waterIcon.getStyle().set("width", "24px").set("height", "24px").set("cursor", "pointer");
        Tooltip.forComponent(waterIcon).setText("Watering: " + plant.watering());

        icons.add(sunIcon, waterIcon);

        if (Boolean.TRUE.equals(plant.medicinal())) {
            Image medicinalIcon = new Image("images/Medicinal.png", "Medicinal Icon");
            medicinalIcon.getStyle().set("width", "24px").set("height", "24px");
            Tooltip.forComponent(medicinalIcon).setText("Medicinal Plant");
            icons.add(medicinalIcon);
        }
        if (Boolean.TRUE.equals(plant.edibleFruit())) {
            Icon fruitIcon = VaadinIcon.CUTLERY.create();
            fruitIcon.getStyle().set("color", "green").set("font-size", "1.5rem");
            Tooltip.forComponent(fruitIcon).setText("Edible Fruit");
            icons.add(fruitIcon);
        }
        if (Boolean.TRUE.equals(plant.poisonousToHumans()) || Boolean.TRUE.equals(plant.poisonousToPets())) {
            Image poisonIcon = new Image("images/Poison.png", "Poison Icon");
            poisonIcon.getStyle().set("width", "24px").set("height", "24px");
            String tooltipText = (Boolean.TRUE.equals(plant.poisonousToHumans()) && Boolean.TRUE.equals(plant.poisonousToPets()))
                    ? "Poisonous to Humans and Pets"
                    : Boolean.TRUE.equals(plant.poisonousToHumans()) ? "Poisonous to Humans" : "Poisonous to Pets";
            Tooltip.forComponent(poisonIcon).setText(tooltipText);
            icons.add(poisonIcon);
        }
        return icons;
    }

    /**
     * Opens a confirm dialog before deleting the specified plant.
     */
    private void confirmPlantDeletion(Plant plant) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirm Deletion");
        confirmDialog.setText("Are you sure you want to remove \"" + plant.name() + "\"?");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Delete");
        confirmDialog.addConfirmListener(event -> {
            userPlantLibraryService.removePlantFromLibrary(plant.libraryId);
            refreshPlantList(); // Re-pull data from the DB and re-render
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

        LibraryEntry libEntry = userSessionData.getPlantLibraryEntryById(plant.libraryId());

        if (libEntry == null) {
            Notification.show("No library entry found for this plant.", 3000, Notification.Position.TOP_CENTER);
            return;
        }


        if (selectedPlantDetails.isVisible() && selectedPlantDetails.getElement().getProperty("data-plant-id", "").equals("" + plant.plantId())) {
            selectedPlantDetails.setVisible(false);
            return;
        }

        selectedPlantDetails.removeAll();

        // Save some metadata to identify if the same plant is clicked
        selectedPlantDetails.getElement().setProperty("data-plant-id", "" + plant.plantId());

        // Plant image
        Image bigPlantImage = new Image(plant.imageUrl(), plant.name());
        bigPlantImage.setWidth("300px");

        // Title + description
        H3 title = new H3(plant.name());
        Paragraph description = new Paragraph(plant.description() != null ? plant.description() : "(No Description)");

        // Water gauge
        WaterGauge gauge = new WaterGauge();
        Optional<Double> gaugeValueOpt = userPlantLibraryService.getWateringGaugePercentage(libEntry.getLibraryId());
        gauge.setWaterLevel(gaugeValueOpt.orElse(0.0));


        // "Mark as Watered" button
        Button waterButton = new Button("Mark as Watered", e ->
                userPlantLibraryService.waterPlant(libEntry.getLibraryId())
                .ifPresent(updated -> {
                    double updatedValue = userPlantLibraryService.getWateringGaugePercentage(updated.getId()).orElse(0.0);
                    gauge.setWaterLevel(updatedValue);
                    Notification.show("Plant marked as watered.", 3000, Notification.Position.TOP_CENTER);
                }));
        waterButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);


        VerticalWaterGauge verticalGauge = new VerticalWaterGauge();

        verticalGauge.setLastWatered(libEntry.getLastWatered());
        verticalGauge.setNextWatering(libEntry.getNextWatering());


        verticalGauge.setWaterLevel(gaugeValueOpt.orElse(0.0));

        selectedPlantDetails.add(gauge);

        // "Close" button
        Button closeButton = new Button("Close", e -> selectedPlantDetails.setVisible(false));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actionBar = new HorizontalLayout(waterButton, closeButton);
        actionBar.setSpacing(true);

        selectedPlantDetails.add(bigPlantImage, title, description, actionBar, gauge, verticalGauge);
        System.out.println("Details shown for plant: " + plant.name());
        selectedPlantDetails.getStyle().remove("display");
        selectedPlantDetails.setVisible(true);


    }
}