package com.flourish.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@Route("forgotpassword")
@PageTitle("Forgot password")
@AnonymousAllowed
public class ForgotpasswordView extends Composite<VerticalLayout> {

    public ForgotpasswordView() {
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
}

