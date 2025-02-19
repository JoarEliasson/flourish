package com.flourish.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.domain.PlantIndex;
import com.flourish.domain.PlantIndexRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for retrieving plant data from the Perenual API species-list endpoint,
 * and storing it in the local database.
 *
 * <p>This method retrieves pages of plant data starting from page 1, and continues to request pages
 * until a total of 99 API requests have been made. It filters records to those whose plant IDs fall
 * within a specified range, maps the DTOs to PlantIndex entities, and saves them in the database.
 * Finally, it prints the last plant ID processed and exits.</p>
 *
 * <p>Null values are handled gracefully during mapping.</p>
 *
 * @see PlantIndex
 * @see PlantIndexRepository
 * @see PlantListResponseDto
 * @see PlantListDto
 * @author
 *   Your Name
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class PlantDataService {

    private final WebClient webClient;
    private final PlantIndexRepository plantIndexRepository;
    private final ObjectMapper objectMapper;

    @Value("${perenual.api.key}")
    private String perenualApiKey;

    @Value("${perenual.api.speciesListUrl:https://perenual.com/api/v2/species-list}")
    private String speciesListUrl;

    /**
     * Constructs a new PlantDataService.
     *
     * @param webClient the WebClient used to make HTTP calls.
     * @param plantIndexRepository the repository for PlantIndex entities.
     */
    public PlantDataService(WebClient webClient, PlantIndexRepository plantIndexRepository) {
        this.webClient = webClient;
        this.plantIndexRepository = plantIndexRepository;
        this.objectMapper = new ObjectMapper();
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
        int currentPage = 1;
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

            // Filter and map plants in the given ID range.
            List<PlantIndex> plantsToSave = response.getData().stream()
                    .filter(dto -> dto.getId() != null && dto.getId() >= startId && dto.getId() <= endId)
                    .map(dto -> {
                        try {
                            // Build a JSON string for image URLs.
                            var imageMap = new java.util.HashMap<String, String>();
                            imageMap.put("regular", dto.getRegularUrl());
                            imageMap.put("medium", dto.getMediumUrl());
                            imageMap.put("small", dto.getSmallUrl());
                            imageMap.put("thumbnail", dto.getThumbnail());
                            String imageJson = objectMapper.writeValueAsString(imageMap);

                            // For fields that are lists, join them into a comma-separated string.
                            String scientificName = (dto.getScientificName() != null)
                                    ? String.join(", ", dto.getScientificName())
                                    : "";
                            String otherName = (dto.getOtherName() != null)
                                    ? String.join(", ", dto.getOtherName())
                                    : "";

                            return new PlantIndex(
                                    dto.getId(),
                                    dto.getCommonName() != null ? dto.getCommonName() : "",
                                    scientificName,
                                    otherName,
                                    imageJson
                            );
                        } catch (JsonProcessingException e) {
                            System.err.println("Error converting image URLs to JSON for plant ID " + dto.getId());
                            return null;
                        }
                    })
                    .filter(pi -> pi != null)
                    .collect(Collectors.toList());

            if (!plantsToSave.isEmpty()) {
                plantIndexRepository.saveAll(plantsToSave);
                // Update lastIdProcessed with the maximum ID in this page.
                long maxId = plantsToSave.stream().mapToLong(PlantIndex::getId).max().orElse(lastIdProcessed);
                lastIdProcessed = maxId;
                System.out.println("Saved " + plantsToSave.size() + " plants from page " + currentPage);
            } else {
                System.out.println("No plants in the specified ID range found on page " + currentPage);
            }

            // Check if we have reached the last page.
            if (response.getCurrentPage() >= response.getLastPage()) {
                morePages = false;
            } else {
                currentPage++;
            }
        }
        System.out.println("After " + apiRequestCount + " API requests, last plant ID processed: " + lastIdProcessed);
    }
}
