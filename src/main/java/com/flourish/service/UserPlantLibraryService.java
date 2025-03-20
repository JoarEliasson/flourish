package com.flourish.service;

import com.flourish.domain.LibraryEntry;
import com.flourish.domain.PlantDetails;
import com.flourish.domain.PlantIndex;
import com.flourish.domain.UserPlantLibrary;
import com.flourish.repository.UserPlantLibraryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user-specific plant library data.
 *
 * <p>This service provides methods to add plants to a user's library, remove them,
 * mark them as watered (updating the last watered and next watering dates), and compute
 * a watering gauge percentage indicating how close the plant is to its next watering date.</p>
 *
 * <p>The watering frequency is derived from the plant details' "watering" field with the following mapping:
 * <ul>
 *   <li>"Frequent" -&gt; 7 days</li>
 *   <li>"Average" -&gt; 10 days</li>
 *   <li>"Minimum" -&gt; 14 days</li>
 * </ul>
 * </p>
 *
 * ----------------------------------------------------------------
 * @MartinFrick 250317
 * Added methods to add, remove and read hashtags from a users plantlibrary.
 * addHashtag    = Transactional
 * removeHashtag = Transactional
 * readHashtags  = NOT Transactional.
 * ----------------------------------------------------------------
 *
 *
 * @see UserPlantLibrary
 * @see UserPlantLibraryRepository
 * @see PlantDetailsService
 *
 * @author
 *   Joar Eliasson, Martin Frick
 * @version
 *   1.1.1
 * @since
 *   2025-03-17
 */
@Service
public class UserPlantLibraryService {

    private final UserPlantLibraryRepository libraryRepository;
    private final PlantDetailsService plantDetailsService;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public UserPlantLibraryService(UserPlantLibraryRepository libraryRepository, PlantDetailsService plantDetailsService) {
        this.libraryRepository = libraryRepository;
        this.plantDetailsService = plantDetailsService;
    }

    /**
     * Translates the watering string from PlantDetails into an interval in days.
     *
     * @param wateringValue the watering string (e.g., "Frequent", "Average", "Minimum").
     * @return the corresponding interval in days.
     */
    public int parseWateringFrequency(String wateringValue) {
        if (wateringValue == null) return 10;
        return switch (wateringValue.toLowerCase()) {
            case "frequent" -> 7;
            case "minimum" -> 14;
            default -> 10;
        };
    }

    /**
     * Adds a plant to a user's library.
     *
     * <p>This method retrieves the public plant details (to parse the watering frequency) using the provided plantId.
     * It then creates a new UserPlantLibrary entry with the watering frequency, setting lastWatered to now and computing
     * nextWatering accordingly.</p>
     *
     * @param userId the ID of the user.
     * @param plantId the public plant ID.
     * @return the saved UserPlantLibrary entry.
     */
    @Transactional
    public Optional<UserPlantLibrary> addPlantToLibrary(Long userId, Long plantId) {
        Optional<PlantDetails> detailsOpt = plantDetailsService.getPlantDetailsById(plantId);
        if (!detailsOpt.isPresent()) {
            return Optional.empty();
        }
        PlantDetails details = detailsOpt.get();
        int wateringFrequency = parseWateringFrequency(details.getWatering());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWatering = now.plusDays(wateringFrequency);
        UserPlantLibrary entry = new UserPlantLibrary(userId, plantId, wateringFrequency, now, nextWatering);
        return Optional.of(libraryRepository.save(entry));
    }

