package org.example;
import java.io.File;
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

        // Create an email with specified sender, recipient, subject, and body
        // and sent it using Gmail's SMTP server.
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Added functionality to attach one or more files to the email.
            // Each file selected for attachment is added as a separate MimeBodyPart,
            // and all parts (text and attachments) are combined into a Multipart object.
            Multipart multipart = new MimeMultipart();

            // Add text as a MimeBodyPart object
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);
            multipart.addBodyPart(textPart);

            // Add each attachment as a MimeBodyPart object
            for (File file : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email sent successfully with attachments.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example email details - replace with actual recipient details
        String to = "amirhossein.andoohgin@gmail.com";
        String subject = "Test Email from Java App";
        String body = "Hello, this is a test email sent from the Java EmailSender class.";

        sendEmail(to, subject, body);
    }
}


