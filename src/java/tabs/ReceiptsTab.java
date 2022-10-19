package tabs;

import classes.Receipt;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ReceiptsTab extends JPanel {
    final DefaultListModel<String> jList = new DefaultListModel<>();
    final List<String> allReceipts;
    final String userID;

    JComboBox<String> selectionBox = new JComboBox<>();
    final JScrollPane scrollPane = new JScrollPane(new JList<>(jList));
    final JPanel displayPanel = new JPanel();

    public ReceiptsTab(String id, DefaultListModel<String> receipt) {

        userID = id;
        allReceipts = Receipt.getReceiptNames(userID);
        populateJList(receipt);

        if (allReceipts == null) {
            this.add(new JLabel("No saved receipts yet"));
            displayPanel.add(scrollPane);
        } else {
            // Initial State
            if (!allReceipts.contains("Current Receipt")) {
                // Update list of saved receipts with current receipt
                allReceipts.add(0, "Current Receipt");
            }

            setComboBox(receipt);
        }

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.setPreferredSize(new Dimension(380, 440));

        this.add(displayPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    public void setComboBox(DefaultListModel<String> receipt) {
        // Remove old versions
        displayPanel.remove(scrollPane);

        if (allReceipts != null) {
            displayPanel.remove(selectionBox);
            // Setup combobox
            selectionBox = new JComboBox<>(allReceipts.toArray(new String[0]));
            selectionBox.addActionListener(e -> {
                int selIndex = selectionBox.getSelectedIndex();
                jList.removeAllElements();
                // Not selecting current receipt
                if (selIndex != 0) {
                    String selectedReceipt = "Receipts/" + userID + "/" + allReceipts.get(selIndex);
                    jList.addAll(Receipt.get(selectedReceipt, false));
                    receipt.addAll(Receipt.get(selectedReceipt, false));
                } else {
                    populateJList(receipt);
                }
                this.revalidate();
            });
            displayPanel.add(selectionBox);
        }

        // Display first label contents by default
        jList.removeAllElements();
        populateJList(receipt);

        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void populateJList(DefaultListModel<String> receipt) {
        for (int i = 0; i < receipt.size(); i++) {
            jList.addElement(receipt.get(i));
        }
    }
}


