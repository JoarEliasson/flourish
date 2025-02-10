package com.flourish.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads database configuration details from a .env file using Java Properties.
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public final class DBConfig {


    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
            System.out.println("Loaded database configuration from .env file");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static final String HOST = properties.getProperty("DB_HOST");
    public static final String PORT = properties.getProperty("DB_PORT");
    public static final String DATABASE = properties.getProperty("DB_DATABASE");
    public static final String USER = properties.getProperty("DB_USER");
    public static final String PASSWORD = properties.getProperty("DB_PASSWORD");

    private DBConfig() {
    }

}
