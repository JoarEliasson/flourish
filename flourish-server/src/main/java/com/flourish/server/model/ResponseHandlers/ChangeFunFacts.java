package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles to change the fun facts
 */
public class ChangeFunFacts implements IResponseHandler {
    private UserRepository userRepository;

    public ChangeFunFacts(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Boolean funFactsActivated = request.getNotifications();
        if (userRepository.changeFunFacts(user, funFactsActivated)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}