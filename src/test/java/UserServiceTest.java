import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.repository.UserSettingsRepository;
import com.flourish.service.UserService;
import com.flourish.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepositoryMock;
    private UserSettingsRepository userSettingsRepositoryMock;
    private PasswordEncoder passwordEncoderMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        passwordEncoderMock = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepositoryMock, userSettingsRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testFindByEmail_ShouldReturnUser_IfExists() {
        // Given
        User existingUser = new User("Jane", "Doe", "jane.doe@example.com", "secret", "USER");
        existingUser.setId(2L);
        when(userRepositoryMock.findByEmail("jane.doe@example.com")).thenReturn(existingUser);

        // When
        Optional<User> result = userService.findByEmail("jane.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("jane.doe@example.com", result.get().getEmail());
    }

}
