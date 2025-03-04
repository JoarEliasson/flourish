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

/**
 * A public view for resetting the password once the user has a valid token.
 *
 * <p>The user provides the token and a new password.
 * If valid, we update the password in DB and log the user in
 * (by placing an Authentication in the SecurityContextHolder).</p>
 *
 * @author
 *   Joar Eliasson, Christoffer Salomonsson
 * @version
 *   1.1.0
 * @since
 *   2025-02-16
 */
@PageTitle("Reset password")
@Route("resetpassword")
@AnonymousAllowed
public class ResetPasswordView extends Composite<VerticalLayout> {

    private final PasswordResetService passwordResetService;

    private final TextField tokenField = new TextField("Reset Token");
    private final PasswordField newPasswordField1 = new PasswordField("New Password");
    private final PasswordField newPasswordField2 = new PasswordField("Confirm New Password");

    /**
     * Constructs a new ResetPasswordView.
     *
     * @param passwordResetService The service handling reset logic.
     */
    public ResetPasswordView(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;

        getStyle().set("background-color", "#e8f5e9");
        VerticalLayout resetPswLayout = getContent();
        resetPswLayout.setSizeFull();
        resetPswLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        resetPswLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("400px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);
        formLayout.setAlignItems(Alignment.CENTER);

        tokenField.setWidthFull();
        newPasswordField1.setWidthFull();
        newPasswordField2.setWidthFull();

        Button resetButton = new Button("Reset Password", e -> handleResetPassword());

        formLayout.add(tokenField, newPasswordField1, newPasswordField2, resetButton);

        resetPswLayout.add(formLayout);
    }

    /**
     * Validates the token, updates the user's password if valid,
     * and logs the user in programmatically.
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
