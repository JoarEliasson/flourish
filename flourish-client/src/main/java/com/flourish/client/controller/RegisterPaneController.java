package com.flourish.client.controller;

import com.flourish.client.model.BoxTitle;
import com.flourish.client.model.LoggedInUser;
import com.flourish.client.model.RootName;
import com.flourish.client.model.Verifier;
import com.flourish.client.service.ServerConnection;
import com.flourish.client.view.MessageBox;
import com.flourish.shared.Message;
import com.flourish.shared.MessageType;
import com.flourish.shared.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * Created by Anton Holm
 */
public class RegisterPaneController {
    @FXML
    public TextField txtFldNewEmail;
    @FXML
    public TextField txtFldNewEmail1;
    @FXML
    public TextField txtFldNewUsername;
    @FXML
    public PasswordField passFldNewPassword;
    @FXML
    public PasswordField passFldNewPassword1;
    @FXML
    public Label goBackIcon;

    private Verifier verifier;

    @FXML
    public void initialize() {
        verifier = new Verifier();
        goBackIcon.setFocusTraversable(false);
    }

    @FXML
    private void registerButtonPressed() {
        int answer = MessageBox.askYesNo(BoxTitle.GDPR, "Your account details will be saved in accordance with GDPR requirements" + "\n" + "Do you still want to create the account?");
        if (answer == 1) {
            boolean verifiedRegistration = verifier.validateRegistration(this);
            Thread registerThread = new Thread(() -> {
                if (!verifiedRegistration) {
                    return;
                }
                Message registerRequest = new Message(MessageType.REGISTER, new User(txtFldNewEmail.getText(), txtFldNewUsername.getText(), passFldNewPassword.getText(), true));
                ServerConnection connection = ServerConnection.getClientConnection();
                Message registerResponse = connection.makeRequest(registerRequest);

                if (registerResponse != null) {
                    if (registerResponse.isSuccess()) {
                        LoggedInUser.getInstance().setUser(registerResponse.getUser());
                        Platform.runLater(() -> MessageBox.display(BoxTitle.Success, "Account created successfully! Now logged in as " + LoggedInUser.getInstance().getUser().getUsername()));
                        try {
                            switchToMainPane();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "An account with this email address already exists here at My Happy Plants."));
                    }
                } else {
                    Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
                }
            });
            registerThread.start();
        } else {
            return;
        }
    }

    @FXML
    private void switchToMainPane() throws IOException {
        ClientApplication.setRoot(String.valueOf(RootName.mainPane));
    }

    public void swapToLogin(MouseEvent mouseEvent) {
        try {
            ClientApplication.setRoot(RootName.loginPane.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getComponentsToVerify() {
        String[] loginInfoToCompare = new String[5];
        loginInfoToCompare[0] = txtFldNewEmail.getText();
        loginInfoToCompare[1] = txtFldNewEmail1.getText();
        loginInfoToCompare[2] = txtFldNewUsername.getText();
        loginInfoToCompare[3] = passFldNewPassword.getText();
        loginInfoToCompare[4] = passFldNewPassword1.getText();
        return loginInfoToCompare;
    }
}
