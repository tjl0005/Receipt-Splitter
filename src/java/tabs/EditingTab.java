package tabs;

import receipt.Checklist;
import receipt.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class EditingTab extends JPanel {
    List<Integer> currentSelection = new ArrayList<>();

    JScrollPane scrollPane = new JScrollPane();
    final JPanel displayPanel = new JPanel(new BorderLayout());
    final JPanel buttonPanel = new JPanel(new FlowLayout());

    final JTextField editTextField = new JTextField();
    final JButton editButton = new JButton("Edit");
    final JButton centerButton = new JButton("Center");
    final JButton addButton = new JButton("Add");
    final JButton deleteButton = new JButton("Delete");


    public EditingTab(DefaultListModel<String> receipt) {
        displayReceipt(receipt); // Display current version of receipt
        buttonsValid(receipt); // Buttons initially disabled

        addButton.addActionListener(e -> {
            Track.recordChange(receipt);
            receipt.add(currentSelection.get(0), editTextField.getText()); // Add new line before selected index
            displayReceipt(receipt);
        });

        centerButton.addActionListener(e ->{
            Track.recordChange(receipt);
            for (int index : currentSelection) { // Update for all currently selected
                String line = String.format("%52s", receipt.get(index)); // 52 is string width, can be a bit iffy
                receipt.set(index, line);
            }
            displayReceipt(receipt);
        });

        editButton.addActionListener(e -> {
            Track.recordChange(receipt);
            receipt.set(currentSelection.get(0), editTextField.getText()); // Update old value
            displayReceipt(receipt);
        });

        deleteButton.addActionListener(e -> {
            Track.recordChange(receipt);
            for (int i = 0; i < currentSelection.size(); i++){
                receipt.remove(currentSelection.get(i) - i); // Using i as a counter to correct the old index
            }
            displayReceipt(receipt);
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 490)); // Optimised for receipt

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(centerButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setFocusable(false);

        this.add(displayPanel);
        this.add(editTextField);
        this.add(buttonPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    public void displayReceipt(DefaultListModel<String> receipt){
        displayPanel.remove(scrollPane); // Remove old version of scroll pane
        // Set up the list
        JList<Checklist.CheckboxListItem> list = Checklist.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                Checklist.getSelectedItems(event, currentSelection);
                buttonsValid(receipt);
            }
        });

        scrollPane = Checklist.buildScrollPane(list);
        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // Hide buttons until valid selection made
    private void buttonsValid(DefaultListModel<String> receipt) {
        editButton.setEnabled(currentSelection.size() == 1 && currentSelection.get(0) != 0);
        addButton.setEnabled(currentSelection.size() == 1 && currentSelection.get(0) != 0);
        deleteButton.setEnabled(currentSelection.size() > 0);
        centerButton.setEnabled(currentSelection.size() > 0);

        if (currentSelection.size() == 1) {
            editTextField.setText(receipt.get(currentSelection.get(0)));
        } else {
            editTextField.setText("Selected Line");
        }
    }
}
