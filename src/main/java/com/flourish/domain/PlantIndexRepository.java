package com.flourish.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing PlantIndex entities.
 *
 * <p>Provides basic CRUD operations for plant index data,
 * which is used for search and quick reference.</p>
 *
 * @see PlantIndex
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@Repository
public interface PlantIndexRepository extends JpaRepository<PlantIndex, Long> {

    /**
     * Finds a PlantIndex entry by its common name.
     *
     * @param commonName the common name to search by.
     * @return the matching PlantIndex entity, or null if none found.
     */
    PlantIndex findByCommonName(String commonName);
}
