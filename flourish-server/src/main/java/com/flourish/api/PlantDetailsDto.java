package com.flourish.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for detailed plant information from Trefle.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDetailsDto {

    @JsonProperty("genus")
    private GenusDto genus;

    @JsonProperty("scientific_name")
    private String scientificName;

    @JsonProperty("light")
    private int light;

    @JsonProperty("water_frequency")
    private int waterFrequency;

    @JsonProperty("family")
    private FamilyDto family;


    public GenusDto getGenus() {
        return genus;
    }

    public void setGenus(GenusDto genus) {
        this.genus = genus;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getWaterFrequency() {
        return waterFrequency;
    }

    public void setWaterFrequency(int waterFrequency) {
        this.waterFrequency = waterFrequency;
    }

    public FamilyDto getFamily() {
        return family;
    }

    public void setFamily(FamilyDto family) {
        this.family = family;
    }
}
