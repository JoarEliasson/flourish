package se.myhappyplants.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.myhappyplants.api.PlantObj;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrefleApiResponse {
    private List<PlantObj> data;

    @JsonProperty("links")
    private Map<String, String> links;  // Stores pagination links (next, previous, etc.)

    public List<PlantObj> getData() {
        return data;
    }

    public void setData(List<PlantObj> data) {
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
