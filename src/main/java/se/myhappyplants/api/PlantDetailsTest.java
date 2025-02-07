package se.myhappyplants.api;

import se.myhappyplants.server.db.DefaultQueryExecutor;
import se.myhappyplants.server.db.MySQLDatabaseConnection;
import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.server.db.QueryExecutor;
import se.myhappyplants.shared.PlantDetails;

import java.sql.SQLException;

public class PlantDetailsTest {
    public static void main(String[] args) {
        try {
            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);

            TrefleApiClient apiClient = new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN);

            PlantRepository plantRepository = new PlantRepository(queryExecutor, apiClient);

            int speciesId = 12345;
            PlantDetails details = plantRepository.getPlantDetailsFromApi(speciesId);
            if (details != null) {
                System.out.println("Plant Details:");
                System.out.println("Genus: " + details.getGenus());
                System.out.println("Scientific Name: " + details.getScientificName());
                System.out.println("Light: " + details.getLight());
                System.out.println("Water Frequency: " + details.getWaterFrequency());
                System.out.println("Family: " + details.getFamily());
            } else {
                System.out.println("Failed to retrieve plant details for species id: " + speciesId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
