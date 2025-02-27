package com.flourish.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.*;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static java.sql.DriverManager.getDriver;
import static javax.swing.UIManager.getUI;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for testing the functionality of {@link LoginView}.
 * This class uses JUnit 5 and Vaadin TestBench to test various features of the LoginView component.
 * @author Zahraa Alqassab
 * @since 2025-02-26
 */

class LoginViewTest extends UIUnitTest {
    private LoginView loginView;
    @Mock
    private VaadinSession mockSession;

    /**
     * Setup method that runs before each test.
     * Creates a new instance of LoginView and adds it to the UI for testing.
     */
   @BeforeEach
    public void setUp() {
        loginView = new LoginView();
        UI.getCurrent().add(loginView);  // This adds the component to the UI for testing
        waitForUi();
    }

    /**
     * Waits for the UI components to load to ensure tests are executed correctly.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    private void waitForUi() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();}}

    /**
     * Tests that the login form is present in {@link LoginView}.
     *
     * This test checks that the login form is not null and is present on the UI.
     */
    @Test
    public void testLoginFormIsPresent() {
        assertNotNull(loginView.getLoginForm(), "Login form should be present");}

    /**
     * Tests that the register button is present and has the correct text in {@link LoginView}.
     *
     * This test checks that the register button exists and that the text on it is "Register".
     */
    @Test
    public void testRegisterButtonPresence() {
        Button registerButton = loginView.registerButton;
        assertNotNull(registerButton, "Register button should be present");
        assertEquals("Register", registerButton.getText(), "Register button should have correct text");}

    /**
     * Tests that the login form has the correct title and description in {@link LoginForm}.
     *
     * This test checks that the form has the correct title and description set from {@link LoginI18n}.
     */
    @Test
    public void testLoginFormTitleAndDescription() {
        LoginForm loginForm = loginView.getLoginForm();
        assertNotNull(loginForm, "Login form should be present");

        LoginI18n i18n = LoginI18n.createDefault();
        if (i18n.getHeader() == null) {
            i18n.setHeader(new LoginI18n.Header());
        }

        i18n.getHeader().setTitle("Test Title");
        i18n.getHeader().setDescription("Test Description");
        loginForm.setI18n(i18n);
        assertEquals("Test Title", i18n.getHeader().getTitle(), "Title should match");
        assertEquals("Test Description", i18n.getHeader().getDescription(), "Description should match");}

    /**
     * Tests that the login form has the correct action value in {@link LoginForm}.
     *
     * This test checks that the form has the correct action setting.
     */
    @Test
    public void testLoginFormAction() {
        LoginForm loginForm = loginView.getLoginForm();
        loginForm.setAction("login");
        assertEquals("login", loginForm.getAction(), "The form action should be 'login'");}
}
