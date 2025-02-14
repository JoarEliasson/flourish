package com.flourish.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * The main entry point for the Flourish Server application.
 * <p>
 * This class bootstraps the Spring Boot application and instructs Spring
 * to scan the packages for JPA entities and UI components.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@SpringBootApplication
@EntityScan(basePackages = "com.flourish.domain.model")
@ComponentScan(basePackages = {"com.flourish.server", "com.flourish.client", "com.flourish.domain"})
public class FlourishServerApplication {

    /**
     * Main method to launch the Spring Boot application.
     *
     * @param args command-line arguments passed during startup
     */
    public static void main(String[] args) {
        SpringApplication.run(FlourishServerApplication.class, args);
        System.out.println("=============== Flourish Server started successfully! ===============");
        System.out.println("                Press Ctrl+C to stop the server.\n");
        System.out.println("To access the application, open a web browser and navigate to:");
        System.out.println("""
                http://localhost:8080/""");
    }
}
