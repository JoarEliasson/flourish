package se.myhappyplants.api;

import se.myhappyplants.api.PlantObj;
import se.myhappyplants.api.TrefleApiClient;
import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.shared.Plant;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that integrates Trefle API data with the local database.
 * <p>
 * This service fetches plant data from the API and ensures that species
 * are stored in the database.
 * </p>
 */
public class PlantDataService {

    private final TrefleApiClient trefleApiClient;
    private final PlantRepository plantRepository;

    public PlantDataService(TrefleApiClient trefleApiClient, PlantRepository plantRepository) {
        this.trefleApiClient = trefleApiClient;
        this.plantRepository = plantRepository;
    }

    /**
     * Fetches one page of plant data from Trefle, checks each species in the DB,
     * and if not found, inserts it.
     *
     * @return a list of Plant domain objects representing species data.
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     * @throws SQLException if a database access error occurs.
     */
    public List<Plant> fetchAndStoreOnePage() throws IOException, InterruptedException, SQLException {
        List<PlantObj> plantObjs = trefleApiClient.fetchOnePage();
        List<Plant> plants = new ArrayList<>();

        for (PlantObj pObj : plantObjs) {
            // Check if the species already exists in the database
            if (!plantRepository.speciesExists(pObj.getId())) {
                // Convert the API response (PlantObj) to our domain Plant object.
                Plant plant = convertToPlant(pObj);
                // Insert the new species data into the database.
                plantRepository.insertSpecies(plant);
                plants.add(plant);
            } else {
                // Load existing species data from the database.
                Plant plant = plantRepository.getSpeciesById(pObj.getId());
                plants.add(plant);
            }
        }
        return plants;
    }

    /**
     * Converts a PlantObj (from the API) to the domain Plant object.
     *
     * @param pObj the PlantObj from the API.
     * @return a Plant object.
     */
    private Plant convertToPlant(PlantObj pObj) {
        // Build a Plant from API data. Adjust field mapping as needed.
        return new Plant(
                pObj.getId(),
                pObj.getCommonName(),
                pObj.getScientificName(),
                pObj.getGenus(),
                pObj.getFamily(),
                pObj.getImageUrl(),
                pObj.getSynonyms()
        );
    }
}
