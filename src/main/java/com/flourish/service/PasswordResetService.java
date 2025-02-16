package com.flourish.service;

import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.PasswordResetTokenRepository;
import com.flourish.domain.User;
import com.flourish.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides operations related to password-reset tokens,
 * including generation, validation, and applying new passwords.
 *
 * <p>Follows the SOLID principle by encapsulating all logic
 * for handling password-reset flow in one place.</p>
 *
 * <p>TDD approach: each public method can be tested for
 * correct behavior (token creation, expiry check, password update).</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new PasswordResetService.
     *
     * @param tokenRepository The repository for PasswordResetToken entities.
     * @param userRepository The repository for User entities.
     * @param passwordEncoder The password encoder for encrypting new passwords.
     */
    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a new password reset token for the given email,
     * and saves it in the database.
     *
     * @param email The user's email address.
     * @param expirationMinutes How many minutes until token expires.
     * @return The generated PasswordResetToken entity.
     */
    public PasswordResetToken createPasswordResetToken(String email, int expirationMinutes) {
        String token = UUID.randomUUID().toString(); // or any random generation strategy
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(expirationMinutes);

        PasswordResetToken resetToken = new PasswordResetToken(email, token, expiry);
        return tokenRepository.save(resetToken);
    }

    /**
     * Validates the given token. If valid and not expired, returns the associated email.
     *
     * @param token The token string.
     * @return An Optional containing the email if valid, otherwise empty.
     */
    public Optional<String> validateToken(String token) {
        PasswordResetToken prt = tokenRepository.findByToken(token);
        if (prt == null) {
            return Optional.empty();
        }
        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            // token expired
            return Optional.empty();
        }
        return Optional.of(prt.getEmail());
    }

    /**
     * Updates the user's password if the token is valid, then deletes the token.
     *
     * @param token The reset token string.
     * @param newPassword The new plain-text password to set.
     * @return true if the password was successfully updated; false otherwise.
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<String> maybeEmail = validateToken(token);
        if (maybeEmail.isEmpty()) {
            return false;
        }

        String email = maybeEmail.get();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        // Encrypt and save new password
        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);
        userRepository.save(user);

        // Optional: delete the token or mark it used
        PasswordResetToken prt = tokenRepository.findByToken(token);
        if (prt != null) {
            tokenRepository.delete(prt);
        }

        return true;
    }
}
