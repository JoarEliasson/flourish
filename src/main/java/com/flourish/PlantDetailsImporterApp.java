package com.flourish;

import com.flourish.service.PlantDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * CommandLineRunner to import plant details data for plant IDs between a given range.
 *
 * <p>The importer sends individual API requests for plant details, stopping after 99 requests,
 * then prints the last plant ID processed.</p>
 */
@SpringBootApplication
public class PlantDetailsImporterApp {

    public static void main(String[] args) {
        SpringApplication.run(PlantDetailsImporterApp.class, args);
    }

    /**
     * Runs the plant details import on startup.
     *
     * @param plantDataService the service for retrieving and storing plant details.
     * @return a CommandLineRunner instance.
     */
    @Bean
    public CommandLineRunner importPlantDetails(PlantDataService plantDataService) {
        return args -> {
            int startId = 1782;
            int endId = 1857;
            plantDataService.fetchAndStorePlantDetailsLimited(startId, endId);
        };
    }
}
