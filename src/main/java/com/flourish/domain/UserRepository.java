package com.flourish.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing User entities.
 *
 * <p>This interface uses Spring Data JPA to provide basic CRUD operations
 * for the User entity.</p>
 *
 * @author
 *   Your Name
 * @version
 *   1.0.0
 * @since
 *   1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by the email address.
     *
     * @param email The email to search by.
     * @return A User object if found, or null if no user exists with the given email.
     */
    User findByEmail(String email);

    /**
     * Finds a user by the password reset token.
     *
     * @param resetToken The token string.
     * @return A User if found, or null if no user has the given reset token.
     */
    User findByResetToken(String resetToken);
}
