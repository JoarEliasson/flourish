package com.flourish.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;

/**
 * A Vaadin view that serves as the login page.
 *
 * <p>Route-based security is handled by VaadinWebSecurity:
 * If a user is not authenticated and tries to access a secured route,
 * they're redirected here.</p>
 *
 * <p>This route is "login", matching setLoginView in SecurityConfig.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;


    /**
     * Constructs a new LoginView with a Vaadin LoginForm.
     */
    public LoginView() {
        loginForm = new LoginForm();
        loginForm.setAction("login");
/*
        UI ui1 = UI.getCurrent();
        System.out.println("Innan");
        if (ui1 != null) {
            System.out.println("UI is present" + ui1);
            String path1 = ui1.getInternals().getActiveViewLocation().getPath();
            System.out.println("Path: "+path1);
            String queryParams1 = ui1.getInternals()
                    .getActiveViewLocation()
                    .getQueryParameters()
                    .getQueryString();  // Get the full query string

            System.out.println("Current URL: " + path1 + "?" + queryParams1);
            String currentUrl = ui1.getInternals().getActiveViewLocation().getPath();


            System.out.println("Current URL: " + currentUrl);
            Map<String, List<String>> queryParams = ui1.getInternals()
                    .getActiveViewLocation()
                    .getQueryParameters()
                    .getParameters();
            System.out.println("Query params: " + queryParams);

            String query = queryParams.getOrDefault("error", List.of()).stream().findFirst().orElse(null);
            System.out.println("Query: " + query);

            if (query != null) {
                System.out.println("Wrong pass");
                LoginForm loginForm = new LoginForm();
                loginForm.setError(true);
                Notification notification = Notification.show("Incorrect username or password", 3000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                add(loginForm);
                return;
            }
        }
        System.out.println("Efter");

 */

        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setWidth("400px");
        loginLayout.setPadding(true);
        loginLayout.setSpacing(true);
        loginLayout.setAlignItems(Alignment.CENTER);
/*
        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

 */
        /*
        getUI().ifPresent(ui -> {
            String query = ui.getInternals()
                    .getActiveViewLocation()
                    .getQueryParameters()
                    .getParameters()
                    .getOrDefault("error", List.of())
                    .stream()
                    .findFirst()
                    .orElse(null);
            System.out.println("Query: " + query);

            if (query != null) {
                System.out.println("Wrong pass");
                loginForm.setError(true);
                Notification notification = Notification.show("Incorrect username or password", 3000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();

            }
        });

         */


        System.out.println("In constructor");

        LoginI18n i18n = LoginI18n.createDefault();
        if (i18n.getHeader() == null) {
            i18n.setHeader(new LoginI18n.Header());
        }
        i18n.getHeader().setTitle("Please Log In");
        i18n.getHeader().setDescription("Enter your credentials");
        i18n.getForm().setForgotPassword("NYTT LÖSENORD TACK");
        loginForm.setI18n(i18n);

        loginForm.addForgotPasswordListener(e -> getUI().ifPresent(ui -> ui.navigate("forgotpassword")));

        Button registerButton = new Button("Register", e ->
                getUI().ifPresent(ui -> ui.navigate("register"))
        );

        H3 header = new H3("New to Flourish?");

        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        registerButton.getStyle().set("cursor", "pointer");
        loginLayout.add(loginForm, header, registerButton);
        add(loginLayout);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event){
        // Access query parameters directly before rendering the view
        Map<String, List<String>> queryParams = event.getLocation().getQueryParameters().getParameters();
        System.out.println("Before Enter: Query params: " + queryParams);

        // Handle the "error" query parameter
        String query = queryParams.getOrDefault("error", List.of())
                .stream()
                .findFirst()
                .orElse(null);
        System.out.println("Before Enter: Query: " + query);

        if (query != null) {
            System.out.println("Wrong pass");
            loginForm.setError(true);
            Notification notification = Notification.show("Incorrect username or password", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
    }

}
