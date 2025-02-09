package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;

import java.util.ArrayList;

/**
 * Class that handles the request of a search
 */
public class Search implements IResponseHandler {
    private PlantRepository plantRepository;

    public Search(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        String searchText = request.getMessageText();
        try {
            ArrayList<Plant> plantList = plantRepository.searchPlants(searchText);
            response = new Message(plantList, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
