package com.flourish.views;

import static org.mockito.Mockito.*;
import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.service.MailService;
import com.flourish.service.PasswordResetService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.testbench.unit.UIUnitTest;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import org.mockito.MockedStatic;

/**
 * Unit tests for the {@link ForgotPasswordView} class.
 *
 * This test class verifies the functionality of handling forgot password requests
 * by simulating different scenarios such as empty email, user not found,
 * and successful password reset token generation and email sending.
 * It uses the Mockito framework for mocking dependencies and verifying interactions
 * with them, as well as JUnit 5 for test execution and assertions.
 *
 * @author zahraa Alqassab
 * @since 2025-02-24
 */
@ExtendWith(MockitoExtension.class)
class ForgotPasswordViewTest extends UIUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private ForgotPasswordView forgotPasswordView;

    /**
     * Setup method to initialize the {@link ForgotPasswordView} instance with mocked dependencies
     * before each test method is executed.
     */
    @BeforeEach
    public void setUp() {
        forgotPasswordView = new ForgotPasswordView(userRepository, passwordResetService, mailService);
    }

    /**
     * Test case to verify that a notification is shown when the email field is empty.
     * This simulates the case where the user has not entered an email and attempts
     * to trigger the forgot password action.
     *
     * It verifies that the notification "Please enter your email." is shown when the email
     * field is empty.
     */
    @Test
    void testHandleForgotPassword_EmailFieldEmpty_ShowsNotification() {
        forgotPasswordView.setEmailFieldValue("");
        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            forgotPasswordView.triggerHandelForgotPassword();
            notificationMock.verify(() -> Notification.show("Please enter your email."));
        }
    }

    /**
     * Test case to verify that a notification is shown when the user is not found
     * based on the email address entered. This simulates the case where no user
     * is associated with the given email.
     *
     * It verifies that the notification "If this email exists, a reset token will be sent."
     * is shown when no user is found with the provided email address.
     */
    @Test
    void testHandleForgotPassword_UserNotFound_ShowsNotification() {
        forgotPasswordView.setEmailFieldValue("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            forgotPasswordView.triggerHandelForgotPassword();
            notificationMock.verify(() -> Notification.show("If this email exists, a reset token will be sent."));
        }
    }

    /**
     * Test case to verify that when the user exists, a password reset token is created
     * and an email is sent to the user with the token. This simulates the successful
     * flow where a valid user is found and the password reset process is initiated.
     *
     * It verifies that when a valid user is found, a password reset token is created
     * and the corresponding email is sent.
     *
     * @throws MessagingException If there is an error while sending the email.
     */
    @Test
    void testHandleForgotPassword_UserExists_SendsResetToken() throws MessagingException {
        forgotPasswordView.setEmailFieldValue("test@example.com");
        User user = mock(User.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        PasswordResetToken resetToken = new PasswordResetToken("test@example.com", "token123", LocalDateTime.now().plusMinutes(30));
        when(passwordResetService.createPasswordResetToken(anyString(), anyInt())).thenReturn(resetToken);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            forgotPasswordView.triggerHandelForgotPassword();
            verify(mailService, times(1)).sendPasswordResetToken(eq("test@example.com"), eq("token123"));

            notificationMock.verify(() -> Notification.show("A reset token was sent. Check your email."));
        }
    }
}
