package pages;

import classes.Receipt;
import classes.ReceiptPDF;
import tabs.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MainPage {
    final ImageIcon img = new ImageIcon("Assets/Logo.jpg");
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    final JLabel userID = new JLabel("User"); // Not yet implemented
    final JButton saveButton = new JButton("Save");
    final JButton resetButton = new JButton("New Receipt");

    public MainPage(DefaultListModel<String> receiptModel, Map<String, Double> labelMap) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(400, 590);
        frame.setTitle("Receipt Splitter");
        frame.setIconImage(img.getImage());
        frame.setResizable(false);

        setupMenu(frame, receiptModel, labelMap);

        // Declare tabs outside tabbedPane so they can be updated
        ReceiptsTab receiptsTab = new ReceiptsTab(userID.getText(), receiptModel);
        EditingTab editTab = new EditingTab(receiptModel);
        LabellingTab labellingTab =  new LabellingTab(labelMap, receiptModel);
        CostsTab costsTab = new CostsTab(labelMap, receiptModel);

        tabbedPane.add("All Receipts", receiptsTab);
        tabbedPane.add("Edit", editTab);
        tabbedPane.add("Label", labellingTab);
        tabbedPane.add("Costs", costsTab);

        // Not available until labels exist
        tabbedPane.setEnabledAt(3, false);

        // Switching tabs
        tabbedPane.getModel().addChangeListener(e -> {
            // Update components using model
            receiptsTab.setComboBox(receiptModel);
            editTab.setScrollPane(receiptModel);
            labellingTab.setScrollPane(receiptModel);

            if (!labelMap.isEmpty()){
                tabbedPane.setEnabledAt(3, true); // User can now access costs tab
                // Update combo box and table due to label updates
                costsTab.setComboBox(labelMap, receiptModel);
                costsTab.setTable(labelMap, receiptModel);
            }
            else{
                tabbedPane.setEnabledAt(3, false);
            }
        });

        frame.add(tabbedPane);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private void setupMenu(JFrame frame, DefaultListModel<String> receiptModel, Map<String, Double> labelMap) {
        JMenuBar menu = new JMenuBar();

        // Save receipt with given name
        saveButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Save As"); // Get file name
            if (!Objects.equals(fileName, null) && !fileName.equals("")) {
                // Prompt user with file selection
                int fileType = JOptionPane.showOptionDialog(frame,"Select below", "Save As", JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, new String[]{"PDF", "Text File"}, null);

                if (fileType == 0) { // PDF
                    ReceiptPDF.saveAsPDF(receiptModel, labelMap, userID.getText(), fileName);
                }
                else{ // Text file
                    Receipt.toTxt(receiptModel, labelMap, userID.getText(), fileName);
                }
                // Refresh instance
                frame.dispose();
                new MainPage(receiptModel, labelMap);
            }
            else{ // Invalid filename
                JOptionPane.showMessageDialog(frame, "A file name is required");
            }
        });

        // Restart the application without saving
        resetButton.addActionListener(e -> {
            int cancel = JOptionPane.showConfirmDialog(frame, "If current receipt is not saved it will be lost."
                            + "", "Continue?", JOptionPane.YES_NO_OPTION);
            if (cancel == 0) {
                frame.dispose();
                new StartPage();
            }
        });

        saveButton.setFocusable(false);
        resetButton.setFocusable(false);

        menu.add(saveButton);
        menu.add(resetButton);
        menu.add(new JSeparator());
        menu.add(userID);
        menu.add(new JSeparator());

        frame.setJMenuBar(menu);
    }
}