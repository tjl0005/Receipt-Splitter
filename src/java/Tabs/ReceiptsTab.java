package Tabs;

import Classes.Receipt;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReceiptsTab extends JPanel {
    public ReceiptsTab(List<String> currentReceipt, String userID) {

        List<String> allReceipts = Receipt.getReceiptNames(userID);
        DefaultListModel<String> jList = new DefaultListModel<>();

        if (allReceipts == null){
            this.add(new JLabel("No saved receipts yet"));
            jList.addAll(currentReceipt);
        }
        else{
            // Initial State
            if (!allReceipts.contains("Current Receipt")) {
                // Update list of saved receipts with current receipt
                allReceipts.add(0, "Current Receipt");
                jList.addAll(currentReceipt);
            }

            final JComboBox<String> selectionBox = new JComboBox<>(allReceipts.toArray(new String[0]));
            selectionBox.addActionListener(e -> {
                int selIndex = selectionBox.getSelectedIndex();
                jList.removeAllElements();
                if (selIndex != 0){
                    String selectedReceipt = "Receipts/" + userID + "/" + allReceipts.get(selIndex);
                    jList.addAll(Receipt.getReceiptFile(selectedReceipt));
                } else{
                    jList.addAll(currentReceipt);
                }
                this.revalidate();
            });


            this.add(selectionBox);
        }

        JScrollPane scrollPane = new JScrollPane(new JList<>(jList));
        scrollPane.setPreferredSize(new Dimension(380, 445));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        this.add(scrollPane);

        this.setVisible(true);
    }
}


