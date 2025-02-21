package com.flourish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Represents detailed plant information (available on the basic plan) as retrieved from the Perenual API.
 *
 * <p>This entity stores only the fields available without an upgrade. The key linking fields (ID,
 * common name, scientific name, and other name) are stored separately to allow for relational joins with
 * a plant index table. Other fields that are arrays or objects are stored as JSON strings (in TEXT columns)
 * to keep the schema simple. Null values are allowed for optional fields.</p>
 *
 * <p>Available fields include:
 * <ul>
 *   <li>id</li>
 *   <li>commonName</li>
 *   <li>type</li>
 *   <li>pruningCount (as JSON)</li>
 *   <li>saltTolerant</li>
 *   <li>careGuides</li>
 *   <li>wateringBenchmarkValue, wateringBenchmarkUnit</li>
 *   <li>growthRate</li>
 *   <li>harvestSeason</li>
 *   <li>cones</li>
 *   <li>attracts (as JSON)</li>
 *   <li>pestSusceptibility (as JSON)</li>
 *   <li>flowers</li>
 *   <li>invasive</li>
 *   <li>seeds</li>
 *   <li>poisonousToHumans</li>
 *   <li>propagation (as JSON)</li>
 *   <li>cultivar</li>
 *   <li>genus</li>
 *   <li>indoor</li>
 *   <li>speciesEpithet</li>
 *   <li>defaultImage details (split into subfields)</li>
 *   <li>subspecies</li>
 *   <li>thorny</li>
 *   <li>floweringSeason</li>
 *   <li>origin (as JSON)</li>
 *   <li>edibleFruit</li>
 *   <li>description</li>
 *   <li>soil (as JSON)</li>
 *   <li>medicinal</li>
 *   <li>cycle</li>
 *   <li>fruits</li>
 *   <li>droughtTolerant</li>
 *   <li>cuisine</li>
 *   <li>hardinessLocation (full URL and full iframe)</li>
 *   <li>plantAnatomy (as JSON)</li>
 *   <li>leaf</li>
 *   <li>pruningMonth (as JSON)</li>
 *   <li>hardinessMin, hardinessMax</li>
 *   <li>tropical</li>
 *   <li>sunlight (as JSON)</li>
 *   <li>watering</li>
 *   <li>poisonousToPets</li>
 *   <li>careLevel</li>
 *   <li>edibleLeaf</li>
 *   <li>family</li>
 *   <li>maintenance</li>
 *   <li>dimensions (as JSON)</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "plant_details")
public class PlantDetails {

