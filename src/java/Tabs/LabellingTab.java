package Tabs;

import Classes.CheckboxListRenderer;
import Pages.MainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LabellingTab extends JPanel {
    final List<Integer> currentSelection = new ArrayList<>(); // Store selected indexes of selected lines
    final JTextField labelTextField = new JTextField("My Label");
    final JButton labelButton = new JButton("Add Label");
    final JPanel displayPanel = new JPanel(new GridLayout());

    public LabellingTab(JFrame frame, List<String> receipt) {
        labelButton.addActionListener(e -> {
            for (int index : currentSelection) {
                receipt.set(index, labelTextField.getText() + " " + receipt.get(index));
                frame.dispose();
                new MainPage(receipt, 1);
            }
        });

        // Create check box list with current receipt
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                @SuppressWarnings("unchecked")
                JList<CheckboxListRenderer.CheckboxListItem> list = (JList<CheckboxListRenderer.CheckboxListItem>) event.getSource();

                // Get the selected line
                int listIndex = list.locationToIndex(event.getPoint());
                CheckboxListRenderer.CheckboxListItem item = list.getModel().getElementAt(listIndex);

                // Update to reflect either selection or deselection
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(listIndex, listIndex));

                // Update tracking of current selections
                if (item.isSelected()) {
                    currentSelection.add(listIndex);
                } else {
                    currentSelection.remove((Integer) listIndex);
                }
                // Only enabled if a selection is made
                labelButton.setEnabled(currentSelection.size() > 0);
            }
        });

        displayPanel.add(new JScrollPane(list));
        displayPanel.setPreferredSize(new Dimension(350, 400)); // Optimised for receipts

        // Only enabled if a selection is made
        labelButton.setEnabled(currentSelection.size() > 0);
        labelButton.setFocusPainted(false);

        this.add(displayPanel);
        this.add(labelTextField);
        this.add(labelButton);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }
}
