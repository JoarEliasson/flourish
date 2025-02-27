package com.flourish.views;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.testbench.unit.UIUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MainView}.
 *
 * <p>Tests ensure that the MainView component initializes correctly
 * and contains the expected welcome message.</p>
 *
 * @author Zahraa Alqassab
 * @version 1.1.0
 * @since 2025-02-24
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
     * Verifies that the {@link MainView} instance is created successfully
     * and is a subclass of {@link VerticalLayout}.
     * This test ensures that the main view component is correctly instantiated
     * and inherits from the appropriate layout class.
     */
    @Test
    void shouldCreateMainView() {
        assertNotNull(mainView, "MainView should be instantiated.");
        assertTrue(mainView instanceof VerticalLayout, "MainView should extend VerticalLayout.");
    }

    /**
     * Ensures that the {@link MainView} contains an {@link H1} element
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
