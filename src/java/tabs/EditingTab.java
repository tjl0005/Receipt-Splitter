package tabs;

import classes.CheckboxListRenderer;

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
    final JButton addButton = new JButton("Add");
    final JButton deleteButton = new JButton("Delete");

    public EditingTab(DefaultListModel<String> receiptModel) {
        setScrollPane(receiptModel); // This is the initial setup
        buttonsValid(receiptModel); // Buttons initially disabled

        addButton.addActionListener(e -> {
            receiptModel.add(currentSelection.get(0), editTextField.getText()); // Updating model with receipt copy
            setScrollPane(receiptModel); // Now updating scroll pane
        });

        editButton.addActionListener(e -> {
            receiptModel.set(currentSelection.get(0), editTextField.getText());
            setScrollPane(receiptModel);
        });

        deleteButton.addActionListener(e -> {
            for (int index : currentSelection) {
                receiptModel.remove(index);
            }
            setScrollPane(receiptModel);
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 490)); // Optimised for receipt

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setFocusable(false);

        this.add(displayPanel);
        this.add(editTextField);
        this.add(buttonPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    private void mouseSelection(MouseEvent event){
        @SuppressWarnings("unchecked") // No need to check cast
        JList<CheckboxListRenderer.CheckboxListItem> list = (JList<CheckboxListRenderer.CheckboxListItem>) event.getSource();

        // Get the selected item
        int listIndex = list.locationToIndex(event.getPoint());
        CheckboxListRenderer.CheckboxListItem item = list.getModel().getElementAt(listIndex);

        item.setSelected(!item.isSelected());
        list.repaint(list.getCellBounds(listIndex, listIndex));

        // Update tracking of current selections
        if (item.isSelected()) {
            currentSelection.add(listIndex);
        } else {
            currentSelection.remove((Integer) listIndex);
        }
    }

    public void setScrollPane(DefaultListModel<String> receiptModel){
        // Removing the old scrollPane and resetting the current selection
        displayPanel.remove(scrollPane);
        currentSelection = new ArrayList<>();

        // Set up the list
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(receiptModel);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                mouseSelection(event);
                buttonsValid(receiptModel);
            }
        });

        // Define new scrollPane
        scrollPane = new JScrollPane(list);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        // Refresh with updated display panel
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // Hide buttons until valid selection made
    private void buttonsValid(DefaultListModel<String> receipt) {
        editButton.setEnabled(currentSelection.size() == 1);
        addButton.setEnabled(currentSelection.size() == 1);
        deleteButton.setEnabled(currentSelection.size() > 0);

        if (currentSelection.size() == 1) {
            editTextField.setText(receipt.get(currentSelection.get(0)));
        } else {
            editTextField.setText("Selected Line");
        }
    }
}
