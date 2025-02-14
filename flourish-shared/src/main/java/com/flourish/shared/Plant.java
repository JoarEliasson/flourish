package com.flourish.shared;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Represents a plant, including both species-level data and user-specific details.
 * <p>
 * This class is used to represent data obtained from the Species table (immutable species information)
 * as well as a user's specific plant instance (which includes a nickname, last watered date, and recommended
 * watering frequency in days).
 * </p>
 *
 * @author Joar Eliasson
 * @since 2025-02-03
 */
public class Plant implements Serializable {

    private static final long serialVersionUID = 867522155232174497L;

    // --- Species-level data (immutable once set) ---
    private final int speciesId;
    private final String commonName;
    private final String scientificName;
    private final String genus;
    private final String family;
    private final String imageUrl;
    private final List<String> synonyms;

    // --- User-specific data ---
    private String nickname;
    private LocalDate lastWatered;
    private int waterFrequencyDays;
    private String customImageURL;

    // ===================== Constructors =====================

    /**
     * Constructs a Plant object using species-level data only.
     * <p>
     * This constructor is used when retrieving species data.
     * </p>
     *
     * @param speciesId      the unique species identifier (from the Species table)
     * @param commonName     the common name of the species
     * @param scientificName the scientific name of the species
     * @param family         the family name of the species
     * @param imageUrl       the default image URL for the species
     */
    public Plant(int speciesId, String commonName, String scientificName, String genus, String family, String imageUrl, List<String> synonyms) {
        this.speciesId = speciesId;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.genus = genus;
        this.family = family;
        this.imageUrl = imageUrl;
        this.synonyms = synonyms;
    }

    /**
     * Constructs a Plant object representing a user's plant.
     * <p>
     * This constructor adds user-specific data (nickname, last watered date,
     * and watering frequency) to the species information.
     * </p>
     *
     * @param speciesId          the unique species identifier (from the Species table)
     * @param nickname           the user's nickname for the plant
     * @param lastWatered        the date the plant was last watered
     * @param waterFrequencyDays the recommended watering frequency in days
     * @param customImageURL     the image URL for the user's plant; if null, the species default image is used
     */
    public Plant(int speciesId, String nickname, LocalDate lastWatered, int waterFrequencyDays, String customImageURL) {
        this.speciesId = speciesId;
        this.nickname = nickname;
        this.lastWatered = lastWatered;
        this.waterFrequencyDays = waterFrequencyDays;
        this.customImageURL = customImageURL;
        this.commonName = null;
        this.scientificName = null;
        this.genus = null;
        this.family = null;
        this.imageUrl = null;
        this.synonyms = null;
    }

    // ===================== Getters & Setters =====================

    /**
     * Returns the unique species identifier.
     *
     * @return speciesId as an integer.
     */
    public int getSpeciesId() {
        return speciesId;
    }

    /**
     * Returns the common name of the species.
     *
     * @return the common name, or null if not set.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Returns the scientific name of the species.
     *
     * @return the scientific name, or null if not set.
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Returns the genus name of the species.
     *
     * @return the genus name, or null if not set.
     */
    public String getGenus() {
        return genus;
    }

    /**
     * Returns the family name of the species.
     *
     * @return the family name, or null if not set.
     */
    public String getFamily() {
        return family;
    }

    /**
     * Returns the image URL for the plant.
     * <p>
     * If a custom image URL is set for the user’s plant, it is returned; otherwise,
     * the default species image is returned. If neither is set, a random image is provided.
     * Additionally, this method converts any “https” protocol to “http”.
     * </p>
     *
     * @return a valid image URL.
     */
    public String getImageURL() {
        String image = (customImageURL != null) ? customImageURL : imageUrl;
        if (image == null) {
            image = "https://source.unsplash.com/featured/?plant";
        }
        return image.replace("https", "http");
    }

    /**
     * Returns the list of synonyms for the species.
     *
     * @return the list of synonyms, or an empty list if not set.
     */
    public List<String> getSynonyms() {
        return synonyms;
    }

    /**
     * Sets the custom image URL for the user's plant.
     *
     * @param customImageURL the new image URL.
     */
    public void setCustomImageURL(String customImageURL) {
        this.customImageURL = customImageURL;
    }

    /**
     * Returns the user's nickname for the plant.
     *
     * @return the nickname, or null if not set.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the user's nickname for the plant.
     *
     * @param nickname the new nickname.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the date the plant was last watered.
     *
     * @return the last watered date, or null if not set.
     */
    public LocalDate getLastWatered() {
        return lastWatered;
    }

    /**
     * Sets the date the plant was last watered.
     *
     * @param lastWatered the new last watered date.
     */
    public void setLastWatered(LocalDate lastWatered) {
        this.lastWatered = lastWatered;
    }

    /**
     * Returns the recommended watering frequency in days.
     *
     * @return the watering frequency.
     */
    public int getWaterFrequencyDays() {
        return waterFrequencyDays;
    }

    /**
     * Sets the recommended watering frequency (in days).
     *
     * @param waterFrequencyDays the new watering frequency.
     */
    public void setWaterFrequencyDays(int waterFrequencyDays) {
        this.waterFrequencyDays = waterFrequencyDays;
    }

    // ===================== Business Methods =====================

    /**
     * Calculates the watering progress as a fraction of days elapsed since the plant was last watered
     * relative to the recommended watering interval.
     * <p>
     * The returned value is clamped between 0.02 (minimum) and 1.0 (if the plant is overdue for watering).
     * </p>
     *
     * @return a double between 0.02 and 1.0 indicating the progress.
     */
    public double getProgress() {
        if (lastWatered == null || waterFrequencyDays <= 0) {
            return 0.02;
        }
        long daysElapsed = ChronoUnit.DAYS.between(lastWatered, LocalDate.now());
        double progress = (double) daysElapsed / waterFrequencyDays;
        if (progress < 0.02) {
            return 0.02;
        } else if (progress > 1.0) {
            return 1.0;
        } else {
            return progress;
        }
    }

    /**
     * Provides a user-friendly message indicating how many days until the plant needs watering.
     *
     * @return a message such as "Needs water in X days" or "You need to water this plant now!".
     */
    public String getDaysUntilWater() {
        if (lastWatered == null || waterFrequencyDays <= 0) {
            return "Watering information unavailable.";
        }
        long daysElapsed = ChronoUnit.DAYS.between(lastWatered, LocalDate.now());
        long daysUntilWatering = waterFrequencyDays - daysElapsed;
        if (daysUntilWatering <= 0) {
            return "You need to water this plant now!";
        } else {
            return String.format("Needs water in %d day%s", daysUntilWatering, daysUntilWatering > 1 ? "s" : "");
        }
    }

    /**
     * Returns a string representation of the plant.
     * <p>
     * If species-level data is available, it returns common, family, and scientific names.
     * Otherwise, it shows the species ID and the user-assigned nickname.
     * </p>
     *
     * @return a formatted string.
     */
    @Override
    public String toString() {
        if (commonName != null && scientificName != null && family != null) {
            return String.format("Common name: %s\tFamily name: %s\tScientific name: %s",
                    commonName, family, scientificName);
        } else {
            return "Plant [Species ID: " + speciesId + ", Nickname: " + nickname + "]";
        }
    }
}
