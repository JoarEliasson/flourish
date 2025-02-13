package com.flourish.server.repository;

import com.flourish.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit standard data access operations and
 * provides additional query methods for user-specific queries.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the found {@link User}, or empty if no user is found
     */
    Optional<User> findByUsername(String username);
}
