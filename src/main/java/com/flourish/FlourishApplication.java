package com.flourish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the single-module Flourish application.
 * <p>
 * This class scans the entire com.flourish package, ensuring that Vaadin routes
 * and other Spring components are discovered automatically.
 * </p>
 */
@SpringBootApplication
public class FlourishApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlourishApplication.class, args);
    }
}
