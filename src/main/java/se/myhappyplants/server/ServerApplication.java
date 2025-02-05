package se.myhappyplants.server;

import se.myhappyplants.api.ApiConfig;
import se.myhappyplants.api.TrefleApiClient;
import se.myhappyplants.server.controller.ResponseController;
import se.myhappyplants.server.db.*;
import se.myhappyplants.server.services.Server;

import java.sql.SQLException;

/**
 * Entry point for the MyHappyPlants server application.
 *
 * <p>Temporary solution before major refactoring of server logic</p>
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public class ServerApplication {

    /**
     * Main method that starts the server.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);

            UserRepository userRepository = new UserRepository(queryExecutor);
            PlantRepository plantRepository = new PlantRepository(queryExecutor, new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN));
            UserPlantRepository userPlantRepository = new UserPlantRepository(queryExecutor, plantRepository);

            ResponseController responseController = new ResponseController(userRepository, userPlantRepository, plantRepository);

            Server server = new Server(2555, responseController);
            server.run();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to start the server due to a database error.");
        }
    }
}
