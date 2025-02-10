package com.flourish.server.controller;

import com.flourish.server.db.PasswordResetTokenRepository;
import com.flourish.server.db.PlantRepository;
import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.server.model.ResponseContext;
import com.flourish.shared.Message;
import com.flourish.shared.MessageType;

import java.io.IOException;

/**
 * Class that handles the logic from the Server
 * Created by: Linn Borgström
 * Updated by: Linn Borgström, 2021-05-13
 */

public class ResponseController {

    private ResponseContext responseContext;


    public ResponseController(UserRepository userRepository, UserPlantRepository userPlantRepository, PlantRepository plantRepository, PasswordResetTokenRepository tokenRepository) {
        responseContext = new ResponseContext(userRepository, userPlantRepository, plantRepository, tokenRepository);
    }

    /**
     * Gets a response depending on the type of requests received
     *
     * @param request request object received from client
     * @return response to be sent back to client
     */
    public Message getResponse(Message request) throws IOException, InterruptedException {

        Message response;
        MessageType messageType = request.getMessageType();

        IResponseHandler responseHandler = responseContext.getResponseHandler(messageType);
        response = responseHandler.getResponse(request);
        return response;
    }
}
