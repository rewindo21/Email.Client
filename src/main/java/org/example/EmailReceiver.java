package org.example;
import javax.mail.*;
import java.util.Properties;

public class EmailReceiver {
    public static void receiveEmail(String username, String password) {
        // Set up properties to access Gmail's IMAP server
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            Session emailSession = Session.getDefaultInstance(properties);

            // Utilize the Session object to obtain a Store object configured for IMAP
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", username, password);

            // Open the "INBOX" folder from the store in read-only mode,
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Fetch an array of Message objects from the inbox
            Message[] messages = emailFolder.getMessages();
            System.out.println("Number of emails: " + messages.length);

            for (Message message : messages) {
                System.out.println("Email Subject: " + message.getSubject());
            }

            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String username = "yourgmail@gmail.com"; // Replace with your Gmail username
        String password = "yourpassword"; // Replace with your Gmail password

        receiveEmail(username, password);
    }
}