package se.myhappyplants.server.db;

import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.PlantDetails;
import se.myhappyplants.shared.WaterCalculator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Repository class responsible for plant-related database operations.
 * <p>
 * Provides methods for searching plants, retrieving detailed information,
 * and calculating watering frequency.
 * </p>
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public class PlantRepository {

    private final QueryExecutor queryExecutor;

    /**
     * Constructs a PlantRepository with the specified QueryExecutor.
     *
     * @param queryExecutor the QueryExecutor to use for database operations.
     */
    public PlantRepository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * Searches for plants in the Species table by matching the scientific or common name.
     *
     * @param plantSearch the search string to match against plant names.
     * @return a list of matching Plant objects; an empty list if no matches are found.
     */
    public ArrayList<Plant> searchPlants(String plantSearch) {
        ArrayList<Plant> plantList = new ArrayList<>();
        String escapedSearch = escapeString(plantSearch);
        String query = "SELECT id, common_name, scientific_name, family, image_url FROM Species " +
                "WHERE scientific_name LIKE '%" + escapedSearch + "%' " +
                "OR common_name LIKE '%" + escapedSearch + "%';";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            while (resultSet.next()) {
                String plantId = Integer.toString(resultSet.getInt("id"));
                String commonName = resultSet.getString("common_name");
                String scientificName = resultSet.getString("scientific_name");
                String familyName = resultSet.getString("family");
                String imageUrl = resultSet.getString("image_url");
                plantList.add(new Plant(plantId, commonName, scientificName, familyName, imageUrl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plantList;
    }

    /**
     * Retrieves detailed information for the specified plant from the Species table.
     *
     * @param plant the Plant object for which to retrieve details.
     * @return a PlantDetails object containing detailed information, or null if not found.
     */
    public PlantDetails getPlantDetails(Plant plant) {
        PlantDetails plantDetails = null;
        String query = "SELECT genus, scientific_name, light, water_frequency, family FROM Species " +
                "WHERE id = " + plant.getPlantId() + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                String genus = resultSet.getString("genus");
                String scientificName = resultSet.getString("scientific_name");
                String lightText = resultSet.getString("light");
                String waterText = resultSet.getString("water_frequency");
                String family = resultSet.getString("family");

                int light = isNumeric(lightText) ? Integer.parseInt(lightText) : -1;
                int water = isNumeric(waterText) ? Integer.parseInt(waterText) : -1;

                plantDetails = new PlantDetails(genus, scientificName, light, water, family);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plantDetails;
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
