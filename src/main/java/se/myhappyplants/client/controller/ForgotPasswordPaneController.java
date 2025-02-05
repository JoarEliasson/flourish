package se.myhappyplants.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import se.myhappyplants.client.model.RootName;
import se.myhappyplants.client.model.Verifier;

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
        goBackIcon.setFocusTraversable(true); //sets the goback button on focus to remove from first textfield
    }

    @FXML
    private void switchToMainPane() throws IOException {
        StartClient.setRoot(String.valueOf(RootName.mainPane));
    }

    public void swapToLogin(MouseEvent mouseEvent) {
        try {
            StartClient.setRoot(RootName.loginPane.toString());
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
    }
}
