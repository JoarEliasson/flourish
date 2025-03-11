package com.flourish.security;

import com.flourish.domain.LibraryEntry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

/**
 * A session-scoped bean for holding additional user data after successful login.
 *
 * <p>This bean stores minimal but frequently used user information (such as user ID,
 * username, and language preference) so that other parts of the application (for example,
 * the UI for user settings or plant library) can easily access it without querying the database repeatedly.
 * Only non-sensitive and essential data is stored here.</p>
 *
 * @author
 *   Your Name
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@SessionScope
public class UserSessionData {

    private Long userId;
    private String username;
    private List<LibraryEntry> plantLibraryEntries;

    /**
     * Returns the user ID.
     *
     * @return the user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Returns the username.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's plant library entries.
     *
     * @return the user's plant library entries.
     */
    public List<LibraryEntry> getPlantLibraryEntries() {
        return plantLibraryEntries;
    }

    /**
     * Sets the user's plant library entries.
     *
     * @param plantLibraryEntries the user's plant library entries to set.
     */
    public void setPlantLibraryEntries(List<LibraryEntry> plantLibraryEntries) {
        this.plantLibraryEntries = plantLibraryEntries;
    }

    /**
     * Returns a specific plant library entry by its ID.
     *
     * <p>This method searches through the user's plant library entries
     * and returns the entry that matches the given ID.</p>
     *
     * @param id the ID of the plant library entry to retrieve.
     * @return the matching LibraryEntry, or null if not found.
     */
    public LibraryEntry getPlantLibraryEntryById(Long id) {
        if (plantLibraryEntries != null) {
            for (LibraryEntry entry : plantLibraryEntries) {
                if (entry.getLibraryId().equals(id)) {
                    return entry;
                }
            }
        }
        return null;
    }
}

