package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles to change all plants to watered
 */
public class ChangeAllToWatered implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public ChangeAllToWatered(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        if (userPlantRepository.changeAllToWatered(user)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
