package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.User;
import com.flourish.repository.PasswordResetTokenRepository;
import com.flourish.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Unit tests for {@link com.flourish.service.PasswordResetService}.
 *
 * <p>Ensures correct handling of:
 * <ul>
 *   <li>Token creation with expiration</li>
 *   <li>Token validation (missing, expired, valid)</li>
 *   <li>Password reset flow: encode new password, update user, delete token</li>
 * </ul>
 * </p>
 *
 * <p>Utilizes Mockito to mock repositories and the password encoder.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class PasswordResetServiceTest {

    private PasswordResetTokenRepository tokenRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordResetService passwordResetService;

    /**
     * Sets up the service with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        tokenRepository = mock(PasswordResetTokenRepository.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        passwordResetService = new PasswordResetService(tokenRepository, userRepository, passwordEncoder);
    }

    /**
     * Tests creating a password reset token and verifying it is saved.
     */
    @Test
    void testCreatePasswordResetToken_SavesToken() {
        String email = "user@example.com";
        int expirationMinutes = 30;

        PasswordResetToken savedToken = new PasswordResetToken(email, "fake-uuid", LocalDateTime.now().plusMinutes(expirationMinutes));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(savedToken);

        PasswordResetToken result = passwordResetService.createPasswordResetToken(email, expirationMinutes);

        verify(tokenRepository).save(any(PasswordResetToken.class));
        assertNotNull(result, "Expected a non-null token");
        assertEquals(email, result.getEmail(), "Email should match");
        assertNotNull(result.getToken(), "Token string should be generated");
        assertTrue(result.getExpiryDate().isAfter(LocalDateTime.now()),
                "Expiry date should be in the future");
    }

    /**
     * Tests validateToken returns empty if token not found in the repository.
     */
    @Test
    void testValidateToken_NotFound() {
        when(tokenRepository.findByToken("nonexistent")).thenReturn(null);

        Optional<String> result = passwordResetService.validateToken("nonexistent");

        assertTrue(result.isEmpty(), "Expected an empty Optional if token not found");
    }

    /**
     * Tests validateToken returns empty if the token has expired.
     */
    @Test
    void testValidateToken_Expired() {
        PasswordResetToken expiredToken = new PasswordResetToken(
                "user@example.com",
                "expired-token",
                LocalDateTime.now().minusMinutes(1)
        );
        when(tokenRepository.findByToken("expired-token")).thenReturn(expiredToken);

        Optional<String> result = passwordResetService.validateToken("expired-token");

        assertTrue(result.isEmpty(), "Expected an empty Optional if token is expired");
    }

    /**
     * Tests validateToken returns an email if the token is valid.
     */
    @Test
    void testValidateToken_Valid() {
        PasswordResetToken validToken = new PasswordResetToken(
                "user@example.com",
                "valid-token",
                LocalDateTime.now().plusMinutes(10)
        );
        when(tokenRepository.findByToken("valid-token")).thenReturn(validToken);

        Optional<String> result = passwordResetService.validateToken("valid-token");

        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get(), "Should return the associated email");
    }

    /**
     * Tests resetPassword returns false if validateToken fails.
     */
    @Test
    void testResetPassword_TokenValidationFails() {
        boolean resetResult = passwordResetService.resetPassword("invalid-token", "newPass");
        assertFalse(resetResult, "Expected reset to fail if token is invalid/absent");
    }

    /**
     * Tests resetPassword returns false if user is not found for the valid token's email.
     */
    @Test
    void testResetPassword_UserNotFound() {
        PasswordResetToken validToken = new PasswordResetToken(
                "user@example.com",
                "valid-token",
                LocalDateTime.now().plusMinutes(10)
        );
        when(tokenRepository.findByToken("valid-token")).thenReturn(validToken);
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);

        boolean resetResult = passwordResetService.resetPassword("valid-token", "newPass");

        assertFalse(resetResult, "Expected reset to fail if the user doesn't exist");
    }

    /**
     * Tests resetPassword updates the user's password if the token and user are valid,
     * and then deletes the token.
     */
    @Test
    void testResetPassword_Success() {
        PasswordResetToken validToken = new PasswordResetToken(
                "user@example.com",
                "valid-token",
                LocalDateTime.now().plusMinutes(10)
        );
        when(tokenRepository.findByToken("valid-token")).thenReturn(validToken);

        User existingUser = new User("John", "Doe", "user@example.com", "oldPass", "USER");
        existingUser.setId(1L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(existingUser);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        boolean resetResult = passwordResetService.resetPassword("valid-token", "newPass");

        assertTrue(resetResult, "Expected successful password reset");
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(existingUser);
        assertEquals("encodedPass", existingUser.getPassword(), "User password should be updated");

        verify(tokenRepository).delete(validToken);
    }
}
