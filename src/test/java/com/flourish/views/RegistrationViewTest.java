package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link RegistrationView} class.
 * This class contains various test cases to verify the functionality of the Registration view, including the presence of required fields,
 * handling empty fields during registration, registering a user when all fields are filled, and handling registration failure scenarios.
 *
 * The tests use mocks to simulate dependencies and behavior, such as user registration and notifications.
 * @author Zahraa Alqassab
 * @version 1.1.
 * @since 2025-02-24
 */
@ExtendWith(MockitoExtension.class)
class RegistrationViewTest extends UIUnitTest {

    /**
     * The view being tested: {@link RegistrationView}.
     */
    @InjectMocks
    private RegistrationView registrationView;

    /**
     * Mocked {@link UserService} to simulate user registration behavior.
     */
    private UserService userService;

    /**
     * Mocked static methods for {@link Notification}.
     */
    private MockedStatic<Notification> mockedNotification;

    /**
     * Mocked static methods for {@link UI}.
     */
    private MockedStatic<UI> mockedUI;

    /**
     * Sets up the test environment by initializing the mock dependencies and creating the {@link RegistrationView} instance.
     * This method is executed before each test.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class); // Create a mocked UserService
        registrationView = new RegistrationView(userService);

        // Initialize the static mocks for Notification and UI
        mockedNotification = Mockito.mockStatic(Notification.class);
        mockedUI = Mockito.mockStatic(UI.class);
    }

    /**
     * Cleans up after each test by closing the static mocks.
     * This method is executed after each test.
     */
    @AfterEach
    void tearDown() {
        // Deregister the static mocks after each test
        mockedNotification.close();
        mockedUI.close();
    }

    /**
     * Verifies that the {@link RegistrationView} is created successfully.
     * This test checks that the RegistrationView object is not null after initialization.
     */
    @Test
    void shouldCreateRegistrationView() {
        assertNotNull(registrationView);
    }

    /**
     * Verifies that an error message is displayed if the required fields are empty during registration.
     * This test ensures that a notification is shown with the message "Please fill all fields."
     */
    @Test
    void shouldShowErrorIfFieldsAreEmpty() {
        Notification notification = mock(Notification.class);
        mockedNotification.when(() -> Notification.show(anyString())).thenReturn(notification);

        // Set values in the form to trigger the error
        registrationView.getFirstNameField().setValue("");
        registrationView.getLastNameField().setValue("");
        registrationView.getEmailField().setValue("");
        registrationView.getPasswordField().setValue("");

        // Trigger registration and verify Notification.show() was called
        registrationView.triggerRegistration();
        mockedNotification.verify(() -> Notification.show("Please fill all fields."), times(1));
    }

    /**
     * Verifies that the user is registered when all fields are filled.
     * This test checks that the {@link UserService} is called to create the user.
     */
    @Test
    void shouldRegisterUserWhenFieldsAreFilled() {
        UI ui = mock(UI.class);
        mockedUI.when(UI::getCurrent).thenReturn(ui);

        registrationView.getFirstNameField().setValue("John");
        registrationView.getLastNameField().setValue("Doe");
        registrationView.getEmailField().setValue("john.doe@example.com");
        registrationView.getPasswordField().setValue("securepassword");
        registrationView.triggerRegistration();

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Verifies that an error message is displayed when registration fails due to an exception.
     * This test ensures that a notification is shown with the message "Registration failed: Database error"
     * when an exception occurs during user creation.
     */
    @Test
    void shouldShowErrorOnRegistrationFailure() {
        UI ui = mock(UI.class);
        mockedUI.when(UI::getCurrent).thenReturn(ui);

        doThrow(new RuntimeException("Database error"))
                .when(userService).createUser(any(User.class));

        Notification notification = mock(Notification.class);
        mockedNotification.when(() -> Notification.show(anyString())).thenReturn(notification);

        registrationView.getFirstNameField().setValue("John");
        registrationView.getLastNameField().setValue("Doe");
        registrationView.getEmailField().setValue("john.doe@example.com");
        registrationView.getPasswordField().setValue("securepassword");

        registrationView.triggerRegistration();

        mockedNotification.verify(() -> Notification.show("Registration failed: Database error"), times(1));
    }

    /**
     * Verifies that the {@link RegistrationView} contains all the required input fields for registration.
     * This test ensures that the view includes fields for the first name, email, and password.
     */
    @Test
    void shouldContainAllInputFields() {
        FormLayout formLayout = (FormLayout) registrationView.getChildren()
                .filter(component -> component instanceof FormLayout)
                .findFirst()
                .orElseThrow(() -> new AssertionError("FormLayout not found"));

        formLayout.getChildren().forEach(component ->
                System.out.println("Component class: " + component.getClass().getName()));

        long firstNameFields = formLayout.getChildren()
                .filter(component -> component instanceof TextField)
                .filter(component -> ((TextField) component).getLabel().equals("First Name"))
                .count();
        assertEquals(1, firstNameFields, "Should contain first name field. Found: " + firstNameFields);

        long emailFields = formLayout.getChildren()
                .filter(component -> component instanceof EmailField)
                .count();
        assertEquals(1, emailFields, "Should contain email field. Found: " + emailFields);

        long passwordFields = formLayout.getChildren()
                .filter(component -> component instanceof PasswordField)
                .count();
        assertEquals(1, passwordFields, "Should contain password field. Found: " + passwordFields);
    }
}
