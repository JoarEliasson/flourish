package com.flourish.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a single plant species record as returned in the "data" array
 * of the species-list API response.
 *
 * <p>This DTO maps the JSON keys from the API to Java fields. Note that the API returns
 * the scientific_name and other_name fields as arrays; therefore, these are modeled as lists.
 * In our mapping to the PlantIndex entity, we join the lists into a single comma-separated string.</p>
 *
 * @see PlantListResponseDto
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantListDto {

    private Long id;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private List<String> scientificName;

    @JsonProperty("other_name")
    private List<String> otherName;


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

}