    /**
     * Adds a plant to a user's library.
     *
     * <p>This method retrieves the public plant details (to parse the watering frequency) using the provided plantId.
     * It then creates a new UserPlantLibrary entry with the watering frequency, setting lastWatered to now and computing
     * nextWatering accordingly.</p>
     *
     * <p>This method is an overloaded version of the {@link #addPlantToLibrary(Long, Long)} method, which accepts a
     * {@code PlantIndex} object instead of a plant ID.</p>
     *
     * @param userId the ID of the user.
     * @param plantIndex the {@code PlantIndex} object containing the plant ID.
     * @return the saved UserPlantLibrary entry.
     */
    @Transactional
    public Optional<UserPlantLibrary> addPlantToLibrary(Long userId, PlantIndex plantIndex) {
        Long plantId = plantIndex.getId();
        Optional<PlantDetails> detailsOpt = plantDetailsService.getPlantDetailsById(plantId);
        if (!detailsOpt.isPresent()) {
            return Optional.empty();
        }
        PlantDetails details = detailsOpt.get();
        int wateringFrequency = parseWateringFrequency(details.getWatering());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWatering = now.plusDays(wateringFrequency);
        UserPlantLibrary entry = new UserPlantLibrary(userId, plantId, wateringFrequency, now, nextWatering);
        return Optional.of(libraryRepository.save(entry));
    }



    /**
     * Removes a plant from the user's library.
     *
     * @param libraryEntryId the ID of the library entry to remove.
     */
    @Transactional
    public void removePlantFromLibrary(Long libraryEntryId) {
        libraryRepository.deleteById(libraryEntryId);
    }

    /**
     * Marks a plant as watered by updating the last watered timestamp and recalculating the next watering date.
     *
     * @param libraryEntryId the ID of the library entry to update.
     * @return the updated UserPlantLibrary entry, or an empty Optional if not found.
     */
    @Transactional
    public Optional<UserPlantLibrary> waterPlant(Long libraryEntryId) {
        Optional<UserPlantLibrary> opt = libraryRepository.findById(libraryEntryId);
        if (!opt.isPresent()) {
            return Optional.empty();
        }
        UserPlantLibrary entry = opt.get();
        LocalDateTime now = LocalDateTime.now();
        entry.setLastWatered(now);
        entry.setNextWatering(now.plusDays(entry.getWateringFrequency()));
        return Optional.of(libraryRepository.save(entry));
    }

    /**
     * Retrieves the public PlantDetails for a specific library entry.
     *
     * @param libraryEntryId the ID of the library entry.
     * @return an Optional containing the PlantDetails if found; otherwise, an empty Optional.
     */
    public Optional<PlantDetails> getPlantDetailsForLibraryEntry(Long libraryEntryId) {
        Optional<UserPlantLibrary> libraryEntryOpt = libraryRepository.findById(libraryEntryId);
        if (!libraryEntryOpt.isPresent() || libraryEntryOpt.get().getPlantId() == null) {
            return Optional.empty();
        }
        return plantDetailsService.getPlantDetailsById(libraryEntryOpt.get().getPlantId());
    }

    /**
     * Computes a watering gauge percentage for the given library entry.
     *
     * <p>The gauge is calculated based on the elapsed time since the plant was last watered relative
     * to the watering frequency. The gauge returns:
     * <ul>
     *   <li>100% if the plant was just watered (elapsed = 0)</li>
     *   <li>0% if the current time equals the next watering date</li>
     *   <li>-100% if the current time is one full watering interval past the next watering date</li>
     * </ul>
     * Values in between are linearly interpolated.
     * </p>
     *
     * @param libraryEntryId the ID of the library entry.
     * @return a double representing the gauge percentage.
     */
    public Optional<Double> getWateringGaugePercentage(Long libraryEntryId) {
        Optional<UserPlantLibrary> opt = libraryRepository.findById(libraryEntryId);
        if (!opt.isPresent()) {
            return Optional.empty();
        }
        UserPlantLibrary entry = opt.get();
        LocalDateTime lastWatered = entry.getLastWatered();
        LocalDateTime nextWatering = entry.getNextWatering();

        LocalDateTime now = LocalDateTime.now();
        if (lastWatered.equals(nextWatering)) {
            return Optional.of(100.0);
        }

        long totalMillis = Duration.between(lastWatered, nextWatering).toMillis();
        long elapsedMillis = Duration.between(lastWatered, now).toMillis();

        double fraction = (double) elapsedMillis / totalMillis;
        double gauge = 100 * (1 - fraction);

        if (gauge < -100) {
            gauge = -100;
        }
        gauge = Math.round(gauge);
        return Optional.of(gauge);
    }

