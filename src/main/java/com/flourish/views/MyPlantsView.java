package com.flourish.views;

import com.flourish.domain.LibraryEntry;
import com.flourish.security.UserSessionData;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Displays and manages the user's plant collection.
 *
 * <p>Provides a searchable list of plants from the user's library, a details panel for
 * viewing and watering actions, and options for removing plants.</p>
 *
 * <p>Implements {@link BeforeEnterObserver} to refresh data upon navigation events.</p>
 *
 * <ul>
 *   <li>Displays a list of plant cards, each with an image, icons for characteristics, and removal capability.</li>
 *   <li>Offers a details panel with a water gauge and "Mark as Watered" button.</li>
 *   <li>Presents a "Find New Plants" button if the library is empty.</li>
 * </ul>
 *
 * @author
 *   Kenan Al Tal, Joar Eliasson
 * @version
 *   1.0.0
 * @since
 *   2025-03-14
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
   * Represents a user-owned plant entry with relevant attributes for the UI.
   *
   * @param libraryId the library entry ID
   * @param plantId the plant ID
   * @param name the plant's common name
   * @param description the plant description
   * @param imageUrl the URL of a representative image
   * @param watering watering frequency string
   * @param sunlight sunlight requirement string
   * @param type the plant's category or type
   * @param edibleFruit whether the plant has edible fruit
   * @param poisonousToHumans whether the plant is poisonous to humans
   * @param poisonousToPets whether the plant is poisonous to pets
   * @param medicinal whether the plant is medicinal
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
          Boolean medicinal
  ) {}

  /**
   * Constructs a new MyPlantsView and initializes UI elements.
   *
   * @param userPlantLibraryService the service managing library operations
   * @param userSessionData the session data containing user details and library entries
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
    plantLayout.addClassName("my-plants-layout");
    selectedPlantDetails.addClassName("my-plants-details");
    emptyLibraryNotice.addClassName("my-plants-empty-notice");
    searchField.addClassName("my-plants-search");

    H2 title = new H2("My Plants");
    title.addClassName("my-plants-title");

    searchField.addValueChangeListener(e -> filterPlantList(e.getValue()));

    plantLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
    plantLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
    plantLayout.setAlignItems(FlexLayout.Alignment.START);
    plantLayout.setWidthFull();

    selectedPlantDetails.setVisible(false);

    emptyLibraryNotice.setVisible(false);

    HorizontalLayout topBar = new HorizontalLayout(title, searchField);
    topBar.setWidthFull();
    topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

    getContent().add(topBar, selectedPlantDetails, plantLayout, emptyLibraryNotice);
  }

  /**
   * Ensures the user's plant list is refreshed each time the view is entered.
   *
   * @param event the navigation event
   */
  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    refreshPlantList();
  }

  /**
   * Reloads the user's library entries and updates the UI.
   */
  private void refreshPlantList() {
    List<LibraryEntry> entries = userPlantLibraryService.getAllLibraryEntriesForUser(userSessionData.getUserId());
    userSessionData.setPlantLibraryEntries(entries);
    List<Plant> mappedPlants = mapLibraryEntriesToPlants(entries);
    String currentQuery = searchField.getValue() != null ? searchField.getValue() : "";
    List<Plant> filtered = mappedPlants.stream()
            .filter(p -> p.name().toLowerCase().contains(currentQuery.toLowerCase()))
            .collect(Collectors.toList());
    updatePlantLayout(filtered);
  }

  /**
   * Filters the displayed plants by matching the given query against names.
   *
   * @param query the substring to match against plant names
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
                    entry.getPlantDetails().getMedicinal()
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
    nextWateringLabel.setText("Next Watering: " + lib.getNextWatering().format(DATE_FORMAT));

    infoContainer.add(plantName, iconLayout, nextWateringLabel);
    plantDiv.add(imageContainer, infoContainer, deleteIcon);

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
      String tooltipText = (Boolean.TRUE.equals(plant.poisonousToHumans()) && Boolean.TRUE.equals(plant.poisonousToPets()))
              ? "Poisonous to Humans and Pets"
              : Boolean.TRUE.equals(plant.poisonousToHumans()) ? "Poisonous to Humans" : "Poisonous to Pets";
      poisonousIcon.addClassName("my-plants-icon");
      Tooltip.forComponent(poisonousIcon).setText(tooltipText);
      icons.add(poisonousIcon);
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
   * Shows a detail panel with an image, description, water gauge, and watering actions for the selected plant.
   *
   * @param plant the plant record whose details are displayed
   */
  private void showPlantDetails(Plant plant) {
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

    Button waterButton = new Button("Mark as Watered", e ->
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

    Button closeButton = new Button("Close", e -> selectedPlantDetails.setVisible(false));
    closeButton.addClassName("close-button");
    closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

    HorizontalLayout actionBar = new HorizontalLayout(waterButton, closeButton);

    selectedPlantDetails.add(bigPlantImage, title, description, actionBar, gauge);
    selectedPlantDetails.getStyle().remove("display");
    selectedPlantDetails.setVisible(true);

  }
}
