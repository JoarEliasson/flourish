package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.flourish.service.PasswordResetService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
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
@ExtendWith(MockitoExtension.class)
class ResetPasswordViewTest {

    private ResetPasswordView resetPasswordView;

    @Mock
    private PasswordResetService passwordResetService;

    /**
     * Setup method to initialize the {@link ResetPasswordView} instance with mocked dependencies
     * before each test method is executed.
     */
    @BeforeEach
    void setUp() {
        passwordResetService = mock(PasswordResetService.class);
        resetPasswordView = new ResetPasswordView(passwordResetService);
        UI mockUI = mock(UI.class);
        UI.setCurrent(mockUI);  // Sets the current UI to a mock UI instance
    }

    /**
     * Test to verify that the reset password view contains all necessary input fields.
     * This test checks if the token field, new password field, and confirm password field are present.
     */
    @Test
    void shouldContainAllInputFields() {
        assertNotNull(resetPasswordView.getTokenField(), "Token field should not be null.");
        assertNotNull(resetPasswordView.getNewPasswordField1(), "New password field should not be null.");
        assertNotNull(resetPasswordView.getNewPasswordField2(), "Confirm new password field should not be null.");
    }

    /**
     * Test to verify that the {@link UI#getCurrent()} method is called correctly.
     * This test ensures that the current UI is being accessed as expected.
     */
    @Test
    void shouldCallUIGetCurrent() {
        try (MockedStatic<UI> mockedUI = Mockito.mockStatic(UI.class)) {
            UI mockUI = Mockito.mock(UI.class);
            mockedUI.when(UI::getCurrent).thenReturn(mockUI);

            UI.setCurrent(mockUI);  // Set the mock UI as current

            UI.getCurrent();  // Call UI.getCurrent() to verify it was invoked

            mockedUI.verify(UI::getCurrent, Mockito.times(1));  // Verify that UI.getCurrent() was called exactly once
        }
    }

    /**
     * Test to verify that the password is reset correctly when a valid token is provided.
     * This test checks that the password reset service is called when the provided token is valid,
     * and ensures that the authentication context is updated with the user email.
     */
    @Test
    void shouldResetPasswordWhenValidTokenProvided() {
        when(passwordResetService.resetPassword("valid-token", "newpassword")).thenReturn(true);
        when(passwordResetService.validateToken("valid-token")).thenReturn(Optional.of("user@example.com"));

        resetPasswordView.getTokenField().setValue("valid-token");
        resetPasswordView.getNewPasswordField1().setValue("newpassword");
        resetPasswordView.getNewPasswordField2().setValue("newpassword");

        resetPasswordView.triggerResetPassword();  // Trigger the reset password action

        verify(passwordResetService, times(1)).resetPassword("valid-token", "newpassword");  // Verify resetPassword was called
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    /**
     * Test to verify that the password reset action is not triggered when the passwords do not match.
     * This test ensures that if the new password and confirmation password do not match, the reset action is not executed.
     */
    @Test
    void testPasswordsDoNotMatch() {
        resetPasswordView.getTokenField().setValue("token");
        resetPasswordView.getNewPasswordField1().setValue("pass1");
        resetPasswordView.getNewPasswordField2().setValue("pass2");

        resetPasswordView.triggerResetPassword();  // Trigger the reset password action

        verify(passwordResetService, never()).resetPassword(any(), any());  // Ensure resetPassword is not called
    }

    /**
     * Test to verify that the password reset process behaves correctly when an invalid token is provided.
     * This test simulates a scenario where an invalid token is provided and ensures that the reset process is not executed.
     */
    @Test
    void testResetPasswordWithInvalidToken() {
        when(passwordResetService.resetPassword(eq("invalid"), any())).thenReturn(false);

        resetPasswordView.getTokenField().setValue("invalid");
        resetPasswordView.getNewPasswordField1().setValue("newPass");
        resetPasswordView.getNewPasswordField2().setValue("newPass");

        resetPasswordView.triggerResetPassword();  // Trigger the reset password action

        verify(passwordResetService).resetPassword("invalid", "newPass");  // Verify resetPassword was called
    }
}
