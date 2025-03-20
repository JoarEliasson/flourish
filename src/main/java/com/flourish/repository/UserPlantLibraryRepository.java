package com.flourish.repository;

import com.flourish.domain.UserPlantLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing UserPlantLibrary entries.
 *
 * <p>Provides CRUD operations for user-specific plant library records.</p>
 *
 * @see UserPlantLibrary
 *
 * @author
 *   Joar Eliasson, Martin Frick
 * @version
 *   1.1.1
 * @since
 *   2025-03-18
 */

@Repository
public interface UserPlantLibraryRepository extends JpaRepository<UserPlantLibrary, Long> {

    List<UserPlantLibrary> findByUserId(Long userId);

    @Query("SELECT upl FROM UserPlantLibrary upl LEFT JOIN FETCH upl.hashtags WHERE upl.userId = :userId AND upl.plantId = :plantId")
    Optional<UserPlantLibrary> findByUserIdAndPlantId(@Param("userId") Long userId, @Param("plantId") Long plantId);

/*    @Query(value = "SELECT hashtag FROM user_plant_hashtags WHERE user_plant_id = :plantId", nativeQuery = true)
    List<String> findHashtagsForUserPlant(@Param("userId") Long userId, @Param("plantId") Long plantId);*/

    @Query(value = "SELECT hashtag FROM user_plant_hashtags WHERE user_plant_id = :libraryId", nativeQuery = true)
    List<String> findHashtagsForUserPlant(@Param("libraryId") Long libraryId);

}
