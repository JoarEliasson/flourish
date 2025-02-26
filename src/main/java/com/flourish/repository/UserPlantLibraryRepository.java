package com.flourish.repository;

import com.flourish.domain.UserPlantLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing UserPlantLibrary entries.
 *
 * <p>Provides CRUD operations for user-specific plant library records.</p>
 *
 * @see UserPlantLibrary
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Repository
public interface UserPlantLibraryRepository extends JpaRepository<UserPlantLibrary, Long> {

}
