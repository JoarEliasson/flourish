package com.flourish.service;

import com.flourish.domain.PlantDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link com.flourish.service.PlantDataProvider}.
 *
 * <p>Ensures that the static method {@code getAvailableFields(PlantDetails)}
 * correctly identifies which fields are populated (non-null / non-empty) within
 * a {@link com.flourish.domain.PlantDetails} object.</p>
 *
 * <p>We test boundary conditions such as:
 * <ul>
 *   <li>All fields null or empty</li>
 *   <li>Some fields populated, others not</li>
 * </ul>
 * </p>
 *
 * <strong>Note:</strong> No Spring context is required;
 * we rely on plain JUnit test with optional {@link ActiveProfiles} annotation.
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class PlantDataProviderTest {

    private PlantDetails emptyDetails;

    /**
     * Sets up a mostly empty PlantDetails for boundary tests.
     */
    @BeforeEach
    void setUp() {
        emptyDetails = new PlantDetails(
                null,
                "",
                "",
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                false,
                false,
                false,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                false,
                null,
                null,
                false,
                null,
                false,
                false,
                false,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null
        );
    }

    /**
     * Tests that a PlantDetails object with all null or empty fields
     * returns a map of booleans all set to false.
     */
    @Test
    void testGetAvailableFields_AllNullOrEmpty() {
        Map<String, Boolean> result = PlantDataProvider.getAvailableFields(emptyDetails);

        assertFalse(result.get("id"), "id should be false when null");
        assertFalse(result.get("commonName"), "commonName should be false when empty");
        assertFalse(result.get("flowers"), "flowers should be false when null");
        assertFalse(result.get("sunlight"), "sunlight should be false when empty");
        assertFalse(result.get("poisonousToPets"), "poisonousToPets should be false when null");
        assertEquals(53, result.size());
    }

    /**
     * Tests that a PlantDetails object with certain fields populated
     * returns true for those fields and false for the others.
     */
    @Test
    void testGetAvailableFields_PartialPopulation() {
        PlantDetails partialDetails = new PlantDetails(
                123L,
                "Rose",
                "",
                null,
                true,
                null,
                null,
                null,
                true,
                null,
                null,
                false,
                false,
                false,
                false,
                null,
                null,
                true,
                null,
                "license",
                "",
                "http://original.url",
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                false,
                null,
                null,
                true,
                null,
                true,
                false,
                true,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                false,
                null,
                "",
                false,
                "",
                false,
                null,
                "",
                null
        );

        Map<String, Boolean> result = PlantDataProvider.getAvailableFields(partialDetails);

        assertTrue(result.get("id"), "id should be true when not null");
        assertTrue(result.get("commonName"), "commonName should be true when non-empty");
        assertTrue(result.get("saltTolerant"), "saltTolerant is non-null => true");
        assertTrue(result.get("cones"), "cones is non-null => true");
        assertTrue(result.get("indoor"), "indoor is non-null => true");
        assertTrue(result.get("defaultImageLicenseName"), "license is non-empty => true");
        assertTrue(result.get("defaultImageOriginalUrl"), "non-empty => true");
        assertTrue(result.get("edibleFruit"), "not null => true");
        assertTrue(result.get("medicinal"), "not null => true");
        assertTrue(result.get("fruits"), "not null => true");
        assertTrue(result.get("cuisine"), "not null => true");
        assertTrue(result.get("poisonousToPets"), "not null => true");

        assertFalse(result.get("type"), "type should be false when null");
        assertFalse(result.get("pruningCount"), "pruningCount should be false when null");
        assertFalse(result.get("defaultImageLicenseUrl"), "empty string => false");
        assertFalse(result.get("watering"), "empty string => false");
    }
}
