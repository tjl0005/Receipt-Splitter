package pages;

import receipt.Save;
import receipt.Track;
import tabs.CostsTab;
import tabs.EditingTab;
import tabs.LabellingTab;
import tabs.ReceiptsTab;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

// TODO: Optimise -> Once done rebuild jar file -> Move Jar file to main directory

/**
 * Used to set up the main landing page of the application and provide access to tabs
 */
class MainPage {
    private final JFrame frame = new JFrame();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JMenuItem undo = new JMenuItem("Undo");
    private final JMenuItem redo = new JMenuItem("Redo");

    /**
     * Generates a frame which basic formatting, a tabbed pane and an options menu
     * @param receipt a list model containing the scanned contents of the receipt
     * @param labels a hashmap containing the labels and their total costs
     */
    MainPage(DefaultListModel<String> receipt, Map<String, Double> labels) {
        frame.setTitle("Open Book");
        frame.setLayout(new GridLayout());
        frame.setSize(390, 535);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/Logo.png"))).getImage());
        frame.setResizable(false); // Application setup for receipt size documents
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setJMenuBar(setupMenu(frame, receipt, labels)); // Initial JMenu setup

        // Declare tabs outside tabbedPane so they can be updated
        ReceiptsTab receiptsTab = new ReceiptsTab(receipt);
        EditingTab editTab = new EditingTab(receipt);
        LabellingTab labellingTab = new LabellingTab(labels, receipt);
        CostsTab costsTab = new CostsTab(labels, receipt);

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
     *
     * @param frame   the frame to add the menu to
     * @param receipt the list model containing the receipt
     * @param labels  the hashmap containing the labels and their total costs
     * @return the menu bar to added to the frame
     */
    private JMenuBar setupMenu(JFrame frame, DefaultListModel<String> receipt, Map<String, Double> labels) {
        JMenuBar menu = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem save = new JMenuItem("Save");
        JMenuItem open = new JMenuItem("Open");
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
            if (StartPage.openOperation()) {
            frame.dispose();
            } else {
            JOptionPane.showMessageDialog(frame, "There was an issue preparing your file");
            }
        });

        // Help messages
        usage.addActionListener(e -> JOptionPane.showMessageDialog(frame, """
                The Receipt tab will show the current receipt and any saved receipts.
                The Edit tab allows you to to select a line to edit, delete or center the line or to add a new line.
                The Labelling tab allows you to track individual lines of the receipt.
                The Costs tab shows a breakdown your labels.
                Undo and Redo have a limit of Five changes."""));
        disclaimer.addActionListener(e -> JOptionPane.showMessageDialog(frame, """
                This application has very basic functionality and is designed for receipts, it will struggle to handle large documents
                If the non-current receipt is edited when selected from the receipts menu it must be saved for changes to be retained.
                Tesseract OCR is used for image transcription.
                Due to the nature of PDFs and images there will be mistakes with the translated receipts.
                Thank you for reading."""));

        optionsMenu.add(open);
        optionsMenu.add(save);
        optionsMenu.add(undo);
        optionsMenu.add(redo);

        helpMenu.add(usage);
        helpMenu.add(disclaimer);

        menu.add(optionsMenu);
        menu.add(helpMenu);

        return menu;
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