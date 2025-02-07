package se.myhappyplants.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for the API response when retrieving plant details.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDetailsResponse {

    @JsonProperty("data")
    private PlantDetailsDto data;

    public PlantDetailsDto getData() {
        return data;
    }

    public void setData(PlantDetailsDto data) {
        this.data = data;
    }
}
