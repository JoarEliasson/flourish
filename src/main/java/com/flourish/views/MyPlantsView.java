package com.flourish.views;

import com.flourish.domain.LibraryEntry;
import com.flourish.service.UserSessionData;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.views.components.WaterGauge;
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
import com.vaadin.flow.component.html.Span;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**

 * Displays and manages the user's plant collection by:
 * <ul>
 *   <li>Loading the user's library from the backend.</li>
 *   <li>Offering a searchable list of plants, each shown as a card.</li>
 *   <li>Providing a right-side area for hashtag-based filtering.</li>
 *   <li>Allowing users to view plant details, water them, and manage hashtags.</li>
 *   <li>Removing plants from the library upon confirmation.</li>
 * </ul>
 *
 * <p>The class uses Vaadin components to build a user-friendly interface
 * with a top bar for search, a central area for plant cards, and a sidebar
 * for filters. It also includes a detail panel that appears on card selection,
 * showing a larger image, description, watering gauge, and options to
 * add/remove hashtags.</p>
 *
 * <p><strong>Key Layout Sections:</strong>
 * <ul>
 *   <li><em>Top Bar:</em> Page title and search field.</li>
 *   <li><em>Center Area:</em> A FlexLayout containing plant cards.</li>
 *   <li><em>Detail Panel:</em> A hidden panel that appears upon clicking a plant,
 *       showing more information and actions.</li>
 *   <li><em>Right Sidebar:</em> A header label, "Reset Filter" button, and
 *       hashtag listing for quick filter toggling.</li>
 * </ul>
 *
 * @author
 *   Kenan Al Tal, Joar Eliasson, Martin Frick
 * @version
 *   1.1.0
 * @since
 *   2025-03-20
 */
