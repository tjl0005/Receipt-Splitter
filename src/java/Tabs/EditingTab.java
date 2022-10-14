package Tabs;

import Classes.CheckboxListRenderer;
import Pages.MainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EditingTab extends JPanel {
    final List<Integer> currentSelection = new ArrayList<>();
    final JTextField editTextField = new JTextField();
    final JPanel displayPanel = new JPanel(new BorderLayout());
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JButton editButton = new JButton("Edit");
    final JButton addButton = new JButton("Add");
    final JButton deleteButton = new JButton("Delete");

    public EditingTab(JFrame frame, List<String> receipt) {
        buttonsValid(receipt); // Disable buttons if no selections made

        addButton.addActionListener(e -> {
            receipt.add(currentSelection.get(0), editTextField.getText());
            resetTabs(frame, receipt);
        });

        editButton.addActionListener(e -> {
            int editIndex = currentSelection.get(0);
            receipt.set(editIndex, editTextField.getText());
            resetTabs(frame, receipt);
        });

        deleteButton.addActionListener(e -> {
            for (int index : currentSelection) {
                receipt.remove(index);
            }
            resetTabs(frame, receipt);
        });

        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                @SuppressWarnings("unchecked")
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

                buttonsValid(receipt);
            }
        });

        displayPanel.add(new JScrollPane(list));
        displayPanel.setPreferredSize(new Dimension(350, 400)); // Optimised for receipt

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

    private void resetTabs(JFrame frame, List<String> receipt) {
        frame.dispose();
        new MainPage(receipt, 0);
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
