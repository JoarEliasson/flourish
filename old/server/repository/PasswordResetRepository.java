package com.flourish.server.repository;

import com.flourish.domain.model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link PasswordReset} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard data access operations as well as custom query
 * methods related to password reset tokens.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    /**
     * Retrieves a {@link PasswordReset} entry by its token.
     *
     * @param token the password reset token to search for
     * @return an {@link Optional} containing the matching {@link PasswordReset} entry, or empty if none is found
     */
    Optional<PasswordReset> findByToken(String token);
}
