package com.flourish.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a user in the Flourish application.
 * <p>
 * This entity maps to the <code>Users</code> table in the database.
 * It contains user credentials and preferences.
 * </p>
 * <p>
 * <strong>Note:</strong> Passwords should be stored in an encrypted format.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Entity
@Table(name = "Users")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique username of the user.
     */
    @Column(length = 25, nullable = false, unique = true)
    private String username;

    /**
     * The email address of the user.
     */
    @Column(length = 50, unique = true)
    private String email;

    /**
     * The encrypted password of the user.
     */
    @Column(length = 100, nullable = false)
    private String password;

    /**
     * Flag indicating if notifications are activated.
     */
    @Column(name = "notification_activated", nullable = false)
    private Boolean notificationActivated;

    /**
     * Flag indicating if fun facts are activated.
     */
    @Column(name = "fun_facts_activated", nullable = false)
    private Boolean funFactsActivated;

    /**
     * The timestamp when the user was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA.
     */
    public User() {
    }

    /**
     * Constructs a new {@code User} with the given attributes.
     *
     * @param username              the username
     * @param email                 the email address
     * @param password              the encrypted password
     * @param notificationActivated whether notifications are activated
     * @param funFactsActivated     whether fun facts are activated
     */
    public User(String username, String email, String password,
                Boolean notificationActivated, Boolean funFactsActivated) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.notificationActivated = notificationActivated;
        this.funFactsActivated = funFactsActivated;
    }

    /**
     * Callback invoked before the entity is persisted.
     * Sets the created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    /**
     * Callback invoked before the entity is updated.
     * Updates the {@code updatedAt} timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the user ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the encrypted password.
     *
     * @return the encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encrypted password.
     *
     * @param password the encrypted password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if notifications are activated.
     *
     * @return {@code true} if notifications are activated, otherwise {@code false}
     */
    public Boolean getNotificationActivated() {
        return notificationActivated;
    }

    /**
     * Sets whether notifications are activated.
     *
     * @param notificationActivated {@code true} to activate notifications
     */
    public void setNotificationActivated(Boolean notificationActivated) {
        this.notificationActivated = notificationActivated;
    }

    /**
     * Checks if fun facts are activated.
     *
     * @return {@code true} if fun facts are activated, otherwise {@code false}
     */
    public Boolean getFunFactsActivated() {
        return funFactsActivated;
    }

    /**
     * Sets whether fun facts are activated.
     *
     * @param funFactsActivated {@code true} to activate fun facts
     */
    public void setFunFactsActivated(Boolean funFactsActivated) {
        this.funFactsActivated = funFactsActivated;
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

    /**
     * Gets the last updated timestamp.
     *
     * @return the last updated timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated timestamp.
     *
     * @param updatedAt the last updated timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
