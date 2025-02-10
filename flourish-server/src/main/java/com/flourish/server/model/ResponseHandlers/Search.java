package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.PlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;

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
