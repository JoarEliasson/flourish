package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
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
import com.vaadin.flow.component.button.Button;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI unit tests for {@link RegistrationView}.
 * <p>
 * This class verifies the component hierarchy and registration logic of
 * the RegistrationView without launching a full server. The tests inspect
 * the view’s children to ensure:
 * <ul>
 *   <li>The header displays "Register New Account".</li>
 *   <li>A {@link FormLayout} exists containing the first name, last name, email, and password fields, and a register {@link Button} with text "Register".</li>
 *   <li>Registration fails when fields are empty (i.e. {@code UserService#createUser} is not called).</li>
 *   <li>Registration succeeds when all fields are filled (i.e. {@code UserService#createUser} is called exactly once).</li>
 * </ul>
 * </p>
 *
 * @author
 *   Zahraa Alqassab
 * @version
 *   1.1.0
 * @since
 *   2025-02-27
 */
class RegistrationViewTest extends UIUnitTest {
    private RegistrationView registrationView;
    private UserService userService;

    /**
     * Initializes a fresh instance of {@link RegistrationView} with a mocked {@link UserService}
     * before each test.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        registrationView = new RegistrationView(userService);
    }
    /**
     * Test #1: Verify that the header exists and displays "Register New Account".
     * <p>
     * This test traverses the top-level children of the {@link RegistrationView} to find an {@link H2}
     * component and asserts that its text is "Register New Account".
     * </p>
     */
    @Test
    void testHeaderExists() {
        List<Component> children = registrationView.getChildren().collect(Collectors.toList());
        H2 header = children.stream()
                .filter(component -> component instanceof H2)
                .map(component -> (H2) component)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Header not found in RegistrationView."));
        assertEquals("Register New Account", header.getText(), "Expected header text to be 'Register New Account'.");
    }

    /**
     * Test #2: Verify that the form layout contains the expected input fields and register button.
     * <p>
     * This test locates the {@link FormLayout} within the {@link RegistrationView} and ensures that it contains:
     * <ul>
     *   <li>A first name {@link TextField} labeled "First Name".</li>
     *   <li>A last name {@link TextField} labeled "Last Name".</li>
     *   <li>An {@link EmailField} labeled "Email".</li>
     *   <li>A {@link PasswordField} labeled "Password".</li>
     *   <li>A {@link Button} with the text "Register".</li>
     * </ul>
     * </p>
     */
    @Test
    void testFormLayoutContainsFieldsAndButton() {
        List<Component> children = registrationView.getChildren().collect(Collectors.toList());
        FormLayout formLayout = children.stream()
                .filter(component -> component instanceof FormLayout)
                .map(component -> (FormLayout) component)
                .findFirst()
                .orElseThrow(() -> new AssertionError("FormLayout not found in RegistrationView."));

        List<Component> formChildren = formLayout.getChildren().collect(Collectors.toList());

        boolean hasFirstName = formChildren.stream()
                .anyMatch(comp -> comp instanceof TextField &&
                        "First Name".equals(((TextField) comp).getLabel()));
        boolean hasLastName = formChildren.stream()
                .anyMatch(comp -> comp instanceof TextField &&
                        "Last Name".equals(((TextField) comp).getLabel()));
        boolean hasEmail = formChildren.stream()
                .anyMatch(comp -> comp instanceof EmailField &&
                        "Email".equals(((EmailField) comp).getLabel()));
        boolean hasPassword = formChildren.stream()
                .anyMatch(comp -> comp instanceof PasswordField &&
                        "Password".equals(((PasswordField) comp).getLabel()));
        boolean hasRegisterButton = formChildren.stream()
                .anyMatch(comp -> comp instanceof Button &&
                        "Register".equals(((Button) comp).getText()));

        assertTrue(hasFirstName, "FormLayout should contain a 'First Name' field.");
        assertTrue(hasLastName, "FormLayout should contain a 'Last Name' field.");
        assertTrue(hasEmail, "FormLayout should contain an 'Email' field.");
        assertTrue(hasPassword, "FormLayout should contain a 'Password' field.");
        assertTrue(hasRegisterButton, "FormLayout should contain a 'Register' button.");
    }

    /**
     * Test #3: Verify that registration fails when one or more fields are empty.
     * <p>
     * This test ensures that if the input fields are empty, the registration logic does not
     * call {@code UserService#createUser}.
     * </p>
     */
    @Test
    void testRegistrationFailsIfFieldsEmpty() {
        // Set all fields to empty.
        registrationView.getFirstNameField().setValue("");
        registrationView.getLastNameField().setValue("");
        registrationView.getEmailField().setValue("");
        registrationView.getPasswordField().setValue("");

        // Trigger registration.
        registrationView.triggerRegistration();

        // Verify that no user is created.
        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test #4: Verify that registration succeeds when all fields are filled.
     * <p>
     * This test simulates a successful registration by filling in valid values for all fields.
     * It verifies that {@code UserService#createUser} is called exactly once.
     * </p>
     */
    @Test
    void testRegistrationSucceeds() {
        // Fill in valid values.
        registrationView.getFirstNameField().setValue("John");
        registrationView.getLastNameField().setValue("Doe");
        registrationView.getEmailField().setValue("john.doe@example.com");
        registrationView.getPasswordField().setValue("password123");

        User createdUser = new User("John", "Doe", "john.doe@example.com", "password123", "USER");
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        registrationView.triggerRegistration();

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test #5: Verifies that the registration form layout contains all required input fields.
     * <p>
     * This test checks that the {@link RegistrationView}'s form layout includes:
     * <ul>
     *   <li>Exactly one {@link TextField} labeled "First Name".</li>
     *   <li>Exactly one {@link EmailField}.</li>
     *   <li>Exactly one {@link PasswordField}.</li>
     * </ul>
     * Additionally, it prints the class names of all children components in the form layout for debugging purposes.
     * If any of the expected fields are missing or the count is not exactly one, the test will fail.
     * </p>
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
