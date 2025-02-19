package com.flourish.server.controller;

import com.flourish.domain.model.User;
import com.flourish.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user registration requests.
 * <p>
 * Provides an endpoint to register a new user. The user's raw password is encrypted using BCrypt
 * before storage, and duplicate usernames or emails are rejected.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final UserService userService;

    /**
     * Constructs a RegistrationController with the specified UserService.
     *
     * @param userService the service responsible for user registration
     */
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user with the provided details.
     * <p>
     * Expects parameters for username, email, and password (as well as optional parameters for notification
     * and fun facts preferences). The raw password is encrypted prior to storage.
     * </p>
     *
     * @param username              the desired username
     * @param email                 the user's email address
     * @param password              the user's raw password
     * @param notificationActivated whether notifications are activated (default is false)
     * @param funFactsActivated     whether fun facts are activated (default is false)
     * @return a {@link ResponseEntity} containing the newly registered {@link User} if successful,
     * or an error message if registration fails
     */
    @PostMapping
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam(defaultValue = "false") Boolean notificationActivated,
                                      @RequestParam(defaultValue = "false") Boolean funFactsActivated) {
        try {
            User user = userService.register(username, email, password, notificationActivated, funFactsActivated);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
