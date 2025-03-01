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

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.mail.MessagingException;

/**
 * A public view for initiating a password-reset flow.
 *
 * <p>Asks the user for their email address. If it exists in the system,
 * a reset token is generated and emailed. The user is then guided to
 * the reset password page.</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@Route("forgotpassword")
@PageTitle("Forgot password")
@AnonymousAllowed
public class ForgotPasswordView extends Composite<VerticalLayout> {

    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;
    private final MailService mailService;

    private final EmailField emailField = new EmailField("Email");

    /**
     * Constructs a new ForgotPasswordView.
     *
     * @param userRepository The repository for looking up users.
     * @param passwordResetService The service to create tokens.
     * @param mailService The service to send emails.
     */
    public ForgotPasswordView(UserRepository userRepository,
                              PasswordResetService passwordResetService,
                              MailService mailService) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
        this.mailService = mailService;

        getContent().setSizeFull();
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getContent().setSpacing(true);

        H2 header = new H2("Reset Your Password");
        H2 instructions = new H2("Enter your email to receive your reset code in the mail.");
        instructions.getStyle().set("font-size", "16px").set("font-weight", "normal");


        Button sendButton = new Button("Send Reset Token", e -> handleForgotPassword());
        /*
        Button resetButton = new Button("Reset Password", event -> {
            getUI().ifPresent(ui -> ui.navigate("resetpassword"));
        });

         */

        getContent().add(header, instructions, emailField, emailField, sendButton);

    }

/*
    public ForgotPasswordView(boolean backupConstructor) {
        getContent().setSizeFull();
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getContent().setSpacing(true);

        H2 header = new H2("Reset Your Password");
        H2 instructions = new H2("Enter your email to receive your reset code in the mail.");
        instructions.getStyle().set("font-size", "16px").set("font-weight", "normal");

        EmailField emailField = new EmailField("Your Email");
        EmailField emailConfirmField = new EmailField("Confirm Email");
        Button resetButton = new Button("Reset Password", event -> {
            getUI().ifPresent(ui -> ui.navigate("resetpassword"));
        });
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getContent().add(header, instructions, emailField, emailConfirmField, resetButton);
    }

 */

    /**
     * Handles the forgot-password logic by checking if the email exists,
     * generating a token, and emailing it if so.
     *
     * TODO: Implement different logic for when the email doesn't exist.
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
            // TODO: Here see todo
            getUI().ifPresent(ui -> ui.navigate("resetpassword"));
        } catch (MessagingException ex) {
            Notification.show("Failed to send email: " + ex.getMessage());
        }
    }
    public void triggerHandelForgotPassword(){
        handleForgotPassword();
    }
    public void setEmailFieldValue(String value) {
        emailField.setValue(value);
    }




}

