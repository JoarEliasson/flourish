import com.flourish.client.views.LoginView;
import com.flourish.domain.model.User;
import com.flourish.domain.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class LoginViewTest {

    private UserService userService;
    private LoginView loginView;

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        // Initialize the view with the mocked service
        loginView = new LoginView(userService);
        // Set a dummy UI for navigation; required for Vaadin components
        UI.setCurrent(new UI());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Given valid credentials
        String username = "testUser";
        String password = "securePassword";
        User user = new User(username, "test@example.com", "$2a$10$encrypted", true, false);
        when(userService.authenticate(username, password)).thenReturn(user);

        // Set the field values
        loginView.getUsernameField().setValue(username);
        loginView.getPasswordField().setValue(password);

        // When clicking the login button
        Button loginButton = loginView.getLoginButton();
        loginButton.click();

        // Then verify that authenticate was called
        verify(userService, times(1)).authenticate(username, password);
        // Additional assertions (e.g., navigation or notifications) can be added here.
        assert true;
    }

    @Test
    public void testLoginFailure() throws Exception {
        // Given invalid credentials
        String username = "testUser";
        String password = "wrongPassword";
        when(userService.authenticate(username, password)).thenThrow(new Exception("Invalid username or password"));

        loginView.getUsernameField().setValue(username);
        loginView.getPasswordField().setValue(password);
        loginView.getLoginButton().click();

        verify(userService, times(1)).authenticate(username, password);
        // Further tests can check for error notifications if the behaviour is exposed.
    }

}
