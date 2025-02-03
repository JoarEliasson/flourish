package se.myhappyplants.server;

import se.myhappyplants.server.controller.ResponseController;
import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.server.db.UserPlantRepository;
import se.myhappyplants.server.db.UserRepository;
import se.myhappyplants.server.db.DatabaseConnection;
import se.myhappyplants.server.services.*;

import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * Class that starts the server
 * Created by: Frida Jacobson, Eric Simonson, Anton Holm, Linn Borgström, Christopher O'Driscoll
 * Updated by: Frida Jacobsson 2021-05-21
 */
public class StartServer {
    public static void main(String[] args) throws UnknownHostException, SQLException {
        DatabaseConnection connectionMyHappyPlants = new DatabaseConnectionOLD("MyHappyPlants");
        DatabaseConnection connectionSpecies = new DatabaseConnectionOLD("Species");
        IQueryExecutor databaseMyHappyPlants = new QueryExecutorOld(connectionMyHappyPlants);
        IQueryExecutor databaseSpecies = new QueryExecutorOld(connectionSpecies);
        UserRepository userRepository = new UserRepository(databaseMyHappyPlants);
        PlantRepository plantRepository = new PlantRepository(databaseSpecies);
        UserPlantRepository userPlantRepository = new UserPlantRepository(plantRepository, databaseMyHappyPlants);
        ResponseController responseController = new ResponseController(userRepository, userPlantRepository, plantRepository);
        new Server(2555, responseController);
    }
}
