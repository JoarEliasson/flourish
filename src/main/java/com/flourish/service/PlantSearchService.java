package com.flourish.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.domain.PlantIndex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching PlantIndex data loaded from a local JSON backup file.
 *
 * <p>This service loads the plant_index backup file (a JSON array of PlantIndex objects)
 * into memory on startup and provides a search method. The search method looks for a query
 * (case-insensitive) across key fields (commonName, scientificName, and otherName) and returns
 * suggestions sorted by a match score (lower scores indicate better matches).
 *
 * <p>Since there are exactly 3000 entries a linear scan is sufficient; if the dataset was larger or increases in size
 * during later stages of development, consider using an inverted index or Trie.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-21
 */
@Service
public class PlantSearchService {

    private List<PlantIndex> plantIndexList = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${plant.index.backup.file:plant_index_backup.txt}")
    String plantIndexBackupFile;

    /**
     * Loads the plant index backup file into memory at startup.
     */
    @PostConstruct
    public void init() {
        try {
            String json = Files.readString(Path.of(plantIndexBackupFile), StandardCharsets.UTF_8);
            plantIndexList = objectMapper.readValue(json, new TypeReference<List<PlantIndex>>() {});
            System.out.println("Loaded " + plantIndexList.size() + " plant index records from backup.");
        } catch (IOException e) {
            System.err.println("Error loading plant index backup: " + e.getMessage());
        }
    }

    /**
     * Searches for plant index records matching the given query.
     *
     * <p>The search is performed across the commonName, scientificName, and otherName fields,
     * using a case-insensitive substring match. The results are sorted by a computed match score,
     * where lower scores indicate a closer match.</p>
     *
     * @param query the search query.
     * @return a list of PlantIndex records that match the query.
     */
    public List<PlantIndex> search(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        String lowerQuery = query.toLowerCase();
        return plantIndexList.stream()
                .filter(plant -> matches(plant, lowerQuery))
                .sorted(Comparator.comparingInt(plant -> computeMatchScore(plant, lowerQuery)))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether the given plant record contains the query in any of its searchable fields.
     *
     * @param plant the PlantIndex record.
     * @param lowerQuery the lower-cased query.
     * @return true if any field contains the query; false otherwise.
     */
    private boolean matches(PlantIndex plant, String lowerQuery) {
        return (plant.getCommonName() != null && plant.getCommonName().toLowerCase().contains(lowerQuery))
                || (plant.getScientificName() != null && plant.getScientificName().toLowerCase().contains(lowerQuery))
                || (plant.getOtherName() != null && plant.getOtherName().toLowerCase().contains(lowerQuery));
    }

    /**
     * Computes a match score for the given {@link PlantIndex} record against a lower-cased query.
     * <p>
     * This version consolidates the repeated logic for each field:
     * it collects non-null fields into a list, converts them to lower-case,
     * and then finds the earliest substring position among all fields.
     *
     * <ul>
     *   <li>If the query appears as a prefix (position = 0) in any field, returns 0 immediately.</li>
     *   <li>If only mid-substring matches occur, returns the smallest positive position found.</li>
     *   <li>If no matches, returns 100.</li>
     * </ul>
     * </p>
     *
     * @param plant The {@link PlantIndex} record to score.
     * @param lowerQuery The already-lowercased query string.
     * @return The computed match score, where 0 is best (prefix match) and 100 means no matches.
     */
    private int computeMatchScore(PlantIndex plant, String lowerQuery) {
        List<String> fields = new ArrayList<>();
        if (plant.getCommonName() != null) {
            fields.add(plant.getCommonName().toLowerCase());
        }
        if (plant.getScientificName() != null) {
            fields.add(plant.getScientificName().toLowerCase());
        }
        if (plant.getOtherName() != null) {
            fields.add(plant.getOtherName().toLowerCase());
        }

        int score = Integer.MAX_VALUE;
        for (String field : fields) {
            int pos = field.indexOf(lowerQuery);
            if (pos == 0) {
                return 0;
            }
            if (pos > 0 && pos < score) {
                score = pos;
            }
        }
        return score == Integer.MAX_VALUE ? 100 : score;
    }

    /**
     * Sets the plant index list to use for testing.
     * <p>This method is intended for testing purposes only.</p>
     *
     * @param mockData the mock plant index list.
     */
    public void setPlantIndexList(List<PlantIndex> mockData) {
        plantIndexList = mockData;
    }
}
