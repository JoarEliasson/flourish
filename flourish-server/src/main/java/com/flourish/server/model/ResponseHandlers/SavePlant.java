package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.User;

/**
 * Class that saved a users plant
 */
public class SavePlant implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public SavePlant(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        if (userPlantRepository.savePlant(user, plant)) {
            response = new Message(true);

        } else {
            response = new Message(false);
        }
        return response;
    }
}
