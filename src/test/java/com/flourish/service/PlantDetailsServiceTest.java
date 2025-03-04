package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.flourish.domain.PlantDetails;
import com.flourish.domain.PlantIndex;
import com.flourish.repository.PlantDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

/**
 * Unit tests for {@link PlantDetailsService}.
 *
 * <p>Demonstrates 100% line and branch coverage by testing all
 * conditional paths, including handling of null/invalid IDs
 * and boundary ID checks. Mocks {@link PlantDetails} objects
 * instead of directly calling the protected constructor.</p>
 *
 * <strong>Coverage Goals:</strong>
 * <ul>
 *   <li>{@link PlantDetailsService#getPlantDetailsById(Long)} - found vs. not found, boundary ID examples</li>
 *   <li>{@link PlantDetailsService#getPlantDetailsByPlantIndex(PlantIndex)} - null index, null ID, valid ID</li>
 * </ul>
 *
 * <p><strong>Test Scenarios:</strong>
 * <ul>
 *   <li>{@code getPlantDetailsById(...)} - found vs. not found</li>
 *   <li>{@code getPlantDetailsById(...)} - boundary value examples (IDs 1 and 3001)</li>
 *   <li>{@code getPlantDetailsByPlantIndex(null)} - returns empty</li>
 *   <li>{@code getPlantDetailsByPlantIndex(...)} - null ID returns empty</li>
 *   <li>{@code getPlantDetailsByPlantIndex(...)} - valid ID delegates to {@code getPlantDetailsById}</li>
 * </ul>
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-27
 */
class PlantDetailsServiceTest {

    private PlantDetailsService plantDetailsService;

    @Mock
    private PlantDetailsRepository plantDetailsRepository;

    /**
     * Opens Mockito mocks and instantiates the service under test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        plantDetailsService = new PlantDetailsService(plantDetailsRepository);
    }

    /**
     * Confirms that a non-empty Optional is returned when the repository
     * finds a matching {@link PlantDetails} record.
     */
    @Test
    @DisplayName("getPlantDetailsById: Returns found entity")
    void testGetPlantDetailsById_Found() {
        PlantDetails mockDetails = mock(PlantDetails.class);
        when(mockDetails.getId()).thenReturn(123L);

        when(plantDetailsRepository.findById(123L)).thenReturn(Optional.of(mockDetails));

        Optional<PlantDetails> result = plantDetailsService.getPlantDetailsById(123L);
        assertTrue(result.isPresent(), "Expected a non-empty Optional when repository returns a record.");
        assertEquals(123L, result.get().getId(), "PlantDetails ID should match the mocked getId().");
    }

    /**
     * Confirms that an empty Optional is returned when the repository
     * indicates no matching record exists.
     */
    @Test
    @DisplayName("getPlantDetailsById: Returns empty if not found")
    void testGetPlantDetailsById_NotFound() {
        when(plantDetailsRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PlantDetails> result = plantDetailsService.getPlantDetailsById(999L);
        assertTrue(result.isEmpty(), "Expected empty Optional for a non-existent record.");
    }

    /**
     * Verifies boundary values for typical ID ranges.
     * Shows how ID=1 might exist and ID=3001 might not,
     * though the service does not enforce these constraints.
     */
    @Test
    @DisplayName("getPlantDetailsById: Boundary IDs (1, 3001)")
    void testGetPlantDetailsById_BoundaryValues() {
        PlantDetails mockDetailsMin = mock(PlantDetails.class);
        when(mockDetailsMin.getId()).thenReturn(1L);

        when(plantDetailsRepository.findById(1L)).thenReturn(Optional.of(mockDetailsMin));
        Optional<PlantDetails> minResult = plantDetailsService.getPlantDetailsById(1L);
        assertTrue(minResult.isPresent(), "Expected a non-empty Optional for ID=1.");

        when(plantDetailsRepository.findById(3001L)).thenReturn(Optional.empty());
        Optional<PlantDetails> outOfRangeResult = plantDetailsService.getPlantDetailsById(3001L);
        assertTrue(outOfRangeResult.isEmpty(), "Expected empty Optional for ID=3001 (out of typical range).");
    }

    /**
     * Checks that a null {@link PlantIndex} leads to an empty Optional
     * in {@link PlantDetailsService#getPlantDetailsByPlantIndex(PlantIndex)}.
     */
    @Test
    @DisplayName("getPlantDetailsByPlantIndex: Null index returns empty")
    void testGetPlantDetailsByPlantIndex_NullIndex() {
        Optional<PlantDetails> result = plantDetailsService.getPlantDetailsByPlantIndex(null);
        assertTrue(result.isEmpty(), "Expected empty Optional for null PlantIndex.");
    }

    /**
     * Checks that a {@link PlantIndex} with a null ID leads
     * to an empty Optional. No repository call is expected.
     */
    @Test
    @DisplayName("getPlantDetailsByPlantIndex: Index with null ID returns empty")
    void testGetPlantDetailsByPlantIndex_NullId() {
        PlantIndex indexWithNullId = new PlantIndex(null, "Rose", "Rosa", null);

        Optional<PlantDetails> result = plantDetailsService.getPlantDetailsByPlantIndex(indexWithNullId);
        assertTrue(result.isEmpty(), "Expected empty when the PlantIndex ID is null.");

        verifyNoInteractions(plantDetailsRepository);
    }

    /**
     * Confirms that a valid {@link PlantIndex} leads to a repository call.
     * The service should return a non-empty Optional if the record is found.
     */
    @Test
    @DisplayName("getPlantDetailsByPlantIndex: Valid ID delegates to getPlantDetailsById")
    void testGetPlantDetailsByPlantIndex_ValidId() {
        PlantIndex validIndex = new PlantIndex(200L, "Basil", "Ocimum basilicum", null);

        PlantDetails mockDetails = mock(PlantDetails.class);
        when(mockDetails.getId()).thenReturn(200L);

        when(plantDetailsRepository.findById(200L)).thenReturn(Optional.of(mockDetails));

        Optional<PlantDetails> result = plantDetailsService.getPlantDetailsByPlantIndex(validIndex);
        assertTrue(result.isPresent(), "Expected a non-empty Optional for valid PlantIndex ID=200.");
        assertEquals(200L, result.get().getId(), "Expected ID=200 for the returned PlantDetails.");
    }
}
