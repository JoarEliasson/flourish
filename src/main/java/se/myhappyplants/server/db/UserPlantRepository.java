package se.myhappyplants.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

/**
 * Repository class responsible for handling user plant library operations.
 * <p>
 * Provides methods to save, retrieve, update, and delete plants from a user's library.
 * </p>
 *
 * @author  Joar Eliasson
 * @since   2025-02-03
 */
public class UserPlantRepository {

    private final QueryExecutor queryExecutor;
    private final PlantRepository plantRepository;

    /**
     * Constructs a UserPlantRepository with the specified QueryExecutor and PlantRepository.
     *
     * @param queryExecutor   the QueryExecutor to use for database operations.
     * @param plantRepository the PlantRepository for water frequency calculations.
     */
    public UserPlantRepository(QueryExecutor queryExecutor, PlantRepository plantRepository) {
        this.queryExecutor = queryExecutor;
        this.plantRepository = plantRepository;
    }

    /**
     * Saves a new plant for the specified user.
     *
     * @param user  the user who owns the plant.
     * @param plant the Plant object to be saved.
     * @return true if the plant was saved successfully; false otherwise.
     */
    public boolean savePlant(User user, Plant plant) {
        String safeNickname = escapeString(plant.getNickname());
        // Note: The column "plant_id" has been renamed to "species_id" in our new design.
        String query = "INSERT INTO Plants (user_id, nickname, species_id, last_watered, image_url) " +
            "VALUES (" + user.getUniqueId() + ", '" + safeNickname + "', " + plant.getPlantId() +
            ", '" + plant.getLastWatered() + "', '" + plant.getImageURL() + "');";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the library of plants for the specified user.
     *
     * @param user the user whose plant library is to be retrieved.
     * @return a list of Plant objects representing the user's library.
     */
    public List<Plant> getUserLibrary(User user) {
        List<Plant> plantList = new ArrayList<>();
        String query = "SELECT nickname, species_id, last_watered, image_url FROM Plants " +
            "WHERE user_id = " + user.getUniqueId() + ";";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String plantId = Integer.toString(resultSet.getInt("species_id"));
                LocalDate lastWatered = resultSet.getDate("last_watered").toLocalDate();
                String imageURL = resultSet.getString("image_url");
                long waterFrequency = plantRepository.getWaterFrequency(plantId);
                Plant plant = new Plant(nickname, plantId, lastWatered, waterFrequency, imageURL);
                plantList.add(plant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plantList;
    }

    /**
     * Retrieves a specific plant by nickname for the given user.
     *
     * @param user     the user who owns the plant.
     * @param nickname the nickname of the plant.
     * @return the Plant object if found; null otherwise.
     */
    public Plant getPlant(User user, String nickname) {
        String safeNickname = escapeString(nickname);
        String query = "SELECT nickname, species_id, last_watered, image_url FROM Plants " +
            "WHERE user_id = " + user.getUniqueId() + " AND nickname = '" + safeNickname + "';";
        try (ResultSet resultSet = queryExecutor.executeQuery(query)) {
            if (resultSet.next()) {
                String plantId = Integer.toString(resultSet.getInt("species_id"));
                LocalDate lastWatered = resultSet.getDate("last_watered").toLocalDate();
                String imageURL = resultSet.getString("image_url");
                long waterFrequency = plantRepository.getWaterFrequency(plantId);
                return new Plant(safeNickname, plantId, lastWatered, waterFrequency, imageURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a plant with the specified nickname for the given user.
     *
     * @param user     the user who owns the plant.
     * @param nickname the nickname of the plant to delete.
     * @return true if the plant was deleted successfully; false otherwise.
     */
    public boolean deletePlant(User user, String nickname) {
        String safeNickname = escapeString(nickname);
        String query = "DELETE FROM Plants WHERE user_id = " + user.getUniqueId() +
            " AND nickname = '" + safeNickname + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the last watered date for a specific plant.
     *
     * @param user     the user who owns the plant.
     * @param nickname the nickname of the plant.
     * @param date     the new last watered date.
     * @return true if the update was successful; false otherwise.
     */
    public boolean changeLastWatered(User user, String nickname, LocalDate date) {
        String safeNickname = escapeString(nickname);
        String query = "UPDATE Plants SET last_watered = '" + date.toString() + "' " +
            "WHERE user_id = " + user.getUniqueId() + " AND nickname = '" + safeNickname + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Changes the nickname of a plant.
     *
     * @param user        the user who owns the plant.
     * @param nickname    the current nickname of the plant.
     * @param newNickname the new nickname for the plant.
     * @return true if the nickname was changed successfully; false otherwise.
     */
    public boolean changeNickname(User user, String nickname, String newNickname) {
        String safeNickname = escapeString(nickname);
        String safeNewNickname = escapeString(newNickname);
        String query = "UPDATE Plants SET nickname = '" + safeNewNickname + "' " +
            "WHERE user_id = " + user.getUniqueId() + " AND nickname = '" + safeNickname + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets the last watered date to the current date for all plants of the specified user.
     *
     * @param user the user whose plants should be updated.
     * @return true if the update was successful; false otherwise.
     */
    public boolean changeAllToWatered(User user) {
        LocalDate today = LocalDate.now();
        String query = "UPDATE Plants SET last_watered = '" + today.toString() + "' " +
            "WHERE user_id = " + user.getUniqueId() + ";";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the picture URL for a specific plant.
     *
     * @param user  the user who owns the plant.
     * @param plant the Plant object containing the updated image URL.
     * @return true if the picture was updated successfully; false otherwise.
     */
    public boolean changePlantPicture(User user, Plant plant) {
        String safeNickname = escapeString(plant.getNickname());
        String query = "UPDATE Plants SET image_url = '" + plant.getImageURL() + "' " +
            "WHERE user_id = " + user.getUniqueId() + " AND nickname = '" + safeNickname + "';";
        try {
            queryExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
