package pages;

import receipt.SaveFinal;
import receipt.Track;
import tabs.CostsTab;
import tabs.EditingTab;
import tabs.LabellingTab;
import tabs.ReceiptsTab;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;


public class MainPage {
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    JMenuItem undo = new JMenuItem("Undo");
    JMenuItem redo = new JMenuItem("Redo");

    MainPage(DefaultListModel<String> receipt, Map<String, Double> labels) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(400, 590);
        frame.setTitle("Receipt Splitter");
        frame.setIconImage(new ImageIcon("Assets/Logo.png").getImage());
        frame.setResizable(false); // Application setup for receipt size documents
        frame.setBackground(Color.decode("#ce8e8e7"));

        // Declare tabs outside tabbedPane so they can be updated
        ReceiptsTab receiptsTab = new ReceiptsTab(receipt);
        EditingTab editTab = new EditingTab(receipt);
        LabellingTab labellingTab = new LabellingTab(labels, receipt);
        CostsTab costsTab = new CostsTab(labels, receipt);

        setupMenu(frame, receipt, labels); // Initial JMenu setup

        tabbedPane.add("Receipts", receiptsTab);
        tabbedPane.add("Edit", editTab);
        tabbedPane.add("Label", labellingTab);
        tabbedPane.add("Costs", costsTab);

        tabbedPane.setEnabledAt(3, false); // Not available until labels created

        // Switching tabs
        tabbedPane.getModel().addChangeListener(e -> {
            refreshTabs(receipt, receiptsTab, editTab, labellingTab);
            if (!labels.isEmpty()) {
                tabbedPane.setEnabledAt(3, true); // User can now access costs tab
                // Update combo box and table due to label updates
                costsTab.setupLabelSelection(labels, receipt);
                costsTab.setupLabelSelection(labels, receipt);
            } else {
                tabbedPane.setEnabledAt(3, false);
            }
        });

        undo.addActionListener(e -> {
            if (Track.undoTracker.size() > 0) { // Potential undoes need to exist
                Track.undo(receipt);
                refreshTabs(receipt, receiptsTab, editTab, labellingTab);
            } else {
                JOptionPane.showMessageDialog(frame, "No edits to be undone");
            }
        });
        redo.addActionListener(e -> {
            if (Track.redoTracker.size() > 0) {
                Track.redo(receipt);
                refreshTabs(receipt, receiptsTab, editTab, labellingTab);
            } else {
                JOptionPane.showMessageDialog(frame, "No edits to be redone");
            }
        });

        frame.add(tabbedPane);
        frame.setLocationByPlatform(true); // Attempt to select best position for GUI
        frame.setVisible(true);
    }

    private void setupMenu(JFrame frame, DefaultListModel<String> receipt, Map<String, Double> labels) {
        JMenuBar menu = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem save = new JMenuItem("Save");
        JMenuItem restart = new JMenuItem("Restart");
        JMenuItem usage = new JMenuItem("Usage");
        JMenuItem disclaimer = new JMenuItem("Disclaimer");

        save.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Save As"); // Get file name
            if (!Objects.equals(fileName, null) && !fileName.equals("")) {
                // Prompt user with file selection
                int fileType = JOptionPane.showOptionDialog(frame, "Select below", "Save As", JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, new String[]{"PDF", "Text File"}, null);
                if (fileType == 0) { // PDF
                    SaveFinal.asPDF(receipt, labels, fileName);
                } else { // Text file
                    SaveFinal.asTXT(receipt, labels, fileName);
                }
                // Refresh instance
                frame.dispose();
                new MainPage(receipt, labels);
            } else { // Invalid filename
                JOptionPane.showMessageDialog(frame, "A file name is required");
            }
        });
        // Dispose of current frame and open new start page
        restart.addActionListener(e -> {
            int cancel = JOptionPane.showConfirmDialog(frame, "If current receipt is not saved it will be lost."
                    + "", "Continue?", JOptionPane.YES_NO_OPTION);
            if (cancel == 0) {
                frame.dispose();
                new StartPage();
            }
        });

        // Help messages
        usage.addActionListener(e -> JOptionPane.showMessageDialog(frame, """
                The Receipt tab will show the current receipt and any saved receipts.
                The Edit tab allows you to to select a line to edit, delete or center the line or to add a new line.
                The Labelling tab allows you to track individual lines of the receipt.
                The Costs tab shows a breakdown your labels."""));
        disclaimer.addActionListener(e -> JOptionPane.showMessageDialog(frame, """
                This application has very basic functionality and is designed for receipts, it will struggle to handle large documents
                Once a receipt is saved its labels cannot be edited.
                Tesseract OCR is used for image transcription.
                Due to the nature of PDFs and images there will be mistakes with the translated receipts.
                Thank you for your usage."""));

        optionsMenu.add(save);
        optionsMenu.add(restart);
        optionsMenu.add(undo);
        optionsMenu.add(redo);
        helpMenu.add(usage);
        helpMenu.add(disclaimer);

        menu.add(optionsMenu);
        menu.add(helpMenu);
        frame.setJMenuBar(menu);
    }

    private void refreshTabs(DefaultListModel<String> receipt, ReceiptsTab receipts, EditingTab edit, LabellingTab label) {
        // Update components using model
        receipts.setupSelection(receipt);
        edit.displayReceipt(receipt);
        label.displayReceipt(receipt);
    }
}