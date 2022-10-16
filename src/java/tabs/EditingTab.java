package tabs;

import classes.CheckboxListRenderer;
import pages.MainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class EditingTab extends JPanel {
    List<Integer> currentSelection = new ArrayList<>();
    final JTextField editTextField = new JTextField();
    final JPanel displayPanel = new JPanel(new BorderLayout());
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JButton editButton = new JButton("Edit");
    final JButton addButton = new JButton("Add");
    final JButton deleteButton = new JButton("Delete");
    JScrollPane scrollPane = new JScrollPane();

    public EditingTab(MainPage.DefaultListModel receiptModel) {
        List<String> receipt = receiptModel.getValue(); // Copying to make editing easier (need to improve this)
        setScrollPane(receiptModel.getValue()); // This is the initial setup
        buttonsValid(receipt); // Buttons initially disabled

        addButton.addActionListener(e -> {
            receipt.add(currentSelection.get(0), editTextField.getText()); // Adding value to receipt copy
            receiptModel.setValue(receipt); // Updating model with receipt copy
            setScrollPane(receiptModel.getValue()); // Now updating scroll pane
        });

        editButton.addActionListener(e -> {
            receipt.set(currentSelection.get(0), editTextField.getText());
            receiptModel.setValue(receipt);
            setScrollPane(receiptModel.getValue());
        });

        deleteButton.addActionListener(e -> {
            for (int index : currentSelection) {
                receipt.remove(index);
            }
            receiptModel.setValue(receipt);
            setScrollPane(receiptModel.getValue());
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

    public void setScrollPane(List<String> receipt){
        // Removing the old scrollPane and resetting the current selection
        displayPanel.remove(scrollPane);
        currentSelection = new ArrayList<>();

        // Set up the list
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                mouseSelection(event);
                buttonsValid(receipt);
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
    private void buttonsValid(List<String> receipt) {
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
