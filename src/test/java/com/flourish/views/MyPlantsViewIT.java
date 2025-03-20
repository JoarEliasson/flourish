package com.flourish.views;

import com.flourish.FlourishApplication;
import com.flourish.domain.LibraryEntry;
import com.flourish.domain.User;
import com.flourish.domain.UserPlantLibrary;
import com.flourish.service.UserPlantLibraryService;
import com.flourish.service.UserService;
import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration (E2E) tests for {@link MyPlantsView} without using mocks.
 * <p>
 * This demonstrates how to:
 * <ul>
 *   <li>Create a unique test user in the real database (via {@link UserService})</li>
 *   <li>Log in with that user using Vaadin's LoginView flow</li>
 *   <li>Navigate to {@code /my-plants} and verify its UI content</li>
 *   <li>Add a plant to the user's library via {@link UserPlantLibraryService} (real DB save),
 *       then refresh to ensure the UI updates</li>
 *   <li>After each test, delete only the user created by the test (rather than wiping the whole DB)</li>
 * </ul>
 *
 * <strong>References:</strong>
 * <ul>
 *   <li><a href="https://vaadin.com/docs/latest/tools/testbench/overview">Vaadin TestBench Docs</a></li>
 *   <li><a href="https://spring.io/projects/spring-boot">Spring Boot Docs</a></li>
 * </ul>
 *
 * <p>This approach is similar to {@link LoginViewIT}, but exercises the {@code MyPlantsView}
 * end-to-end with the real {@link UserPlantLibraryService} and database instead of Mockito mocks.</p>
 *
 * @author
 *   Emil Åqvist, Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-21
 */
@SpringBootTest(
        classes = FlourishApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MyPlantsViewIT extends TestBenchTestCase {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPlantLibraryService userPlantLibraryService;

    private String testUserEmail;
    private static final String TEST_USER_PASSWORD = "plainTextPass";

    /**
     * Installs the Chrome WebDriver once for all tests.
     */
    @BeforeAll
    static void globalSetup() {
        WebDriverManager.chromedriver().setup();
    }

    /**
     * Creates a unique user in the DB, then opens a headless browser and logs in.
     * Navigates to the /my-plants route.
     */
    @BeforeEach
    void setUp() {
        testUserEmail = "testUser_" + Instant.now().toEpochMilli() + "@example.com";
        userService.createUser(
                new User("Test", "User", testUserEmail, TEST_USER_PASSWORD, "USER")
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        setDriver(TestBench.createDriver(new ChromeDriver(options)));

        getDriver().get("http://localhost:" + port + "/login");

        LoginFormElement loginForm = $(LoginFormElement.class).first();
        loginForm.getUsernameField().setValue(testUserEmail);
        loginForm.getPasswordField().setValue(TEST_USER_PASSWORD);
        loginForm.getSubmitButton().click();

        if (getDriver().getCurrentUrl() != null && getDriver().getCurrentUrl().contains("/login")) {
            System.out.println("Login attempt failed; test user might not have been created properly.");
        }

        getDriver().get("http://localhost:" + port + "/my-plants");
    }

    /**
     * After each test, close the browser and delete the test user from the DB.
     * This approach leaves all other DB data intact.
     */
    @AfterEach
    void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
        }
        userService.deleteByEmail(testUserEmail);
    }

    /**
     * Test scenario: brand-new user => the library is empty => "No plants in your library yet"
     * should appear somewhere in the DOM.
     */
    @Test
    void testEmptyPlantListMessage() {
        assertTrue(
                getPageSource().contains("No plants in your library yet"),
                "Expected an empty library message for a brand-new user."
        );
    }

    /**
     * Test scenario: after adding a plant for the user, the view should no longer
     * display the "No plants..." text.
     */
    @Test
    void testPlantListMessageWithPlant() {
        Optional<User> maybeUser = userService.findByEmail(testUserEmail);
        assertTrue(maybeUser.isPresent(), "User must exist after setUp().");
        Long userId = maybeUser.get().getId();

        userPlantLibraryService.addPlantToLibrary(userId, 100L);

        getDriver().navigate().refresh();

        String pageHtml = getPageSource();
        assertFalse(
                pageHtml.contains("No plants in your library yet"),
                "Once the library has a plant, the empty message should not appear."
        );
    }

    /**
     * Simple scenario to demonstrate removing a plant from the library
     * and verifying the UI updates. In a real test, you'd click a delete
     * icon or confirm dialog, but here we do it programmatically in the DB
     * then refresh and check.
     */
    @Test
    void testRemovePlantCall() {
        Optional<User> maybeUser = userService.findByEmail(testUserEmail);
        assertTrue(maybeUser.isPresent(), "User must exist after setUp().");
        Long userId = maybeUser.get().getId();

        userPlantLibraryService.addPlantToLibrary(userId, 200L);

        getDriver().navigate().refresh();
        String pageHtml = getPageSource();
        assertFalse(
                pageHtml.contains("No plants in your library yet"),
                "Should see at least one plant in the library before removing it."
        );

        List<UserPlantLibrary> entries = userPlantLibraryService
                .getAllLibraryEntriesForUser(userId)
                .stream()
                .map(LibraryEntry::getUserPlantLibrary)
                .toList();

        for (UserPlantLibrary upl : entries) {
            userPlantLibraryService.removePlantFromLibrary(upl.getId());
        }

        getDriver().navigate().refresh();

        String afterRemovalHtml = getPageSource();
        assertTrue(
                afterRemovalHtml.contains("No plants in your library yet"),
                "After removing all library entries, the empty message should re-appear."
        );
    }

    /**
     * Utility method. Sometimes you want to see the entire rendered page for debugging.
     * getDriver().getPageSource() is the raw HTML.
     */
    private String getPageSource() {
        return getDriver().getPageSource();
    }
}
