package org.example;

import javax.swing.*;
import java.awt.*;

public class EmailClientGUI extends JFrame {
    public EmailClientGUI() {
        setTitle("Java Email Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setVisible(true);
    }

    // Initialization of UI components
    private void initUI() {
        // Utilized JList with a DefaultListModel to list email subjects,
        // making it scrollable by adding it to a JScrollPane.
        DefaultListModel<String> emailListModel = new DefaultListModel<>();
        JList<String> emailList = new JList<>(emailListModel);
        add(new JScrollPane(emailList), BorderLayout.WEST);
        // Added a JTextArea for displaying the content of the selected email,
        // also within a JScrollPane to enable scrolling.
        JTextArea emailContent = new JTextArea();
        emailContent.setEditable(false);
        add(new JScrollPane(emailContent), BorderLayout.CENTER);
        // Added a simple button for initiating the email composition process.
        JButton composeButton = new JButton("Compose");
        add(composeButton, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmailClientGUI());
        // To launch the GUI on the Event Dispatch Thread (EDT)
        // It ensures that the GUI is initialized and displayed correctly, even if other tasks are being performed by the application's main thread.
    }
}