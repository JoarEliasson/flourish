package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.flourish.domain.PlantIndex;

/**
 * Unit tests for {@link com.flourish.service.PlantSearchService}.
 *
 * <p>Uses {@link ActiveProfiles}("test") to ensure test-specific properties are loaded.
 * This class demonstrates boundary value and state-based testing for search functionality,
 * without spinning up the Spring context or a real database.</p>
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

    @BeforeEach
    void setUp() {
        plantSearchService = new PlantSearchService();

        List<PlantIndex> mockData = new ArrayList<>();
        mockData.add(new PlantIndex(1L, "Rose", "Rosa rubiginosa", "Sweet Briar"));
        mockData.add(new PlantIndex(2L, "Basil", "Ocimum basilicum", "Thai Basil"));
        mockData.add(new PlantIndex(3L, "Sunflower", "Helianthus annuus", "Common Sunflower"));
        mockData.add(new PlantIndex(4L, "Rosemary", "Salvia rosmarinus", null));
        plantSearchService.setPlantIndexList(mockData);
    }

    @Test
    void testSearch_EmptyQuery_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search("");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_NullQuery_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_SingleCharQuery_FindsMultipleMatches() {
        List<PlantIndex> results = plantSearchService.search("R");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> "Rose".equalsIgnoreCase(p.getCommonName())));
        assertTrue(results.stream().anyMatch(p -> "Rosemary".equalsIgnoreCase(p.getCommonName())));
    }

    @Test
    void testSearch_CaseInsensitiveQuery_FindsMatch() {
        List<PlantIndex> results = plantSearchService.search("basIL");
        assertFalse(results.isEmpty());
        assertEquals("Basil", results.get(0).getCommonName());
    }

    @Test
    void testSearch_NoMatch_ReturnsEmptyList() {
        List<PlantIndex> results = plantSearchService.search("xyz123");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_PrefixMatch_HasLowestScore() {
        List<PlantIndex> results = plantSearchService.search("Rose");
        assertFalse(results.isEmpty());
        assertEquals("Rose", results.get(0).getCommonName());
    }
}
