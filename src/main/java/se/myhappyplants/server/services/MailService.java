package se.myhappyplants.server.services;

import se.myhappyplants.server.config.MailConfig;
import se.myhappyplants.server.db.MySQLDatabaseConnection;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class MailService {


    private String to = "joar@bomikael.se" ;
    private String from = MailConfig.MAIL_USER;
    private String host = MailConfig.MAIL_HOST;
    private Session session;


    public MailService() {

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MailConfig.MAIL_HOST);
        properties.put("mail.smtp.port", MailConfig.MAIL_PORT);
        properties.put("mail.smtp.ssl.enable", MailConfig.MAIL_SMTP_SSL_ENABLE);
        properties.put("mail.smtp.auth", MailConfig.MAIL_SMTP_AUTH);

        this.session = Session.getInstance(System.getProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConfig.MAIL_USER, "dzjofhkturdvqbkn");
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

    public static boolean sendPasswordRecoveryMail(String email) {

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MailConfig.MAIL_HOST);
        properties.put("mail.smtp.port", MailConfig.MAIL_PORT);
        properties.put("mail.smtp.ssl.enable", MailConfig.MAIL_SMTP_SSL_ENABLE);
        properties.put("mail.smtp.auth", MailConfig.MAIL_SMTP_AUTH);

        Session session = Session.getInstance(System.getProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConfig.MAIL_USER, MailConfig.MAIL_PASSWORD);
            }
        });

        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(MailConfig.MAIL_USER));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            message.setSubject("Password recovery");

            message.setText("Your new password is: ");

            System.out.println("sending...");
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        new MailService();
    }

}
