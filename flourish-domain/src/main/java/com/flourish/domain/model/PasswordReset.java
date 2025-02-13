package com.flourish.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a password reset request for a user.
 * <p>
 * This entity maps to the <code>PasswordReset</code> table in the database.
 * It is used to handle password reset tokens and their expiration.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Entity
@Table(name = "PasswordReset")
public class PasswordReset {

    /**
     * Unique identifier for the password reset entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user associated with this password reset.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The token used for resetting the password.
     */
    @Column(nullable = false, length = 255)
    private String token;

    /**
     * The expiration timestamp of the reset token.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Flag indicating whether the reset token has been used.
     */
    @Column(nullable = false)
    private Boolean used;

    /**
     * The timestamp when the reset entry was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for JPA.
     */
    public PasswordReset() {
    }

    /**
     * Constructs a new {@code PasswordReset} instance.
     *
     * @param user      the user for whom the reset is requested
     * @param token     the reset token
     * @param expiresAt the expiration timestamp of the token
     * @param used      flag indicating if the token has been used
     */
    public PasswordReset(User user, String token, LocalDateTime expiresAt, Boolean used) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    /**
     * Callback invoked before the entity is persisted.
     * Sets the created timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Gets the unique identifier of the password reset entry.
     *
     * @return the reset entry ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the password reset entry.
     *
     * @param id the reset entry ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the associated user.
     *
     * @return the user associated with this reset entry
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the associated user.
     *
     * @param user the user to associate
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the reset token.
     *
     * @return the reset token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the reset token.
     *
     * @param token the reset token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the expiration timestamp of the token.
     *
     * @return the expiration timestamp
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the expiration timestamp of the token.
     *
     * @param expiresAt the expiration timestamp to set
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Checks if the token has been used.
     *
     * @return {@code true} if the token has been used, otherwise {@code false}
     */
    public Boolean getUsed() {
        return used;
    }

    /**
     * Sets whether the token has been used.
     *
     * @param used {@code true} if the token is used
     */
    public void setUsed(Boolean used) {
        this.used = used;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
