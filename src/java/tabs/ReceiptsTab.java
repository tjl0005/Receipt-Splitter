package tabs;

import receipt.Prepare;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ReceiptsTab extends JPanel {
    final DefaultListModel<String> receiptDisplay = new DefaultListModel<>();
    final List<String> allReceipts;

    JComboBox<String> receiptSelection = new JComboBox<>();
    final JScrollPane scrollPane = new JScrollPane(new JList<>(receiptDisplay));
    final JPanel displayPanel = new JPanel();

    public ReceiptsTab(DefaultListModel<String> receipt) {
        allReceipts = Prepare.getNames();
        populateDisplayList(receipt);

        if (allReceipts == null) {
            this.add(new JLabel("No saved receipts yet"));
            displayPanel.add(scrollPane);
        } else {
            // Initial State
            if (!allReceipts.contains("Current Receipt")) {
                // Update list of saved receipts with current receipt
                allReceipts.add(0, "Current Receipt");
            }

            setupSelection(receipt);
        }

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.setPreferredSize(new Dimension(380, 440));

        this.add(displayPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    public void setupSelection(DefaultListModel<String> receipt) {
        displayPanel.remove(scrollPane); // Removing old version

        if (allReceipts != null) {
            displayPanel.remove(receiptSelection);
            // Setup combobox with receipt names
            receiptSelection = new JComboBox<>(allReceipts.toArray(new String[0]));
            receiptSelection.addActionListener(e -> {
                int selIndex = receiptSelection.getSelectedIndex();
                receiptDisplay.removeAllElements();
                // Not selecting current receipt
                if (selIndex != 0) {
                    String selectedReceipt = "Receipts/OpenBook/" + allReceipts.get(selIndex);
                    receiptDisplay.addAll(Prepare.get(selectedReceipt)); // Update current display
                } else {
                    populateDisplayList(receipt); // Just displaying current receipt
                }
                this.revalidate();
            });
            displayPanel.add(receiptSelection);
        }

        // Display first label contents by default
        receiptDisplay.removeAllElements();
        populateDisplayList(receipt);

        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void populateDisplayList(DefaultListModel<String> receipt) {
        // Add current receipt model contents to display list
        for (int i = 0; i < receipt.size(); i++) {
            receiptDisplay.addElement(receipt.get(i));
        }
    }
}


