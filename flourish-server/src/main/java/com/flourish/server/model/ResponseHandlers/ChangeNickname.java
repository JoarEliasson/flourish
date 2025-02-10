package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.User;

/**
 * Class that handles the chang of a nickname of a plant
 */
public class ChangeNickname implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public ChangeNickname(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        String nickname = plant.getNickname();
        String newNickname = request.getNewNickname();
        if (userPlantRepository.changeNickname(user, nickname, newNickname)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
