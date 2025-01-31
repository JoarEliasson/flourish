package se.myhappyplants.client.model;

import se.myhappyplants.shared.User;

/**
 * Singleton class that keeps track of the current user
 * that is logged in to the application
 * Created by: Christopher O'Driscoll
 * Updated by: Christopher, 2021-04-13
 */
public final class LoggedInUser {

    private final static LoggedInUser INSTANCE = new LoggedInUser();
    private User user;

    /**
     * Private constructor to get only one instance of the class
     */
    private LoggedInUser() {
    }

    /**
     * Getter method to get the only instance of the class
     *
     * @return
     */
    public static LoggedInUser getInstance() {
        return INSTANCE;
    }

    /**
     * Getter method to get the current user
     *
     * @return
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Setter method to set the current user
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }
}

