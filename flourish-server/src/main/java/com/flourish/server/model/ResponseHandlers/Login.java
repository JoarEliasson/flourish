package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles the procedure when a user logs in
 */
public class Login implements IResponseHandler {
    private UserRepository userRepository;

    public Login(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        String email = request.getUser().getEmail();
        String password = request.getUser().getPassword();
        if (userRepository.checkLogin(email, password)) {
            User user = userRepository.getUserDetails(email);
            response = new Message(user, true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
