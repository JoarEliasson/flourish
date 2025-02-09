package se.myhappyplants.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import se.myhappyplants.client.model.Verifier;

public class ResetPasswordController {

    @FXML
    public Verifier verifier;
    @FXML
    //public Label goBackIcon;
    public Button btnCreateNewPass;
    @FXML
    public TextField txtFldOneTimePass;
    @FXML
    public PasswordField txtFldNewPass;
    @FXML
    public PasswordField txtFldNewPassRe;

    public void initialize() {
        verifier = new Verifier();
        //goBackIcon.setFocusTraversable(false); //sets the goback button on focus to remove from first textfield
    }
    public void registerNewPass() {

    }
}
