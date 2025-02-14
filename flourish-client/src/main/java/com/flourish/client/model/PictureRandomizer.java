package com.flourish.client.model;

import javafx.scene.image.Image;

import java.util.Random;

/**
 * Class that randomizes pictures.
 */
public class PictureRandomizer {

    private static final String[] IMAGE_PATHS = {
            "/com/flourish/client/controller/images/blomma2.jpg",
            "/com/flourish/client/controller/images/blomma5.jpg",
            "/com/flourish/client/controller/images/blomma6.jpg",
            "/com/flourish/client/controller/images/blomma9.jpg",
            "/com/flourish/client/controller/images/blomma10.jpg",
            "/com/flourish/client/controller/images/blomma17.jpg",
            "/com/flourish/client/controller/images/blomma18.jpg",
            "/com/flourish/client/controller/images/blomma19.jpg",
            "/com/flourish/client/controller/images/blomma21.jpg"
    };

    private static final Image[] FLOWER_IMAGES = new Image[IMAGE_PATHS.length];

    static {
        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            var resourceUrl = PictureRandomizer.class.getResource(IMAGE_PATHS[i]);
            if (resourceUrl == null) {
                System.err.println("Resource not found: " + IMAGE_PATHS[i]);
                throw new RuntimeException("Missing image resource: " + IMAGE_PATHS[i]);
            }
            FLOWER_IMAGES[i] = new Image(resourceUrl.toExternalForm());
        }
    }

    /**
     * Returns a random Image.
     *
     * @return a randomly selected Image.
     */
    public static Image getRandomPicture() {
        Random random = new Random();
        int index = random.nextInt(FLOWER_IMAGES.length);
        return FLOWER_IMAGES[index];
    }

    /**
     * Returns the URL string for a random picture.
     *
     * @return a URL string to a randomly selected picture resource.
     */
    public static String getRandomPictureURL() {
        return getRandomPicture().getUrl();
    }

}
