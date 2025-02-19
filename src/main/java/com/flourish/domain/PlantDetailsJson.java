package com.flourish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Represents detailed plant information retrieved from the Perenual API,
 * stored as a JSON payload.
 *
 * <p>This entity separates the connection fields (which link this detailed
 * information to the Plant Index) from the rest of the API response. The
 * fields used for relational linking—namely, {@code id}, {@code commonName},
 * {@code scientificName}, and {@code otherName}—are mapped to their own columns.
 * All other information received from the API is stored as a single JSON-formatted
 * string in the {@code detailsJson} column.</p>
 *
 * <p>This design simplifies schema updates if the API response changes and allows
 * us to quickly store and retrieve the entire API payload without extensive column
 * mapping, while still retaining the ability to join with the Plant Index data.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@Entity
@Table(name = "plant_details_json")
public class PlantDetailsJson {

    /**
     * The unique identifier for the plant (provided by the API).
     */
    @Id
    private Long id;

    /**
     * The common name of the plant.
     */
    @Column(name = "common_name", nullable = false)
    private String commonName;

    /**
     * The scientific name of the plant.
     */
    @Column(name = "scientific_name", nullable = false)
    private String scientificName;

    /**
     * Other names or aliases for the plant.
     */
    @Column(name = "other_name")
    private String otherName;

    /**
     * The JSON-formatted string containing all additional details from the API response.
     *
     * <p>This column stores the complete API payload (except for the connecting fields)
     * and can be parsed as needed by the application.</p>
     */
    @Lob
    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    /**
     * Default constructor required by JPA.
     */
    protected PlantDetailsJson() {
        // For JPA
    }

    /**
     * Constructs a new PlantDetailsJson entity.
     *
     * @param id the unique identifier for the plant.
     * @param commonName the common name of the plant.
     * @param scientificName the scientific name of the plant.
     * @param otherName other names or aliases for the plant.
     * @param detailsJson the JSON-formatted string containing additional plant details.
     */
    public PlantDetailsJson(Long id, String commonName, String scientificName, String otherName, String detailsJson) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.otherName = otherName;
        this.detailsJson = detailsJson;
    }

    /**
     * Returns the plant's unique identifier.
     *
     * @return the plant ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the common name of the plant.
     *
     * @return the common name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets the common name of the plant.
     *
     * @param commonName the common name to set.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Returns the scientific name of the plant.
     *
     * @return the scientific name.
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Sets the scientific name of the plant.
     *
     * @param scientificName the scientific name to set.
     */
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    /**
     * Returns other names or aliases of the plant.
     *
     * @return the other name(s).
     */
    public String getOtherName() {
        return otherName;
    }

    /**
     * Sets other names or aliases of the plant.
     *
     * @param otherName the other name(s) to set.
     */
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    /**
     * Returns the JSON-formatted details of the plant.
     *
     * @return a JSON string containing additional plant details.
     */
    public String getDetailsJson() {
        return detailsJson;
    }

    /**
     * Sets the JSON-formatted details of the plant.
     *
     * @param detailsJson a JSON string containing additional plant details.
     */
    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }
}
