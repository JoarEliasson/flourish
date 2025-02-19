package com.flourish.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the Flourish Server application.
 * <p>
 * The <code>@SpringBootApplication</code> annotation scans both the "com.flourish.server" and
 * "com.flourish.client" packages, ensuring that Vaadin routes in the client module are discovered.
 * </p>
 *
 * @author  Joar Eliasson
 * @version 1.0
 * @since   2025-02-14
 */
@SpringBootApplication(scanBasePackages = {
        "com.flourish.server",
        "com.flourish.client"
})
@EntityScan(basePackages = "com.flourish.domain.model")
public class FlourishServerApplication {

    /**
     * Launches the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FlourishServerApplication.class, args);
        System.out.println("=============== Flourish Server started successfully! ===============");
        System.out.println("                Press Ctrl+C to stop the server.\n");
        System.out.println("                Browser will open automatically.\n");
        System.out.println("=====================================================================");
    }
}
