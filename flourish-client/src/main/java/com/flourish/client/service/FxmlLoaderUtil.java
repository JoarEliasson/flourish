package com.flourish.client.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for loading FXML files.
 *
 * <p>This class provides a static method for loading FXML files from the classpath.</p>
 *
 * @author Joar Eliasson
 * @since 2025-01-28
 */
public class FxmlLoaderUtil {

    /**
     * Loads the FXML file from the given path.
     *
     * @param fxmlPath The path to the FXML file (starting with a slash for an absolute resource).
     * @return The loaded Parent node.
     */
    public static Parent load(String fxmlPath) {
        URL resource = FxmlLoaderUtil.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found at " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        try {
            return loader.load();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load FXML file: " + fxmlPath, ex);
        }
    }
}
