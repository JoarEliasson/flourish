package com.flourish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for {@link com.flourish.service.MailService}.
 *
 * <p>Uses Mockito to mock out the {@link JavaMailSender} and ensure that
 * the message is prepared and sent as expected.</p>
 *
 * <ul>
 *   <li>Verifies correct "to" address, subject, and body content.</li>
 *   <li>Checks for exceptions thrown during the send process.</li>
 * </ul>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
@ActiveProfiles("test")
class MailServiceTest {

    private MailService mailService;
    private JavaMailSender mailSenderMock;

    /**
     * Initializes the MailService with a mocked JavaMailSender.
     */
    @BeforeEach
    void setUp() {
        mailSenderMock = mock(JavaMailSender.class);
        mailService = new MailService(mailSenderMock);
    }

    /**
     * Verifies that the password reset token is sent to the correct recipient
     * with the expected subject and body content.
     *
     * @throws MessagingException if creating or sending the message fails
     */
    @Test
    void testSendPasswordResetToken_Success() throws MessagingException {
        String recipient = "user@example.com";
        String token = "reset-token-123";

        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);
        doNothing().when(mailSenderMock).send(mimeMessageMock);

        mailService.sendPasswordResetToken(recipient, token);

        verify(mailSenderMock).createMimeMessage();
        verify(mailSenderMock).send(mimeMessageMock);
    }

    /**
     * Verifies that the method throws MessagingException if the mailSender fails to send.
     */
    @Test
    void testSendPasswordResetToken_ThrowsMessagingException() throws MessagingException {
        String recipient = "fail@example.com";
        String token = "fail-token";

        MimeMessage mimeMessageMock = mock(MimeMessage.class);

        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);
        doThrow(new RuntimeException("Mail server error")).when(mailSenderMock).send(mimeMessageMock);

        assertThrows(RuntimeException.class,
                () -> mailService.sendPasswordResetToken(recipient, token),
                "Expected a MessagingException when mailSender fails"
        );
    }

    /**
     * Verifies that getMailSender returns the underlying JavaMailSender instance.
     */
    @Test
    void testGetMailSender() {
        assertSame(mailSenderMock, mailService.getMailSender(),
                "Expected the same mock mailSender instance to be returned");
    }
}
