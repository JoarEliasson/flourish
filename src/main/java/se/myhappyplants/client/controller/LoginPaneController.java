package se.myhappyplants.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.model.LoggedInUser;
import se.myhappyplants.client.model.RootName;
import se.myhappyplants.client.service.ServerConnection;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.PopupBox;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.MessageType;
import se.myhappyplants.shared.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Controls the inputs from a user that hasn't logged in
 * Created by: Eric Simonsson, Christopher O'Driscoll
 * Updated by: Linn Borgström, 2021-05-13
 */
public class LoginPaneController {


    @FXML
    private TextField txtFldEmail;
    @FXML
    private PasswordField passFldPassword;
    @FXML
    public Button btnLogin;
    @FXML
    public Button btnForgotPassword;
    @FXML
    public Hyperlink registerLink;


    /**
     * Switches to 'logged in' scene
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        String lastLoggedInUser;

        File file = new File("local_variables/lastLogin.txt");
        if (!file.exists()) {
            file.createNewFile();

        } else if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader("local_variables/lastLogin.txt"));) {
                lastLoggedInUser = br.readLine();
                txtFldEmail.setText(lastLoggedInUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method which tries to log in user. If it's successful, it changes scene
     *
     * @throws IOException
     */
    @FXML
    private void loginButtonPressed() {
        Thread loginThread = new Thread(() -> {
            Message loginMessage = new Message(MessageType.LOGIN, new User(txtFldEmail.getText(), passFldPassword.getText()));
            ServerConnection connection = ServerConnection.getClientConnection();
            Message loginResponse = connection.makeRequest(loginMessage);

            if (loginResponse != null) {
                if (loginResponse.isSuccess()) {
                    LoggedInUser.getInstance().setUser(loginResponse.getUser());
                    Platform.runLater(() -> PopupBox.display("Now logged in as\n" + LoggedInUser.getInstance().getUser().getUsername()));
                    try {
                        switchToMainPane();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "Sorry, we couldn't find an account with that email or you typed the password wrong. Try again or create a new account."));

                }
            } else {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }
        });
        loginThread.start();
    }

    /**
     * Method to switch to the mainPane FXML
     *
     * @throws IOException
     */
    @FXML
    private void switchToMainPane() throws IOException {
        ClientApplication.setRoot(String.valueOf(RootName.mainPane));
    }

    /**
     * Method to switch to the registerPane
     */
    public void swapToRegister() {
        try {
            ClientApplication.setRoot(RootName.registerPane.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void swapToForgotPassword() {
        try {
            ClientApplication.setRoot(RootName.forgotPasswordPane.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
