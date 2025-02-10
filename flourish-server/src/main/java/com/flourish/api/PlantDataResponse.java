package com.flourish.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * TrefleApiResponse represents a response from the Trefle API.
 * <p>
 * Used as a wrapper for the list of PlantObj objects returned by the API.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDataResponse {
    private List<PlantDto> data;

    @JsonProperty("links")
    private Map<String, String> links;

    public List<PlantDto> getData() {
        return data;
    }

    public void setData(List<PlantDto> data) {
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
