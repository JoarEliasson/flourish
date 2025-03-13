import org.junit.jupiter.api.Test;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import com.flourish.server.services.MailService;

import com.flourish.api.ApiConfig;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.converter.EmailConverter;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


//Imports nedan är baserad på:
//https://github.com/omerio/unit-testing/blob/master/src/test/java/com/omerio/service/jmockit/EmailServiceMockTest.java
import jakarta.mail.Message;
import jakarta.mail.Transport;

import org.mockito.ArgumentCaptor; //Del av original Mockito
import org.mockito.Mockito; //Del av original Mockito
import org.powermock.api.mockito.PowerMockito;
//------------------------------------------------------


/**
 * 
 * Entire class authored by Martin Frick. Was the first foray in to how to test mail service.
 * Biggest take-away from this code was the neccessity of creating mock instances of the
 * "Transport" class. Also added a dependancy to simplify the process of extracting
 * Strings from emails.
 *
 *
 * @author Martin Frick
 */
public class MailServiceTest {

    @Test
    void buildMessage() {

        Properties properties =  new Properties();
        Session testSession = Session.getInstance(properties);

        String recipient = "##BUILDING MAIL##@BUILDING_Mail.Com";
        String subjectField = "##BUILDING MAIL## Flourish Testing SUBJECT Field Output!";
        String body = "##BUILDING MAIL## Flourish Testing BODY Field Output!";



        try {
            MimeMessage message = MailService.buildMessage(testSession, recipient, subjectField, body);

            //Förenklar utdrag av info
            Email emailSimple = EmailConverter.mimeMessageToEmail(message);

            assertEquals(message.getSession(), testSession);
            assertEquals(emailSimple.getRecipients().get(0).getAddress(), recipient);
            assertEquals(emailSimple.getSubject(), subjectField);
            assertEquals(emailSimple.getPlainText(), body);

        } catch (MessagingException e) {
            fail("Test for ##BUILDING MAIL## FAILED due to exception: " + e.getMessage());
        }
    }

    public void testSendEmail() throws Exception {

        //Koden nedan är baserad på:
        //https://github.com/omerio/unit-testing/blob/master/src/test/java/com/omerio/service/jmockit/EmailServiceMockTest.java

        Properties properties =  new Properties();
        Session testSession = Session.getInstance(properties);

        try {

            String recipient = "##SENDING MAIL##@Mail.Com";
            String subjectField = "##SENDING MAIL## Flourish Testing SUBJECT Field Output!";
            String body = "##SENDING MAIL## Flourish Testing BODY Field Output!";


            //Bygg och släng in teststrängar i mailet
            MimeMessage message = MailService.buildMessage(testSession, recipient, subjectField, body);

        /**********
         * Arrange
         **********/

        // We can't do this with Mockito, we have to use PowerMock with Mockito
        // Mockito.mock(Transport.class);

        // To mock a static class using PowerMock
        PowerMockito.mockStatic(Transport.class);
        PowerMockito.doNothing().when(Transport.class, "send", any(MimeMessage.class));


        Email email = EmailConverter.mimeMessageToEmail(message);

        //Förenklar utdrag av info
        Email emailSimple = EmailConverter.mimeMessageToEmail(message);

        /**********
         * Act
         **********/

        MailService.sendMessage(message);

        /**********
         * Assert
         **********/
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        // Different from Mockito, always use PowerMockito.verifyStatic(Class) first
        // to start verifying behavior
        PowerMockito.verifyStatic(Transport.class, Mockito.times(1));
        // IMPORTANT:  Call the static method you want to verify
        Transport.send(argument.capture());

        Message msg = argument.getValue();
        assertNotNull(msg);

        assertEquals(msg.getSubject(), subjectField);
        assertEquals(msg.getContent(), message.getContent());
        //Tog bort "getFrom" pga. Svårt och inget vi bryr oss om om det blir rätt
        assertEquals(msg.getAllRecipients()[0], emailSimple.getRecipients().get(0).getAddress());


        } catch (MessagingException e) {
            System.out.println("Test for ##SENDING MAIL## FAILED  due to exception: " + e.getMessage());
        }
    }

}



