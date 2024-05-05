package org.example;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        final String username = "youremail@gmail.com"; // Replace with your Gmail username
        final String password = "yourpassword"; // Replace with your Gmail password

        // Set up properties to access Gmail's STMP server
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // Enable TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create an email with specified sender, recipient, subject, and body and sent it using Gmail's SMTP server.
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully via Gmail");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example email details - replace with actual recipient details
        String to = "recipient@example.com";
        String subject = "Test Email from Java App";
        String body = "Hello, this is a test email sent from the Java EmailSender class.";

        sendEmail(to, subject, body);
    }
}


