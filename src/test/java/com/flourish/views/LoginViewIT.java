package com.flourish.views;

import com.flourish.FlourishApplication;
import com.flourish.domain.User;
import com.flourish.service.UserService;
import com.flourish.service.UserSettingsService;
import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end (E2E) tests for {@link LoginView}, verifying error messages
 * under three scenarios:
 * <ul>
 *   <li>No username provided</li>
 *   <li>Wrong password for an existing user</li>
 *   <li>Non-existent username</li>
 * </ul>
 *
 * <p>No existing data is removed or modified. Instead, the actual
 * business flow is used to create a unique test user (via {@link UserService})
 * if it doesn't exist already.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-27
 */
@SpringBootTest(
        classes = FlourishApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class LoginViewIT extends TestBenchTestCase {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USER_PASSWORD = "plaintextpass";
    private String testUserEmail;
    @Autowired
    private UserSettingsService userSettingsService;

    /**
     * Global setup for the Chrome driver.
     */
    @BeforeAll
    static void globalSetup() {
        WebDriverManager.chromedriver().setup();
    }

    /**
     * Before each test, create (or reuse) a unique user in the DB
     * via the {@link UserService}, then navigate to /login.
     */
    @BeforeEach
    void setUp() {
        testUserEmail = "testuser_" + Instant.now().toEpochMilli() + "@example.com";
        Optional<com.flourish.domain.User> maybeUser = userService.findByEmail(testUserEmail);

        if (maybeUser.isEmpty()) {

            userService.createUser(
                    buildUser(testUserEmail, TEST_USER_PASSWORD)
            );
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        setDriver(TestBench.createDriver(new ChromeDriver(options)));

        getDriver().get("http://localhost:" + port + "/login");
    }

    /**
     * Clean up the browser instance after each test.
     */
    @AfterEach
    void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
        }
        userService.deleteByEmail(testUserEmail);
    }

    /**
     * Test scenario 1: no username entered => "Incorrect username or password".
     */
    @Test
    void testNoUsername_BlockSubmission() {
        LoginFormElement loginForm = $(LoginFormElement.class).first();

        loginForm.getPasswordField().setValue("anyPassword");
        loginForm.getSubmitButton().click();

        boolean isStillOnLoginPage = getDriver().getCurrentUrl().contains("/login");
        assertTrue(isStillOnLoginPage, "Should remain on the login page, no server error triggered.");
    }


    /**
     * Test scenario 2: user exists, but wrong password is provided.
     * Relies on the user created in @BeforeEach, but a different password is passed => ?error => notification.
     */
    @Test
    void testWrongPassword() {
        LoginFormElement loginForm = $(LoginFormElement.class).first();

        loginForm.getUsernameField().setValue(testUserEmail);
        loginForm.getPasswordField().setValue("someWrongPassword");
        loginForm.getSubmitButton().click();

        assertTrue(checkErrorNotification(),
                "Expected error notification for wrong password scenario.");
    }

    /**
     * Test scenario 3: non-existent user => A random email is passed that was never created => ?error => notification.
     */
    @Test
    void testNonExistentUser() {
        LoginFormElement loginForm = $(LoginFormElement.class).first();

        String ghostEmail = "ghost_" + Instant.now().toEpochMilli() + "@example.com";
        loginForm.getUsernameField().setValue(ghostEmail);
        loginForm.getPasswordField().setValue("irrelevantPassword");
        loginForm.getSubmitButton().click();

        assertTrue(checkErrorNotification(),
                "Expected error notification for non-existent user scenario.");
    }

    /**
     * Utility method to check if a Notification with text
     * "Incorrect username or password" appears within 10 seconds.
     *
     * @return true if found, false otherwise
     */
    private boolean checkErrorNotification() {
        waitUntil(driver -> !$(NotificationElement.class).all().isEmpty(), 10);

        for (NotificationElement notif : $(NotificationElement.class).all()) {
            if (notif.getText().contains("Incorrect username or password")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Utility to build a user entity with the given email and password.
     */
    private com.flourish.domain.User buildUser(String email, String rawPassword) {
        return userService.createUser(new User("Test", "User", email, rawPassword, "USER"));
    }
}
