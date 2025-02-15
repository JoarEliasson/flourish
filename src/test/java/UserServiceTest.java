import static org.junit.jupiter.api.Assertions.*;

import com.flourish.domain.User;
import com.flourish.domain.UserRepository;
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
    private PasswordEncoder passwordEncoderMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testCreateUser_ShouldEncryptPassword_AndReturnSavedUser() {
        // Given
        User inputUser = new User("John", "Doe", "john.doe@example.com", "plainpass", "USER");

        // Mock password encoding
        Mockito.when(passwordEncoderMock.encode("plainpass")).thenReturn("encodedpass");
        // Mock repository save
        User savedUser = new User("John", "Doe", "john.doe@example.com", "encodedpass", "USER");
        savedUser.setId(1L);

        Mockito.when(userRepositoryMock.save(Mockito.any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(inputUser);

        // Then
        assertNotNull(result.getId());
        assertEquals("encodedpass", result.getPassword());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testFindByEmail_ShouldReturnUser_IfExists() {
        // Given
        User existingUser = new User("Jane", "Doe", "jane.doe@example.com", "secret", "USER");
        existingUser.setId(2L);
        Mockito.when(userRepositoryMock.findByEmail("jane.doe@example.com")).thenReturn(existingUser);

        // When
        Optional<User> result = userService.findByEmail("jane.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("jane.doe@example.com", result.get().getEmail());
    }

}
