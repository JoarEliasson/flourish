package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.flourish.service.MailService;
import com.flourish.service.PasswordResetService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.mail.MessagingException;

/**
 * Provides a public view for initiating a password-reset flow.
 * Asks the user for their email address and, if valid, emails a reset token.
 *
 * <p>Redirects to {@code reset-password} upon successful token generation.</p>
 *
 * <p>This view may be accessed anonymously, hence {@link AnonymousAllowed}.</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.2.0
 * @since
 *   2025-03-14
 */
@Route("forgot-password")
@PageTitle("Forgot Password")
@AnonymousAllowed
public class ForgotPasswordView extends Composite<VerticalLayout> {

    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;
    private final MailService mailService;
    private final EmailField emailField = new EmailField("Email");

    /**
     * Constructs a new ForgotPasswordView.
     *
     * @param userRepository the user repository
     * @param passwordResetService the service creating reset tokens
     * @param mailService the service sending emails
     */
    public ForgotPasswordView(
            UserRepository userRepository,
            PasswordResetService passwordResetService,
            MailService mailService
    ) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
        this.mailService = mailService;

        getContent().addClassName("forgot-password-view");
        getContent().setSizeFull();
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getContent().setSpacing(true);

        H2 header = new H2("Reset Your Password");
        header.addClassName("forgot-password-title");

        H2 instructions = new H2("Enter your email to receive a reset code.");
        instructions.addClassName("forgot-password-instructions");

        emailField.addClassName("forgot-password-input");

        Button sendButton = new Button("Send Reset Token", e -> handleForgotPassword());
        sendButton.addClassName("forgot-password-button");

        getContent().add(header, instructions, emailField, sendButton);
    }

    /**
     * Validates the email and sends a password-reset token if a user is found.
     * Redirects to {@code reset-password} upon successful dispatch.
     */
    private void handleForgotPassword() {
        String email = emailField.getValue();
        if (email.isEmpty()) {
            Notification.show("Please enter your email.");
            return;
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            Notification.show("If this email exists, a reset token will be sent.");
            return;
        }
        var resetToken = passwordResetService.createPasswordResetToken(email, 30);
        try {
            mailService.sendPasswordResetToken(email, resetToken.getToken());
            Notification.show("A reset token was sent. Check your email.");
            getUI().ifPresent(ui -> ui.navigate("reset-password"));
        } catch (MessagingException ex) {
            Notification.show("Failed to send email: " + ex.getMessage());
        }
    }

    /**
     * Triggers the forgot-password logic for testing or programmatic invocation.
     */
    public void triggerHandelForgotPassword() {
        handleForgotPassword();
    }

    /**
     * Sets the email field value, useful for testing or pre-population.
     *
     * @param value the email to set
     */
    public void setEmailFieldValue(String value) {
        emailField.setValue(value);
    }
}
