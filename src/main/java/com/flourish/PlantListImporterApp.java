package com.flourish;

import com.flourish.service.PlantDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * A CommandLineRunner that triggers the plant list import using a maximum of 99 API requests.
 *
 * <p>This application starts up, calls the method to fetch plant data for IDs between 0 and 2999,
 * and then prints the last plant ID processed.</p>
 *
 */
@SpringBootApplication
public class PlantListImporterApp {

    public static void main(String[] args) {
        SpringApplication.run(PlantListImporterApp.class, args);
    }

    /**
     * Runs the limited plant list import on startup.
     *
     * @param plantDataService the service for retrieving and storing plant data.
     * @return a CommandLineRunner instance.
     */
    @Bean
    public CommandLineRunner importPlantList(PlantDataService plantDataService) {
        return args -> {
            int startId = 2900;
            int endId = 3001;
            plantDataService.fetchAndStorePlantListLimited(startId, endId);
        };
    }
}
