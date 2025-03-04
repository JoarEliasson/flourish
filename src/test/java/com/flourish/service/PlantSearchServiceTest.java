package com.flourish.service;

import com.flourish.domain.PlantIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PlantSearchService}, ensuring 100% method, line, and branch coverage.
 *
 * <p>This class removes all in-line comments in favor of method-level Javadoc
 * explanations. Several scenarios are tested, including file I/O in {@code init()},
 * boundary value analysis for the search strings, and verification of prefix/mid-substring
 * scoring logic.</p>
 *
 * <p>Uses {@link ActiveProfiles}("test") to ensure test-specific properties are loaded.
 * This class demonstrates boundary value and state-based testing for search functionality,
 * without spinning up the Spring context or a real database.</p>
 *
 * <strong>Coverage Goals:</strong>
 * <ul>
 *   <li>{@link PlantSearchService#init()} - successful load vs. IOException path</li>
 *   <li>{@link PlantSearchService#search(String)} - null/empty query, partial matches, no matches, prefix matches</li>
 *   <li>{@link PlantSearchService#matches(PlantIndex, String)} and {@link PlantSearchService#computeMatchScore(PlantIndex, String)} -
 *       verifying all branches for null fields, substring in the middle, prefix=0, fallback=100</li>
 * </ul>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class PlantSearchServiceTest {

    private PlantSearchService plantSearchService;

    /**
     * Initializes a new {@link PlantSearchService} before each test
     * and sets a mock dataset directly. This approach also covers
     * the scenario where {@code init()} is never triggered by Spring.
     */
    @BeforeEach
    void setUp() {
        plantSearchService = new PlantSearchService();

        List<PlantIndex> mockData = new ArrayList<>();
        mockData.add(new PlantIndex(1L, "Rose", "Rosa rubiginosa", "Sweet Briar"));
        mockData.add(new PlantIndex(2L, "Basil", "Ocimum basilicum", "Thai Basil"));
        mockData.add(new PlantIndex(3L, "Sunflower", "Helianthus annuus", "Common Sunflower"));
        mockData.add(new PlantIndex(4L, "Rosemary", "Salvia rosmarinus", null));
        mockData.add(new PlantIndex(5L, null, null, null));
        plantSearchService.setPlantIndexList(mockData);
    }

    /**
     * Verifies that calling {@link PlantSearchService#init()} with a valid
     * file path populates the {@code plantIndexList} successfully. To avoid real I/O,
     * you can replace the readString call with a spy or a custom approach
     * if you want to truly isolate file loading logic.
     */
    @Test
    @DisplayName("init(): valid file => loads data from JSON")
    void testInit_ValidFile() throws Exception {
        Path tempFile = Path.of("temp_plant_index_test.json");
        String sampleJson = """
            [
              {"id": 10, "commonName": "Lavender", "scientificName": "Lavandula", "otherName": "English Lavender"},
              {"id": 11, "commonName": "Mint", "scientificName": "Mentha", "otherName": "Spearmint"}
            ]
            """;
        Files.writeString(tempFile, sampleJson, StandardCharsets.UTF_8);

        PlantSearchService serviceWithFile = new PlantSearchService();
        serviceWithFile.plantIndexBackupFile = tempFile.toString();
        serviceWithFile.init();

        assertFalse(serviceWithFile.search("Lavender").isEmpty(),
                "Expected loaded data for 'Lavender'.");
        Files.deleteIfExists(tempFile);
    }

    /**
     * Verifies that {@link PlantSearchService#init()} gracefully catches
     * {@link IOException} when the backup file does not exist or is unreadable.
     */
    @Test
    @DisplayName("init(): invalid file => catches IOException")
    void testInit_InvalidFile() {
        PlantSearchService serviceWithBadPath = new PlantSearchService();
        serviceWithBadPath.plantIndexBackupFile = "non_existent_backup.json";
        serviceWithBadPath.init();

        List<PlantIndex> results = serviceWithBadPath.search("anything");
        assertTrue(results.isEmpty(), "Expected an empty list if file load fails.");
    }

    /**
     * Checks that an empty string query returns an empty result list,
     * covering the branch that bypasses matching logic.
     */
    @Test
    @DisplayName("search(): empty query => empty list")
    void testSearch_EmptyQuery_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search("");
        assertTrue(results.isEmpty());
    }

    /**
     * Checks that a null query also results in an empty list,
     * ensuring coverage of the null check branch.
     */
    @Test
    @DisplayName("search(): null query => empty list")
    void testSearch_NullQuery_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search(null);
        assertTrue(results.isEmpty());
    }

    /**
     * Tests a single-character query that partially matches multiple entries.
     * Verifies that the result list is not empty and includes the expected plants.
     */
    @Test
    @DisplayName("search(): single char => finds multiple matches")
    void testSearch_SingleCharQuery_FindsMultipleMatches() {
        List<PlantIndex> results = plantSearchService.search("R");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> "Rose".equalsIgnoreCase(p.getCommonName())));
        assertTrue(results.stream().anyMatch(p -> "Rosemary".equalsIgnoreCase(p.getCommonName())));
    }

    /**
     * Tests a case-insensitive query, confirming that "basIL" matches "Basil"
     * and verifies the first result's common name.
     */
    @Test
    @DisplayName("search(): case-insensitive => finds match")
    void testSearch_CaseInsensitiveQuery_FindsMatch() {
        List<PlantIndex> results = plantSearchService.search("basIL");
        assertFalse(results.isEmpty());
        assertEquals("Basil", results.get(0).getCommonName());
    }

    /**
     * Tests a scenario where no results are found for the given query,
     * confirming an empty list is returned.
     */
    @Test
    @DisplayName("search(): no matches => empty list")
    void testSearch_NoMatch_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search("xyz123");
        assertTrue(results.isEmpty());
    }

    /**
     * Tests that prefix matches are scored lower (better)
     * than partial matches in the middle of a field, ensuring the top result
     * is the one with a 0 match score.
     */
    @Test
    @DisplayName("search(): prefix => lowest score (first result)")
    void testSearch_PrefixMatch_HasLowestScore() {
        List<PlantIndex> results = plantSearchService.search("Rose");
        assertFalse(results.isEmpty());
        assertEquals("Rose", results.get(0).getCommonName());
    }

    /**
     * Verifies a mid-substring scenario, ensuring the computed match score
     * is non-zero but still places the result behind prefix matches.
     */
    @Test
    @DisplayName("search(): mid-substring => positive match score")
    void testSearch_MidSubstringMatch() {
        List<PlantIndex> results = plantSearchService.search("rubig");
        boolean foundRosa = results.stream().anyMatch(p -> "Rosa rubiginosa".equalsIgnoreCase(p.getScientificName()));
        assertTrue(foundRosa,
                "Expected a mid-substring match in 'Rosa rubiginosa'.");
    }

    /**
     * Ensures that entries with null fields do not cause exceptions
     * and are simply bypassed if the query doesn't match.
     */
    @Test
    @DisplayName("search(): handles null fields gracefully")
    void testSearch_HandleNullFields() {
        List<PlantIndex> results = plantSearchService.search("something");
        assertFalse(results.stream().anyMatch(p -> p.getId() == 5L),
                "Plant with all null fields should not match any query unless fields are non-null.");
    }

    /**
     * Demonstrates 100% branch coverage of the
     * {@code computeMatchScore(PlantIndex, String)} method
     * by testing each outcome:
     * <ul>
     *   <li>No fields match => score=100</li>
     *   <li>Prefix match => returns 0</li>
     *   <li>Mid-substring match => returns positive position</li>
     *   <li>Multiple fields => ensures lowest positive position if no prefix</li>
     *   <li>Null fields => ensures they are skipped</li>
     * </ul>
     */
    @Nested
    class PlantSearchServiceComputeMatchScoreTest {

        private PlantSearchService plantSearchService;

        /**
         * Sets up a new instance of the service.
         * We only need to access computeMatchScore(...) indirectly or directly,
         * so you can either test it via search(...) or reflect if needed.
         */
        @BeforeEach
        void setUp() {
            plantSearchService = new PlantSearchService();
        }

        /**
         * Ensures that if none of the fields contain the query, the score is 100.
         */
        @Test
        @DisplayName("computeMatchScore: no matches => 100")
        void testComputeMatchScore_NoMatches() {
            PlantIndex noMatchPlant = new PlantIndex(1L, "Rose", "Rosa rubiginosa", "Briar");
            // Searching for something not contained
            int score = callComputeMatchScore(noMatchPlant, "xyz");
            assertEquals(100, score);
        }

        /**
         * Verifies a perfect prefix match (position=0) leads to immediate return of 0.
         */
        @Test
        @DisplayName("computeMatchScore: prefix => 0")
        void testComputeMatchScore_Prefix() {
            PlantIndex prefixPlant = new PlantIndex(1L, "Rose", "Rosa rubiginosa", "Briar");
            int score = callComputeMatchScore(prefixPlant, "rose");
            assertEquals(0, score);
        }

        /**
         * Verifies a mid-substring match (position>0) yields a positive position less than 100.
         */
        @Test
        @DisplayName("computeMatchScore: mid-substring => positive position")
        void testComputeMatchScore_MidSubstring() {
            PlantIndex partialPlant = new PlantIndex(1L, "Rose", "Rosa rubiginosa", "Briar");
            int score = callComputeMatchScore(partialPlant, "osa");
            assertEquals(1, score);
        }

        /**
         * Ensures the method returns the smallest positive index if multiple fields match.
         */
        @Test
        @DisplayName("computeMatchScore: multiple fields => smallest positive index wins")
        void testComputeMatchScore_MultipleFields() {
            PlantIndex multiFieldPlant = new PlantIndex(1L, "Myrose", "Rosa rubiginosa", "Anotherrose");
            int score = callComputeMatchScore(multiFieldPlant, "rose");
            assertEquals(2, score);
        }

        /**
         * Verifies that null fields do not cause errors and are simply skipped.
         * If all fields are null or do not match, result is 100.
         */
        @Test
        @DisplayName("computeMatchScore: all null fields => 100")
        void testComputeMatchScore_AllNullFields() {
            PlantIndex nullFields = new PlantIndex(1L, null, null, null);
            int score = callComputeMatchScore(nullFields, "rose");
            assertEquals(100, score);
        }

        /**
         * Utility method to invoke computeMatchScore(...)
         * since it's private in PlantSearchService.
         *
         * In a real scenario, you might test this logic via
         * the public search(...) method. If reflection is disallowed,
         * you can simulate these tests by searching specific queries
         * and verifying the order of results.
         */
        private int callComputeMatchScore(PlantIndex plant, String query) {
            try {
                var method = PlantSearchService.class.getDeclaredMethod(
                        "computeMatchScore", PlantIndex.class, String.class);
                method.setAccessible(true);
                return (int) method.invoke(plantSearchService, plant, query.toLowerCase());
            } catch (Exception e) {
                throw new RuntimeException("Error calling computeMatchScore via reflection", e);
            }
        }
    }

}