    @Id
    private Long id;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "pruning_count", columnDefinition = "TEXT")
    private String pruningCount;

    @Column(name = "salt_tolerant")
    private Boolean saltTolerant;

    @Column(name = "care_guides")
    private String careGuides;

    @Column(name = "growth_rate")
    private String growthRate;

    @Column(name = "harvest_season")
    private String harvestSeason;

    @Column(name = "cones")
    private Boolean cones;

    @Lob
    @Column(name = "attracts", columnDefinition = "TEXT")
    private String attracts;

    @Lob
    @Column(name = "pest_susceptibility", columnDefinition = "TEXT")
    private String pestSusceptibility;

    @Column(name = "flowers")
    private Boolean flowers;

    @Column(name = "invasive")
    private Boolean invasive;

    @Column(name = "seeds")
    private Boolean seeds;

    @Column(name = "poisonous_to_humans")
    private Boolean poisonousToHumans;

    @Lob
    @Column(name = "propagation", columnDefinition = "TEXT")
    private String propagation;

    @Column(name = "genus")
    private String genus;

    @Column(name = "indoor")
    private Boolean indoor;

    @Column(name = "species_epithet")
    private String speciesEpithet;

    @Column(name = "default_image_license_name")
    private String defaultImageLicenseName;

    @Column(name = "default_image_license_url")
    private String defaultImageLicenseUrl;

    @Lob
    @Column(name = "default_image_original_url", columnDefinition = "TEXT")
    private String defaultImageOriginalUrl;

    @Lob
    @Column(name = "default_image_regular_url", columnDefinition = "TEXT")
    private String defaultImageRegularUrl;

    @Lob
    @Column(name = "default_image_medium_url", columnDefinition = "TEXT")
    private String defaultImageMediumUrl;

    @Lob
    @Column(name = "default_image_small_url", columnDefinition = "TEXT")
    private String defaultImageSmallUrl;

    @Lob
    @Column(name = "default_image_thumbnail", columnDefinition = "TEXT")
    private String defaultImageThumbnail;

    @Column(name = "thorny")
    private Boolean thorny;

    @Column(name = "flowering_season")
    private String floweringSeason;

    @Lob
    @Column(name = "origin", columnDefinition = "TEXT")
    private String origin;

    @Column(name = "edible_fruit")
    private Boolean edibleFruit;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "soil", columnDefinition = "TEXT")
    private String soil;

    @Column(name = "medicinal")
    private Boolean medicinal;

    @Column(name = "cycle")
    private String cycle;

    @Column(name = "fruits")
    private Boolean fruits;

    @Column(name = "drought_tolerant")
    private Boolean droughtTolerant;

    @Column(name = "cuisine")
    private Boolean cuisine;

    @Column(name = "hardiness_location_full_url")
    private String hardinessLocationFullUrl;

    @Lob
    @Column(name = "hardiness_location_full_iframe", columnDefinition = "TEXT")
    private String hardinessLocationFullIframe;

    @Lob
    @Column(name = "plant_anatomy", columnDefinition = "TEXT")
    private String plantAnatomy;

    @Column(name = "leaf")
    private Boolean leaf;

    @Lob
    @Column(name = "pruning_month", columnDefinition = "TEXT")
    private String pruningMonth;

    @Column(name = "hardiness_min")
    private String hardinessMin;

    @Column(name = "hardiness_max")
    private String hardinessMax;

    @Column(name = "tropical")
    private Boolean tropical;

    @Lob
    @Column(name = "sunlight", columnDefinition = "TEXT")
    private String sunlight;

    @Column(name = "watering")
    private String watering;

    @Column(name = "poisonous_to_pets")
    private Boolean poisonousToPets;

    @Column(name = "care_level")
    private String careLevel;

    @Column(name = "edible_leaf")
    private Boolean edibleLeaf;

    @Column(name = "family")
    private String family;

    @Column(name = "maintenance")
    private String maintenance;

    @Lob
    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions;

    protected PlantDetails() { }

    /**
     * Constructs a new PlantDetails entity with the provided values.
     *
     * @param id the plant ID.
     * @param commonName the common name.
     * @param type the plant type.
     * @param pruningCount the pruning count as JSON.
     * @param saltTolerant whether the plant is salt tolerant.
     * @param careGuides URL for care guides.
     * @param growthRate the growth rate.
     * @param harvestSeason the harvest season.
     * @param cones whether the plant produces cones.
     * @param attracts JSON string for attracted species.
     * @param pestSusceptibility JSON string for pest susceptibility.
     * @param flowers whether the plant flowers.
     * @param invasive whether the plant is invasive.
     * @param seeds whether the plant produces seeds.
     * @param poisonousToHumans whether the plant is poisonous to humans.
     * @param propagation JSON string for propagation methods.
     * @param genus the genus.
     * @param indoor whether the plant is indoor.
     * @param speciesEpithet the species epithet.
     * @param defaultImageLicenseName the default image license name.
     * @param defaultImageLicenseUrl the default image license URL.
     * @param defaultImageOriginalUrl the default image original URL.
     * @param defaultImageRegularUrl the default image regular URL.
     * @param defaultImageMediumUrl the default image medium URL.
     * @param defaultImageSmallUrl the default image small URL.
     * @param defaultImageThumbnail the default image thumbnail URL.
     * @param thorny whether the plant is thorny.
     * @param floweringSeason the flowering season.
     * @param origin JSON string for origin.
     * @param edibleFruit whether the plant is edible.
     * @param description the description.
     * @param soil JSON string for soil.
     * @param medicinal whether the plant is medicinal.
     * @param cycle the growth cycle.
     * @param fruits whether the plant produces fruits.
     * @param droughtTolerant whether the plant is drought tolerant.
     * @param cuisine whether the plant is used in cuisine.
     * @param hardinessLocationFullUrl the hardiness location full URL.
     * @param hardinessLocationFullIframe the hardiness location full iframe.
     * @param plantAnatomy JSON string for plant anatomy.
     * @param leaf whether the plant has leaves.
     * @param pruningMonth JSON string for pruning month.
     * @param hardinessMin the minimum hardiness.
     * @param hardinessMax the maximum hardiness.
     * @param tropical whether the plant is tropical.
     * @param sunlight JSON string for sunlight.
     * @param watering the watering information.
     * @param poisonousToPets whether the plant is poisonous to pets.
     * @param careLevel the care level.
     * @param edibleLeaf whether the leaves are edible.
     * @param family the family.
     * @param maintenance the maintenance information.
     * @param dimensions JSON string for dimensions.
     */
    public PlantDetails(Long id, String commonName, String type, String pruningCount, Boolean saltTolerant,
                        String careGuides, String growthRate, String harvestSeason, Boolean cones, String attracts,
                        String pestSusceptibility, Boolean flowers, Boolean invasive, Boolean seeds,
                        Boolean poisonousToHumans, String propagation, String genus, Boolean indoor,
                        String speciesEpithet, String defaultImageLicenseName,
                        String defaultImageLicenseUrl, String defaultImageOriginalUrl, String defaultImageRegularUrl,
                        String defaultImageMediumUrl, String defaultImageSmallUrl, String defaultImageThumbnail,
                        Boolean thorny, String floweringSeason, String origin, Boolean edibleFruit, String description,
                        String soil, Boolean medicinal, String cycle, Boolean fruits, Boolean droughtTolerant,
                        Boolean cuisine, String hardinessLocationFullUrl, String hardinessLocationFullIframe,
                        String plantAnatomy, Boolean leaf, String pruningMonth, String hardinessMin,
                        String hardinessMax, Boolean tropical, String sunlight, String watering,
                        Boolean poisonousToPets, String careLevel, Boolean edibleLeaf, String family,
                        String maintenance, String dimensions) {
        this.id = id;
        this.commonName = commonName;
        this.type = type;
        this.pruningCount = pruningCount;
        this.saltTolerant = saltTolerant;
        this.careGuides = careGuides;
        this.growthRate = growthRate;
        this.harvestSeason = harvestSeason;
        this.cones = cones;
        this.attracts = attracts;
        this.pestSusceptibility = pestSusceptibility;
        this.flowers = flowers;
        this.invasive = invasive;
        this.seeds = seeds;
        this.poisonousToHumans = poisonousToHumans;
        this.propagation = propagation;
        this.genus = genus;
        this.indoor = indoor;
        this.speciesEpithet = speciesEpithet;
        this.defaultImageLicenseName = defaultImageLicenseName;
        this.defaultImageLicenseUrl = defaultImageLicenseUrl;
        this.defaultImageOriginalUrl = defaultImageOriginalUrl;
        this.defaultImageRegularUrl = defaultImageRegularUrl;
        this.defaultImageMediumUrl = defaultImageMediumUrl;
        this.defaultImageSmallUrl = defaultImageSmallUrl;
        this.defaultImageThumbnail = defaultImageThumbnail;
        this.thorny = thorny;
        this.floweringSeason = floweringSeason;
        this.origin = origin;
        this.edibleFruit = edibleFruit;
        this.description = description;
        this.soil = soil;
        this.medicinal = medicinal;
        this.cycle = cycle;
        this.fruits = fruits;
        this.droughtTolerant = droughtTolerant;
        this.cuisine = cuisine;
        this.hardinessLocationFullUrl = hardinessLocationFullUrl;
        this.hardinessLocationFullIframe = hardinessLocationFullIframe;
        this.plantAnatomy = plantAnatomy;
        this.leaf = leaf;
        this.pruningMonth = pruningMonth;
        this.hardinessMin = hardinessMin;
        this.hardinessMax = hardinessMax;
        this.tropical = tropical;
        this.sunlight = sunlight;
        this.watering = watering;
        this.poisonousToPets = poisonousToPets;
        this.careLevel = careLevel;
        this.edibleLeaf = edibleLeaf;
        this.family = family;
        this.maintenance = maintenance;
        this.dimensions = dimensions;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPruningCount() {
        return pruningCount;
    }

    public void setPruningCount(String pruningCount) {
        this.pruningCount = pruningCount;
    }

    public Boolean getSaltTolerant() {
        return saltTolerant;
    }

    public void setSaltTolerant(Boolean saltTolerant) {
        this.saltTolerant = saltTolerant;
    }

    public String getCareGuides() {
        return careGuides;
    }

    public void setCareGuides(String careGuides) {
        this.careGuides = careGuides;
    }

    public String getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(String growthRate) {
        this.growthRate = growthRate;
    }

    public String getHarvestSeason() {
        return harvestSeason;
    }

    public void setHarvestSeason(String harvestSeason) {
        this.harvestSeason = harvestSeason;
    }

    public Boolean getCones() {
        return cones;
    }

    public void setCones(Boolean cones) {
        this.cones = cones;
    }

    public String getAttracts() {
        return attracts;
    }

    public void setAttracts(String attracts) {
        this.attracts = attracts;
    }

    public String getPestSusceptibility() {
        return pestSusceptibility;
    }

    public void setPestSusceptibility(String pestSusceptibility) {
        this.pestSusceptibility = pestSusceptibility;
    }

    public Boolean getFlowers() {
        return flowers;
    }

    public void setFlowers(Boolean flowers) {
        this.flowers = flowers;
    }

    public Boolean getInvasive() {
        return invasive;
    }

    public void setInvasive(Boolean invasive) {
        this.invasive = invasive;
    }

    public Boolean getSeeds() {
        return seeds;
    }

    public void setSeeds(Boolean seeds) {
        this.seeds = seeds;
    }

    public Boolean getPoisonousToHumans() {
        return poisonousToHumans;
    }

    public void setPoisonousToHumans(Boolean poisonousToHumans) {
        this.poisonousToHumans = poisonousToHumans;
    }

    public String getPropagation() {
        return propagation;
    }

    public void setPropagation(String propagation) {
        this.propagation = propagation;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public Boolean getIndoor() {
        return indoor;
    }

    public void setIndoor(Boolean indoor) {
        this.indoor = indoor;
    }

    public String getSpeciesEpithet() {
        return speciesEpithet;
    }

    public void setSpeciesEpithet(String speciesEpithet) {
        this.speciesEpithet = speciesEpithet;
    }

    public String getDefaultImageLicenseName() {
        return defaultImageLicenseName;
    }

    public void setDefaultImageLicenseName(String defaultImageLicenseName) {
        this.defaultImageLicenseName = defaultImageLicenseName;
    }

    public String getDefaultImageLicenseUrl() {
        return defaultImageLicenseUrl;
    }

    public void setDefaultImageLicenseUrl(String defaultImageLicenseUrl) {
        this.defaultImageLicenseUrl = defaultImageLicenseUrl;
    }

    public String getDefaultImageOriginalUrl() {
        return defaultImageOriginalUrl;
    }

    public void setDefaultImageOriginalUrl(String defaultImageOriginalUrl) {
        this.defaultImageOriginalUrl = defaultImageOriginalUrl;
    }

    public String getDefaultImageRegularUrl() {
        return defaultImageRegularUrl;
    }

    public void setDefaultImageRegularUrl(String defaultImageRegularUrl) {
        this.defaultImageRegularUrl = defaultImageRegularUrl;
    }

    public String getDefaultImageMediumUrl() {
        return defaultImageMediumUrl;
    }

    public void setDefaultImageMediumUrl(String defaultImageMediumUrl) {
        this.defaultImageMediumUrl = defaultImageMediumUrl;
    }

    public String getDefaultImageSmallUrl() {
        return defaultImageSmallUrl;
    }

    public void setDefaultImageSmallUrl(String defaultImageSmallUrl) {
        this.defaultImageSmallUrl = defaultImageSmallUrl;
    }

    public String getDefaultImageThumbnail() {
        return defaultImageThumbnail;
    }

    public void setDefaultImageThumbnail(String defaultImageThumbnail) {
        this.defaultImageThumbnail = defaultImageThumbnail;
    }

    public Boolean getThorny() {
        return thorny;
    }

    public void setThorny(Boolean thorny) {
        this.thorny = thorny;
    }

    public String getFloweringSeason() {
        return floweringSeason;
    }

    public void setFloweringSeason(String floweringSeason) {
        this.floweringSeason = floweringSeason;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Boolean getEdibleFruit() {
        return edibleFruit;
    }

    public void setEdibleFruit(Boolean edibleFruit) {
        this.edibleFruit = edibleFruit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public Boolean getMedicinal() {
        return medicinal;
    }

    public void setMedicinal(Boolean medicinal) {
        this.medicinal = medicinal;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Boolean getFruits() {
        return fruits;
    }

    public void setFruits(Boolean fruits) {
        this.fruits = fruits;
    }

    public Boolean getDroughtTolerant() {
        return droughtTolerant;
    }

    public void setDroughtTolerant(Boolean droughtTolerant) {
        this.droughtTolerant = droughtTolerant;
    }

    public Boolean getCuisine() {
        return cuisine;
    }

    public void setCuisine(Boolean cuisine) {
        this.cuisine = cuisine;
    }

    public String getHardinessLocationFullUrl() {
        return hardinessLocationFullUrl;
    }

    public void setHardinessLocationFullUrl(String hardinessLocationFullUrl) {
        this.hardinessLocationFullUrl = hardinessLocationFullUrl;
    }

    public String getHardinessLocationFullIframe() {
        return hardinessLocationFullIframe;
    }

    public void setHardinessLocationFullIframe(String hardinessLocationFullIframe) {
        this.hardinessLocationFullIframe = hardinessLocationFullIframe;
    }

    public String getPlantAnatomy() {
        return plantAnatomy;
    }

    public void setPlantAnatomy(String plantAnatomy) {
        this.plantAnatomy = plantAnatomy;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getPruningMonth() {
        return pruningMonth;
    }

    public void setPruningMonth(String pruningMonth) {
        this.pruningMonth = pruningMonth;
    }

    public String getHardinessMin() {
        return hardinessMin;
    }

    public void setHardinessMin(String hardinessMin) {
        this.hardinessMin = hardinessMin;
    }

    public String getHardinessMax() {
        return hardinessMax;
    }

    public void setHardinessMax(String hardinessMax) {
        this.hardinessMax = hardinessMax;
    }

    public Boolean getTropical() {
        return tropical;
    }

    public void setTropical(Boolean tropical) {
        this.tropical = tropical;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public Boolean getPoisonousToPets() {
        return poisonousToPets;
    }

    public void setPoisonousToPets(Boolean poisonousToPets) {
        this.poisonousToPets = poisonousToPets;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public Boolean getEdibleLeaf() {
        return edibleLeaf;
    }

    public void setEdibleLeaf(Boolean edibleLeaf) {
        this.edibleLeaf = edibleLeaf;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
}
