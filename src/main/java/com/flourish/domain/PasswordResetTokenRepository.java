package com.flourish.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing PasswordResetToken entities.
 *
 * <p>Uses Spring Data JPA to provide basic CRUD operations.</p>
 *
 * @see PasswordResetToken
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a token by its string value.
     *
     * @param token the token string to search by.
     * @return the matching PasswordResetToken, or null if none found.
     */
    PasswordResetToken findByToken(String token);
}
