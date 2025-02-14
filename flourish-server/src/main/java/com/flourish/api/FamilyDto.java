package com.flourish.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing the family information from Trefle.
 * <p>
 *
 * @author Joar Eliasson
 * @since 2025-02-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyDto {

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
