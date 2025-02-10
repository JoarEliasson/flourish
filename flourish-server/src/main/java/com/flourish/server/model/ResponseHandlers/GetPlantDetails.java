package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.PlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.PlantDetails;

/**
 * Class that gets the plant details
 */
public class GetPlantDetails implements IResponseHandler {
    private PlantRepository plantRepository;

    public GetPlantDetails(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        Plant plant = request.getPlant();
        try {
            PlantDetails plantDetails = plantRepository.getPlantDetails(plant);
            response = new Message(plantDetails, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
