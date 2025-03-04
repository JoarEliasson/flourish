package com.flourish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents an entry in a user's personal plant library.
 *
 * <p>This entity associates a user (by userId) with a public plant (identified by plantId)
 * and stores data related to watering. When a plant is added to the library,
 * the system calculates a watering frequency based on the public plant’s watering field:
 * <ul>
 *   <li>"Frequent" &rarr; 7 days</li>
 *   <li>"Average" &rarr; 10 days</li>
 *   <li>"Minimum" &rarr; 14 days</li>
 * </ul>
 * The entity stores the wateringFrequency (in days), the lastWatered date (set upon addition or watering),
 * and the nextWatering date (computed as lastWatered plus wateringFrequency days).
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-26
 */
@Entity
@Table(name = "user_plant_library")
public class UserPlantLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the user who owns this library entry.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The plant ID from the public plant index/details.
     */
    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    /**
     * Watering frequency in days (e.g. 7, 10, or 14).
     */
    @Column(name = "watering_frequency", nullable = false)
    private int wateringFrequency;

    /**
     * The timestamp when the plant was last watered (or added).
     */
    @Column(name = "last_watered", nullable = false)
    private LocalDateTime lastWatered;

    /**
     * The timestamp when the plant should be watered next.
     */
    @Column(name = "next_watering", nullable = false)
    private LocalDateTime nextWatering;

    protected UserPlantLibrary() { }

    /**
     * Constructs a new UserPlantLibrary entry.
     *
     * @param userId the user’s ID.
     * @param plantId the public plant ID.
     * @param wateringFrequency the watering frequency (in days).
     * @param lastWatered the timestamp when the plant was last watered.
     * @param nextWatering the timestamp when the plant should be watered next.
     */
    public UserPlantLibrary(Long userId, Long plantId, int wateringFrequency, LocalDateTime lastWatered, LocalDateTime nextWatering) {
        this.userId = userId;
        this.plantId = plantId;
        this.wateringFrequency = wateringFrequency;
        this.lastWatered = lastWatered;
        this.nextWatering = nextWatering;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPlantId() {
        return plantId;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public LocalDateTime getLastWatered() {
        return lastWatered;
    }

    public LocalDateTime getNextWatering() {
        return nextWatering;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public void setLastWatered(LocalDateTime lastWatered) {
        this.lastWatered = lastWatered;
    }

    public void setNextWatering(LocalDateTime nextWatering) {
        this.nextWatering = nextWatering;
    }
}
