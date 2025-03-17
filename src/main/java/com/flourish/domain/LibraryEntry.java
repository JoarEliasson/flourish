package com.flourish.domain;

import com.flourish.domain.PlantDetails;
import com.flourish.domain.UserPlantLibrary;

import java.time.LocalDateTime;

public class LibraryEntry {
    private final PlantDetails plantDetails;
    private final UserPlantLibrary userPlantLibrary;

    public LibraryEntry(PlantDetails plantDetails, UserPlantLibrary userPlantLibrary) {
        this.plantDetails = plantDetails;
        this.userPlantLibrary = userPlantLibrary;
    }

    public PlantDetails getPlantDetails() {
        return plantDetails;
    }

    public UserPlantLibrary getUserPlantLibrary() {
        return userPlantLibrary;
    }

    public Long getPlantId() {
        return userPlantLibrary.getPlantId();
    }

    public Long getLibraryId() {
        return userPlantLibrary.getId();
    }

    public LocalDateTime getLastWatered() {
        return userPlantLibrary.getLastWatered();
    }

    public LocalDateTime getNextWatering() {
        return userPlantLibrary.getNextWatering();
    }

    public int getWateringFrequency() {
        return userPlantLibrary.getWateringFrequency();
    }

    public void setLastWatered(LocalDateTime lastWatered) {
        userPlantLibrary.setLastWatered(lastWatered);
    }

}