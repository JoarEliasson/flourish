import com.flourish.client.controller.ForgotPasswordPaneController;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
This testclass isn't complete and will need modification
after switching to webbased frontface
 */
class ForgotPasswordPaneControllerTest {

    private ForgotPasswordPaneController controller;

    @BeforeEach
    void setUp() {
        controller = new ForgotPasswordPaneController();
        controller.txtFieldUserEmail = new TextField();
    }

    @Test
    void testGetComponentsToVerify_ValidEmail() {
        String expectedEmail = "test@example.com";
        controller.txtFieldUserEmail.setText(expectedEmail);


        String[] result = controller.getComponentsToVerify();

        assertNotNull(result); // Ensure that the result is not Null
        assertEquals(1, result.length); // Ensure the array contains exactly one element
        assertEquals(expectedEmail, result[0]); // Verify the email matches the one that was entered
    }

    @Test
    void testGetComponentsToVerify_EmptyEmail() {
        controller.txtFieldUserEmail.setText("");
        String[] result = controller.getComponentsToVerify();

        assertNotNull(result); // Ensure that the result is not Null
        assertEquals(1, result.length); // Ensure the array contains exactly one element
        assertEquals("", result[0]); // The email returned should be an empty string
    }
}