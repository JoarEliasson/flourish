package com.flourish.service;

import com.flourish.domain.LibraryEntry;
import com.flourish.domain.PlantDetails;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for monitoring user plant library entries and sending notifications
 * when plant care (e.g., watering or sunlight requirements) needs attention.
 * <p>
 * This service utilizes the {@code UserPlantLibraryService} to retrieve user plant entries
 * and periodically checks if a plant needs watering or if the plant is not receiving
 * sufficient sunlight.
 * </p>
 *
 * @author Zahraa Alqassab
 * @since 2025-03-11
 */
@Service
public class PlantNotificationService {
    private final UserPlantLibraryService userPlantLibraryService;

    /**
     * Constructs a new {@code PlantNotificationService} with the specified
     * {@code UserPlantLibraryService} dependency.
     *
     * @param userPlantLibraryService the service used to access user plant library data.
     */
    public PlantNotificationService(UserPlantLibraryService userPlantLibraryService) {
        this.userPlantLibraryService = userPlantLibraryService;
    }

    /**
     * Periodically sends notifications to users regarding the condition of their plants.
     * <p>
     * This method is scheduled to run at a fixed rate of one hour (3600000 milliseconds).
     * It iterates over all users, checks each plant entry for watering levels and sunlight conditions,
     * and sends a notification if the plant needs attention.
     * </p>
     */
    @Scheduled(fixedRate = 3600000)
    public void sendNotifications() {
        List<Long> userIds = getAllUserIds();
        for (Long userId : userIds) {
            List<LibraryEntry> entries = userPlantLibraryService.getAllLibraryEntriesForUser(userId);
            for (LibraryEntry entry : entries) {
                Optional<Double> gaugeOpt = userPlantLibraryService.getWateringGaugePercentage(entry.getLibraryId());
                if (gaugeOpt.isPresent() && gaugeOpt.get() < 20.0) {
                    sendUserNotification(userId, "Your plant '" + entry.getPlantDetails().getCommonName() +
                            "' needs watering soon. Watering gauge: " + gaugeOpt.get() + "%");
                }

                String sunlightRequirement = entry.getPlantDetails().getSunlight();
                String currentSunlight = getCurrentSunlightConditionForUserPlant(userId, entry);
                if (!isSunlightSufficient(sunlightRequirement, currentSunlight)) {
                    sendUserNotification(userId, "Your plant '" + entry.getPlantDetails().getCommonName() +
                            "' may not be receiving sufficient sunlight. Required: " + sunlightRequirement +
                            ", current: " + currentSunlight);
                }
            }
        }
    }

    /**
     * Retrieves a list of user IDs for which notifications should be sent.
     * <p>
     * <strong>Note:</strong> This implementation returns a static list of IDs.
     * </p>
     *
     * @return a list of user IDs.
     */
    private List<Long> getAllUserIds() {
        return List.of(1L, 2L, 3L);
    }

    /**
     * Retrieves the current sunlight condition for the specified user's plant entry.
     * <p>
     * <strong>Note:</strong> This is a placeholder implementation that always returns "partial".
     * </p>
     *
     * @param userId the ID of the user.
     * @param entry  the library entry representing the user's plant.
     * @return the current sunlight condition (e.g., "full", "partial", etc.).
     */
    private String getCurrentSunlightConditionForUserPlant(Long userId, LibraryEntry entry) {
        return "partial";
    }

    /**
     * Determines if the current sunlight condition is sufficient compared to the required condition.
     *
     * @param required the required sunlight condition for the plant.
     * @param current  the current sunlight condition.
     * @return {@code true} if the current condition matches the required condition (ignoring case), {@code false} otherwise.
     */
    private boolean isSunlightSufficient(String required, String current) {
        return required.equalsIgnoreCase(current);
    }

    /**
     * Sends a notification message to the specified user.
     * <p>
     * <strong>Note:</strong> This method currently implements notification by printing the message to the console.
     * </p>
     *
     * @param userId  the ID of the user to notify.
     * @param message the notification message.
     */
    private void sendUserNotification(Long userId, String message) {
        System.out.println("Notifying user " + userId + ": " + message);
    }

    /**
     * Generates a list of notification messages for the specified user based on watering gauge levels.
     * <p>
     * For each plant in the user's library, if the watering gauge percentage is below 20%,
     * a notification message is generated.
     * </p>
     *
     * @param userId the ID of the user.
     * @return a list of notification messages.
     */
    public List<String> generateNotificationsForUser(Long userId) {
        List<String> notifications = new ArrayList<>();
        List<LibraryEntry> libraryEntries = userPlantLibraryService.getAllLibraryEntriesForUser(userId);

        for (LibraryEntry entry : libraryEntries) {
            Optional<Double> gaugeOpt = userPlantLibraryService.getWateringGaugePercentage(entry.getLibraryId());
            if (gaugeOpt.isPresent() && gaugeOpt.get() < 20.0) {
                PlantDetails details = entry.getPlantDetails();
                notifications.add("Your plant '" + details.getCommonName() +
                        "' needs watering soon. (" + String.format("%.0f", gaugeOpt.get()) + "% remaining)");
            }
        }
        return notifications;
    }
}

