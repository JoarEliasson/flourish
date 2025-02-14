package com.flourish.server.model;

import com.flourish.server.db.PasswordResetTokenRepository;
import com.flourish.server.db.PlantRepository;
import com.flourish.server.db.UserPlantRepository;
import com.flourish.server.db.UserRepository;
import com.flourish.server.model.ResponseHandlers.*;
import com.flourish.shared.MessageType;

import java.util.HashMap;

/**
 * Class that stores all the different handlers for database requests
 *
 * <p>Updated with password reset functionality</p>
 *
 * @author Joar Eliasson
 * @since 2025-02-05
 */
public class ResponseContext {

    private HashMap<MessageType, IResponseHandler> responders = new HashMap<>();
    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private PlantRepository plantRepository;
    private PasswordResetTokenRepository tokenRepository;

    public ResponseContext(UserRepository userRepository, UserPlantRepository userPlantRepository, PlantRepository plantRepository, PasswordResetTokenRepository tokenRepository) {

        this.userRepository = userRepository;
        this.userPlantRepository = userPlantRepository;
        this.plantRepository = plantRepository;
        this.tokenRepository = tokenRepository;

        createResponders();
    }

    /**
     * Links the relevant ResponseHandlers to each MessageType
     */
    private void createResponders() {
        responders.put(MessageType.CHANGE_ALL_TO_WATERED, new ChangeAllToWatered(userPlantRepository));
        responders.put(MessageType.CHANGE_FUN_FACTS, new ChangeFunFacts(userRepository));
        responders.put(MessageType.CHANGE_LAST_WATERED, new ChangeLastWatered(userPlantRepository));
        responders.put(MessageType.CHANGE_NICKNAME, new ChangeNickname(userPlantRepository));
        responders.put(MessageType.CHANGE_NOTIFICATIONS, new ChangeNotifications(userRepository));
        responders.put(MessageType.CHANGE_PLANT_PICTURE, new ChangePlantPicture(userPlantRepository));
        responders.put(MessageType.DELETE_ACCOUNT, new DeleteAccount(userRepository));
        responders.put(MessageType.DELETE_PLANT, new DeletePlant(userPlantRepository));
        responders.put(MessageType.GET_LIBRARY, new GetLibrary(userPlantRepository));
        responders.put(MessageType.GET_MORE_PLANT_INFO, new GetPlantDetails(plantRepository));
        responders.put(MessageType.LOGIN, new Login(userRepository));
        responders.put(MessageType.REGISTER, new Register(userRepository));
        responders.put(MessageType.SAVE_PLANT, new SavePlant(userPlantRepository));
        responders.put(MessageType.SEARCH, new Search(plantRepository));
        responders.put(MessageType.FORGOT_PASSWORD, new ForgotPassword(userRepository, tokenRepository));
        responders.put(MessageType.RESET_PASSWORD, new ResetPassword(tokenRepository, userRepository));
    }

    public IResponseHandler getResponseHandler(MessageType messageType) {
        return responders.get(messageType);
    }
}