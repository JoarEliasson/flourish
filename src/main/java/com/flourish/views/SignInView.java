package com.flourish.views;

import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A custom sign-in view at "/signin".
 * <p>
 * Uses a Vaadin {@link LoginForm} that, by default, posts credentials to "/login",
 * which VaadinWebSecurity handles.
 * </p>
 */
@Route("signin")
public class SignInView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public SignInView(UserService userService) {
        this.userService = userService;
        initLayout();
    }

    private void initLayout() {
        H1 heading = new H1("Sign In");
        LoginForm loginForm = new LoginForm();

        Button testLoginButton = new Button("Manual Test Login", e -> {
            try {
                User user = userService.authenticate("test", "pass");
                getUI().ifPresent(ui -> ui.navigate("main"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(heading, loginForm, testLoginButton);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }
}
