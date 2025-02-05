package se.myhappyplants.api;

import se.myhappyplants.server.db.DefaultQueryExecutor;
import se.myhappyplants.server.db.MySQLDatabaseConnection;
import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.server.db.QueryExecutor;
import se.myhappyplants.shared.Plant;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * TreflePlantSyncRunner is responsible for synchronizing plant species data
 * from the Trefle API into the local database. It fetches data one page at a time,
 * tracks progress, and uses additional delays so that API rate limits are not exceeded.
 */
public class TreflePlantSyncRunner {
    private final TrefleApiClient apiClient;
    private final PlantRepository plantRepository;

    // Progress tracking variables.
    private int totalPlantsFetched = 0;
    private int totalPlantsInserted = 0;

    public TreflePlantSyncRunner(TrefleApiClient apiClient, PlantRepository plantRepository) {
        this.apiClient = apiClient;
        this.plantRepository = plantRepository;
    }

    /**
     * Runs the synchronization: fetches all plant species from the API and for each species,
     * checks whether it is already stored in the database; if not, inserts it.
     */
    public void runSync() {
        try {
            List<PlantObj> allPlantObjs = apiClient.fetchAllPlants();
            totalPlantsFetched = allPlantObjs.size();

            for (PlantObj pObj : allPlantObjs) {
                if (!plantRepository.speciesExists(pObj.getId())) {
                    // Convert API response to the domain model.
                    Plant plant = new Plant(
                            pObj.getId(),
                            pObj.getCommonName(),
                            pObj.getScientificName(),
                            pObj.getGenus(),
                            pObj.getFamily(),
                            pObj.getImageUrl(),
                            pObj.getSynonyms() // List<String> synonyms
                    );
                    plantRepository.insertSpecies(plant);
                    totalPlantsInserted++;
                    System.out.println("Inserted: " + plant.getScientificName());
                } else {
                    System.out.println("Already exists: " + pObj.getScientificName());
                }
            }
            System.out.println("Sync complete. Total fetched: " + totalPlantsFetched +
                    ", Total inserted: " + totalPlantsInserted);
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method for testing the synchronization process.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Create the API client using the token from configuration.
            TrefleApiClient apiClient = new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN);
            // Initialize database connection and repository.
            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);
            PlantRepository plantRepository = new PlantRepository(queryExecutor);

            // Create and run the sync runner.
            TreflePlantSyncRunner syncRunner = new TreflePlantSyncRunner(apiClient, plantRepository);
            syncRunner.runSync();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}