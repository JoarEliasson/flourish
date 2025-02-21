package com.flourish.repository;

import com.flourish.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing User entities.
 *
 * <p>Uses Spring Data JPA to provide basic CRUD operations.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email The email to search by.
     * @return A User, or null if none found.
     */
    User findByEmail(String email);

    /**
     * Finds a user by password reset token.
     *
     * @param resetToken The token.
     * @return A User, or null if none found.
     */
    User findByResetToken(String resetToken);
}
