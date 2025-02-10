package com.flourish.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads database configuration details from a .env file using Java Properties.
 *
 * @author Joar Eliasson, Christoffer Salomonsson
 * @since 2025-02-03
 */
public class MailConfig {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
            System.out.println("Loaded mail configuration from .env file");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static final String MAIL_HOST = properties.getProperty("MAIL_HOST");
    public static final String MAIL_PORT = properties.getProperty("MAIL_PORT");
    public static final String MAIL_USER = properties.getProperty("MAIL_USER");
    public static final String MAIL_PASSWORD = properties.getProperty("MAIL_PASSWORD");
    public static final String MAIL_SMTP_AUTH = properties.getProperty("MAIL_AUTH");
    public static final String MAIL_SMTP_SSL_ENABLE = properties.getProperty("MAIL_SMTP_SSL_ENABLE");
    public static final String MAIL_AUTH_PASSWORD = properties.getProperty("MAIL_AUTH_PASSWORD");

    private MailConfig() {
    }
}
