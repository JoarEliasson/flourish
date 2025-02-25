package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class ExampleUserServiceTest {

    private UserService userService;
    private UserRepository userRepositoryMock;
    private PasswordEncoder passwordEncoderMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        passwordEncoderMock = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testCreateUser_ShouldEncryptPassword_AndReturnSavedUser() {
        User inputUser = new User("John", "Doe", "john.doe@example.com", "plainpass", "USER");

        when(passwordEncoderMock.encode("plainpass")).thenReturn("encodedpass");
        User savedUser = new User("John", "Doe", "john.doe@example.com", "encodedpass", "USER");
        savedUser.setId(1L);

        when(userRepositoryMock.save(Mockito.any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(inputUser);

        assertNotNull(result.getId());
        assertEquals("encodedpass", result.getPassword());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testFindByEmail_ShouldReturnUser_IfExists() {
        User existingUser = new User("Jane", "Doe", "jane.doe@example.com", "secret", "USER");
        existingUser.setId(2L);
        when(userRepositoryMock.findByEmail("jane.doe@example.com")).thenReturn(existingUser);

        Optional<User> result = userService.findByEmail("jane.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals("jane.doe@example.com", result.get().getEmail());
    }

}
