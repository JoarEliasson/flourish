package com.flourish.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.domain.PlantDetails;
import com.flourish.domain.PlantIndex;
import com.flourish.repository.PlantDetailsRepository;
import com.flourish.repository.PlantIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link com.flourish.service.BackupService} that address
 * ambiguous method overloading in repository findAll() and the large constructor
 * in {@link com.flourish.domain.PlantDetails}.
 *
 * <p>Ensures JSON files are generated with correct data in ascending order of ID.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class BackupServiceTest {

    private PlantIndexRepository plantIndexRepository;
    private PlantDetailsRepository plantDetailsRepository;
    private BackupService backupService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        plantIndexRepository = mock(PlantIndexRepository.class);
        plantDetailsRepository = mock(PlantDetailsRepository.class);
        backupService = new BackupService(plantIndexRepository, plantDetailsRepository);
    }

    /**
     * Tests backing up PlantIndex records, ensuring file creation and JSON correctness.
     */
    @Test
    void testBackupPlantIndexToFile(@TempDir Path tempDir) throws IOException {
        List<PlantIndex> indexList = List.of(
                new PlantIndex(1L, "Basil", "Ocimum basilicum", "Thai Basil"),
                new PlantIndex(2L, "Rose", "Rosa rubiginosa", "Sweet Briar")
        );
        when(plantIndexRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(indexList);

        Path outputFile = tempDir.resolve("plant_index_backup.txt");
        backupService.backupPlantIndexToFile(outputFile.toString());

        String fileContent = Files.readString(outputFile);
        assertNotNull(fileContent);
        assertFalse(fileContent.isEmpty());

        List<PlantIndex> parsedList = objectMapper.readValue(fileContent, new TypeReference<>() {});
        assertEquals(2, parsedList.size());
        assertEquals(1L, parsedList.get(0).getId());
        assertEquals(2L, parsedList.get(1).getId());
    }

    /**
     * Tests backing up an empty PlantIndex list results in an empty array "[]".
     */
    @Test
    void testBackupPlantIndex_EmptyList(@TempDir Path tempDir) throws IOException {
        when(plantIndexRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        Path outputFile = tempDir.resolve("plant_index_backup_empty.txt");
        backupService.backupPlantIndexToFile(outputFile.toString());

        String fileContent = Files.readString(outputFile);
        assertEquals("[ ]", fileContent.trim());
    }

    /**
     * Tests backing up PlantDetails records with correct JSON and all constructor parameters.
     */
    @Test
    void testBackupPlantDetailsToFile(@TempDir Path tempDir) throws IOException {
        PlantDetails plantA = new PlantDetails(
                10L, "Aloe Vera", "Succulent",
                null, false,
                null, null, null, false,
                null, null, false, false, false,
                false, null, null, false, null,
                null, null, null, null, null,
                null, null, false, null, null, false,
                null, null, false, null, false,
                false, false, null, null, null,
                false, null, null, null, false,
                null, null, false, null, false, null, null, null
        );

        PlantDetails plantB = new PlantDetails(
                11L, "Ficus", "Tree",
                null, false,
                null, null, null, false,
                null, null, false, false, false,
                false, null, null, false, null,
                null, null, null, null, null,
                null, null, false, null, null, false,
                null, null, false, null, false,
                false, false, null, null, null,
                false, null, null, null, false,
                null, null, false, null, false, null, null, null
        );

        List<PlantDetails> detailsList = List.of(plantA, plantB);

        when(plantDetailsRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(detailsList);

        Path outputFile = tempDir.resolve("plant_details_backup.txt");
        backupService.backupPlantDetailsToFile(outputFile.toString());

        String fileContent = Files.readString(outputFile);
        assertNotNull(fileContent);
        assertFalse(fileContent.isEmpty());

        List<PlantDetails> parsed = objectMapper.readValue(fileContent, new TypeReference<>() {});
        assertEquals(2, parsed.size());
        assertEquals(10L, parsed.get(0).getId());
        assertEquals(11L, parsed.get(1).getId());
    }

    /**
     * Tests backing up an empty PlantDetails list returns "[]".
     */
    @Test
    void testBackupPlantDetails_EmptyList(@TempDir Path tempDir) throws IOException {
        when(plantDetailsRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(List.of());

        Path outputFile = tempDir.resolve("plant_details_backup_empty.txt");
        backupService.backupPlantDetailsToFile(outputFile.toString());

        String fileContent = Files.readString(outputFile);
        assertEquals("[ ]", fileContent.trim());
    }
}
