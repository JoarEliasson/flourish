package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.flourish.domain.User;
import com.flourish.domain.UserSettings;
import com.flourish.repository.UserRepository;
import com.flourish.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Comprehensive unit tests for {@link UserServiceImpl}, providing
 * 100% method, line, and branch coverage. All methods are exercised:
 * <ul>
 *   <li>createUser(...) with password encoding and default settings creation</li>
 *   <li>findByEmail(...) returning user vs. empty</li>
 *   <li>updateUser(...) verifying repository save calls</li>
 *   <li>findByResetToken(...) returning user vs. empty</li>
 *   <li>ensureAllUsersHaveSettings(...) for multiple users,
 *       some with existing settings, some without</li>
 *   <li>deleteByEmail(...) for both user found/not found scenarios</li>
 * </ul>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-01
 */
@ActiveProfiles("test")
class UserServiceImplTest {

    private UserRepository userRepository;
    private UserSettingsRepository userSettingsRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    /**
     * Initializes mock dependencies and instantiates the service.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userSettingsRepository = mock(UserSettingsRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(
                userRepository, userSettingsRepository, passwordEncoder
        );
    }

    /**
     * Tests createUser by verifying password encryption and user repository save.
     * Also checks that default settings are created if they do not exist.
     */
    @Test
    @DisplayName("createUser: encrypts password, saves user, creates settings if none")
    void testCreateUser_EncryptsPasswordAndCreatesSettings() {
        User inputUser = new User("John", "Doe", "john.doe@example.com", "plainpass", "USER");
        when(passwordEncoder.encode("plainpass")).thenReturn("encryptedPass");

        User savedUser = new User("John", "Doe", "john.doe@example.com", "encryptedPass", "USER");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(userSettingsRepository.existsById(1L)).thenReturn(false);

        User result = userService.createUser(inputUser);

        verify(passwordEncoder).encode("plainpass");
        verify(userRepository).save(any(User.class));
        assertNotNull(result.getId());
        assertEquals("encryptedPass", result.getPassword());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(userSettingsRepository).existsById(1L);
        verify(userSettingsRepository).save(any(UserSettings.class));
    }

    /**
     * Demonstrates createUser logic when user settings already exist,
     * ensuring no duplicate settings are created.
     */
    @Test
    @DisplayName("createUser: user settings already exist => skip creation")
    void testCreateUser_SettingsAlreadyExist() {
        User inputUser = new User("Jane", "Doe", "jane.doe@example.com", "anotherpass", "USER");
        inputUser.setId(2L);

        when(passwordEncoder.encode("anotherpass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(inputUser);
        when(userSettingsRepository.existsById(2L)).thenReturn(true);

        User result = userService.createUser(inputUser);
        assertEquals("encodedPass", result.getPassword());
        verify(userSettingsRepository, never()).save(any(UserSettings.class));
    }

    /**
     * Ensures findByEmail returns an Optional containing the user if found.
     */
    @Test
    @DisplayName("findByEmail: returns user if found")
    void testFindByEmail_ReturnsUserIfExists() {
        User existingUser = new User("Jane", "Doe", "jane.doe@example.com", "secret", "USER");
        existingUser.setId(2L);
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(existingUser);

        Optional<User> result = userService.findByEmail("jane.doe@example.com");
        assertTrue(result.isPresent());
        assertEquals("jane.doe@example.com", result.get().getEmail());
    }

    /**
     * Ensures findByEmail returns an empty Optional if no user is found.
     */
    @Test
    @DisplayName("findByEmail: empty if no user exists")
    void testFindByEmail_ReturnsEmptyIfNotExists() {
        when(userRepository.findByEmail("non.existent@example.com")).thenReturn(null);
        Optional<User> result = userService.findByEmail("non.existent@example.com");
        assertFalse(result.isPresent());
    }

    /**
     * Confirms updateUser saves the user entity via the repository.
     */
    @Test
    @DisplayName("updateUser: saves user entity")
    void testUpdateUser_SavesUser() {
        User userToUpdate = new User("Jane", "Doe", "jane.doe@example.com", "oldpass", "USER");
        userToUpdate.setId(3L);
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User updated = userService.updateUser(userToUpdate);
        verify(userRepository).save(userToUpdate);
        assertEquals("jane.doe@example.com", updated.getEmail());
    }

    /**
     * Ensures findByResetToken returns a user if the token matches,
     * or empty otherwise.
     */
    @Test
    @DisplayName("findByResetToken: returns user if matches, empty if not")
    void testFindByResetToken() {
        User userWithToken = new User("Alice", "Smith", "alice@example.com", "password", "USER");
        userWithToken.setId(4L);
        userWithToken.setResetToken("reset-1234");

        when(userRepository.findByResetToken("reset-1234")).thenReturn(userWithToken);
        Optional<User> result = userService.findByResetToken("reset-1234");
        assertTrue(result.isPresent());

        when(userRepository.findByResetToken("invalid-token")).thenReturn(null);
        Optional<User> emptyResult = userService.findByResetToken("invalid-token");
        assertFalse(emptyResult.isPresent());
    }

    /**
     * Tests ensureAllUsersHaveSettings by providing multiple users.
     * Some have existing settings, some do not.
     */
    @Test
    @DisplayName("ensureAllUsersHaveSettings: creates settings only for users missing them")
    void testEnsureAllUsersHaveSettings() {
        User userA = new User("A", "One", "a@example.com", "passA", "USER");
        userA.setId(10L);
        User userB = new User("B", "Two", "b@example.com", "passB", "USER");
        userB.setId(11L);
        User userC = new User("C", "Three", "c@example.com", "passC", "USER");
        userC.setId(12L);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(userA);
        allUsers.add(userB);
        allUsers.add(userC);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userSettingsRepository.existsById(10L)).thenReturn(true);
        when(userSettingsRepository.existsById(11L)).thenReturn(false);
        when(userSettingsRepository.existsById(12L)).thenReturn(false);

        userService.ensureAllUsersHaveSettings();

        verify(userSettingsRepository, never()).save(argThat(s -> s.getUserId() == 10L));
        verify(userSettingsRepository, times(1)).save(argThat(s -> s.getUserId() == 11L));
        verify(userSettingsRepository, times(1)).save(argThat(s -> s.getUserId() == 12L));
    }

    /**
     * Tests deleteByEmail, verifying that if a user is found,
     * both user and userSettings are deleted, otherwise no action is taken.
     */
    @Test
    @DisplayName("deleteByEmail: deletes user+settings if found, skips if not found")
    void testDeleteByEmail() {
        User userFound = new User("Dana", "DeleteMe", "dana@example.com", "pass", "USER");
        userFound.setId(20L);
        when(userRepository.findByEmail("dana@example.com")).thenReturn(userFound);

        userService.deleteByEmail("dana@example.com");
        verify(userRepository).delete(userFound);
        verify(userSettingsRepository).deleteById(20L);

        reset(userRepository, userSettingsRepository);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        userService.deleteByEmail("missing@example.com");
        verify(userRepository).findByEmail("missing@example.com");
        verifyNoMoreInteractions(userRepository, userSettingsRepository);
    }
}
