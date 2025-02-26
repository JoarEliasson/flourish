package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

/**
 * Unit tests for {@link com.flourish.service.UserServiceImpl}.
 *
 * <p>Demonstrates Mockito-based verification of:
 * <ul>
 *   <li>Password encoding before user creation</li>
 *   <li>Lookups by email and reset token</li>
 *   <li>User updates</li>
 * </ul>
 * </p>
 *
 * <p>Uses {@link @ActiveProfiles("test")} to ensure test-specific properties,
 * though no database or SpringBootTest context is required here.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class UserServiceImplTest {

    private UserRepository userRepository;
    private UserSettingsRepository userSettingsRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    /**
     * Sets up the mocks and the UserService.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userSettingsRepository = mock(UserSettingsRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, userSettingsRepository, passwordEncoder);
    }

    /**
     * Tests that createUser encrypts the password and saves the user.
     */
    @Test
    void testCreateUser_EncryptsPasswordAndSaves() {
        User inputUser = new User("John", "Doe", "john.doe@example.com", "plainpass", "USER");
        when(passwordEncoder.encode("plainpass")).thenReturn("encryptedPass");

        User savedUser = new User("John", "Doe", "john.doe@example.com", "encryptedPass", "USER");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(inputUser);

        verify(passwordEncoder).encode("plainpass");
        verify(userRepository).save(any(User.class));

        assertNotNull(result.getId());
        assertEquals("encryptedPass", result.getPassword());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    /**
     * Tests that findByEmail returns an Optional with a user if found.
     */
    @Test
    void testFindByEmail_ReturnsUserIfExists() {
        User existingUser = new User("Jane", "Doe", "jane.doe@example.com", "secret", "USER");
        existingUser.setId(2L);

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(existingUser);

        Optional<User> result = userService.findByEmail("jane.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals("jane.doe@example.com", result.get().getEmail());
    }

    /**
     * Tests that findByEmail returns an empty Optional if no user is found.
     */
    @Test
    void testFindByEmail_ReturnsEmptyIfNotExists() {
        when(userRepository.findByEmail("non.existent@example.com")).thenReturn(null);
        Optional<User> result = userService.findByEmail("non.existent@example.com");
        assertFalse(result.isPresent());
    }

    /**
     * Tests that updateUser saves the user entity.
     */
    @Test
    void testUpdateUser_SavesUser() {
        User userToUpdate = new User("Jake", "Doe", "jake.doe@example.com", "oldpass", "USER");
        userToUpdate.setId(3L);

        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User updated = userService.updateUser(userToUpdate);

        verify(userRepository).save(userToUpdate);
        assertEquals("jake.doe@example.com", updated.getEmail());
    }

    /**
     * Tests that findByResetToken returns a user if the token matches.
     */
    @Test
    void testFindByResetToken_ReturnsUserIfMatches() {
        User userWithToken = new User("Alice", "Smith", "alice@example.com", "password", "USER");
        userWithToken.setId(4L);
        userWithToken.setResetToken("reset-1234");

        when(userRepository.findByResetToken("reset-1234")).thenReturn(userWithToken);

        Optional<User> result = userService.findByResetToken("reset-1234");

        assertTrue(result.isPresent());
        assertEquals("reset-1234", result.get().getResetToken());
    }

    /**
     * Tests that findByResetToken returns empty Optional if no user has that token.
     */
    @Test
    void testFindByResetToken_ReturnsEmptyIfNoMatch() {
        when(userRepository.findByResetToken("invalid-token")).thenReturn(null);

        Optional<User> result = userService.findByResetToken("invalid-token");
        assertFalse(result.isPresent());
    }
}
