package com.flourish.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending emails via Spring's JavaMailSender.
 *
 * <p>Follows the Single Responsibility principle:
 * only handles email sending logic.</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * Constructs a new MailService.
     *
     * @param mailSender The JavaMailSender bean configured with SMTP details.
     */
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a password reset token to the given email address.
     *
     * @param toEmail The recipient's email.
     * @param token The token string to include in the message.
     * @throws MessagingException If sending fails due to mail server issues.
     *
     * TODO: should return a boolean indicating success or failure
     */
    public void sendPasswordResetToken(String toEmail, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(toEmail);
        helper.setSubject("Flourish Password Reset");
        String body = "You requested a password reset.\n\n"
                + "Use this token to reset your password: " + token
                + "\n\nIf you didn't request this, please ignore.";
        helper.setText(body, false);

        mailSender.send(message);
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }
}
