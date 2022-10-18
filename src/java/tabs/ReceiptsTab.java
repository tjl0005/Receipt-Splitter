package tabs;

import classes.Receipt;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Currently cannot get saved PDF receipts, this will be implemented

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

        if (allReceipts == null){
            this.add(new JLabel("No saved receipts yet"));
            for (int i=0;i < receipt.size();i++)
            {
                jList.addElement(receipt.get(i));
            }
            displayPanel.add(scrollPane);
        }
        else{
            // Initial State
            if (!allReceipts.contains("Current Receipt")) {
                // Update list of saved receipts with current receipt
                allReceipts.add(0, "Current Receipt");
                for (int i=0;i < receipt.size();i++)
                {
                    jList.addElement(receipt.get(i));
                }
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

    public void setComboBox(DefaultListModel<String> receipt){
        // Remove old versions
        displayPanel.remove(scrollPane);

        if (allReceipts != null) {
            displayPanel.remove(selectionBox);

            // Setup combobox
            selectionBox = new JComboBox<>(allReceipts.toArray(new String[0]));
            selectionBox.addActionListener(e -> {
                int selIndex = selectionBox.getSelectedIndex();
                jList.removeAllElements();
                receipt.removeAllElements();
                // Not selecting current receipt
                if (selIndex != 0) {
                    String selectedReceipt = "Receipts/" + userID + "/" + allReceipts.get(selIndex);
                    jList.addAll(Receipt.getReceiptTxtFile(selectedReceipt));
                    receipt.addAll(Receipt.getReceiptTxtFile(selectedReceipt));
                } else {
                    // Need a backup copy of the current receipt
                    for (int i = 0; i < receipt.size(); i++) {
                        jList.addElement(receipt.get(0));
                        receipt.addElement(receipt.get(0));
                    }
                }
                this.revalidate();
            });
            displayPanel.add(selectionBox);
        }

    // Display first label contents by default
    jList.removeAllElements();
        for (int i=0;i < receipt.size();i++)
        {
            jList.addElement(receipt.get(i));
        }
    // Refresh with updated display panel
    displayPanel.add(scrollPane);
    displayPanel.revalidate();
    displayPanel.repaint();
}
}


