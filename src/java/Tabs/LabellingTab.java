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
    final JButton removeButton = new JButton("Remove Label");
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JPanel displayPanel = new JPanel(new GridLayout());

    public LabellingTab(JFrame frame, List<String> receipt, List<String> labels) {
        // Create check box list with current receipt
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {mouseSelection(event);
                labelButton.setEnabled(currentSelection.size() > 0);
                removeButton.setEnabled(currentSelection.size() > 0);}
        });
        JScrollPane scrollPane = new JScrollPane(list);

        labelButton.setEnabled(currentSelection.size() > 0);
        removeButton.setEnabled(currentSelection.size() > 0);

        labelButton.addActionListener(e -> {
            String newLabel = "<" + labelTextField.getText() + "> ";

            if (!labels.contains(newLabel)){
                labels.add(newLabel);
            }
            for (int index : currentSelection) {
                receipt.set(index, newLabel + receipt.get(index));
            }
            frame.dispose();
            new MainPage(receipt, labels, 2);
        });

        removeButton.addActionListener(e -> {
            boolean update = false;
            for (int index : currentSelection) {
                String line = receipt.get(index);
                if (line.contains("<") && line.contains(">")){
                    receipt.set(index, line.replaceAll("<.*>", ""));
                    update = true;
                }
            }
            if (update){
                frame.dispose();
                new MainPage(receipt, labels, 2);
            }
        });

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        displayPanel.add(scrollPane);
        displayPanel.setPreferredSize(new Dimension(350, 490)); // Optimised for receipts
        displayPanel.setBackground(Color.WHITE);

        buttonPanel.add(labelButton);
        buttonPanel.add(removeButton);
        buttonPanel.setFocusable(false);

        this.add(displayPanel);
        this.add(labelTextField);
        this.add(buttonPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
    }

    private void mouseSelection(MouseEvent event){
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

    }
}
