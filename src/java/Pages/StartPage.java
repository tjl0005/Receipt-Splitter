package Pages;

import Classes.Receipt;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class StartPage {
    final JFrame frame = new JFrame();
    final JButton startButton = new JButton("Add Receipt");

    public StartPage() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(300, 300, 300, 300);
        frame.setTitle("Open File");
        frame.setIconImage(new ImageIcon("Assets/Logo.jpg").getImage());

        startButton.setBounds(125, 200, 250, 25);
        startButton.setFocusable(false); // Remove highlight box
        startButton.addActionListener(e -> {
            if (e.getSource() == startButton) {
                // Only allow selection of PNG, JPG and PDF files
                FileNameExtensionFilter typeFilter = new FileNameExtensionFilter(
                        "PNG, JPG & PDF Files", "png", "jpg", "pdf");

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(typeFilter);
                fileChooser.setCurrentDirectory(new File("Receipts/Original/"));
                // Get user to select file
                int selectedFile = fileChooser.showOpenDialog(null);

                // Open file
                if (selectedFile == JFileChooser.APPROVE_OPTION) {
                    String file = fileChooser.getSelectedFile().getName();
                    new MainPage(Receipt.get(file), 0);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "There was an issue preparing your file");
                }
            }
        });

        frame.add(startButton);
        frame.setVisible(true);
    }

}