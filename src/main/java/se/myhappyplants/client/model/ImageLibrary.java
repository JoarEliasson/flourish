package se.myhappyplants.client.model;

import javafx.scene.image.Image;
import java.net.URL;

/**
 * Container class for the images.
 * <p>
 * This version loads images from the classpath using getResource().
 * If a resource is not found, it falls back to an online placeholder image.
 * </p>
 */
public class ImageLibrary {

    private static final Image plusSign;
    private static final Image loadingImage;
    private static final Image defaultPlantImage;

    static {
        plusSign = loadImage("/images/plusSign.png");       // file name exactly as in resources
        loadingImage = loadImage("/images/img.png");
        defaultPlantImage = loadImage("/images/Grn_vxt.png");
    }

    /**
     * Loads an image from the classpath using the given resource path.
     *
     * @param resourcePath the resource path (e.g. "/images/plusSign.png")
     * @return a JavaFX Image
     */
    private static Image loadImage(String resourcePath) {
        URL resourceUrl = ImageLibrary.class.getResource(resourcePath);
        if (resourceUrl == null) {
            System.err.println("Resource not found: " + resourcePath);
            // Fallback to an online placeholder image
            return new Image("https://via.placeholder.com/150");
        }
        try {
            return new Image(resourceUrl.toExternalForm());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading image from: " + resourcePath + ". " + e.getMessage());
            return new Image("https://via.placeholder.com/150");
        }
    }

    public static Image getPlusSign() {
        return plusSign;
    }

    public static Image getLoadingImage() {
        return loadingImage;
    }

    public static Image getDefaultPlantImage() {
        return defaultPlantImage;
    }
}