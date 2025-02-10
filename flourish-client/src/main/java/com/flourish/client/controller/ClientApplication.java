package com.flourish.client.controller;

import com.flourish.client.model.RootName;
import com.flourish.client.view.ConfirmationBox;
import com.flourish.client.view.JResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


/**
 * Class that starts the client application.
 * <p>
 * @author  Joar Eliasson
 * @since   2025-02-07
 */
public class ClientApplication extends Application {

    private static Scene scene;
    private static Stage window;

    public static Stage getStage() {
        return window;
    }

    /**
     * Method to set the root
     *
     * @param fxml to set
     * @throws IOException
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Method to load the right fxml-file
     *
     * @param fxml to load
     * @return
     * @throws IOException
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource(fxml + ".fxml"));
        System.out.println(fxml);
        return fxmlLoader.load();
    }

    /**
     * Alternative run method (not needed)
     *
     * @param args
     **/
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the application by opening window. Method handles close on request.
     *
     * @param stage instance of Stage to start
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        window.setResizable(false);
        window.initStyle(StageStyle.DECORATED);
        window.setOnCloseRequest(action -> {
            action.consume();
            close();
        });
        scene = new Scene(loadFXML(RootName.loginPane.toString()), 1010, 640);
        String css = JResourceManager.getCssResource("/com/flourish/client/controller/styles.css");

        scene.getStylesheets().add(css);
        window.setScene(scene);
        window.show();
    }

    /**
     * Method handles close on request.
     */
    private void close() {
        if (ConfirmationBox.display("Exit", "Are you sure?")) {
            window.close();
        }
    }
}