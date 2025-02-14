package com.flourish.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads API configuration values from the .env file in the project root.
 *
 * @author Joar Eliasson
 * @since 2025-02-04
 */
public final class ApiConfig {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    /**
     * The Trefle API token.
     */
    public static final String TREFLE_API_TOKEN = properties.getProperty("TREFLE_API_TOKEN");

    private ApiConfig() {
    }
}
