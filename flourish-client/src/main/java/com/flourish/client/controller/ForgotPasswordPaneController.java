package com.flourish.client.controller;

import com.flourish.client.model.BoxTitle;
import com.flourish.client.model.RootName;
import com.flourish.client.model.Verifier;
import com.flourish.client.service.ServerConnection;
import com.flourish.client.view.MessageBox;
import com.flourish.shared.Message;
import com.flourish.shared.MessageType;
import com.flourish.shared.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * @author Joar Eliasson, Christoffer Salomonsson
 */
public class ForgotPasswordPaneController {

    @FXML
    public TextField txtFieldUserEmail;

    @FXML
    public Label goBackIcon;

    @FXML
    public Button btnSendEmail;

    private Verifier verifier;

    @FXML
    public void initialize() {
        verifier = new Verifier();
        goBackIcon.setFocusTraversable(false);
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
        loginInfoToCompare[0] = txtFieldUserEmail.getText();

        return loginInfoToCompare;
    }

    public void sendEmail() {
        String email = txtFieldUserEmail.getText();
        Message msg = new Message(MessageType.FORGOT_PASSWORD, new User(email, null));
        Message response = ServerConnection.getClientConnection().makeRequest(msg);
        if (response.isSuccess()) {
            MessageBox.display(BoxTitle.Success, response.getMessageText());
            try {
                ClientApplication.setRoot(String.valueOf(RootName.resetPasswordPane.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.display(BoxTitle.Failed, response.getMessageText());
        }
    }
}
