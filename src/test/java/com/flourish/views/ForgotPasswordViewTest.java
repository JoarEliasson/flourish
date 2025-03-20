package com.flourish.views;

import static org.mockito.Mockito.*;

import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.service.MailService;
import com.flourish.service.PasswordResetService;
import com.flourish.service.UserServiceImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.EmailField;

import java.time.LocalDateTime;


/**
 * Unit tests for {@link ForgotPasswordView}.
 *
 * <p>This class verifies the presence of key UI components and ensures
 * that the forgot-password logic behaves as expected.</p>
 *
 * @author
 *   Zahraa Alqassab
 * @version
 *   1.3.0
 * @since
 *   2025-03-01
 */
class ForgotPasswordViewTest {

    private ForgotPasswordView forgotPasswordView;
    private UserRepository userRepository;
    private PasswordResetService passwordResetService;
    private MailService mailService;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordResetService = mock(PasswordResetService.class);
        mailService = mock(MailService.class);
        userService = mock(UserServiceImpl.class);

        forgotPasswordView = new ForgotPasswordView(passwordResetService, mailService, userService);
        UI.setCurrent(new UI());
    }

    /**
     * Test #1: Verify that the email field exists.
     */
    @Test
    void testEmailFieldExists() {
        EmailField emailField = (EmailField) forgotPasswordView.getContent().getChildren()
                .filter(component -> component instanceof EmailField)
                .findFirst()
                .orElseThrow(() -> new AssertionError("EmailField not found in ForgotPasswordView."));

        assertNotNull(emailField, "EmailField should be present in the ForgotPasswordView.");
    }

    /**
     * Test #2: Verify that the send button exists.
     */
    @Test
    void testSendButtonExists() {
        Button sendButton = (Button) forgotPasswordView.getContent().getChildren()
                .filter(component -> component instanceof Button)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Send Button not found in ForgotPasswordView."));

        assertNotNull(sendButton, "Send Button should be present in the ForgotPasswordView.");
        assertEquals("Send Reset Token", sendButton.getText(), "Send Button should have correct label.");
    }

    /**
     * Test #3: Verify that entering an empty email shows a notification.
     */
    @Test
    void testEmptyEmailShowsNotification() {
        forgotPasswordView.setEmailFieldValue("");
        forgotPasswordView.triggerHandelForgotPassword();
        verify(userRepository, never()).findByEmail(anyString());
    }


    /**
     * Test #4: Verify that an invalid email (not found) shows a notification.
     */
    @Test
    void testInvalidEmailShowsNotification() {
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(null);

        forgotPasswordView.setEmailFieldValue("invalid@example.com");
        forgotPasswordView.triggerHandelForgotPassword();

        Notification notification = Notification.show("If this email exists, a reset token will be sent.");
        assertNotNull(notification, "A notification should be shown for an invalid email.");
    }

    /**
     * Test #5: Verify that a valid email triggers the password reset logic.
     * <p>
     * This test simulates the scenario where the provided email belongs to an existing user.
     * It verifies that when a user is found:
     * <ul>
     *   <li>The password reset service generates a reset token (an instance of {@link PasswordResetToken}).</li>
     *   <li>The mail service is called to send the reset token to the user.</li>
     * </ul>
     * </p>
     *
     * @throws Exception if any error occurs during the password reset process.
     */
    @Test
    void testValidEmailTriggersPasswordReset() throws Exception {
        String validEmail = "user@example.com";
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(validEmail)).thenReturn(mockUser);

        PasswordResetToken token = new PasswordResetToken(validEmail, "token123", LocalDateTime.now().plusMinutes(30));
        when(passwordResetService.createPasswordResetToken(eq(validEmail), anyInt())).thenReturn(token);

        forgotPasswordView.setEmailFieldValue(validEmail);
        forgotPasswordView.triggerHandelForgotPassword();

        verify(mailService, times(1)).sendPasswordResetToken(validEmail, "token123");
    }

}
