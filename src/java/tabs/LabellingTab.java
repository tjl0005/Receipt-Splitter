package tabs;

import classes.CheckboxListRenderer;
import pages.MainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class LabellingTab extends JPanel {
    List<Integer> currentSelection = new ArrayList<>(); // Store selected indexes of selected lines
    final JTextField labelTextField = new JTextField("My Label");
    final JButton labelButton = new JButton("Add Label");
    final JButton removeButton = new JButton("Remove Label");
    final JPanel buttonPanel = new JPanel(new FlowLayout());
    final JPanel displayPanel = new JPanel(new GridLayout());
    JScrollPane scrollPane = new JScrollPane();

    public LabellingTab( MainPage.DefaultListModel labelModel, MainPage.DefaultListModel receiptModel) {
        List<String> receipt = receiptModel.getValue();
        List<String> labels = labelModel.getValue();

        setScrollPane(receipt);

        labelButton.setEnabled(currentSelection.size() > 0);
        removeButton.setEnabled(currentSelection.size() > 0);

        labelButton.addActionListener(e -> {
            String newLabel = "<" + labelTextField.getText() + "> ";

            // Ensure label model does not get overwhelmed, needs updating
            if (!labels.contains(newLabel)){
                labels.add(newLabel);
                labelModel.setValue(labels);
            }
            for (int index : currentSelection) {
                receipt.set(index, newLabel + receipt.get(index));
            }
            receiptModel.setValue(receipt);
            setScrollPane(receiptModel.getValue());
        });

        removeButton.addActionListener(e -> {
            // TODO: Need to track amount of labels
            for (int index : currentSelection) {
                String line = receipt.get(index);
                if (line.contains("<") && line.contains(">")){
                    receipt.set(index, line.replaceAll("<.*>", ""));
                }
            }
            receiptModel.setValue(receipt);
            setScrollPane(receiptModel.getValue());
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 490)); // Optimised for receipt

        buttonPanel.add(labelButton);
        buttonPanel.add(removeButton);
        buttonPanel.setFocusable(false);

        this.add(displayPanel);
        this.add(labelTextField);
        this.add(buttonPanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(true);
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
}
