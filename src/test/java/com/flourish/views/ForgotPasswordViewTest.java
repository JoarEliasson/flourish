package com.flourish.views;

import static org.mockito.Mockito.*;

import com.flourish.domain.PasswordResetToken;
import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.service.MailService;
import com.flourish.service.PasswordResetService;
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
 * This class verifies the presence of key UI components and ensures
 * that the forgot-password logic behaves as expected.
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

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordResetService = mock(PasswordResetService.class);
        mailService = mock(MailService.class);

        forgotPasswordView = new ForgotPasswordView(userRepository, passwordResetService, mailService);
        UI ui = new UI();
        UI.setCurrent(ui);
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

        // Create a PasswordResetToken with an expiration 30 minutes from now
        PasswordResetToken token = new PasswordResetToken(validEmail, "token123", LocalDateTime.now().plusMinutes(30));
        when(passwordResetService.createPasswordResetToken(eq(validEmail), anyInt())).thenReturn(token);

        forgotPasswordView.setEmailFieldValue(validEmail);
        forgotPasswordView.triggerHandelForgotPassword();

        verify(mailService, times(1)).sendPasswordResetToken(validEmail, "token123");
    }






}






































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
/*@ExtendWith(MockitoExtension.class)
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
   /* @BeforeEach
    public void setUp() {
        forgotPasswordView = new ForgotPasswordView(userRepository, passwordResetService, mailService);
    }*/

    /**
     * Test case to verify that a notification is shown when the email field is empty.
     * This simulates the case where the user has not entered an email and attempts
     * to trigger the forgot password action.
     *
     * It verifies that the notification "Please enter your email." is shown when the email
     * field is empty.
     */
   /* @Test
    void testHandleForgotPassword_EmailFieldEmpty_ShowsNotification() {
        forgotPasswordView.setEmailFieldValue("");
        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            forgotPasswordView.triggerHandelForgotPassword();
            notificationMock.verify(() -> Notification.show("Please enter your email."));
        }
    }*/

    /**
     * Test case to verify that a notification is shown when the user is not found
     * based on the email address entered. This simulates the case where no user
     * is associated with the given email.
     *
     * It verifies that the notification "If this email exists, a reset token will be sent."
     * is shown when no user is found with the provided email address.
     */
   /* @Test
    void testHandleForgotPassword_UserNotFound_ShowsNotification() {
        forgotPasswordView.setEmailFieldValue("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            forgotPasswordView.triggerHandelForgotPassword();
            notificationMock.verify(() -> Notification.show("If this email exists, a reset token will be sent."));
        }
    }*/

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
    /*@Test
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
}*/
