package com.flourish.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a user in the Flourish application.
 *
 * <p>This entity stores user credentials, including an encrypted password,
 * and optionally a reset token for password resetting in the future.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    /**
     * The user's email address. Must be unique and is used for login.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The encrypted (BCrypt) password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The user’s role (e.g. "USER" or "ADMIN").
     */
    @Column(nullable = false)
    private String role = "USER";

    /**
     * (Optional) Token for password reset flows.
     */
    private String resetToken;

    /**
     * (Optional) When the reset token expires.
     */
    private LocalDateTime resetTokenExpiry;

    protected User() {}

    public User(String firstName, String lastName, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
