package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.flourish.service.PasswordResetService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

/**
 * Unit tests for the {@link ResetPasswordView} class.
 * This class contains test cases for the functionality related to the reset password view,
 * including verifying that required input fields are present, handling errors for empty or mismatched fields,
 * and ensuring correct behavior when a valid or invalid password reset token is provided.
 *
 * @author Zahraa Alqassab
 * @version 1.3.0
 * @since 2025-03-01
 */
class ResetPasswordViewTest {

    private ResetPasswordView resetPasswordView;
    private PasswordResetService passwordResetService;

    /**
     * Sets up a new instance of {@link ResetPasswordView} with a mocked {@link PasswordResetService}
     * before each test, and clears the SecurityContext.
     */
    @BeforeEach
    void setUp() {
        passwordResetService = mock(PasswordResetService.class);
        resetPasswordView = new ResetPasswordView(passwordResetService);
        // Clear any existing authentication before each test
        SecurityContextHolder.clearContext();
    }
    /**
     * Test #1: Verifies that the ResetPasswordView contains the required input fields.
     * <p>
     * The test checks that the view includes:
     * <ul>
     *   <li>A token {@link TextField} for the reset token.</li>
     *   <li>A {@link PasswordField} for the new password.</li>
     *   <li>A {@link PasswordField} for confirming the new password.</li>
     * </ul>
     * </p>
     */
    @Test
    void testInputFieldsExist() {
        TextField tokenField = resetPasswordView.getTokenField();
        PasswordField newPasswordField1 = resetPasswordView.getNewPasswordField1();
        PasswordField newPasswordField2 = resetPasswordView.getNewPasswordField2();

        assertNotNull(tokenField, "Token field should not be null");
        assertNotNull(newPasswordField1, "New password field 1 should not be null");
        assertNotNull(newPasswordField2, "New password field 2 should not be null");
    }

    /**
     * Test #2: Verifies that a notification is shown when required fields are empty.
     * <p>
     * The test simulates an attempt to reset the password with empty token and password fields,
     * and verifies that the appropriate error notification is displayed.
     * </p>
     */
    @Test
    void testResetPasswordWithEmptyFields() {
        resetPasswordView.getTokenField().setValue("");
        resetPasswordView.getNewPasswordField1().setValue("");
        resetPasswordView.getNewPasswordField2().setValue("");

        try (var notificationMock = mockStatic(Notification.class)) {
            resetPasswordView.triggerResetPassword();
            notificationMock.verify(() ->
                    Notification.show("Please fill in all the required fields."), times(1));
        }
    }

    /**
     * test #3:Verifies that a notification is shown when the new passwords do not match.
     * <p>
     * The test sets different values for the new password fields and ensures that a notification
     * is displayed indicating that the passwords do not match.
     * </p>
     */
    @Test
    void testResetPasswordWithNonMatchingPasswords() {
        resetPasswordView.getTokenField().setValue("sometoken");
        resetPasswordView.getNewPasswordField1().setValue("password1");
        resetPasswordView.getNewPasswordField2().setValue("password2");

        try (var notificationMock = mockStatic(Notification.class)) {
            resetPasswordView.triggerResetPassword();
            notificationMock.verify(() ->
                    Notification.show("Passwords do not match."), times(1));
        }
    }

    /**
     * Test #4: Verifies that an invalid or expired token results in an error notification.
     * <p>
     * The test simulates an invalid token scenario by stubbing the resetPassword method to return false,
     * and verifies that the appropriate error notification is displayed.
     * </p>
     */
    @Test
    void testResetPasswordWithInvalidToken() {
        resetPasswordView.getTokenField().setValue("validtoken");
        resetPasswordView.getNewPasswordField1().setValue("newpassword");
        resetPasswordView.getNewPasswordField2().setValue("newpassword");

        when(passwordResetService.resetPassword("validtoken", "newpassword")).thenReturn(false);

        try (var notificationMock = mockStatic(Notification.class)) {
            resetPasswordView.triggerResetPassword();
            notificationMock.verify(() ->
                    Notification.show("Invalid or expired token."), times(1));
        }
    }

    /**
     * Test #5:Verifies that a successful password reset triggers the correct actions.
     * <p>
     * The test simulates a successful password reset by stubbing the resetPassword method to return true
     * and the validateToken method to return an Optional containing the user's email. It verifies that:
     * <ul>
     *   <li>A success notification is displayed.</li>
     *   <li>An authentication token is set in the SecurityContext.</li>
     * </ul>
     * </p>
     */
    @Test
    void testResetPasswordSuccess() {
        resetPasswordView.getTokenField().setValue("validtoken");
        resetPasswordView.getNewPasswordField1().setValue("newpassword");
        resetPasswordView.getNewPasswordField2().setValue("newpassword");

        when(passwordResetService.resetPassword("validtoken", "newpassword")).thenReturn(true);
        when(passwordResetService.validateToken("validtoken")).thenReturn(Optional.of("user@example.com"));

        try (var notificationMock = mockStatic(Notification.class)) {
            resetPasswordView.triggerResetPassword();
            notificationMock.verify(() ->
                    Notification.show("Password reset successful! You are now logged in."), times(1));
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should be set after successful reset");
        assertEquals("user@example.com", auth.getPrincipal(),
                "Authenticated principal should be the user's email");
    }
}
