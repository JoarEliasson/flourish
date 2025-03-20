package com.flourish.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
 * ------------------------------------------------------------
 * 250317  Martin Frick
 * Fields to allow users to associate a plant with a hashtag was added.
 *
 * In order to reduce keep the hastag data normalised (Avoid redundant data, i.e. not have more data than needed)
 * separate tables are created which are then joined to represent list of hastags.
 * See https://en.wikibooks.org/wiki/Java_Persistence/ElementCollection for more info.
 *
 * ElementCollection works with a "CollectionTable" which in effect creates the table which
 * is able to, in the end, represent the list of hashtags.
 * See https://docs.oracle.com/javaee/6/api/javax/persistence/CollectionTable.htmt for more info.
 *
 * The job of the "ElementCollection" is to highlight the "hashtags" as "basic values" and not an
 * entity onto itself (like for example the user_id is or plant_id).
 *
 * In case of this code, each plants unique plant_id is associated with list of x-amount of strings = hashtag(s)
 * which in database terms is a one-to-many relationship.
 *
 * There is no enforcement to make the plant_id reference (to each list of strings) based on the
 * plant_id. This is to not have to handle errors coming from entering incorrect values in database,
 * this is assumed to be correct in the code which puts the hashtags in the lists in the relevant code.
 *
 * Hashtag strings are allowed to be duplicate.
 * ------------------------------------------------------------
 *
 *
 *
 *
 * @author
 *   Joar Eliasson, Martin Frick
 * @version
 *   1.1.1
 * @since
 *   2025-03-17
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

    /**
     * @author Martin Frick
     * @date   250317
     *
     * "ElementCollection" is used to handle relationship mapping between
     * plant_id and hashtags in the collectiontable "user_plant_hashtags".
     *
     *
     */
    @ElementCollection
    @CollectionTable(name = "user_plant_hashtags", joinColumns = @JoinColumn(name = "user_plant_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    protected UserPlantLibrary() { }

    /**
     * Constructs a new UserPlantLibrary entry.
     *
     * @MartinFrick Added getter and setter for hashtags.
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

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        //We set a list of hashtags to make the transfer to database easier. Appends etc is
        //handled outside.
        this.hashtags = hashtags;
    }

}
