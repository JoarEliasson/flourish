package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;

import java.time.LocalDate;

/**
 * Class that handles the change of the last watered date
 */

public class ChangeLastWatered implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public ChangeLastWatered(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        Plant plant = request.getPlant();
        String nickname = plant.getNickname();
        LocalDate lastWatered = request.getDate();
        if (userPlantRepository.changeLastWatered(request.getUser(), nickname, lastWatered)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
