package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.User;

/**
 * Class that handles the change when the user wants to delete a plant
 */
public class DeletePlant implements IResponseHandler {

    private UserPlantRepository userPlantRepository;

    public DeletePlant(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }


    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        String nickname = plant.getNickname();
        if (userPlantRepository.deletePlant(user, nickname)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
