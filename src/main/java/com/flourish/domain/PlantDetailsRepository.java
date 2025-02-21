package com.flourish.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing PlantDetailsJson entities.
 *
 * <p>Provides CRUD operations for detailed plant data stored as a JSON blob,
 * while relational fields (id, commonName, scientificName, otherName) remain separate.</p>
 *
 * @see PlantDetails
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@Repository
public interface PlantDetailsRepository extends JpaRepository<PlantDetails, Long> {
    
    /**
     * Finds a PlantDetailsJson entry by its common name.
     *
     * @param commonName the common name to search by.
     * @return the matching PlantDetailsJson entity, or null if none found.
     */
    PlantDetails findByCommonName(String commonName);

}
