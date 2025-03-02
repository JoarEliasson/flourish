package com.flourish.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.domain.PlantDetails;
import com.flourish.repository.PlantDetailsRepository;
import com.flourish.domain.PlantIndex;
import com.flourish.repository.PlantIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A Spring Boot service that backs up the contents of the plant_index and plant_details
 * tables to two separate .txt files in JSON format, ordered by ascending ID.
 *
 * <p>The JSON files are written using UTF-8 encoding, making it easy to reinstantiate the objects
 * later or insert the data into a database.</p>
 *
 * <strong>Behaviour & Usage</strong>
 * <ul>
 *     <li>This class can be called manually or automatically run on startup.</li>
 *     <li>To enable running on startup, add:<br>{@code implements CommandLineRunner}<br> to the class head.</li>
 *     <li>If enabled add the following import statement:<br>
 *     {@code org.springframework.boot.CommandLineRunner;}</li>
 * </ul>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-21
 */
@Configuration
public class BackupService {

    private final PlantIndexRepository plantIndexRepository;
    private final PlantDetailsRepository plantDetailsRepository;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new BackupService with the required repositories and an ObjectMapper.
     *
     * @param plantIndexRepository    the repository for PlantIndex entities.
     * @param plantDetailsRepository  the repository for PlantDetails entities.
     */
    @Autowired
    public BackupService(PlantIndexRepository plantIndexRepository,
                         PlantDetailsRepository plantDetailsRepository) {
        this.plantIndexRepository = plantIndexRepository;
        this.plantDetailsRepository = plantDetailsRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Runs the backup process on application startup, creating two files:
     * "plant_index_backup.txt" and "plant_details_backup.txt" in ascending order by ID.
     *
     * <p>Uncomment {@code @Override & method calls if run on startup is enabled/class implements CommandLineRunner}</p>
     *
     * @param args command-line arguments (not used).
     * @throws Exception if an error occurs during backup.
     */
    //@Override
    public void run(String... args) throws Exception {
        //backupPlantIndexToFile("var/app/backups/plant_index_backup.txt");
        //backupPlantDetailsToFile("var/app/backups/plant_details_backup.txt");
    }

    /**
     * Backs up the PlantIndex records to a specified file in JSON format, sorted in ascending order by ID.
     *
     * @param fileName the name of the output file (e.g., "plant_index_backup.txt").
     * @throws IOException if an error occurs while writing the file.
     */
    public void backupPlantIndexToFile(String fileName) throws IOException {
        List<PlantIndex> indexList = plantIndexRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id")
        );

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(indexList);
        Files.writeString(Path.of(fileName), json);
        System.out.println("Backup of plant_index completed. File: " + fileName);
    }

    /**
     * Backs up the PlantDetails records to a specified file in JSON format, sorted in ascending order by ID.
     *
     * @param fileName the name of the output file (e.g., "plant_details_backup.txt").
     * @throws IOException if an error occurs while writing the file.
     */
    public void backupPlantDetailsToFile(String fileName) throws IOException {
        List<PlantDetails> detailsList = plantDetailsRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id")
        );

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailsList);
        Files.writeString(Path.of(fileName), json);
        System.out.println("Backup of plant_details completed. File: " + fileName);
    }
}
