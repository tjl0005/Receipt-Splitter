import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class StartPage implements ActionListener {
    final JFrame frame = new JFrame();
    final JButton startButton = new JButton("Add Receipt");
    private String file = "";

    public StartPage() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);
        frame.setTitle("Receipt Splitter - Open File");

        startButton.setBounds(125, 200, 250, 25);
        startButton.setFocusable(false); // Remove highlight box
        startButton.addActionListener(this);

        frame.add(startButton);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

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
                file = fileChooser.getSelectedFile().getName();
            }

            // Passing file to displayed
            new ViewingPage(receipt.get(file));
            frame.dispose();
        }
    }
}