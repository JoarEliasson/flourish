package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles the procedure of a registration
 */
public class Register implements IResponseHandler {
    private UserRepository userRepository;

    public Register(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        if (userRepository.saveUser(user)) {
            User savedUser = userRepository.getUserDetails(user.getEmail());
            response = new Message(savedUser, true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
