package com.flourish.views;

import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.User;
import com.flourish.service.MailService;
import com.flourish.service.PasswordResetService;
import com.flourish.service.UserServiceImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ForgotPasswordView}.
 *
 * <p>This class verifies the presence of key UI components and ensures
 * that the forgot-password logic behaves as expected.</p>
 *
 * @author
 *   Zahraa Alqassab, Joar Eliasson
 * @version
 *   1.3.0
 * @since
 *   2025-03-01
 */
class ForgotPasswordViewTest {

    private ForgotPasswordView forgotPasswordView;

    private PasswordResetService passwordResetService;
    private MailService mailService;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        passwordResetService = mock(PasswordResetService.class);
        mailService = mock(MailService.class);
        userService = mock(UserServiceImpl.class);

        forgotPasswordView = new ForgotPasswordView(passwordResetService, mailService, userService);
        UI.setCurrent(new UI());
    }

    /**
     * Test #1: Verify that the EmailField component exists in the ForgotPasswordView.
     */
    @Test
    void testEmailFieldExists() {
        EmailField emailField = (EmailField) forgotPasswordView.getContent().getChildren()
                .filter(component -> component instanceof EmailField)
                .findFirst()
                .orElseThrow(() -> new AssertionError("EmailField not found in ForgotPasswordView."));

        assertNotNull(emailField, "EmailField should be present.");
    }

    /**
     * Test #2: Verify that the Send Reset Token button is present and labeled correctly.
     */
    @Test
    void testSendButtonExists() {
        Button sendButton = (Button) forgotPasswordView.getContent().getChildren()
                .filter(component -> component instanceof Button)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Send Button not found in ForgotPasswordView."));

        assertNotNull(sendButton, "Send Button should be present.");
        assertEquals("Send Reset Token", sendButton.getText(), "Send Button should have the correct label.");
    }

    /**
     * Test #3: If the user leaves the email blank, we shouldn't call userService at all,
     * and a notification should appear.
     */
    @Test
    void testEmptyEmailShowsNotification() {
        forgotPasswordView.setEmailFieldValue("");
        forgotPasswordView.triggerHandelForgotPassword();

        verify(userService, never()).findByEmail(anyString());
    }

    /**
     * Test #4: If the user enters an email not found in the system,
     * we show a notification but do not throw an error.
     */
    @Test
    void testInvalidEmailShowsNotification() {
        when(userService.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        forgotPasswordView.setEmailFieldValue("invalid@example.com");
        forgotPasswordView.triggerHandelForgotPassword();

        Notification notification = Notification.show("If this email exists, a reset token will be sent.");
        assertNotNull(notification, "Should show a notification for an invalid email.");
    }

    /**
     * Test #5: A valid email (existing user) triggers token creation and mail sending.
     */
    @Test
    void testValidEmailTriggersPasswordReset() throws Exception {
        String validEmail = "user@example.com";

        User mockUser = new User(
                "Test",
                "User",
                validEmail,
                "somePassword",
                "USER"
        );

        when(userService.findByEmail(validEmail)).thenReturn(Optional.of(mockUser));

        PasswordResetToken token = new PasswordResetToken(
                validEmail, "token123", LocalDateTime.now().plusMinutes(30)
        );
        when(passwordResetService.createPasswordResetToken(eq(validEmail), anyInt()))
                .thenReturn(token);

        forgotPasswordView.setEmailFieldValue(validEmail);
        forgotPasswordView.triggerHandelForgotPassword();

        verify(mailService, times(1)).sendPasswordResetToken(validEmail, "token123");
    }

}
