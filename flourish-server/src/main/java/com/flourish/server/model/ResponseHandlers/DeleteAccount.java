package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.shared.Message;
import com.flourish.shared.User;

/**
 * Class that handles the changes when the user want to delete an account
 */
public class DeleteAccount implements IResponseHandler {
    private UserRepository userRepository;

    public DeleteAccount(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User userToDelete = request.getUser();
        if (userRepository.deleteAccount(userToDelete.getEmail(), userToDelete.getPassword())) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
