package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.User;

public class ChangePlantPicture implements IResponseHandler {

    private UserPlantRepository userPlantRepository;

    public ChangePlantPicture(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        if (userPlantRepository.changePlantPicture(user, plant)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
