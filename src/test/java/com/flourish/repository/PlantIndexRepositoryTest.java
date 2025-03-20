package com.flourish.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.flourish.domain.PlantIndex;

/**
 * Integration tests for {@link com.flourish.repository.PlantIndexRepository}.
 *
 * <p>Uses {@link DataJpaTest} and an in-memory H2 database. The {@link ActiveProfiles}("test")
 * is applied to load test-specific properties.</p>
 *
 * <p>Checks basic CRUD operations and the custom finder method
 * {@code findByCommonName}.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@DataJpaTest
@ActiveProfiles("test")
class PlantIndexRepositoryTest {

    @Autowired
    private PlantIndexRepository plantIndexRepository;

    @Test
    @DisplayName("Test saving and retrieving a PlantIndex")
    void testSaveAndFindById() {
        PlantIndex plant = new PlantIndex(100L, "Mint", "Mentha", "Spearmint");
        PlantIndex saved = plantIndexRepository.save(plant);

        assertNotNull(saved);
        assertEquals(100L, saved.getId());

        PlantIndex found = plantIndexRepository.findById(100L).orElse(null);
        assertNotNull(found);
        assertEquals("Mint", found.getCommonName());
    }

    @Test
    @DisplayName("Test finding by common name")
    void testFindByCommonName() {
        PlantIndex plant = new PlantIndex(101L, "Bamboo", "Bambusoideae", null);
        plantIndexRepository.save(plant);

        PlantIndex result = plantIndexRepository.findByCommonName("Bamboo");
        assertNotNull(result);
        assertEquals(101L, result.getId());
    }

    @Test
    @DisplayName("Test saving a PlantIndex with a very long common name")
    void testLongCommonName() {
        String longName = "SupercalifragilisticexpialidociousBasil123";
        PlantIndex plant = new PlantIndex(102L, longName, "Ocimum basilicum", "Test Basil");
        PlantIndex saved = plantIndexRepository.save(plant);

        assertNotNull(saved);
        assertEquals(longName, saved.getCommonName());
    }

    @Test
    @DisplayName("Test saving a PlantIndex with a null otherName field")
    void testNullOtherName() {
        PlantIndex plant = new PlantIndex(103L, "Lavender", "Lavandula", null);
        PlantIndex saved = plantIndexRepository.save(plant);

        assertNotNull(saved);
        assertNull(saved.getOtherName());
    }

    @Test
    @DisplayName("Test findById for non-existent entity")
    void testNonExistentRecord() {
        assertTrue(plantIndexRepository.findById(999L).isEmpty());
    }

}
