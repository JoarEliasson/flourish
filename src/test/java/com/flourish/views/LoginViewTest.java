package com.flourish.views;

import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class LoginViewTest extends UIUnitTest {

    @Test
    public void testRegisterButtonText() {

        final LoginView loginView = navigate(LoginView.class);
        String registerButtonText = test(loginView.registerButton).getComponent().getText();

        Assertions.assertEquals("Register", registerButtonText);
    }
}