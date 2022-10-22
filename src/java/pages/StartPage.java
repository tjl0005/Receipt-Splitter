package pages;

import receipt.Prepare;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;


public class StartPage {
    final JFrame frame = new JFrame();
    final JButton startButton = new JButton("Add Receipt");

    /**
     * Initial page shown to the user containing a button allowing the user to select a file to open
     */
    public StartPage() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(300, 300, 300, 300);
        frame.setTitle("Open File");
        frame.setIconImage(new ImageIcon("Assets/Logo.png").getImage());
        frame.setResizable(false);

        startButton.setBounds(125, 200, 250, 25);
        startButton.setFocusable(false); // Remove highlight box
        startButton.addActionListener(this::startOperation);

        frame.add(startButton);
        frame.setVisible(true);
    }

    /**
     * Get the selected file from the user, open it and pass the receipt list model to the main page for viewing/editing
     * @param e the action event of the button
     */
    private void startOperation(ActionEvent e){
        if (e.getSource() == startButton) {
            // Only allow selection of PNG, JPG and PDF files
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter(
                    "PNG, JPG & PDF Files", "png", "jpg", "pdf");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(typeFilter);
            fileChooser.setCurrentDirectory(new File("Receipts/Example/"));
            // Get user to select file
            int selectedFile = fileChooser.showOpenDialog(null);

            // Open file
            if (selectedFile == JFileChooser.APPROVE_OPTION) {
                String file = String.valueOf(fileChooser.getSelectedFile());
                DefaultListModel<String> receiptModel = new DefaultListModel<>();
                receiptModel.addAll(Prepare.get(file));

                new MainPage(receiptModel, new LinkedHashMap<>());
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "There was an issue preparing your file");
            }
        }
    }

}