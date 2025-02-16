package com.flourish.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("Reset password")
@Route("resetpassword")
@AnonymousAllowed
public class ResetpasswordView extends Composite<VerticalLayout> {

    public ResetpasswordView() {
        VerticalLayout mainLayout = getContent();
        mainLayout.setSizeFull();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("400px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);
        formLayout.setAlignItems(Alignment.CENTER);

        TextField textField = new TextField("Reset code");
        textField.setWidthFull();

        PasswordField passwordField = new PasswordField("New password");
        passwordField.setWidthFull();

        PasswordField passwordField2 = new PasswordField("Confirm new password");
        passwordField2.setWidthFull();

        Button buttonPrimary = new Button("Create new password");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        formLayout.add(textField, passwordField, passwordField2, buttonPrimary);

        mainLayout.add(formLayout);
    }

}
