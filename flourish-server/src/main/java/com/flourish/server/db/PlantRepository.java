package com.flourish.server.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.api.TrefleApiClient;
import com.flourish.shared.Plant;
import com.flourish.shared.PlantDetails;
import com.flourish.shared.WaterCalculator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for plant-related database operations.
 * <p>
 * Provides methods for searching species, retrieving detailed information,
 * checking existence, inserting new species, and loading a species by its ID.
 * </p>
 *
 * @author Joar Eliasson
 * @since 2025-02-04
 */
public class PlantRepository {

    private final QueryExecutor queryExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TrefleApiClient apiClient;

    /**
     * Constructs a PlantRepository with the specified QueryExecutor.
     *
     * @param queryExecutor the QueryExecutor to use for database operations.
     * @param apiClient     the TrefleApiClient to use for API operations.
     */
    public PlantRepository(QueryExecutor queryExecutor, TrefleApiClient apiClient) {
        this.queryExecutor = queryExecutor;
        this.apiClient = apiClient;
    }

    /**
     * Searches for species in the Species table by matching the scientific or common name.
     *
     * @param plantSearch the search string to match against plant names.
     * @return a list of matching Plant objects; an empty list if no matches are found.
     */
    public ArrayList<Plant> searchPlants(String plantSearch) {
        ArrayList<Plant> plantList = new ArrayList<>();
        String escapedSearch = escapeString(plantSearch);
        String query = "SELECT id, common_name, scientific_name, genus, family, image_url, synonyms " +
                "FROM Species " +
                "WHERE scientific_name LIKE '%" + escapedSearch + "%' " +
                "OR common_name LIKE '%" + escapedSearch + "%';";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            while (resultSet.next()) {
                int speciesId = resultSet.getInt("id");
                String commonName = resultSet.getString("common_name");
                String scientificName = resultSet.getString("scientific_name");
                String genus = resultSet.getString("genus");
                String family = resultSet.getString("family");
                String imageUrl = resultSet.getString("image_url");
                String synonymsJson = resultSet.getString("synonyms");
                List<String> synonyms = null;
                if (synonymsJson != null) {
                    synonyms = objectMapper.readValue(synonymsJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                }
                Plant plant = new Plant(speciesId, commonName, scientificName, genus, family, imageUrl, synonyms);
                plantList.add(plant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        plantList.removeIf(plant -> plant.getCommonName() == null || plant.getCommonName().isEmpty() ||
                plant.getCommonName().equals("null"));
        return plantList;
    }

    /**
     * Retrieves detailed information for the specified plant from the Species table.
     *
     * @param plant the Plant object for which to retrieve details.
     * @return a PlantDetails object containing detailed information, or null if not found.
     */
    public PlantDetails getPlantDetails(Plant plant) {
        PlantDetails plantDetails = getPlantDetailsFromApi(plant.getSpeciesId());
        String query = "SELECT genus, scientific_name, family " +
                "FROM Species WHERE id = " + plant.getSpeciesId() + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                String genus = resultSet.getString("genus");
                String scientificName = resultSet.getString("scientific_name");
                String family = resultSet.getString("family");

                int light = -1;
                int water = -1;

                plantDetails = new PlantDetails(genus, scientificName, light, water, family);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plantDetails;
    }

    /**
     * Retrieves detailed plant information for the species with the given id by calling the Trefle API.
     *
     * @param speciesId the species id.
     * @return a PlantDetails object with detailed information, or null if an error occurs.
     */
    public PlantDetails getPlantDetailsFromApi(int speciesId) {
        try {
            return apiClient.fetchPlantDetails(speciesId);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates the watering frequency for a plant by retrieving water_frequency from the Species table.
     *
     * @param plantId the plant’s id (as a String) for which to calculate watering frequency.
     * @return the calculated water frequency, or -1 if not available.
     */
    public long getWaterFrequency(String plantId) {
        long waterFrequency = -1;
        String query = "SELECT water_frequency FROM Species WHERE id = " + plantId + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                int water = resultSet.getInt("water_frequency");
                waterFrequency = WaterCalculator.calculateWaterFrequencyForWatering(water);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waterFrequency;
    }

    /**
     * Inserts a new species record into the Species table.
     *
     * @param plant the Plant object containing species data.
     * @throws SQLException if the insertion fails.
     */
    public void insertSpecies(Plant plant) throws SQLException {
        String synonymsJson = null;
        try {
            if (plant.getSynonyms() != null) {
                synonymsJson = objectMapper.writeValueAsString(plant.getSynonyms());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String query = "INSERT INTO Species (id, scientific_name, genus, family, common_name, image_url, synonyms) " +
                "VALUES (" + plant.getSpeciesId() + ", '" + escapeString(plant.getScientificName()) + "', '" +
                escapeString(plant.getGenus()) + "', '" + escapeString(plant.getFamily()) + "', '" +
                escapeString(plant.getCommonName()) + "', '" + escapeString(plant.getImageURL()) + "', " +
                (synonymsJson != null ? "'" + escapeString(synonymsJson) + "'" : "NULL") + ");";
        queryExecutor.executeUpdate(query);
    }

    /**
     * Checks whether a species with the given id exists in the Species table.
     *
     * @param id the species id.
     * @return true if the species exists, false otherwise.
     */
    public boolean speciesExists(int id) {
        String query = "SELECT COUNT(*) FROM Species WHERE id = " + id + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a species record from the Species table by its id.
     *
     * @param speciesId the species id.
     * @return a Plant object populated with species data, or null if not found.
     */
    public Plant getSpeciesById(int speciesId) {
        String query = "SELECT id, common_name, scientific_name, genus, family, image_url, synonyms " +
                "FROM Species WHERE id = " + speciesId + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                String commonName = resultSet.getString("common_name");
                String scientificName = resultSet.getString("scientific_name");
                String genus = resultSet.getString("genus");
                String family = resultSet.getString("family");
                String imageUrl = resultSet.getString("image_url");
                if (imageUrl != null && imageUrl.equals("http://source.unsplash.com/featured/?plant")) {
                    imageUrl = "resources/Blommor/placeholder.png";
                }
                String synonymsJson = resultSet.getString("synonyms");
                List<String> synonyms = null;
                if (synonymsJson != null) {
                    synonyms = objectMapper.readValue(synonymsJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                }
                return new Plant(speciesId, commonName, scientificName, genus, family, imageUrl, synonyms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Escapes single quotes in a string to help prevent SQL injection.
     *
     * @param input the input string.
     * @return the escaped string.
     */
    private String escapeString(String input) {
        return input == null ? null : input.replace("'", "''");
    }

    /**
     * Checks whether the given string represents a numeric value.
     *
     * @param str the string to check.
     * @return true if the string is numeric; false otherwise.
     */
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}