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
 * @version 1.1.0
 * @since 2025-02-24
 */
class ResetPasswordViewTest {

    private ResetPasswordView resetPasswordView;
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = mock(PasswordResetService.class);
        resetPasswordView = new ResetPasswordView(passwordResetService);
        // Clear any existing authentication before each test
        SecurityContextHolder.clearContext();
    }
    /**
     * Verifies that the reset password view contains the required input fields.
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
     * Verifies that a notification is shown when any of the required fields are empty.
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
     * Verifies that a notification is shown when the new passwords do not match.
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
     * Verifies that an invalid token leads to a notification indicating failure.
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
     * Verifies that a successful password reset triggers the appropriate logic:
     * updating the password, setting authentication, and displaying a success notification.
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
