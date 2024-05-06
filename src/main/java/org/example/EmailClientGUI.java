package org.example;

import javax.swing.*;

public class EmailClientGUI extends JFrame {
    public EmailClientGUI() {
        setTitle("Java Email Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Initialization of UI components will be done here.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmailClientGUI());
        // To launch the GUI on the Event Dispatch Thread (EDT)
        // It ensures that the GUI is initialized and displayed correctly, even if other tasks are being performed by the application's main thread.
    }
}