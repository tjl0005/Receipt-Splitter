package tabs;

import classes.CheckboxListRenderer;

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
        setScrollPane(receiptModel);

        labelButton.setEnabled(currentSelection.size() > 0);
        removeButton.setEnabled(currentSelection.size() > 0);

        labelButton.addActionListener(e -> {
            String newLabel = "<" + labelTextField.getText() + "> ";

            // Ensure label model does not get overwhelmed, needs updating
            if (!labelMap.containsKey(newLabel)){
                labelMap.put(newLabel, 0.00);
            }
            for (int index : currentSelection) {
                receiptModel.set(index, newLabel + receiptModel.get(index));
            }
            setScrollPane(receiptModel);
        });

        removeButton.addActionListener(e -> {
            for (int index : currentSelection) {
                String line = receiptModel.get(index);

                if (line.contains("<") && line.contains(">")){
                    String newLine = line.replaceAll("<.*>", "");
                    receiptModel.set(index, newLine);
                }
            }
            setScrollPane(receiptModel);
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

    public void setScrollPane(DefaultListModel<String> receiptModel){
        // Removing the old scrollPane and resetting the current selection
        displayPanel.remove(scrollPane);
        currentSelection = new ArrayList<>();

        // Set up the list
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.
                CheckboxListItem.generateList(receiptModel);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                mouseSelection(event);
                labelButton.setEnabled(currentSelection.size() > 0);
                removeButton.setEnabled(currentSelection.size() > 0);
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

    private void mouseSelection(MouseEvent event) {
        @SuppressWarnings("unchecked") // No need to check cast
        JList<CheckboxListRenderer.CheckboxListItem> list = (JList<CheckboxListRenderer.CheckboxListItem>) event.getSource();

        // Get the selected item
        int listIndex = list.locationToIndex(event.getPoint());
        CheckboxListRenderer.CheckboxListItem item = list.getModel().getElementAt(listIndex);

        item.setSelected(!item.isSelected());
        if (listIndex == 0) {
            currentSelection.clear();
            for (int i = 1; i < list.getModel().getSize() - 1; i++) {
                CheckboxListRenderer.CheckboxListItem current = list.getModel().getElementAt(i);
                current.setSelected(!current.isSelected());
                if (item.isSelected()) {
                    currentSelection.add(i - 1);
                }
            }
        } else {
            listIndex--;
            // Update tracking of current selections
            if (item.isSelected()) {
                currentSelection.add(listIndex);
            } else {
                currentSelection.remove((Integer) listIndex);
            }
        }
        list.repaint();
    }
}
