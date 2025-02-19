package com.flourish.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing Plant entities.
 *
 * <p>Provides CRUD operations for Plant data.</p>
 *
 * @see PlantDetailsJson
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-18
 */
@Repository
public interface PlantRepository extends JpaRepository<PlantDetailsJson, Long> {

    /**
     * Finds a plant by its common name.
     *
     * @param commonName the common name of the plant.
     * @return the matching Plant, or null if none found.
     */
    PlantDetailsJson findByCommonName(String commonName);
}
