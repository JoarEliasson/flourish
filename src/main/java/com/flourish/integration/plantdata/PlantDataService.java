package com.flourish.integration.plantdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.domain.PlantDetails;
import com.flourish.repository.PlantDetailsRepository;
import com.flourish.domain.PlantIndex;
import com.flourish.repository.PlantIndexRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Service for fetching and storing plant data from the Perenual API.
 *
 * @see PlantIndex
 * @see PlantIndexRepository
 * @see PlantListResponseDto
 * @see PlantListDto
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-20
 */
@Service
public class PlantDataService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private final PlantIndexRepository plantIndexRepository;
    private final PlantDetailsRepository plantDetailsRepository;

    @Value("${perenual.api.key}")
    private String perenualApiKey;

    @Value("${perenual.api.speciesListUrl:https://perenual.com/api/v2/species-list}")
    private String speciesListUrl;

    @Value("${perenual.api.plantDetailsUrl:https://perenual.com/api/v2/species/details}")
    private String plantDetailsUrl;

    /**
     * Constructs a new PlantDataService.
     *
     * @param webClient the WebClient used to make HTTP calls.
     * @param plantIndexRepository the repository for PlantIndex entities.
     * @param plantDetailsRepository the repository for PlantDetailsJson entities.
     */
    public PlantDataService(WebClient webClient, PlantIndexRepository plantIndexRepository, PlantDetailsRepository plantDetailsRepository) {
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
        this.plantIndexRepository = plantIndexRepository;
        this.plantDetailsRepository = plantDetailsRepository;
    }

    /**
     * Retrieves plant data from the Perenual API species-list endpoint for plant IDs between startId and endId.
     * The method iterates through pages of results, making at most 99 API requests.
     * After 99 API calls, it prints the last plant ID processed and exits.
     *
     * @param startId the starting plant ID (inclusive).
     * @param endId the ending plant ID (inclusive).
     */
    public void fetchAndStorePlantListLimited(int startId, int endId) {
        int currentPage = 99;
        int apiRequestCount = 0;
        boolean morePages = true;
        long lastIdProcessed = 0;

        while (morePages && apiRequestCount < 99) {
            apiRequestCount++;
            String url = speciesListUrl + "?key=" + perenualApiKey + "&page=" + currentPage;
            PlantListResponseDto response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(PlantListResponseDto.class)
                    .block();

            if (response == null || response.getData() == null) {
                System.out.println("No response received for page " + currentPage);
                break;
            }

            List<PlantIndex> plantsToSave = response.getData().stream()
                    .filter(dto -> dto.getId() != null && dto.getId() >= startId && dto.getId() <= endId)
                    .map(dto -> {
                        try {
                            String scientificName = (dto.getScientificName() != null)
                                    ? String.join(", ", dto.getScientificName())
                                    : "";
                            String otherName = (dto.getOtherName() != null)
                                    ? String.join(", ", dto.getOtherName())
                                    : "";
                            System.out.printf("Plant ID: %d, Common Name: %s, Scientific Name: %s, Other Name: %s%n",
                                    dto.getId(), dto.getCommonName(), scientificName, otherName);

                            return new PlantIndex(
                                    dto.getId(),
                                    dto.getCommonName() != null ? dto.getCommonName() : "",
                                    scientificName,
                                    otherName
                            );
                        } catch (Exception e) {
                            System.out.println("Error mapping plant data: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!plantsToSave.isEmpty()) {
                plantIndexRepository.saveAll(plantsToSave);
                lastIdProcessed = plantsToSave.stream().mapToLong(PlantIndex::getId).max().orElse(lastIdProcessed);
                System.out.println("Saved " + plantsToSave.size() + " plants from page " + currentPage);
            } else {
                System.out.println("No plants in the specified ID range found on page " + currentPage);
            }

            if (response.getCurrentPage() >= response.getLastPage()) {
                morePages = false;
            } else {
                currentPage++;
            }
        }
        System.out.println("After " + apiRequestCount + " API requests, last plant ID processed: " + lastIdProcessed);
    }

    /**
     * Retrieves detailed plant data for plant IDs between startId and endId by sending individual API requests.
     * <p>The process stops after making 99 API requests.
     * For each request, the API response is first retrieved as a String.</p>
     *
     * @param startId the starting plant ID (inclusive)
     * @param endId the ending plant ID (inclusive)
     */
    public void fetchAndStorePlantDetailsLimited(int startId, int endId) {
        int apiRequestCount = 0;
        int currentId = startId;
        while (currentId <= endId && apiRequestCount < 99) {
            apiRequestCount++;
            String url = plantDetailsUrl + "/" + currentId + "?key=" + perenualApiKey;
            String responseBody = null;
            try {
                responseBody = webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                        .block();
            } catch (Exception e) {
                System.out.println("Error retrieving details for plant ID " + currentId + ": " + e.getMessage());
            }
            if (responseBody == null || !responseBody.trim().startsWith("{")) {
                System.out.println("Skipping plant ID " + currentId + " due to unsupported content type or empty response.");
            } else {
                PlantDetails details = mapToPlantDetails(responseBody);
                if (details != null) {
                    plantDetailsRepository.save(details);
                    System.out.println("Saved details for plant ID: " + currentId);
                } else {
                    System.out.println("Mapping failed for plant ID: " + currentId);
                }
            }
            currentId++;
        }
        System.out.println("\nAfter [" + apiRequestCount + "] API requests, last plant ID processed: [" + (currentId - 1) + "]");
        System.out.println("\nNext plant ID to process: [" + currentId + "]\n");
    }

    /**
     * Maps the JSON response (as a String) to a PlantDetails entity.
     *
     * <p>The method deserializes the response into a Map, then extracts available fields.
     * For fields that are arrays or nested objects, the values are re-serialized to JSON strings.</p>
     *
     * @param json the JSON response as a String.
     * @return a PlantDetails entity representing the API response, or null if mapping fails.
     */
    private PlantDetails mapToPlantDetails(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {
            });

            Long id = map.get("id") != null ? ((Number) map.get("id")).longValue() : null;
            String commonName = map.get("common_name") != null ? map.get("common_name").toString() : null;
            String type = map.get("type") != null ? map.get("type").toString() : null;
            String pruningCount = map.get("pruning_count") != null ? objectMapper.writeValueAsString(map.get("pruning_count")) : null;
            Boolean saltTolerant = map.get("salt_tolerant") != null ? Boolean.valueOf(map.get("salt_tolerant").toString()) : null;
            String careGuides = map.get("care_guides") != null ? map.get("care_guides").toString() : null;
            String growthRate = map.get("growth_rate") != null ? map.get("growth_rate").toString() : null;
            String harvestSeason = map.get("harvest_season") != null ? map.get("harvest_season").toString() : null;
            Boolean cones = map.get("cones") != null ? Boolean.valueOf(map.get("cones").toString()) : null;
            String attracts = map.get("attracts") != null ? objectMapper.writeValueAsString(map.get("attracts")) : null;
            String pestSusceptibility = map.get("pest_susceptibility") != null ? objectMapper.writeValueAsString(map.get("pest_susceptibility")) : null;
            Boolean flowers = map.get("flowers") != null ? Boolean.valueOf(map.get("flowers").toString()) : null;
            Boolean invasive = map.get("invasive") != null ? Boolean.valueOf(map.get("invasive").toString()) : null;
            Boolean seeds = map.get("seeds") != null ? Boolean.valueOf(map.get("seeds").toString()) : null;
            Boolean poisonousToHumans = map.get("poisonous_to_humans") != null ? Boolean.valueOf(map.get("poisonous_to_humans").toString()) : null;
            String propagation = map.get("propagation") != null ? objectMapper.writeValueAsString(map.get("propagation")) : null;
            String genus = map.get("genus") != null ? map.get("genus").toString() : null;
            Boolean indoor = map.get("indoor") != null ? Boolean.valueOf(map.get("indoor").toString()) : null;
            String speciesEpithet = map.get("species_epithet") != null ? map.get("species_epithet").toString() : null;
            String defaultImageLicenseName = null;
            String defaultImageLicenseUrl = null;
            String defaultImageOriginalUrl = null;
            String defaultImageRegularUrl = null;
            String defaultImageMediumUrl = null;
            String defaultImageSmallUrl = null;
            String defaultImageThumbnail = null;
            if (map.get("default_image") != null) {
                try {
                    Map<String, Object> defImg = (Map<String, Object>) map.get("default_image");
                    defaultImageLicenseName = defImg.get("license_name") != null ? defImg.get("license_name").toString() : null;
                    defaultImageLicenseUrl = defImg.get("license_url") != null ? defImg.get("license_url").toString() : null;
                    defaultImageOriginalUrl = defImg.get("original_url") != null ? defImg.get("original_url").toString() : null;
                    defaultImageRegularUrl = defImg.get("regular_url") != null ? defImg.get("regular_url").toString() : null;
                    defaultImageMediumUrl = defImg.get("medium_url") != null ? defImg.get("medium_url").toString() : null;
                    defaultImageSmallUrl = defImg.get("small_url") != null ? defImg.get("small_url").toString() : null;
                    defaultImageThumbnail = defImg.get("thumbnail") != null ? defImg.get("thumbnail").toString() : null;
                } catch (Exception e) {
                    System.out.println("Error extracting default image: " + e.getMessage());
                }
            }
            Boolean thorny = map.get("thorny") != null ? Boolean.valueOf(map.get("thorny").toString()) : null;
            String floweringSeason = map.get("flowering_season") != null ? map.get("flowering_season").toString() : null;
            String origin = map.get("origin") != null ? objectMapper.writeValueAsString(map.get("origin")) : null;
            Boolean edibleFruit = map.get("edible_fruit") != null ? Boolean.valueOf(map.get("edible_fruit").toString()) : null;
            String description = map.get("description") != null ? map.get("description").toString() : null;
            String soil = map.get("soil") != null ? objectMapper.writeValueAsString(map.get("soil")) : null;
            Boolean medicinal = map.get("medicinal") != null ? Boolean.valueOf(map.get("medicinal").toString()) : null;
            String cycle = map.get("cycle") != null ? map.get("cycle").toString() : null;
            Boolean fruits = map.get("fruits") != null ? Boolean.valueOf(map.get("fruits").toString()) : null;
            // Removed: variety
            Boolean droughtTolerant = map.get("drought_tolerant") != null ? Boolean.valueOf(map.get("drought_tolerant").toString()) : null;
            Boolean cuisine = map.get("cuisine") != null ? Boolean.valueOf(map.get("cuisine").toString()) : null;
            String hardinessLocationFullUrl = null;
            String hardinessLocationFullIframe = null;
            if (map.get("hardiness_location") != null) {
                try {
                    Map<String, Object> hl = (Map<String, Object>) map.get("hardiness_location");
                    hardinessLocationFullUrl = hl.get("full_url") != null ? hl.get("full_url").toString() : null;
                    hardinessLocationFullIframe = hl.get("full_iframe") != null ? hl.get("full_iframe").toString() : null;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            String plantAnatomy = map.get("plant_anatomy") != null ? objectMapper.writeValueAsString(map.get("plant_anatomy")) : null;
            Boolean leaf = map.get("leaf") != null ? Boolean.valueOf(map.get("leaf").toString()) : null;
            String pruningMonth = map.get("pruning_month") != null ? objectMapper.writeValueAsString(map.get("pruning_month")) : null;
            String hardinessMin = null;
            String hardinessMax = null;
            if (map.get("hardiness") != null) {
                try {
                    Map<String, Object> h = (Map<String, Object>) map.get("hardiness");
                    hardinessMin = h.get("min") != null ? h.get("min").toString() : null;
                    hardinessMax = h.get("max") != null ? h.get("max").toString() : null;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            Boolean tropical = map.get("tropical") != null ? Boolean.valueOf(map.get("tropical").toString()) : null;
            String sunlight = map.get("sunlight") != null ? objectMapper.writeValueAsString(map.get("sunlight")) : null;
            String watering = map.get("watering") != null ? map.get("watering").toString() : null;
            Boolean poisonousToPets = map.get("poisonous_to_pets") != null ? Boolean.valueOf(map.get("poisonous_to_pets").toString()) : null;
            String careLevel = map.get("care_level") != null ? map.get("care_level").toString() : null;
            Boolean edibleLeaf = map.get("edible_leaf") != null ? Boolean.valueOf(map.get("edible_leaf").toString()) : null;
            String family = map.get("family") != null ? map.get("family").toString() : null;
            String maintenance = map.get("maintenance") != null ? map.get("maintenance").toString() : null;
            String dimensions = map.get("dimensions") != null ? objectMapper.writeValueAsString(map.get("dimensions")) : null;

            System.out.println("Mapped plant details for ID: " + id);
            return new PlantDetails(
                    id,
                    commonName,
                    type,
                    pruningCount,
                    saltTolerant,
                    careGuides,
                    growthRate,
                    harvestSeason,
                    cones,
                    attracts,
                    pestSusceptibility,
                    flowers,
                    invasive,
                    seeds,
                    poisonousToHumans,
                    propagation,
                    genus,
                    indoor,
                    speciesEpithet,
                    defaultImageLicenseName,
                    defaultImageLicenseUrl,
                    defaultImageOriginalUrl,
                    defaultImageRegularUrl,
                    defaultImageMediumUrl,
                    defaultImageSmallUrl,
                    defaultImageThumbnail,
                    thorny,
                    floweringSeason,
                    origin,
                    edibleFruit,
                    description,
                    soil,
                    medicinal,
                    cycle,
                    fruits,
                    droughtTolerant,
                    cuisine,
                    hardinessLocationFullUrl,
                    hardinessLocationFullIframe,
                    plantAnatomy,
                    leaf,
                    pruningMonth,
                    hardinessMin,
                    hardinessMax,
                    tropical,
                    sunlight,
                    watering,
                    poisonousToPets,
                    careLevel,
                    edibleLeaf,
                    family,
                    maintenance,
                    dimensions
            );
        } catch (Exception e) {
            System.out.println("Error mapping plant details: " + e.getMessage());
            return null;
        }
    }
}
