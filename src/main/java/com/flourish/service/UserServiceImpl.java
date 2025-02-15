package com.flourish.service;

import com.flourish.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo in-memory user service for authentication.
 * <p>
 * This example just uses a local map. In real apps, you'd connect to the DB,
 * or load from a repository.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> userStore = new HashMap<>();

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        userStore.put("test", passwordEncoder.encode("pass"));
    }

    @Override
    public User authenticate(String username, String password) throws Exception {
        String storedHash = userStore.get(username);
        if (storedHash != null && passwordEncoder.matches(password, storedHash)) {
            return new User(username, storedHash);
        }
        throw new Exception("Invalid username or password");
    }
}
