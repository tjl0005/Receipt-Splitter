package tabs;

import receipt.Checklist;
import receipt.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A tab providing the ability to add labels to given lines of the receipts
 */
public class LabellingTab extends JPanel {
    List<Integer> currentSelection = new ArrayList<>(); // Store selected indexes of selected lines
    JScrollPane scrollPane = new JScrollPane();
    final JTextField labelTextField = new JTextField("My Label");
    final JButton labelButton = new JButton("Add Label");
    final JButton removeButton = new JButton("Remove Label");
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JPanel displayPanel = new JPanel(new GridLayout());


    /**
     * Generate the tab to be used for adding/removing labels from a receipt
     * @param labels a map containing the current labels and their respective total costs
     * @param receipt a list model containing the receipt
     */
    public LabellingTab(Map<String, Double> labels, DefaultListModel<String> receipt) {
        displayReceipt(receipt);

        labelButton.setEnabled(false);
        removeButton.setEnabled(false);

        labelButton.addActionListener(e -> {
            Track.recordChange(receipt);
            String newLabel = "<" + labelTextField.getText() + "> "; // Add prefix and suffix to label
            // Ensure label model does not get overwhelmed, needs updating
            if (!labels.containsKey(newLabel)){
                labels.put(newLabel, 0.00); // Default cost is 0.00
            }
            for (int index : currentSelection) {
                receipt.set(index, newLabel + receipt.get(index));
            }
            displayReceipt(receipt);
        });

        removeButton.addActionListener(e -> {
            Track.recordChange(receipt);
            for (int index : currentSelection) {
                String line = receipt.get(index);
                if (line.contains("<") && line.contains(">")){ // Using prefix and suffix to identify labels
                    String newLine = line.replaceAll("<.*>", "");
                    receipt.set(index, newLine);
                }
            }
            displayReceipt(receipt);
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 600)); // Optimised for receipt

        buttonPanel.add(labelButton);
        buttonPanel.add(removeButton);
        buttonPanel.setFocusable(false);

        add(displayPanel);
        add(labelTextField);
        add(buttonPanel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setVisible(true);
    }

    /**
     * Update the currently displayed receipt
     * @param receiptModel the current receipt as a list model
     */
    public void displayReceipt(DefaultListModel<String> receiptModel){
        // Removing the old scrollPane
        displayPanel.remove(scrollPane);
        // Set up the list
        JList<Checklist.CheckboxListItem> list = Checklist.CheckboxListItem.generateList(receiptModel);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Checklist.getSelectedItems(e, currentSelection);
                labelButton.setEnabled(!currentSelection.isEmpty());
                removeButton.setEnabled(!currentSelection.isEmpty());
            }
        });
        // Refresh scrollPane
        scrollPane = Checklist.buildScrollPane(list);
        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }
}