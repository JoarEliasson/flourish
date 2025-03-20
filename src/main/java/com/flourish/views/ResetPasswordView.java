package com.flourish.views;

import com.flourish.service.PasswordResetService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Allows users to reset their password using a valid reset token.
 * Updates the password in the database and logs the user in upon success.
 *
 * <p>Accessible at "/reset-password." The user is prompted for a token and
 * new password fields. A successful reset navigates to the home page
 * with an active authentication.</p>
 *
 * <p>This view is available to anonymous users.</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-03-14
 */
@PageTitle("Reset password")
@Route("reset-password")
@AnonymousAllowed
public class ResetPasswordView extends Composite<VerticalLayout> {

    private final PasswordResetService passwordResetService;
    private final TextField tokenField = new TextField("Reset Token");
    private final PasswordField newPasswordField1 = new PasswordField("New Password");
    private final PasswordField newPasswordField2 = new PasswordField("Confirm New Password");

    /**
     * Constructs a new ResetPasswordView.
     *
     * @param passwordResetService the service handling reset logic
     */
    public ResetPasswordView(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
        addClassName("reset-password-view");
        VerticalLayout layout = getContent();
        layout.addClassName("reset-password-layout");
        layout.setSizeFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.addClassName("reset-password-form");
        formLayout.setWidth("400px");

        tokenField.addClassName("reset-password-token");
        newPasswordField1.addClassName("reset-password-field");
        newPasswordField2.addClassName("reset-password-field");

        Button resetButton = new Button("Reset Password", event -> handleResetPassword());
        resetButton.addClassName("reset-password-button");

        formLayout.add(tokenField, newPasswordField1, newPasswordField2, resetButton);
        layout.add(formLayout);
    }

    /**
     * Validates the reset token and new passwords, updates the database, and
     * logs the user in if successful.
     */
    private void handleResetPassword() {
        String token = tokenField.getValue();
        String newPassword1 = newPasswordField1.getValue();
        String newPassword2 = newPasswordField2.getValue();
        boolean success = false;

        if (token.isEmpty() || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            Notification.show("Please fill in all the required fields.");
        } else if (!newPassword1.equals(newPassword2)) {
            Notification.show("Passwords do not match.");
        } else {
            success = passwordResetService.resetPassword(token, newPassword1);
        }

        if (success) {
            var maybeEmail = passwordResetService.validateToken(token);
            if (maybeEmail.isPresent()) {
                String email = maybeEmail.get();
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, newPassword1);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            Notification.show("Password reset successful! You are now logged in.");
            getUI().ifPresent(ui -> ui.navigate(""));
        } else {
            Notification.show("Invalid or expired token.");
        }
    }

    /**
     * Gets the token input field.
     * @author Zahraa Alqassab
     * @return the token field where the user enters the reset token.
     */
    public TextField getTokenField() {
        return tokenField;
    }

    /**
     * Gets the first password input field.
     *@author Zahraa Alqassab
     * @return the first password field where the user enters the new password.
     */
    public PasswordField getNewPasswordField1() {
        return newPasswordField1;
    }

    /**
     * Gets the second password input field.
     *@author Zahraa Alqassab
     * @return the second password field where the user confirms the new password.
     */
    public PasswordField getNewPasswordField2() {
        return newPasswordField2;
    }
    public void triggerResetPassword(){
        handleResetPassword();
    }

}
