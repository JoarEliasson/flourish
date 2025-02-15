package com.flourish.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a user in the Flourish application.
 *
 * <p>This entity stores user credentials and other profile details.
 * Passwords are encrypted using BCrypt. A password reset token is optional
 * but included to support password-reset functionality in the future.</p>
 *
 * @author
 *   Your Name
 * @version
 *   1.0.0
 * @since
 *   1.0.0
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for this User.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's first name.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * The user's email address.
     * This should be unique and is used as login principal.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The encrypted user password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * (Optional) The user's role, e.g. "USER" or "ADMIN".
     * Default can be "USER".
     */
    @Column(nullable = false)
    private String role = "USER";

    /**
     * (Optional) A token for password-reset functionality.
     */
    @Column
    private String resetToken;

    /**
     * (Optional) Expiration date/time for the password reset token.
     */
    @Column
    private LocalDateTime resetTokenExpiry;

    /**
     * Default constructor for JPA.
     */
    protected User() {
        // Default no-arg constructor required by JPA
    }

    /**
     * Constructs a new user with the given data.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param email     The user's email address.
     * @param password  The user's (encrypted) password.
     * @param role      The user's role.
     */
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
