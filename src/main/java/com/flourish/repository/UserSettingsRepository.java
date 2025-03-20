package com.flourish.repository;

import com.flourish.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing UserSettings entities.
 *
 * <p>This interface provides CRUD operations for user-specific settings.</p>
 *
 * @see UserSettings
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

}
