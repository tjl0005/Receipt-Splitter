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


public class LabellingTab extends JPanel {
    List<Integer> currentSelection = new ArrayList<>(); // Store selected indexes of selected lines
    JScrollPane scrollPane = new JScrollPane();
    final JTextField labelTextField = new JTextField("My Label");
    final JButton labelButton = new JButton("Add Label");
    final JButton removeButton = new JButton("Remove Label");
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JPanel displayPanel = new JPanel(new GridLayout());


    public LabellingTab(Map<String, Double> labelMap, DefaultListModel<String> receiptModel) {
        displayReceipt(receiptModel);

        labelButton.setEnabled(false);
        removeButton.setEnabled(false);

        labelButton.addActionListener(e -> {
            Track.recordChange(receiptModel);
            String newLabel = "<" + labelTextField.getText() + "> "; // Add prefix and suffix to label
            // Ensure label model does not get overwhelmed, needs updating
            if (!labelMap.containsKey(newLabel)){
                labelMap.put(newLabel, 0.00); // Default cost is 0.00
            }
            for (int index : currentSelection) {
                receiptModel.set(index, newLabel + receiptModel.get(index));
            }
            displayReceipt(receiptModel);
        });

        removeButton.addActionListener(e -> {
            Track.recordChange(receiptModel);
            for (int index : currentSelection) {
                String line = receiptModel.get(index);
                if (line.contains("<") && line.contains(">")){ // Using prefix and suffix to identify labels
                    String newLine = line.replaceAll("<.*>", "");
                    receiptModel.set(index, newLine);
                }
            }
            displayReceipt(receiptModel);
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 600)); // Optimised for receipt

        buttonPanel.add(labelButton);
        buttonPanel.add(removeButton);
        buttonPanel.setFocusable(false);

        this.add(displayPanel);
        this.add(labelTextField);
        this.add(buttonPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    public void displayReceipt(DefaultListModel<String> receiptModel){
        // Removing the old scrollPane
        displayPanel.remove(scrollPane);
        // Set up the list
        JList<Checklist.CheckboxListItem> list = Checklist.CheckboxListItem.generateList(receiptModel);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                Checklist.getSelectedItems(event, currentSelection);
                labelButton.setEnabled(currentSelection.size() > 0);
                removeButton.setEnabled(currentSelection.size() > 0);
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