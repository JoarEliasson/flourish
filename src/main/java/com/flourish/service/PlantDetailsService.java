package com.flourish.service;

import com.flourish.domain.PlantDetails;
import com.flourish.repository.PlantDetailsRepository;
import com.flourish.domain.PlantIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for retrieving detailed plant information.
 *
 * <p>This service provides methods to retrieve a {@code PlantDetails} object from the database
 * based on a given plant ID or a {@code PlantIndex} object. It encapsulates the repository logic
 * so that other layers (such as the UI) do not have to access repositories directly.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-25
 */
@Service
public class PlantDetailsService {

    private final PlantDetailsRepository plantDetailsRepository;

    @Autowired
    public PlantDetailsService(PlantDetailsRepository plantDetailsRepository) {
        this.plantDetailsRepository = plantDetailsRepository;
    }

    /**
     * Retrieves the PlantDetails for the given plant ID.
     *
     * @param id the plant ID
     * @return an Optional containing the PlantDetails if found, otherwise an empty Optional.
     */
    public Optional<PlantDetails> getPlantDetailsById(Long id) {
        return plantDetailsRepository.findById(id);
    }

    /**
     * Retrieves the PlantDetails for the given PlantIndex.
     *
     * @param plantIndex the PlantIndex object containing the plant ID.
     * @return an Optional containing the PlantDetails if found, otherwise an empty Optional.
     */
    public Optional<PlantDetails> getPlantDetailsByPlantIndex(PlantIndex plantIndex) {
        if (plantIndex == null || plantIndex.getId() == null) {
            return Optional.empty();
        }
        return getPlantDetailsById(plantIndex.getId());
    }
}
