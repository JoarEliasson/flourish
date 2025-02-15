package com.flourish.server.services;

import com.flourish.server.config.MailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {


    private static String to = "mskolfrick@outlook.com" ;
    private Session session;


    public MailService() {

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MailConfig.MAIL_HOST);
        properties.put("mail.smtp.port", MailConfig.MAIL_PORT);
        properties.put("mail.smtp.ssl.enable", MailConfig.MAIL_SMTP_SSL_ENABLE);
        properties.put("mail.smtp.auth", MailConfig.MAIL_SMTP_AUTH);

        this.session = Session.getInstance(System.getProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConfig.MAIL_USER, MailConfig.MAIL_AUTH_PASSWORD);
            }
        });

        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(MailConfig.MAIL_USER));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("This is the Subject Line!");

            message.setText("This is actual message");

            System.out.println("sending...");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    public static boolean sendPasswordRecoveryMail(String email, String resetToken) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MailConfig.MAIL_HOST);
        properties.put("mail.smtp.port", MailConfig.MAIL_PORT);
        properties.put("mail.smtp.ssl.enable", MailConfig.MAIL_SMTP_SSL_ENABLE);
        properties.put("mail.smtp.auth", MailConfig.MAIL_SMTP_AUTH);

        Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MailConfig.MAIL_USER, MailConfig.MAIL_AUTH_PASSWORD);
                    }
            }
        );

        try {
      /*      MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MailConfig.MAIL_USER));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Flourish: Password Reset Token");

            message.setText("Use this token to reset your password in the MyHappyPlants app:\n\n" + resetToken);

            Transport.send(message);
            return true;*/

//          Code below are changes done during Unit Test re-writes
            String subjectField = "Flourish: Password Reset Token";
            String body = "Use this token to reset your password in the MyHappyPlants app:\n\n" + resetToken;

            sendMessage( buildMessage(session, to, subjectField, body) );

            return true;


        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Build message method created for Unit Testing purposes.
     *
     * @author Martin Frick
     * 2025-02-14
     * */
    private static MimeMessage buildMessage(Session session, String recipient, String subjectField, String body) throws MessagingException {

        MimeMessage Msg = new MimeMessage(session);

        Msg.setFrom(new InternetAddress(MailConfig.MAIL_USER));
        Msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        Msg.setSubject(subjectField);
        Msg.setText(body);

        return Msg;
    }

    /**
     * Send Message method created for Unit Testing purposes.
     *
     * @author Martin Frick
     * 2025-02-14
     * */
    private static void sendMessage(MimeMessage message) throws MessagingException {

        Transport.send(message);
    }

    public static void main(String[] args) {
        new MailService();
    }
}
