package com.flourish.views;

import com.flourish.domain.PlantDetails;
import com.flourish.service.PlantDetailsService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * A Vaadin view that demonstrates how to retrieve and display detailed plant data.
 *
 * <p>This view allows the user to enter a plant ID and then uses the {@code PlantDetailsService}
 * to retrieve the corresponding {@code PlantDetails} object from the database.
 * The retrieved details are then displayed using a simple form layout.</p>
 *
 * <p><em><strong>Note: </strong>
 * This view is designed to serve as a reference on how to access and display plant details.</em></p>
 *
 * @see com.flourish.service.PlantDetailsService
 * @see com.flourish.domain.PlantDetails
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-25
 */
@Route("plant-details")
@PageTitle("Plant Details")
@RolesAllowed("USER")
public class PlantDetailsView extends VerticalLayout {

    private final PlantDetailsService plantDetailsService;

    private final NumberField plantIdField = new NumberField("Enter Plant ID");
    private final Button fetchButton = new Button("Fetch Details");
    private final FormLayout detailsForm = new FormLayout();

    private final TextArea commonNameField = new TextArea("Common Name");
    private final TextArea scientificNameField = new TextArea("Scientific Name");
    private final TextArea descriptionField = new TextArea("Description");

    /**
     * Constructs a new PlantDetailsView.
     *
     * @param plantDetailsService the service to retrieve PlantDetails from the database.
     */
    @Autowired
    public PlantDetailsView(PlantDetailsService plantDetailsService) {
        this.plantDetailsService = plantDetailsService;
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSpacing(true);
        initializeUI();
    }

    /**
     * Initializes the UI components and layouts.
     */
    private void initializeUI() {
        plantIdField.setMin(1);
        plantIdField.setValue(1.0);
        plantIdField.setStep(1);
        fetchButton.addClickListener(event -> fetchPlantDetails());

        add(plantIdField, fetchButton, detailsForm);
        detailsForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        detailsForm.add(commonNameField, scientificNameField, descriptionField);

        commonNameField.setReadOnly(true);
        scientificNameField.setReadOnly(true);
        descriptionField.setReadOnly(true);
    }

    /**
     * Fetches the PlantDetails for the entered plant ID using the PlantDetailsService.
     * If found, the details are displayed; otherwise, a notification is shown.
     */
    private void fetchPlantDetails() {
        Long id = plantIdField.getValue() != null ? plantIdField.getValue().longValue() : null;
        if (id == null) {
            Notification.show("Please enter a valid Plant ID", 3000, Notification.Position.MIDDLE);
            return;
        }

        Optional<PlantDetails> detailsOpt = plantDetailsService.getPlantDetailsById(id);
        if (detailsOpt.isPresent()) {
            updateDetailsForm(detailsOpt.get());
        } else {
            Notification.show("Plant details not found for ID: " + id, 3000, Notification.Position.MIDDLE);
            clearDetailsForm();
        }
    }

    /**
     * Updates the details form with the provided PlantDetails data.
     *
     * @param details the PlantDetails retrieved from the backend.
     */
    private void updateDetailsForm(PlantDetails details) {
        commonNameField.setValue(details.getCommonName() != null ? details.getCommonName() : "");
        scientificNameField.setValue(details.getSpeciesEpithet() != null ? details.getSpeciesEpithet() : "");
        descriptionField.setValue(details.getDescription() != null ? details.getDescription() : "");
    }

    /**
     * Clears the details form fields.
     */
    private void clearDetailsForm() {
        commonNameField.clear();
        scientificNameField.clear();
        descriptionField.clear();
    }
}