@PageTitle("My Plants")
@Route(value = "my-plants", layout = MainLayout.class)
@RolesAllowed("USER")
public class MyPlantsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    /**
     * Formats watering-related dates shown on the plant cards and detail views.
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserSessionData userSessionData;
    private final UserPlantLibraryService userPlantLibraryService;

    private final FlexLayout plantLayout = new FlexLayout();
    private final Div emptyLibraryNotice = new Div();
    private final Div selectedPlantDetails = new Div();
    private final TextField searchField = new TextField("Search Plants");

    private final Span hashtagSectionHeader = new Span("Hashtag Filters");
    private final Div mainViewHashtagFilterField = new Div();

    private final Set<String> selectedHashtags = new HashSet<>();
    private final Button resetFilterButton = new Button("Reset Filter");

    @Value("${icon.plant.watering.low}")
    private String iconWateringLowUrl;

    @Value("${icon.plant.watering.medium}")
    private String iconWateringMediumUrl;

    @Value("${icon.plant.watering.high}")
    private String iconWateringHighUrl;

    @Value("${icon.plant.sunlight}")
    private String iconWSunlightUrl;

    @Value("${icon.plant.edible}")
    private String iconEdibleWUrl;

    @Value("${icon.plant.medicinal}")
    private String iconWMedicinalUrl;

    @Value("${icon.plant.poisonous}")
    private String iconWPoisonousUrl;

    /**
     * Represents a user-owned plant entry with its important UI-related fields.
     *
     * <p>The record provides direct access to data for populating and filtering
     * in this view. Each object corresponds to one {@code LibraryEntry}
     * from the backend.</p>
     *
     * @param libraryId         the unique ID of this user's library entry
     * @param plantId           the public plant ID
     * @param name              the plant's display name
     * @param description       textual description of the plant
     * @param imageUrl          a URL referencing the default/representative plant image
     * @param watering          the watering frequency category (e.g., "Frequent", "Average", "Minimum")
     * @param sunlight          a summary of the plant's sunlight needs
     * @param type              the plant's category or type (e.g., "Herb", "Shrub")
     * @param edibleFruit       whether the plant has edible fruit
     * @param poisonousToHumans whether the plant is poisonous to humans
     * @param poisonousToPets   whether the plant is poisonous to pets
     * @param medicinal         whether the plant is medicinal
     * @param hashtags          list of hashtags (string tags) associated with this entry
     */
    public record Plant(
            long libraryId,
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
    ) {
    }

    /**
     * Constructs the view that displays and manages the user's plant collection.
     *
     * <p>This constructor also ensures that an anonymous (not logged-in) user
     * cannot access this view and redirects them to the login page if needed.
     * After confirming the user session, it sets up the top bar, flex layout for
     * cards, detail panel, and a right sidebar containing hashtag functionality.</p>
     *
     * @param userPlantLibraryService a service that provides read/write operations
     *                                for the user's plant library
     * @param userSessionData         a session-level data structure that stores the
     *                                current user's ID and cached library entries
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

        getContent().addClassName("my-plants-view");

        H2 title = new H2("My Plants");
        title.addClassName("my-plants-title");

        searchField.addClassName("my-plants-search");
        searchField.addValueChangeListener(e -> refreshPlantList());

        HorizontalLayout topBar = new HorizontalLayout(title, searchField);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        plantLayout.addClassName("my-plants-layout");
        plantLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        plantLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        plantLayout.setAlignItems(FlexLayout.Alignment.START);
        plantLayout.setWidth("100%");

        selectedPlantDetails.addClassName("my-plants-details");
        selectedPlantDetails.setVisible(false);

        emptyLibraryNotice.addClassName("my-plants-empty-notice");
        emptyLibraryNotice.setVisible(false);

        hashtagSectionHeader.addClassName("my-plants-hashtag-header");
        resetFilterButton.addClassName("my-plants-reset-filter-button");
        resetFilterButton.setWidthFull();
        resetFilterButton.addClickListener(e -> resetFilter());
        refreshResetButtonStyle();

        mainViewHashtagFilterField.addClassName("my-plants-hashtag-filter-field");

        VerticalLayout rightBar = new VerticalLayout(hashtagSectionHeader, resetFilterButton, mainViewHashtagFilterField);
        rightBar.addClassName("my-plants-right-bar");
        rightBar.setWidth("250px");
        rightBar.setPadding(false);
        rightBar.setSpacing(true);

        HorizontalLayout mainArea = new HorizontalLayout(plantLayout, rightBar);
        mainArea.setWidthFull();

        getContent().add(topBar, selectedPlantDetails, emptyLibraryNotice, mainArea);
    }

    /**
     * Ensures the list of plants is up to date whenever the user navigates
     * to this view.
     *
     * @param event a navigation event triggered by the framework
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        refreshPlantList();
    }

    /**
     * Retrieves all the user's library entries, converts them to {@link Plant}
     * records, filters them by both the search query and selected hashtags,
     * then updates the display accordingly.
     */
    private void refreshPlantList() {
        List<LibraryEntry> entries = userPlantLibraryService.getAllLibraryEntriesForUser(userSessionData.getUserId());
        userSessionData.setPlantLibraryEntries(entries);

        List<Plant> allPlants = mapLibraryEntriesToPlants(entries);
        String query = (searchField.getValue() != null) ? searchField.getValue().toLowerCase() : "";

        List<Plant> filtered = allPlants.stream()
                .filter(p -> p.name().toLowerCase().contains(query))
                .collect(Collectors.toList());

        if (!selectedHashtags.isEmpty()) {
            filtered = filtered.stream()
                    .filter(p -> p.hashtags().stream().anyMatch(selectedHashtags::contains))
                    .collect(Collectors.toList());
        }

        updatePlantLayout(filtered);
        populateRightBarHashtags();
    }

    /**
     * Converts each {@link LibraryEntry} into a lightweight {@link Plant}
     * record for easier UI handling and filtering.
     *
     * @param entries a list of {@code LibraryEntry} objects
     * @return a list of {@code Plant} records
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
     * Displays plant cards for each {@link Plant} that passes the current
     * search/hashtag filters, or shows a notice if none remain.
     *
     * @param plants the filtered list of plants to be displayed
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
     * Shows a notice indicating that the user's library is empty, with a
     * button to go find new plants.
     */
    private void showEmptyLibraryNotice() {
        emptyLibraryNotice.removeAll();
        emptyLibraryNotice.setText("No plants in your library yet. Start adding plants now!");

        Button goToAllPlantsButton = new Button("Find New Plants", e -> UI.getCurrent().navigate("all-plants"));
        goToAllPlantsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        emptyLibraryNotice.add(goToAllPlantsButton);
        emptyLibraryNotice.setVisible(true);
    }

    /**
     * Constructs a {@code Div} that represents a single plant card, containing:
     * <ul>
     *   <li>An image</li>
     *   <li>Icons for watering, sunlight, etc.</li>
     *   <li>Next watering date</li>
     *   <li>Hashtags to filter on click</li>
     *   <li>A deletion icon</li>
     * </ul>
     *
     * @param plant the {@link Plant} data used to fill the card
     * @return a {@code Div} containing the rendered card components
     */
    private Div createPlantCard(Plant plant) {
        Div plantDiv = new Div();
        plantDiv.addClassName("my-plants-card");

        Div imageContainer = new Div();
        imageContainer.addClassName("my-plants-card-image-container");

        Image plantImage = new Image(plant.imageUrl(), plant.name());
        plantImage.addClickListener(e -> showPlantDetails(plant));
        imageContainer.add(plantImage);

        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.addClassName("my-plants-card-vaadin-icon");
        deleteIcon.addClickListener(e -> confirmPlantDeletion(plant));

        Div infoContainer = new Div();
        infoContainer.addClassName("my-plants-card-info");

        H4 plantName = new H4(plant.name());
        plantName.addClickListener(e -> showPlantDetails(plant));

        HorizontalLayout iconLayout = iconCreator(plant);

        LibraryEntry lib = userSessionData.getPlantLibraryEntryById(plant.libraryId());
        Div nextWateringLabel = new Div();
        if (lib != null && lib.getNextWatering() != null) {
            nextWateringLabel.setText("Next Watering: " + lib.getNextWatering().format(DATE_FORMAT));
        }

        Div tagLine = new Div();
        List<String> tags = plant.hashtags();
        if (tags.isEmpty()) {
            tagLine.setText("No tags.");
            tagLine.addClassName("my-plants-no-tags");
        } else {
            for (String hashtag : tags) {
                Span hashtagSpan = new Span("#" + hashtag);
                hashtagSpan.getStyle()
                        .set("cursor", "pointer")
                        .set("margin-right", "5px")
                        .set("text-decoration", "underline");
                hashtagSpan.addClickListener(e -> toggleHashtagSelection(hashtag));
                tagLine.add(hashtagSpan);
            }
        }

        infoContainer.add(plantName, iconLayout, nextWateringLabel, tagLine);
        plantDiv.add(imageContainer, infoContainer, deleteIcon);
        return plantDiv;
    }

    /**
     * Generates a row of icons representing key plant attributes:
     * <ul>
     *   <li>Sunlight requirement</li>
     *   <li>Watering frequency</li>
     *   <li>Medicinal</li>
     *   <li>Edible</li>
     *   <li>Poisonous</li>
     * </ul>
     *
     * @param plant the {@link Plant} whose attributes will be displayed
     * @return a {@code HorizontalLayout} containing the icons and tooltips
     */
    private HorizontalLayout iconCreator(Plant plant) {
        HorizontalLayout icons = new HorizontalLayout();
        icons.addClassName("plant-icons-container");

        Image sunIcon = new Image(iconWSunlightUrl, "Sunlight Icon");
        sunIcon.addClassName("my-plants-icon");
        Tooltip.forComponent(sunIcon).setText("Sunlight: " + plant.sunlight());

        Image waterIcon;
        if ("Frequent".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image(iconWateringHighUrl, "Watering Icon");
        } else if ("Average".equalsIgnoreCase(plant.watering())) {
            waterIcon = new Image(iconWateringMediumUrl, "Watering Icon");
        } else {
            waterIcon = new Image(iconWateringLowUrl, "Watering Icon");
        }
        waterIcon.addClassName("my-plants-icon");
        Tooltip.forComponent(waterIcon).setText("Watering: " + plant.watering());

        icons.add(sunIcon, waterIcon);

        if (Boolean.TRUE.equals(plant.medicinal())) {
            Image medicinalIcon = new Image(iconWMedicinalUrl, "Medicinal Icon");
            medicinalIcon.addClassName("my-plants-icon");
            Tooltip.forComponent(medicinalIcon).setText("Medicinal Plant");
            icons.add(medicinalIcon);
        }

        if (Boolean.TRUE.equals(plant.edibleFruit())) {
            Image edibleIcon = new Image(iconEdibleWUrl, "Edible Icon");
            edibleIcon.addClassName("my-plants-icon");
            Tooltip.forComponent(edibleIcon).setText("Edible Fruit");
            icons.add(edibleIcon);
        }

        if (Boolean.TRUE.equals(plant.poisonousToHumans()) || Boolean.TRUE.equals(plant.poisonousToPets())) {
            Image poisonousIcon = new Image(iconWPoisonousUrl, "Poisonous Icon");
            String tooltipText =
                    (Boolean.TRUE.equals(plant.poisonousToHumans()) && Boolean.TRUE.equals(plant.poisonousToPets()))
                            ? "Poisonous to Humans and Pets"
                            : (Boolean.TRUE.equals(plant.poisonousToHumans()) ? "Poisonous to Humans"
                            : "Poisonous to Pets");
            poisonousIcon.addClassName("my-plants-icon");
            Tooltip.forComponent(poisonousIcon).setText(tooltipText);
            icons.add(poisonousIcon);
        }

        return icons;
    }

    /**
     * Displays a confirmation dialog, and if accepted, removes the specified
     * {@link Plant} from the user's library.
     *
     * @param plant the {@link Plant} to delete
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
     * Reveals a detail panel containing the selected plant's larger image,
     * description, watering gauge, and hashtag management fields.
     *
     * @param plant the {@link Plant} whose details are shown
     */
    private void showPlantDetails(Plant plant) {
        LibraryEntry libEntry = userSessionData.getPlantLibraryEntryById(plant.libraryId());
        if (libEntry == null) {
            Notification.show("No library entry found for this plant.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        String currentPlantId = selectedPlantDetails.getElement().getProperty("data-plant-id", "");
        if (selectedPlantDetails.isVisible() && currentPlantId.equals(String.valueOf(plant.plantId()))) {
            selectedPlantDetails.setVisible(false);
            return;
        }

        selectedPlantDetails.removeAll();
        selectedPlantDetails.setVisible(true);
        selectedPlantDetails.getElement().setProperty("data-plant-id", String.valueOf(plant.plantId()));

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

        Button waterButton = new Button("Mark as Watered", e -> {
            userPlantLibraryService.waterPlant(libEntry.getLibraryId()).ifPresent(updatedEntry -> {
                double updatedValue = userPlantLibraryService
                        .getWateringGaugePercentage(updatedEntry.getId())
                        .orElse(0.0);
                gauge.setWaterLevel(updatedValue);
                gauge.setWateringDates(updatedEntry.getLastWatered(), updatedEntry.getNextWatering());
                Notification.show("Plant marked as watered.", 3000, Notification.Position.TOP_CENTER);
            });
        });
        waterButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button closeButton = new Button("Close", e -> selectedPlantDetails.setVisible(false));
        closeButton.addClassName("close-button");
        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        HorizontalLayout actionBar = new HorizontalLayout(waterButton, closeButton);

        Div hashtagsDiv = new Div();
        hashtagsDiv.getStyle().set("margin-top", "10px");

        List<String> fetchedHashtags = userPlantLibraryService.readHashtags(userSessionData.getUserId(), plant.libraryId());
        updateHashtagDisplayInDetails(hashtagsDiv, fetchedHashtags);

        TextField addHashtagField = new TextField();
        addHashtagField.setPlaceholder("Add hashtag");
        Button addHashtagButton = new Button("Add", e -> {
            String newHashtag = addHashtagField.getValue().trim();
            if (!newHashtag.isEmpty()) {
                boolean success = userPlantLibraryService.addHashtag(userSessionData.getUserId(), plant.libraryId(), newHashtag);
                if (success) {
                    updateHashtagsDiv(hashtagsDiv, plant.libraryId(), addHashtagField);
                } else {
                    Notification.show("Hashtag already exists.", 3000, Notification.Position.TOP_CENTER);
                }
            }
        });

        TextField removeHashtagField = new TextField();
        removeHashtagField.setPlaceholder("Remove hashtag");
        Button removeHashtagButton = new Button("Remove", e -> {
            String hashtagToRemove = removeHashtagField.getValue().trim();
            if (!hashtagToRemove.isEmpty()) {
                boolean success = userPlantLibraryService.removeHashtag(userSessionData.getUserId(), plant.libraryId(), hashtagToRemove);
                if (success) {
                    updateHashtagsDiv(hashtagsDiv, plant.libraryId(), removeHashtagField);
                } else {
                    Notification.show("Hashtag not found.", 3000, Notification.Position.TOP_CENTER);
                }
            }
        });

        HorizontalLayout hashtagActions = new HorizontalLayout(
                addHashtagField, addHashtagButton, removeHashtagField, removeHashtagButton
        );

        selectedPlantDetails.add(
                bigPlantImage, title, description, hashtagsDiv, actionBar, gauge, hashtagActions
        );
    }

    /**
     * Refreshes the displayed hashtags in the detail panel after adding or
     * removing one, then updates the global sidebar listing of all hashtags.
     *
     * @param hashtagsDiv  the container in the detail panel
     * @param libraryId    the ID of the library entry whose hashtags changed
     * @param clearedField the {@link TextField} to clear after adding or removing a hashtag
     */
    private void updateHashtagsDiv(Div hashtagsDiv, long libraryId, TextField clearedField) {
        List<String> updatedHashtags = userPlantLibraryService.readHashtags(userSessionData.getUserId(), libraryId);
        updateHashtagDisplayInDetails(hashtagsDiv, updatedHashtags);
        clearedField.clear();
        populateRightBarHashtags();
    }

    /**
     * Rebuilds the hashtag listing in the detail panel, converting each
     * hashtag to a clickable button that toggles global filtering.
     *
     * @param hashtagsDiv the container for the hashtags
     * @param hashtags    a list of hashtag strings
     */
    private void updateHashtagDisplayInDetails(Div hashtagsDiv, List<String> hashtags) {
        hashtagsDiv.removeAll();
        if (hashtags.isEmpty()) {
            hashtagsDiv.setText("No hashtags yet.");
        } else {
            for (String hashtag : hashtags) {
                Button hashtagButton = new Button("#" + hashtag, evt -> toggleHashtagSelection(hashtag));
                hashtagButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                if (selectedHashtags.contains(hashtag)) {
                    hashtagButton.addClassName("selected");
                }
                hashtagsDiv.add(hashtagButton);
            }
        }
    }

    /**
     * Toggles the specified hashtag in the global filter set, adjusts the
     * reset button style, and refreshes the plant list.
     *
     * @param hashtag the hashtag string to enable or disable
     */
    private void toggleHashtagSelection(String hashtag) {
        if (selectedHashtags.contains(hashtag)) {
            selectedHashtags.remove(hashtag);
        } else {
            selectedHashtags.add(hashtag);
        }
        refreshResetButtonStyle();
        refreshPlantList();
    }

    /**
     * Clears any active hashtag filters, updating both the button style and
     * the displayed plant list.
     */
    private void resetFilter() {
        selectedHashtags.clear();
        refreshResetButtonStyle();
        refreshPlantList();
    }

    /**
     * Updates the right sidebar's hashtag listing to include all distinct
     * hashtags found in the user's library. Each hashtag is clickable to
     * toggle filtering.
     */
    private void populateRightBarHashtags() {
        mainViewHashtagFilterField.removeAll();

        Set<String> allHashtags = userSessionData.getPlantLibraryEntries().stream()
                .flatMap(entry -> entry.getUserPlantLibrary().getHashtags().stream())
                .collect(Collectors.toSet());

        if (allHashtags.isEmpty()) {
            Div spacer = new Div();
            spacer.setText("No available filters.");
            mainViewHashtagFilterField.add(spacer);
            return;
        }

        for (String hashtag : allHashtags) {
            Span tag = new Span("#" + hashtag);
            tag.getStyle()
                    .set("cursor", "pointer")
                    .set("margin-right", "5px")
                    .set("text-decoration", "underline");
            tag.addClickListener(e -> toggleHashtagSelection(hashtag));
            mainViewHashtagFilterField.add(tag);
        }
    }

    /**
     * Applies an "active" or "inactive" styling to the reset filter button
     * based on whether any hashtags are currently selected. The button is
     * also disabled (and unclickable) if no filters are active.
     */
    private void refreshResetButtonStyle() {
        boolean filtersActive = !selectedHashtags.isEmpty();
        resetFilterButton.setEnabled(filtersActive);

        if (filtersActive) {
            resetFilterButton.removeClassName("my-plants-reset-filter-button-inactive");
            resetFilterButton.addClassName("my-plants-reset-filter-button-active");
        } else {
            resetFilterButton.removeClassName("my-plants-reset-filter-button-active");
            resetFilterButton.addClassName("my-plants-reset-filter-button-inactive");
        }
    }
}