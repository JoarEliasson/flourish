package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flourish.domain.PlantDetails;
import com.flourish.domain.PlantIndex;
import com.flourish.domain.UserPlantLibrary;
import com.flourish.repository.UserPlantLibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>Unit tests for {@link UserPlantLibraryService}, demonstrating full line
 * and branch coverage. All logic paths in the service methods are tested
 * by mocking dependencies and verifying outcomes.</p>
 *
 * <strong>Coverage Goals:</strong>
 * <ul>
 *   <li>{@code parseWateringFrequency(String)} - all branches (null, frequent, minimum, default)</li>
 *   <li>{@code addPlantToLibrary(Long, Long)} - found vs. not found, checking repository save calls</li>
 *   <li>{@code addPlantToLibrary(Long, PlantIndex)} - same scenarios via {@link PlantIndex}</li>
 *   <li>{@code removePlantFromLibrary(Long)} - repository deletion path</li>
 *   <li>{@code waterPlant(Long)} - missing vs. present library entry, verifying updates</li>
 *   <li>{@code getWateringGaugePercentage(Long)} - missing entry vs. fraction=0 => 100, fraction>1 => -100 clamp</li>
 * </ul>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-28
 */
class UserPlantLibraryServiceTest {

    private UserPlantLibraryService userPlantLibraryService;

    @Mock
    private UserPlantLibraryRepository libraryRepository;

    @Mock
    private PlantDetailsService plantDetailsService;

    /**
     * Initializes Mockito mocks and creates the service instance.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userPlantLibraryService = new UserPlantLibraryService(libraryRepository, plantDetailsService);
    }

    /**
     * Tests the {@link UserPlantLibraryService#parseWateringFrequency(String)}
     * method for all watering string branches, including null.
     */
    @Test
    @DisplayName("parseWateringFrequency: verifies all watering string cases")
    void testParseWateringFrequency() {
        assertEquals(10, userPlantLibraryService.parseWateringFrequency(null));
        assertEquals(7, userPlantLibraryService.parseWateringFrequency("Frequent"));
        assertEquals(14, userPlantLibraryService.parseWateringFrequency("minimum"));
        assertEquals(10, userPlantLibraryService.parseWateringFrequency("average"));
    }

    /**
     * Verifies that {@code addPlantToLibrary(Long, Long)} returns an empty
     * Optional if {@link PlantDetailsService} cannot find the plant details.
     */
    @Test
    @DisplayName("addPlantToLibrary(userId, plantId): empty if PlantDetails not found")
    void testAddPlantToLibrary_ById_NotFound() {
        when(plantDetailsService.getPlantDetailsById(123L)).thenReturn(Optional.empty());

        Optional<UserPlantLibrary> result = userPlantLibraryService.addPlantToLibrary(99L, 123L);
        assertTrue(result.isEmpty());
        verifyNoMoreInteractions(libraryRepository);
    }

    /**
     * Verifies that {@code addPlantToLibrary(Long, Long)} saves a new
     * {@link UserPlantLibrary} entry when {@link PlantDetails} is found.
     */
    @Test
    @DisplayName("addPlantToLibrary(userId, plantId): saves library entry if details found")
    void testAddPlantToLibrary_ById_Found() {
        PlantDetails mockDetails = mock(PlantDetails.class);
        when(mockDetails.getWatering()).thenReturn("Frequent");
        when(plantDetailsService.getPlantDetailsById(123L)).thenReturn(Optional.of(mockDetails));

        UserPlantLibrary savedLibrary = mock(UserPlantLibrary.class);
        when(libraryRepository.save(any(UserPlantLibrary.class))).thenReturn(savedLibrary);

        Optional<UserPlantLibrary> result = userPlantLibraryService.addPlantToLibrary(99L, 123L);
        assertTrue(result.isPresent());
        verify(libraryRepository).save(any(UserPlantLibrary.class));
    }

    /**
     * Verifies that {@code addPlantToLibrary(Long, PlantIndex)} returns an empty
     * Optional if the plant details are unavailable.
     */
    @Test
    @DisplayName("addPlantToLibrary(userId, plantIndex): empty if details not found")
    void testAddPlantToLibrary_ByIndex_NotFound() {
        PlantIndex index = new PlantIndex(555L, "Rose", "Rosa", null);
        when(plantDetailsService.getPlantDetailsById(555L)).thenReturn(Optional.empty());

        Optional<UserPlantLibrary> result = userPlantLibraryService.addPlantToLibrary(42L, index);
        assertTrue(result.isEmpty());
        verifyNoMoreInteractions(libraryRepository);
    }

