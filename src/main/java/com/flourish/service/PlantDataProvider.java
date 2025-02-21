package com.flourish.service;

import com.flourish.domain.PlantDetails;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to inspect a PlantDetails record and report which fields have data.
 *
 * <p>This class provides a method to return a Map where each key is a field name
 * from the PlantDetails entity and the value is a Boolean indicating whether the field is populated.
 * For String fields, it checks that the string is not null or empty; for other types, it checks for non-null values.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-21
 */
public class PlantDataProvider {

    /**
     * Returns a map of field names to booleans indicating whether the corresponding field in the provided
     * PlantDetails record is populated (non-null and, for Strings, not empty).
     *
     * @param details the PlantDetails record to inspect.
     * @return a Map of field names to booleans.
     */
    public static Map<String, Boolean> getAvailableFields(PlantDetails details) {
        Map<String, Boolean> available = new HashMap<>();

        available.put("id", details.getId() != null);
        available.put("commonName", details.getCommonName() != null && !details.getCommonName().isEmpty());
        available.put("type", details.getType() != null && !details.getType().isEmpty());
        available.put("pruningCount", details.getPruningCount() != null && !details.getPruningCount().isEmpty());
        available.put("saltTolerant", details.getSaltTolerant() != null);
        available.put("careGuides", details.getCareGuides() != null && !details.getCareGuides().isEmpty());
        available.put("growthRate", details.getGrowthRate() != null && !details.getGrowthRate().isEmpty());
        available.put("harvestSeason", details.getHarvestSeason() != null && !details.getHarvestSeason().isEmpty());
        available.put("cones", details.getCones() != null);
        available.put("attracts", details.getAttracts() != null && !details.getAttracts().isEmpty());
        available.put("pestSusceptibility", details.getPestSusceptibility() != null && !details.getPestSusceptibility().isEmpty());
        available.put("flowers", details.getFlowers() != null);
        available.put("invasive", details.getInvasive() != null);
        available.put("seeds", details.getSeeds() != null);
        available.put("poisonousToHumans", details.getPoisonousToHumans() != null);
        available.put("propagation", details.getPropagation() != null && !details.getPropagation().isEmpty());
        available.put("genus", details.getGenus() != null && !details.getGenus().isEmpty());
        available.put("indoor", details.getIndoor() != null);
        available.put("speciesEpithet", details.getSpeciesEpithet() != null && !details.getSpeciesEpithet().isEmpty());
        available.put("defaultImageLicenseName", details.getDefaultImageLicenseName() != null && !details.getDefaultImageLicenseName().isEmpty());
        available.put("defaultImageLicenseUrl", details.getDefaultImageLicenseUrl() != null && !details.getDefaultImageLicenseUrl().isEmpty());
        available.put("defaultImageOriginalUrl", details.getDefaultImageOriginalUrl() != null && !details.getDefaultImageOriginalUrl().isEmpty());
        available.put("defaultImageRegularUrl", details.getDefaultImageRegularUrl() != null && !details.getDefaultImageRegularUrl().isEmpty());
        available.put("defaultImageMediumUrl", details.getDefaultImageMediumUrl() != null && !details.getDefaultImageMediumUrl().isEmpty());
        available.put("defaultImageSmallUrl", details.getDefaultImageSmallUrl() != null && !details.getDefaultImageSmallUrl().isEmpty());
        available.put("defaultImageThumbnail", details.getDefaultImageThumbnail() != null && !details.getDefaultImageThumbnail().isEmpty());
        available.put("thorny", details.getThorny() != null);
        available.put("floweringSeason", details.getFloweringSeason() != null && !details.getFloweringSeason().isEmpty());
        available.put("origin", details.getOrigin() != null && !details.getOrigin().isEmpty());
        available.put("edibleFruit", details.getEdibleFruit() != null);
        available.put("description", details.getDescription() != null && !details.getDescription().isEmpty());
        available.put("soil", details.getSoil() != null && !details.getSoil().isEmpty());
        available.put("medicinal", details.getMedicinal() != null);
        available.put("cycle", details.getCycle() != null && !details.getCycle().isEmpty());
        available.put("fruits", details.getFruits() != null);
        available.put("droughtTolerant", details.getDroughtTolerant() != null);
        available.put("cuisine", details.getCuisine() != null);
        available.put("hardinessLocationFullUrl", details.getHardinessLocationFullUrl() != null && !details.getHardinessLocationFullUrl().isEmpty());
        available.put("hardinessLocationFullIframe", details.getHardinessLocationFullIframe() != null && !details.getHardinessLocationFullIframe().isEmpty());
        available.put("plantAnatomy", details.getPlantAnatomy() != null && !details.getPlantAnatomy().isEmpty());
        available.put("leaf", details.getLeaf() != null);
        available.put("pruningMonth", details.getPruningMonth() != null && !details.getPruningMonth().isEmpty());
        available.put("hardinessMin", details.getHardinessMin() != null && !details.getHardinessMin().isEmpty());
        available.put("hardinessMax", details.getHardinessMax() != null && !details.getHardinessMax().isEmpty());
        available.put("tropical", details.getTropical() != null);
        available.put("sunlight", details.getSunlight() != null && !details.getSunlight().isEmpty());
        available.put("watering", details.getWatering() != null && !details.getWatering().isEmpty());
        available.put("poisonousToPets", details.getPoisonousToPets() != null);
        available.put("careLevel", details.getCareLevel() != null && !details.getCareLevel().isEmpty());
        available.put("edibleLeaf", details.getEdibleLeaf() != null);
        available.put("family", details.getFamily() != null && !details.getFamily().isEmpty());
        available.put("maintenance", details.getMaintenance() != null && !details.getMaintenance().isEmpty());
        available.put("dimensions", details.getDimensions() != null && !details.getDimensions().isEmpty());

        return available;
    }
}
