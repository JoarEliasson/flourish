package se.myhappyplants.client.view;

import java.net.URL;

/**
 * Manages the resources used in the application.
 * The previous implementation could not handle resources properly.
 *
 * <p>This class provides a method to get the URL of a CSS resource.</p>
 *
 * @author  Joar Eliasson
 * @since   2025-01-28
 */
public class JResourceManager {
    public static String getCssResource(String fileName) {
        URL resource = JResourceManager.class.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + fileName);
        }
        return resource.toExternalForm();
    }
}
