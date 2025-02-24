package com.flourish.integration.plantdata;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for the detailed plant information from the Perenual API.
 *
 * <p>This DTO captures the key relational fields as well as all other properties
 * via a catch-all map. The fields "scientific_name" and "other_name" are modeled as lists.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDetailsDto {

    private Long id;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private List<String> scientificName;

    @JsonProperty("other_name")
    private List<String> otherName;

    /**
     * Map to hold all other properties from the API response.
     */
    private Map<String, Object> otherProperties = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public List<String> getScientificName() {
        return scientificName;
    }

    public void setScientificName(List<String> scientificName) {
        this.scientificName = scientificName;
    }

    public List<String> getOtherName() {
        return otherName;
    }

    public void setOtherName(List<String> otherName) {
        this.otherName = otherName;
    }

    public Map<String, Object> getOtherProperties() {
        return otherProperties;
    }

    /**
     * Captures any additional properties from the API response.
     *
     * @param key the property name.
     * @param value the property value.
     */
    @JsonAnySetter
    public void setOtherProperty(String key, Object value) {
        otherProperties.put(key, value);
    }
}
