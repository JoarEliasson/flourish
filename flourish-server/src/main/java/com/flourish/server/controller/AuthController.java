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
 * REST controller for handling authentication-related requests.
 * <p>
 * Provides endpoints to authenticate users. Uses the {@link UserService} to
 * validate user credentials.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    /**
     * Constructs an {@code AuthController} with the specified {@code UserService}.
     *
     * @param userService the service responsible for user authentication
     */
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles a login request.
     * <p>
     * Expects {@code username} and {@code password} as request parameters and attempts
     * to authenticate the user.
     * </p>
     *
     * @param username the username provided by the client
     * @param password the raw password provided by the client
     * @return a {@link ResponseEntity} containing the authenticated {@link User} if successful,
     * or an error message if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        try {
            User user = userService.authenticate(username, password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
