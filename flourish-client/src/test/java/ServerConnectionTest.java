import com.flourish.client.service.ServerConnection;
import com.flourish.shared.Message;
import com.flourish.shared.MessageType;
import com.flourish.shared.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConnectionTest {

    private ServerConnection serverConnection;

    @BeforeEach
    void setUp() {
        serverConnection = ServerConnection.getClientConnection();
    }

    //Testing correct Singleton behavior
    @Test
    void testSingletonInstance() {
        ServerConnection instance1 = ServerConnection.getClientConnection();
        ServerConnection instance2 = ServerConnection.getClientConnection();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Expected the same singleton instance"); //Ensures both instances are the same to verify singleton behavior
    }

    //Test the behaviour when given a NULL input
    @Test
    void testMakeRequest_NullMessage() {
        Message response = serverConnection.makeRequest(null);
        assertNull(response, "Expected null response when sending null request");
    }

    // Test the method when given a valid request
    @Test
    void testMakeRequest_ValidMessage() {
        Message request = new Message(MessageType.FORGOT_PASSWORD, new User("test@example.com", null));
        Message response = serverConnection.makeRequest(request);

        // Since the real server isn't running, we can't check the response contents.
        // But we can at least check if an exception didn't occur (null response expected).
        assertNull(response, "Expected null response when server is unreachable");
    }
}
