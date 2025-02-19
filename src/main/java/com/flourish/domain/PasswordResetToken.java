package com.flourish.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a password-reset token entity. Links a token string to a specific user
 * (identified by email) and an expiration time.
 *
 * <p>The token is used in the flow where a user clicks "Forgot Password",
 * receives an emailed token, then uses that token in the reset form.</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /**
     * The primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user’s email for which this token was generated.
     */
    @Column(nullable = false)
    private String email;

    /**
     * The unique token string.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The date/time at which this token expires.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Default constructor for JPA.
     */
    protected PasswordResetToken() {}

    /**
     * Constructs a new PasswordResetToken for the given user email.
     *
     * @param email The user's email address.
     * @param token The random token string.
     * @param expiryDate The expiration date/time.
     */
    public PasswordResetToken(String email, String token, LocalDateTime expiryDate) {
        this.email = email;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
