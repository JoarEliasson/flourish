package com.flourish.client.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        Image myImage = null;
        // Resolve the resource relative to this class.
        URL imageUrl = getClass().getResource("images/blomma15.1.png");
        if (imageUrl == null) {
            System.err.println("Image resource not found!");
        } else {
            try {
                Image image = new Image(imageUrl.toExternalForm());
                System.out.println("Image loaded successfully: " + imageUrl);
                ImageView imageView = new ImageView(image);
                StackPane root = new StackPane(imageView);
                Scene scene = new Scene(root, 800, 600);
                primaryStage.setTitle("Image Viewer");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}