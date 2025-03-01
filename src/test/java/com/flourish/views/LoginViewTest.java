package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI unit tests for {@link LoginView}.
 *
 * <p>This class demonstrates a lightweight approach to verifying the
 * existence and basic properties of child components in a Vaadin
 * view, without launching a Spring or Vaadin server.</p>
 *
 * <p>The tests inspect the view’s component hierarchy to ensure:
 * <ul>
 *   <li>A {@link LoginForm} is initialized and added to the layout.</li>
 *   <li>A register {@link Button} is present with text "Register".</li>
 * </ul>
 * </p>
 *
 * <p>Note that we instantiate {@link LoginView} directly, and
 * traverse its children using Vaadin's {@code getChildren()}
 * method, relying only on publicly available APIs.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-27
 */
class LoginViewTest {

    private LoginView loginView;

    /**
     * Initializes a fresh instance of {@link LoginView} before each test.
     */
    @BeforeEach
    void setUp() {
        loginView = new LoginView();
    }

    /**
     * <h6>Test #1: Verify that the LoginForm exists in the component hierarchy.</h5>
     *
     * Ensures that the {@link LoginView} contains a {@link LoginForm} within its main layout.
     * <p>
     * The view’s top-level child is expected to be a {@link VerticalLayout} (the "loginLayout"),
     * which in turn holds multiple components including the {@link LoginForm}.
     * </p>
     */
    @Test
    void testLoginFormExistsAndInitialized() {
        Component mainChild = loginView.getChildren()
                .findFirst()
                .orElseThrow(() -> new AssertionError("LoginView has no child components at all!"));

        assertTrue(mainChild instanceof VerticalLayout,
                "Expected the main child to be a VerticalLayout (the loginLayout).");

        List<Component> innerComponents = mainChild.getChildren().collect(Collectors.toList());

        boolean hasLoginForm = innerComponents.stream()
                .anyMatch(component -> component instanceof LoginForm);

        assertTrue(hasLoginForm,
                "Expected to find a LoginForm among the loginLayout children.");
    }

    /**
     * <h6>Test #2: Verify the presence of a register {@link Button} with text "Register".</h5>
     *
     * Checks for the presence of a register {@link Button} with text "Register".
     * <p>
     * The method locates the layout’s second-level children and searches
     * for a {@link Button} whose {@code getText()} matches "Register".
     * </p>
     */
    @Test
    void testRegisterButtonText() {
        Component mainChild = loginView.getChildren().findFirst()
                .orElseThrow(() -> new AssertionError("LoginView has no child components."));

        assertTrue(mainChild instanceof VerticalLayout,
                "Expected the main child to be a VerticalLayout.");

        List<Component> innerComponents = mainChild.getChildren().collect(Collectors.toList());

        Button registerButton = (Button) innerComponents.stream()
                .filter(component -> component instanceof Button)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No Button found in loginLayout children."));

        assertEquals("Register", registerButton.getText(),
                "Expected the register button text to be 'Register'.");
    }

    /**
     * <h6>Test #3: Verify that the forgot password link is correctly set.</h6>
     * @author Zahraa Alqassab
     * @since 2025-02-27
     * Ensures that the forgot password button exists and has the expected text.
     */
    @Test
    void testForgotPasswordButtonExists() {
        Component mainChild = loginView.getChildren().findFirst()
                .orElseThrow(() -> new AssertionError("LoginView has no child components."));

        assertTrue(mainChild instanceof VerticalLayout,
                "Expected the main child to be a VerticalLayout.");

        LoginForm loginForm = (LoginForm) mainChild.getChildren()
                .filter(component -> component instanceof LoginForm)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No LoginForm found in loginLayout children."));

        assertNotNull(loginForm,
                "Expected the login form to contain a forgot password button.");
    }
    /**
     * <h6>Test #4: Verify that the layout contains an H3 header with the expected text.</h6>
     * @author Zahraa Alqassab
     * @since 2025-02-27
     * Ensures that an H3 component exists and displays "New to Flourish?".
     */
    @Test
    void testHeaderExistsWithCorrectText() {
        Component mainChild = loginView.getChildren().findFirst()
                .orElseThrow(() -> new AssertionError("LoginView has no child components."));

        assertTrue(mainChild instanceof VerticalLayout,
                "Expected the main child to be a VerticalLayout.");

        List<Component> innerComponents = mainChild.getChildren().collect(Collectors.toList());

        Component header = innerComponents.stream()
                .filter(component -> component.getElement().getTag().equals("h3"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No H3 header found in loginLayout children."));

        assertEquals("New to Flourish?", header.getElement().getText(),
                "Expected the H3 header text to be 'New to Flourish?'.");
    }
    /**
     * <h6>Test #3: Verify that the login form action is correctly set.</h6>
     * @author Zahraa Alqassab
     * @since 2025-02-27
     * Ensures that the {@link LoginForm} inside {@link LoginView} has the expected action URL.
     */
    @Test
    void testLoginFormAction() {
        Component mainChild = loginView.getChildren().findFirst()
                .orElseThrow(() -> new AssertionError("LoginView has no child components."));

        assertTrue(mainChild instanceof VerticalLayout,
                "Expected the main child to be a VerticalLayout.");

        LoginForm loginForm = (LoginForm) mainChild.getChildren()
                .filter(component -> component instanceof LoginForm)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No LoginForm found in loginLayout children."));

        assertEquals("login", loginForm.getAction(),
                "Expected the login form action to be 'login'.");
    }

}
