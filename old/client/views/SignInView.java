package com.flourish.old.client.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * A custom sign-in view for Vaadin + Spring Security.
 * <p>
 * The route is "/signin". This class extends {@link VerticalLayout} and embeds a {@link LoginForm}.
 * Spring Security is configured to redirect unauthenticated users here.
 * </p>
 */
@Route("signin")
public class SignInView extends VerticalLayout {

    /**
     * Constructs the sign-in view.
     * <p>
     * Initializes a Vaadin {@link LoginForm} that posts credentials to the default "/login" endpoint.
     * Vaadin's Spring Security integration will handle the form submission automatically.
     * </p>
     */
    public SignInView() {
        initLayout();
    }

    /**
     * Initializes the layout with a label and a login form.
     */
    private void initLayout() {
        Label infoLabel = new Label("Please sign in with your username and password.");
        infoLabel.getStyle().set("font-weight", "bold");

        LoginForm loginForm = new LoginForm();
        // The form defaults to sending credentials to the "/login" endpoint,
        // which VaadinWebSecurity configures automatically.

        add(infoLabel, loginForm);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }
}
