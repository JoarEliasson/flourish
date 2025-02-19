package com.flourish;

import com.flourish.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the Flourish application.
 * <p>
 * The initial route of the application is the {@link SecurityConfig} class.
 *
 * @see SecurityConfig
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@SpringBootApplication
public class FlourishApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlourishApplication.class, args);
    }
}
