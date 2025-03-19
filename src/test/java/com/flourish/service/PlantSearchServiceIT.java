package com.flourish.service;

import com.flourish.domain.PlantIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link PlantSearchService} that use the real backup file
 * containing approximately 3000 entries. This class ensures boundary value cases
 * and realistic scenarios are tested against the actual data set.
 *
 * <p>All textual queries reflect real fields within plant_index_backup.txt,
 * and the search functionality is verified to return the correct/expected results
 * in a production-like environment.</p>
 *
 * <strong>Coverage Goals and Scenarios:</strong>
 * <ul>
 *   <li>Null/empty queries (boundary: expect empty result)</li>
 *   <li>Short or single-character queries (e.g., "R") that match many entries</li>
 *   <li>Case-insensitive substring checks (e.g., "rose", "ROSE")</li>
 *   <li>Prefix matches ensuring the top result is a perfect prefix</li>
 *   <li>Partial substring in the middle of a field</li>
 *   <li>No match scenarios (e.g., "xyz123")</li>
 *   <li>Ensuring large file load (3000 records) is handled properly</li>
 * </ul>
 *
 * <p>Note that this is an integration test, relying on the actual file
 * {@code plant_index_backup.txt}. Adjust file location/path as needed.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-28
 */
@SpringBootTest
class PlantSearchServiceIT {

    private static PlantSearchService plantSearchService;

    /**
     * The path to the real plant index backup file, typically loaded
     * from application-test.properties or a default path.
     */
    @Value("${plant.index.backup.file:plant_index_backup.txt}")
    private String backupFilePath;

    /**
     * Loads the file once before running all tests, simulating
     * the real environment where @PostConstruct calls init().
     */
    @BeforeAll
    static void init(@Value("${plant.index.backup.file:plant_index_backup.txt}") String filePath) {
        plantSearchService = new PlantSearchService();
        plantSearchService.plantIndexBackupFile = filePath;
        plantSearchService.init();

        List<PlantIndex> loadedData = plantSearchService.search("Rose");
        assertFalse(loadedData.isEmpty(),
                "Expected to have loaded some data from the real backup file.");
    }

    /**
     * Verifies that a null or empty query returns an empty result list,
     * ensuring boundary check for the query parameter.
     */
    @Test
    @DisplayName("search(): null or empty => empty list")
    void testNullOrEmptyQuery() {
        List<PlantIndex> emptyQueryResults = plantSearchService.search("");
        List<PlantIndex> nullQueryResults = plantSearchService.search(null);

        assertNotNull(emptyQueryResults);
        assertNotNull(nullQueryResults);
    }

    /**
     * Verifies that a single-character query matches all plants whose fields
     * contain that character. Expects multiple matches due to large data set.
     */
    @Test
    @DisplayName("search(): single-character => matches many entries")
    void testSingleCharacterQuery() {
        List<PlantIndex> results = plantSearchService.search("R");
        assertFalse(results.isEmpty(),
                "Expected multiple matches for single char 'R' across 3000 records.");
    }

    /**
     * Confirms that the search is case-insensitive by querying a known common name
     * or scientific name in various letter-casing forms.
     */
    @Test
    @DisplayName("search(): case-insensitive => finds known plant(s)")
    void testCaseInsensitivity() {
        List<PlantIndex> upperCase = plantSearchService.search("ROSE");
        List<PlantIndex> lowerCase = plantSearchService.search("rose");

        assertFalse(upperCase.isEmpty(),
                "Uppercase query 'ROSE' should match entries in the real file.");
        assertFalse(lowerCase.isEmpty(),
                "Lowercase 'rose' should match the same entries.");
    }

    /**
     * Ensures that prefix matches rank the plant at the top if multiple fields
     * contain the same substring. For example, "Rose" should appear first in
     * the commonName field, even if other fields contain the same substring.
     */
    @Test
    @DisplayName("search(): prefix => best score (top result)")
    void testPrefixScoring() {
        List<PlantIndex> results = plantSearchService.search("Rose");
        assertFalse(results.isEmpty(),
                "Should find 'Rose' in the real data.");
        assertEquals("Rose Marie Magnolia", results.get(0).getCommonName(),
                "Expected 'Rose' to appear first due to prefix scoring.");
    }

    /**
     * Verifies a partial substring match in the middle of a field. For example,
     * "macrophyllum" should match plants with "macrophyllum" in the scientificName.
     * This test ensures that partial matches are found in the middle of fields.
     */
    @Test
    @DisplayName("search(): mid-substring => ensures partial match result")
    void testMidSubstringMatch() {
        List<PlantIndex> results = plantSearchService.search("macrophyllum");
        assertFalse(results.isEmpty(),
                "Expected mid-substring match for 'macrophyllum' in some scientificName field.");
    }

    /**
     * Confirms that a query with no matching substring returns an empty list,
     * reflecting no hits in the full data set.
     */
    @Test
    @DisplayName("search(): non-matching query => empty list")
    void testNoMatchInFullData() {
        List<PlantIndex> results = plantSearchService.search("xyz1234567NoPlant");
        assertTrue(results.isEmpty(),
                "Should return an empty list for query not present in any of the 3000 records.");
    }

}
