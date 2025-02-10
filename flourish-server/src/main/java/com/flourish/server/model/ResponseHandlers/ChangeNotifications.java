package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles the change of the notifications
 */
public class ChangeNotifications implements IResponseHandler {
    private UserRepository userRepository;

    public ChangeNotifications(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Boolean notifications = request.getNotifications();
        if (userRepository.changeNotifications(user, notifications)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
