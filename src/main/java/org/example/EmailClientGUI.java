package org.example;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.awt.Color;


public class EmailClientGUI extends JFrame {

    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);

    // Utilized JList with a DefaultListModel to list email subjects
    private DefaultListModel<String> emailListModel = new DefaultListModel<>();
    private JList<String> emailList = new JList<>(emailListModel);
    // Added a JTextArea for displaying the content of the selected email
    private JTextArea emailContent = new JTextArea();
    private Message[] messages;

    public EmailClientGUI() {
        setTitle("Java Email Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setVisible(true);

        // Add window listener to handle application close
        // This ensures when we exit the application, the email session is properly closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (EmailSessionManager.getInstance() != null) {
                        EmailSessionManager.getInstance().close(); // Close the email session
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static final Color BACKGROUND_COLOR = new Color(23, 240, 250);
    private static final Color ACTION_PANEL_COLOR = new Color(200, 22, 240);
    private static final Color BUTTON_COLOR = new Color(180, 220, 24);


    // Initialization of UI components
    private void initUI() {
        // Main pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // means that the user can select only one item at a time from the list.
        emailList.addListSelectionListener(this::emailListSelectionChanged); // register an event listener that will be notified whenever the selection in the emailList changes.
        JScrollPane listScrollPane = new JScrollPane(emailList); // within a JScrollPane to enable scrolling.
        listScrollPane.setBackground(BACKGROUND_COLOR);

        emailContent.setEditable(false);    // making it uneditable
        JScrollPane contentScrollPane = new JScrollPane(emailContent); // within a JScrollPane to enable scrolling.
        contentScrollPane.setBackground(BACKGROUND_COLOR);

        // Add listScrollPane and contentScrollPane to the main pane
        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(contentScrollPane);

        getContentPane().setBackground(BACKGROUND_COLOR);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Action panel
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.setBackground(ACTION_PANEL_COLOR);

        // Buttons
        JButton composeButton = new JButton("Compose");
        JButton refreshInboxButton = new JButton("Refresh Inbox");
        composeButton.setBackground(BUTTON_COLOR);
        refreshInboxButton.setBackground(BUTTON_COLOR);

        composeButton.addActionListener(e -> showComposeDialog("", "", ""));
        refreshInboxButton.addActionListener(e -> refreshInbox());

        bottomPanel.add(composeButton);
        bottomPanel.add(refreshInboxButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Prompt the user to log in when the application starts
        SwingUtilities.invokeLater(this::showLoginDialog);
    }


    // Implement a login dialog that prompts the user to provide their email credentials.
    private void showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Email:"));
        panel.add(usernameField);
        panel.add(new JLabel("App Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Initialize EmailSessionManager here
                EmailSessionManager.getInstance(username, password);
                refreshInbox(); // Refresh inbox to load emails
            } catch (MessagingException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to initialize email session: " + e.getMessage(),
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Login cancelled.");
        }
    }

    // Define a refreshInbox method to use the EmailSessionManager and its getInstance and receiveEmail methods to fetch emails based on the credentials previously provided.
    private void refreshInbox() {
        try {
            messages = EmailSessionManager.getInstance().receiveEmail();
            emailListModel.clear();
            for (Message message : messages) {
                emailListModel.addElement(message.getSubject() + " - From: " + InternetAddress.toString(message.getFrom()));
            }
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch emails: " + e.getMessage(),
                    "Fetch Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // When a user selects an email from the list, the application fetches and displays the email's subject, sender information, and body content in a dedicated reading area.
    private void emailListSelectionChanged(ListSelectionEvent e) {
        // Checks if the selection change is finalized and a valid item is selected.
        if (!e.getValueIsAdjusting() && emailList.getSelectedIndex() != -1) {
            try {
                Message selectedMessage = messages[emailList.getSelectedIndex()];
                emailContent.setText(""); // Clear previous content
                emailContent.append("Subject: " + selectedMessage.getSubject() + "\n\n");
                emailContent.append("From: " + InternetAddress.toString(selectedMessage.getFrom()) + "\n\n");
                // Use the new method to get and display the email body
                emailContent.append(getTextFromMessage(selectedMessage));
            } catch (MessagingException | IOException ex) {
                emailContent.setText("Error reading email content: " + ex.getMessage());
            }
        }
    }

    // Extracts the textual content from an email message.
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        // If the message is plain text, return the content directly.
        if (message.isMimeType("text/plain")) {
            return (String) message.getContent();
        // If the message is multipart, iterate through each part and return the content of the first plain text part found.
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        // If neither condition is met
        return "No readable content found.";
    }

    private void showComposeDialog(String s, String string, String s1) {
        JDialog composeDialog = new JDialog(this, "Compose Email", true);
        composeDialog.setLayout(new BorderLayout(5, 5));

        // Introduced UI components for entering the recipient, subject, and body of the email.
        Box fieldsPanel = Box.createVerticalBox();
        JTextField toField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea bodyArea = new JTextArea(10, 20);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);

        fieldsPanel.add(new JLabel("To:"));
        fieldsPanel.add(toField);
        fieldsPanel.add(new JLabel("Subject:"));
        fieldsPanel.add(subjectField);

        // Provide a button that uses our AttachmentChooser class to select files for attachment.
        // and add a send button that collects the input data, including attachments, and calls EmailSender.
        JPanel bottomPanel = new JPanel();
        JButton attachButton = new JButton("Attach Files");
        JButton sendButton = new JButton("Send");
        JLabel attachedFilesLabel = new JLabel("No files attached");

        List<File> attachedFiles = new ArrayList<>();

        attachButton.addActionListener(e -> {
            File[] files = AttachmentChooser.chooseAttachments();
            attachedFiles.addAll(Arrays.asList(files));
            attachedFilesLabel.setText(attachedFiles.size() + " files attached");
        });

        sendButton.addActionListener(e -> {
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();
            File[] attachments = attachedFiles.toArray(new File[0]);
            EmailSender.sendEmailWithAttachment(to, subject, body, attachments);
            composeDialog.dispose();
        });

        bottomPanel.add(attachButton);
        bottomPanel.add(sendButton);

        composeDialog.add(fieldsPanel, BorderLayout.NORTH);
        composeDialog.add(new JScrollPane(bodyArea), BorderLayout.CENTER); // Ensure body area is scrollable
        composeDialog.add(bottomPanel, BorderLayout.SOUTH);

        composeDialog.pack(); // Adjust dialog size to fit content
        composeDialog.setLocationRelativeTo(this); // Center dialog relative to the main window
        composeDialog.setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmailClientGUI());
        // To launch the GUI on the Event Dispatch Thread (EDT)
        // It ensures that the GUI is initialized and displayed correctly,
        // even if other tasks are being performed by the application's main thread.
    }
}