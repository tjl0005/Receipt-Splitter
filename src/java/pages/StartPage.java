package pages;

import receipt.Prepare;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.*;


public class StartPage {
    private final JFrame frame = new JFrame();

    /**
     * Initial page shown to the user containing a button allowing the user to select a file to open
     */
    public StartPage() {
        frame.setTitle("Open File");
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/Logo.png"))).getImage());
        frame.setBounds(300, 300, 300, 300);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton openButton = new JButton("Open Receipt");
        openButton.setFocusable(false); // Remove highlight box
        openButton.addActionListener(e -> {
            if (openOperation()) {
                frame.dispose();
            } else{
                JOptionPane.showMessageDialog(frame, "There was an issue preparing your file");
            }
        });

        frame.add(openButton);
        frame.setVisible(true);
    }

    /**
     * Get the selected file from the user, open it and pass the receipt list model to the main page for viewing/editing
     */
    static boolean openOperation(){
            // Only allow selection of PNG, JPG and PDF files
            FileNameExtensionFilter typeFilter = new FileNameExtensionFilter(
                    "PNG, JPG & PDF Files", "png", "jpg", "pdf");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(typeFilter);
            fileChooser.setCurrentDirectory(new File("Receipts/OpenBook/"));
            // Get user to select file
            int selectedFile = fileChooser.showOpenDialog(null);

            // Open file
            if (selectedFile == JFileChooser.APPROVE_OPTION) {
                String file = String.valueOf(fileChooser.getSelectedFile());
                DefaultListModel<String> newReceipt = new DefaultListModel<>();
                newReceipt.addAll(Prepare.getReceipt(file));
                // Attempt to get label details from PDF file
                Map<String, Double> newLabels = Prepare.labelMapFromPDF(file);

                new MainPage(newReceipt, newLabels);
                return true;
            } else {
                return false;
        }
    }
}