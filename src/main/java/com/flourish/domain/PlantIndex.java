package com.flourish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a basic index (summary) of a plant species as retrieved from the Perenual API.
 *
 * <p>This entity is used to store the minimal information required for search and indexing.
 * It contains:
 * <ul>
 *   <li><b>id</b>: The unique identifier for the plant species (provided by the API).</li>
 *   <li><b>commonName</b>: The common name of the plant.</li>
 *   <li><b>scientificName</b>: The scientific name of the plant.</li>
 *   <li><b>otherName</b>: Any alternative names or aliases.</li>
 * </ul>
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@Entity
@Table(name = "plant_index")
public class PlantIndex {

    /**
     * The unique identifier for the plant species.
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
     * Default constructor for JPA.
     */
    protected PlantIndex() {}

    /**
     * Constructs a new PlantIndex with the given parameters.
     *
     * @param id the unique identifier for the plant.
     * @param commonName the common name of the plant.
     * @param scientificName the scientific name of the plant.
     * @param otherName other names or aliases for the plant.
     */
    public PlantIndex(Long id, String commonName, String scientificName, String otherName) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.otherName = otherName;
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
     * Returns other names or aliases for the plant.
     *
     * @return the other name, or null if not set.
     */
    public String getOtherName() {
        return otherName;
    }

    /**
     * Sets the other names or aliases for the plant.
     *
     * @param otherName the other name to set.
     */
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

}
