package tabs;

import receipt.Prepare;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * A tab enabling a user to browse their currently saved receipts as well as the currently edited receipt
 */
public class ReceiptsTab extends JPanel {
    private final DefaultListModel<String> receiptDisplay = new DefaultListModel<>();
    private final List<String> allReceipts;

    private JComboBox<String> receiptSelection = new JComboBox<>();
    private final JScrollPane scrollPane = new JScrollPane(new JList<>(receiptDisplay));
    private final JPanel displayPanel = new JPanel();

    /**
     * Generate the receipts tab to be displayed
     * @param receipt the receipt list model
     */
    public ReceiptsTab(DefaultListModel<String> receipt) {
        allReceipts = Prepare.getNames();
        receiptDisplay.addAll(populateList(receipt));

        if (allReceipts == null) {
            displayPanel.add(scrollPane);
        } else { // Initial State
            if (!allReceipts.contains("Current Receipt")) {
                // Update list of saved receipts with current receipt
                allReceipts.add(0, "Current Receipt");
            }
            setupSelection(receipt);
        }

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.setPreferredSize(new Dimension(385, 430));

        this.add(displayPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    /**
     * Set up a combo box which the user can use to change the currently displayed receipt
     * @param receipt the receipt list model
     */
    public void setupSelection(DefaultListModel<String> receipt) {
        displayPanel.remove(scrollPane); // Removing old version

        if (allReceipts != null) {
            displayPanel.remove(receiptSelection);
            // Setup combobox with receipt names
            receiptSelection = new JComboBox<>(allReceipts.toArray(new String[0]));
            ((JLabel)receiptSelection.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center contents
            receiptSelection.addActionListener(e -> {
                int selIndex = receiptSelection.getSelectedIndex();
                receiptDisplay.removeAllElements();

                if (selIndex != 0) { // Not selecting current receipt
                    String selectedReceipt = System.getProperty("user.home") + "/Documents/OpenBook/" + allReceipts.get(selIndex);
                    List<String> selectedContents = Prepare.getReceipt(selectedReceipt);
                    // Update the labels and receipt to be used
                    receiptDisplay.addAll(selectedContents); // Update current display
                } else {
                    receiptDisplay.addAll(populateList(receipt)); // Use contents from receipt model
                }
                revalidate();
            });
            displayPanel.add(receiptSelection);
        }

        // Display first label contents by default
        receiptDisplay.removeAllElements();
        receiptDisplay.addAll(populateList(receipt));

        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    /**
     * Used to provide a manner of displaying a receipt
     * @param receipt the receipt model
     */
    private List<String> populateList(DefaultListModel<String> receipt) {
        List<String> receiptList = new ArrayList<>();
        // Add current receipt model contents to display list
        for (int i = 0; i < receipt.size(); i++) {
            receiptList.add(receipt.get(i));
        }
        return receiptList;
    }
}


