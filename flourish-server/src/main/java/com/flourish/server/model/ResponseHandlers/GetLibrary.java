package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.Plant;
import com.flourish.shared.User;

import java.util.ArrayList;

/**
 * Class that gets the users library
 */
public class GetLibrary implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public GetLibrary(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        try {
            ArrayList<Plant> userLibrary = userPlantRepository.getUserLibrary(user);
            response = new Message(userLibrary, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