    /**
     * Verifies that {@code addPlantToLibrary(Long, PlantIndex)} saves a new
     * {@link UserPlantLibrary} entry if {@link PlantDetails} is found.
     * Tests a different watering value branch.
     */
    @Test
    @DisplayName("addPlantToLibrary(userId, plantIndex): saves if details found")
    void testAddPlantToLibrary_ByIndex_Found() {
        PlantIndex index = new PlantIndex(555L, "Sunflower", "Helianthus annuus", null);
        PlantDetails mockDetails = mock(PlantDetails.class);
        when(mockDetails.getWatering()).thenReturn("Minimum");
        when(plantDetailsService.getPlantDetailsById(555L)).thenReturn(Optional.of(mockDetails));

        UserPlantLibrary savedEntry = mock(UserPlantLibrary.class);
        when(libraryRepository.save(any(UserPlantLibrary.class))).thenReturn(savedEntry);

        Optional<UserPlantLibrary> result = userPlantLibraryService.addPlantToLibrary(42L, index);
        assertTrue(result.isPresent());
        verify(libraryRepository).save(any(UserPlantLibrary.class));
    }

    /**
     * Verifies that {@code removePlantFromLibrary(Long)} calls the repository's
     * deleteById with the correct argument.
     */
    @Test
    @DisplayName("removePlantFromLibrary: verifies repository deleteById is invoked")
    void testRemovePlantFromLibrary() {
        userPlantLibraryService.removePlantFromLibrary(999L);
        verify(libraryRepository).deleteById(999L);
    }

    /**
     * Verifies that {@code waterPlant(Long)} returns empty if the specified
     * library entry does not exist in the repository.
     */
    @Test
    @DisplayName("waterPlant: empty if entry not found")
    void testWaterPlant_NotFound() {
        when(libraryRepository.findById(777L)).thenReturn(Optional.empty());

        Optional<UserPlantLibrary> result = userPlantLibraryService.waterPlant(777L);
        assertTrue(result.isEmpty());
        verify(libraryRepository).findById(777L);
        verifyNoMoreInteractions(libraryRepository);
    }

    /**
     * Verifies that {@code waterPlant(Long)} updates the entry's timestamps
     * and saves it if the entry is found.
     */
    @Test
    @DisplayName("waterPlant: updates and saves if entry is found")
    void testWaterPlant_Found() {
        UserPlantLibrary mockEntry = mock(UserPlantLibrary.class);
        when(mockEntry.getWateringFrequency()).thenReturn(10);
        when(libraryRepository.findById(888L)).thenReturn(Optional.of(mockEntry));

        UserPlantLibrary savedEntry = mock(UserPlantLibrary.class);
        when(libraryRepository.save(mockEntry)).thenReturn(savedEntry);

        Optional<UserPlantLibrary> result = userPlantLibraryService.waterPlant(888L);
        assertTrue(result.isPresent());
        verify(mockEntry).setLastWatered(any(LocalDateTime.class));
        verify(mockEntry).setNextWatering(any(LocalDateTime.class));
        verify(libraryRepository).save(mockEntry);
    }

    /**
     * Verifies that {@code getWateringGaugePercentage(Long)} returns empty if
     * the specified entry is not found.
     */
    @Test
    @DisplayName("getWateringGaugePercentage: empty if entry not found")
    void testGetWateringGaugePercentage_NotFound() {
        when(libraryRepository.findById(321L)).thenReturn(Optional.empty());
        Optional<Double> result = userPlantLibraryService.getWateringGaugePercentage(321L);
        assertTrue(result.isEmpty());
    }

    /**
     * <p>Verifies that {@code getWateringGaugePercentage(Long)} correctly
     * calculates gauge values.</p>
     * <strong>Scenarios:</strong>
     * <ul>
     *     <li>Scenario 1: fraction=0 => gauge=100</li>
     *     <li>Scenario 2: fraction>1 => gauge < -100 => clamp to -100</li>
     *     <li>Scenario 3: extreme overdue case (3x watering interval) => clamp to -100</li>
     * <ul>
     */
    @Test
    @DisplayName("getWateringGaugePercentage: fraction=0 => 100, fraction>1 => -100 clamp")
    void testGetWateringGaugePercentage_ClampingAndZero() {
        UserPlantLibrary mockEntry = mock(UserPlantLibrary.class);
        when(libraryRepository.findById(55L)).thenReturn(Optional.of(mockEntry));

        LocalDateTime now = LocalDateTime.now();

        when(mockEntry.getLastWatered()).thenReturn(now);
        when(mockEntry.getNextWatering()).thenReturn(now);
        when(mockEntry.getWateringFrequency()).thenReturn(10);
        Optional<Double> gaugeOpt = userPlantLibraryService.getWateringGaugePercentage(55L);
        assertTrue(gaugeOpt.isPresent());
        assertEquals(100.0, gaugeOpt.get(), 0.01);

        when(mockEntry.getLastWatered()).thenReturn(now.minusDays(20));
        when(mockEntry.getNextWatering()).thenReturn(now.minusDays(10));
        gaugeOpt = userPlantLibraryService.getWateringGaugePercentage(55L);
        assertTrue(gaugeOpt.isPresent());
        assertEquals(-100.0, gaugeOpt.get(), 0.01);

        when(mockEntry.getLastWatered()).thenReturn(now.minusDays(30));
        when(mockEntry.getNextWatering()).thenReturn(now.minusDays(20));
        gaugeOpt = userPlantLibraryService.getWateringGaugePercentage(55L);
        assertTrue(gaugeOpt.isPresent());
        assertEquals(-100.0, gaugeOpt.get(), 0.01);

    }
}