package se.myhappyplants.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.model.RootName;
import se.myhappyplants.client.model.Verifier;
import se.myhappyplants.client.service.ServerConnection;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.MessageType;
import se.myhappyplants.shared.User;

import java.io.IOException;

/**
 *
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
        } else {
            MessageBox.display(BoxTitle.Failed, response.getMessageText());
        }
    }
}
