package Pages;

import Classes.Receipt;
import Tabs.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;


public class MainPage {
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    final ImageIcon img = new ImageIcon("Assets/Logo.jpg");
    JButton resetButton = new JButton("New");
    JButton saveButton = new JButton("Save");
    JLabel userID = new JLabel("User");

    public MainPage(List<String> receipt, List<String> labels, int tabIndex) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(400, 590);
        frame.setTitle("Receipt Splitter");
        frame.setJMenuBar(createMenu(receipt, labels));
        frame.setIconImage(img.getImage());
        frame.setResizable(false);

        tabbedPane.add("All Receipts", new ReceiptsTab(receipt, userID.getText()));
        tabbedPane.add("Edit", new EditingTab(frame, receipt, labels));
        tabbedPane.add("Label", new LabellingTab(frame, receipt, labels));
        tabbedPane.add("Costs", new CostsTab(frame, receipt, labels));

        // Reset to original tab
        tabbedPane.setSelectedIndex(tabIndex);
        frame.add(tabbedPane);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JMenuBar createMenu(List<String> receipt, List<String> labels) {
        JMenuBar menu = new JMenuBar();

        saveButton.setFocusable(false);
        resetButton.setFocusable(false);

        saveButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Save As");
            if (!Objects.equals(fileName, null)) {
                Receipt.writeToFile(receipt, userID.getText(), fileName);
                frame.dispose();
                new MainPage(receipt, labels, 0);
            }
        });

        resetButton.addActionListener(e -> {
            int cancel = JOptionPane.showConfirmDialog(frame, "If current receipt is not saved it will be lost." +
                            "", "Continue?",
                    JOptionPane.YES_NO_OPTION);
            if (cancel == 0) {
                frame.dispose();
                new StartPage();
            }
        });

        menu.add(saveButton);
        menu.add(resetButton);
        menu.add(new JSeparator());
        menu.add(userID);
        menu.add(new JSeparator());
        return menu;
    }
}