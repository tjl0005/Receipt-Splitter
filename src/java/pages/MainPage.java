package pages;

import receipt.Prepare;
import receipt.Save;
import receipt.Track;
import tabs.CostsTab;
import tabs.EditingTab;
import tabs.LabellingTab;
import tabs.ReceiptsTab;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * Used to set up the main landing page of the application and provide access to tabs
 */
class MainPage {
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    JMenuItem undo = new JMenuItem("Undo");
    JMenuItem redo = new JMenuItem("Redo");

    /**
     * Generates a frame which basic formatting, a tabbed pane and an options menu
     * @param receipt a list model containing the scanned contents of the receipt
     * @param labels a hashmap containing the labels and their total costs
     */
    MainPage(DefaultListModel<String> receipt, Map<String, Double> labels) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(390, 535);
        frame.setTitle("Open Book");
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

        tabbedPane.setEnabledAt(3, !labels.isEmpty()); // Not available until labels created

        // Switching tabs
        tabbedPane.getModel().addChangeListener(e -> {
            refreshTabs(receipt, receiptsTab, editTab, labellingTab);
            if (!labels.isEmpty()) {
                tabbedPane.setEnabledAt(3, true); // User can now access costs tab
                // Update combo box and table due to label updates
                costsTab.setupLabelSelection(labels, receipt);
                costsTab.setupLabelBreakdown(labels, receipt);
            } else {
                tabbedPane.setEnabledAt(3, false);
            }
        });

        undo.addActionListener(e -> {
            if (!Track.undoTracker.isEmpty()) { // Potential undoes need to exist
                Track.undo(receipt);
                refreshTabs(receipt, receiptsTab, editTab, labellingTab);
            } else {
                JOptionPane.showMessageDialog(frame, "No edits to be undone");
            }
        });
        redo.addActionListener(e -> {
            if (!Track.redoTracker.isEmpty()) {
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

    /**
     * Used to add the menubar to the frame, contains options and help menus
     * @param frame the frame to add the menu to
     * @param receipt the list model containing the receipt
     * @param labels the hashmap containing the labels and their total costs
     */
    private void setupMenu(JFrame frame, DefaultListModel<String> receipt, Map<String, Double> labels) {
        JMenuBar menu = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem save = new JMenuItem("Save");
        JMenuItem restart = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open Receipt");
        JMenuItem usage = new JMenuItem("Usage");
        JMenuItem disclaimer = new JMenuItem("Disclaimer");

        save.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Save As"); // Get file name
            if (!Objects.equals(fileName, null) && !fileName.isEmpty()) {
                // Prompt user with file selection
                int fileType = JOptionPane.showOptionDialog(frame, "Select below", "Save As", JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, new String[]{"PDF", "Text File"}, null);
                if (fileType == 0) { // PDF
                    Save.asPDF(receipt, labels, fileName);
                } else { // Text file
                    Save.asTXT(receipt, labels, fileName);
                }
                // Refresh instance
                frame.dispose();
                new MainPage(receipt, labels);
            } else { // Invalid filename
                JOptionPane.showMessageDialog(frame, "A file name is required");
            }
        });

        open.addActionListener(e ->{
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
                Map<String, Double> newLabels = Prepare.labelMapFromPDF(file);
                DefaultListModel<String> newReceipt = new DefaultListModel<>();
                newReceipt.addAll(Prepare.get(file));

                new MainPage(newReceipt, newLabels);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "There was an issue preparing your file");
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
                The Costs tab shows a breakdown your labels.
                Undo and Redo have a limit of 5 changes."""));
        disclaimer.addActionListener(e -> JOptionPane.showMessageDialog(frame, """
                This application has very basic functionality and is designed for receipts, it will struggle to handle large documents
                Once a receipt is saved its labels cannot be edited.
                Tesseract OCR is used for image transcription.
                Due to the nature of PDFs and images there will be mistakes with the translated receipts.
                Thank you for reading."""));

        optionsMenu.add(restart);
        optionsMenu.add(open);
        optionsMenu.add(save);

        optionsMenu.add(undo);
        optionsMenu.add(redo);
        helpMenu.add(usage);
        helpMenu.add(disclaimer);

        menu.add(optionsMenu);
        menu.add(helpMenu);
        frame.setJMenuBar(menu);
    }

    /**
     * Refresh the current tabs display of receipt models, it is called when a change is made either via tab or undo/redo
     * @param receipt the list model containing the receipt
     * @param receipts the receipts tab object
     * @param edit the editing tab object
     * @param label the labelling tab object
     */
    private static void refreshTabs(DefaultListModel<String> receipt, ReceiptsTab receipts, EditingTab edit, LabellingTab label) {
        // Update components using model
        receipts.setupSelection(receipt);
        edit.displayReceipt(receipt);
        label.displayReceipt(receipt);
    }
}