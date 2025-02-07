package se.myhappyplants.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * PlantObj represents a plant species object from the Trefle API.
 * <p>
 * The object contains fields for the plant's ID, common name, scientific name, genus, family,
 * image URL, and synonyms.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-04
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDto {
    private int id;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private String scientificName;

    private String genus;
    private String family;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<String> synonyms;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getGenus() { return genus; }
    public void setGenus(String genus) { this.genus = genus; }

    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getSynonyms() { return synonyms; }
    public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", commonName='" + commonName + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", genus='" + genus + '\'' +
                ", family='" + family + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
