package com.flourish.api;

import com.flourish.server.db.DefaultQueryExecutor;
import com.flourish.server.db.MySQLDatabaseConnection;
import com.flourish.server.db.PlantRepository;
import com.flourish.server.db.QueryExecutor;
import com.flourish.shared.Plant;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * TreflePlantSyncRunner is responsible for synchronizing plant species data
 * from the Trefle API into the local database. It fetches data one page at a time,
 * tracks progress, and uses additional delays so that API rate limits are not exceeded.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-04
 */
public class TreflePlantSyncRunner {
    private final TrefleApiClient apiClient;
    private final PlantRepository plantRepository;

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
            List<PlantDto> allPlantDtos = apiClient.fetchAllPlants();
            totalPlantsFetched = allPlantDtos.size();

            for (PlantDto pObj : allPlantDtos) {
                if (!plantRepository.speciesExists(pObj.getId())) {
                    Plant plant = new Plant(
                            pObj.getId(),
                            pObj.getCommonName(),
                            pObj.getScientificName(),
                            pObj.getGenus(),
                            pObj.getFamily(),
                            pObj.getImageUrl(),
                            pObj.getSynonyms()
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
            TrefleApiClient apiClient = new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN);
            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);
            PlantRepository plantRepository = new PlantRepository(queryExecutor, apiClient);

            TreflePlantSyncRunner syncRunner = new TreflePlantSyncRunner(apiClient, plantRepository);
            syncRunner.runSync();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}