    /**
     * Retrieves all public PlantDetails for the plants in a user's library.
     *
     * @param userId the user ID.
     * @return a List of PlantDetails for all plants in the user's library.
     */
    public List<PlantDetails> getAllPlantDetailsForUser(Long userId) {
        List<UserPlantLibrary> libraryEntries = libraryRepository.findByUserId(userId);
        List<PlantDetails> detailsList = new ArrayList<>();
        for (UserPlantLibrary entry : libraryEntries) {
            plantDetailsService.getPlantDetailsById(entry.getPlantId()).ifPresent(detailsList::add);
        }
        return detailsList;
    }

    /**
     * Retrieves all library entries for a user, including the associated PlantDetails.
     *
     * @param userId the user ID.
     * @return a List of LibraryEntry objects.
     */
    public List<LibraryEntry> getAllLibraryEntriesForUser(Long userId) {
        List<UserPlantLibrary> libraryEntries = libraryRepository.findByUserId(userId);
        List<LibraryEntry> entries = new ArrayList<>();
        for (UserPlantLibrary libraryEntry : libraryEntries) {
            plantDetailsService.getPlantDetailsById(libraryEntry.getPlantId()).ifPresent(details -> {
                entries.add(new LibraryEntry(details, libraryEntry));
            });
        }
        return entries;
    }

    /**
     * Method to Add a hashtag to database.
     * Functionality to find plant, potential douplicate hashtag is also here.
     *
     * @param userId
     * @param libraryId
     * @param newHashtag
     * @return success (boolean)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addHashtag(Long userId, Long libraryId, String newHashtag) {
        Optional<UserPlantLibrary> tempPlant = libraryRepository.findById(libraryId);
        if (tempPlant.isEmpty()) {
            return false; // No such plant
        }

        UserPlantLibrary plant = tempPlant.get();
        List<String> tmpHashtags = new ArrayList<>(plant.getHashtags());

        if (tmpHashtags.contains(newHashtag)) {
            return false; // Already exists
        }

        tmpHashtags.add(newHashtag);
        plant.setHashtags(tmpHashtags);

         libraryRepository.saveAndFlush(plant);

        return plant.getHashtags().contains(newHashtag);
    }

    /**
     * Method to Remove a hashtag to database.
     * Functionality to find plant, potential douplicate hashtag to remove is also here.
     *
     * @param userId
     * @param libraryId
     * @param hashtagToRemove
     * @return success (boolean)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeHashtag(Long userId, Long libraryId, String hashtagToRemove) {
        Optional<UserPlantLibrary> tempPlant = libraryRepository.findById(libraryId);
        if (tempPlant.isEmpty()) {
            return false;
        }

        UserPlantLibrary plant = tempPlant.get();
        List<String> existingHashtags = new ArrayList<>(plant.getHashtags());

        if (!existingHashtags.contains(hashtagToRemove)) {
            return false;
        }

        existingHashtags.remove(hashtagToRemove);
        plant.setHashtags(existingHashtags);

        libraryRepository.saveAndFlush(plant);

         return !plant.getHashtags().contains(hashtagToRemove);
    }

    /**
     * Method to Read hashtags from database for a particular plant.
     * Functionality to find plant with all hashtags are also located here.
     *
     * @param userId
     * @param libraryId
     * @return success (boolean)
     */
    @Transactional(readOnly = true)
    public List<String> readHashtags(Long userId, Long libraryId) {
        entityManager.clear();
        return libraryRepository.findHashtagsForUserPlant(libraryId);
    }

}
