package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit tests for {@link MainView}.
 *
 * <p>Tests ensure that the MainView component initializes correctly
 * and contains the expected welcome message.</p>
 *
 * @author Zahraa Alqassab
 * @version 1.3.0
 * @since 2025-03-01
 */
class MainViewTest extends UIUnitTest {

    private MainView mainView;

    /**
     * Sets up the test environment by creating a new instance of {@link MainView}.
     * This method runs before each test to ensure the {@link MainView} is properly initialized.
     */
    @BeforeEach
    void setUp() {
        mainView = new MainView();
    }

    /**
     * Test #1: Tests that the MainView contains an H1 element with the correct welcome message.
     * <p>
     * This method retrieves the first child component from MainView and asserts that:
     * <ul>
     *   <li>The first child is an instance of H1.</li>
     *   <li>The text of the H1 element is "Welcome to the Main View!".</li>
     * </ul>
     * If MainView has no child components, an {@link AssertionError} is thrown.
     * </p>
     *
     * @throws AssertionError if MainView does not contain any child components.
     */
    @Test
    void testWelcomeMessageExistsAndInitialized() {
        Component firstChild = mainView.getChildren()
                .findFirst()
                .orElseThrow(() -> new AssertionError("MainView has no child components at all!"));
        System.out.println("Main view first child class: " + firstChild.getClass().getName());
        assertTrue(firstChild instanceof H1,
                "Expected the main child to be an H1 element (the welcome message).");

        H1 welcomeH1 = (H1) firstChild;
        assertEquals("Welcome to the Main View!", welcomeH1.getText(),
                "Expected the welcome message to be 'Welcome to the Main View!'");
    }

    /**
     * Test #2: Ensures that the {@link MainView} contains an {@link H1} element
     * with the expected welcome message text.
     * This test checks if the main view contains an {@link H1} component
     * with the text "Welcome to the Main View!".
     */
    @Test
    void shouldContainWelcomeMessage() {
        boolean hasWelcomeText = mainView.getChildren()
                .filter(component -> component instanceof H1)
                .map(component -> ((H1) component).getText())
                .anyMatch(text -> text.equals("Welcome to the Main View!"));

        assertTrue(hasWelcomeText, "MainView should contain a welcome message.");
    }


}
