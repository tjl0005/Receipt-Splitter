package tabs;

import receipt.Checklist;
import receipt.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * A tab providing the ability to edit the contents of a receipt
 */
public class EditingTab extends JPanel {
    private final List<Integer> currentSelection = new ArrayList<>();

    private JScrollPane scrollPane = new JScrollPane();
    private final JPanel displayPanel = new JPanel(new BorderLayout());
    private final JTextField editTextField = new JTextField();
    private final JButton editButton = new JButton("Edit");
    private final JButton centerButton = new JButton("Center");
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");


    /**
     * Generate the editing tab to be displayed
     * @param receipt the current receipt list model
     */
    public EditingTab(DefaultListModel<String> receipt) {
        displayReceipt(receipt); // Display current version of receipt
        buttonsValid(receipt); // Buttons initially disabled
        editTextField.setHorizontalAlignment(JTextField.CENTER); // Center text field

        addButton.addActionListener(e -> {
            Track.recordChange(receipt);
            receipt.add(currentSelection.get(0), editTextField.getText()); // Add new line before selected index
            displayReceipt(receipt);
        });

        editButton.addActionListener(e -> {
            Track.recordChange(receipt);
            String editedLine = editTextField.getText();
            if (editedLine.length() < 125) {
                receipt.set(currentSelection.get(0), editedLine); // Update old value
                displayReceipt(receipt);
            }
            else{
                JOptionPane.showMessageDialog(this, "Edited line to long");
            }
        });

        deleteButton.addActionListener(e -> {
            Track.recordChange(receipt);
            for (int i = 0; i < currentSelection.size(); i++){
                receipt.remove(currentSelection.get(i) - i); // Using i as a counter to correct the old index
            }
            displayReceipt(receipt);
        });

        centerButton.addActionListener(e ->{
            Track.recordChange(receipt);
            System.out.println(currentSelection);
            for (int index : currentSelection) { // Update for all currently selected
                String line = String.format("%52s", receipt.get(index)); // 52 is string width, can be a bit iffy
                receipt.set(index, line);
            }
            displayReceipt(receipt);
        });

        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(350, 490)); // Optimised for receipt

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(centerButton);
        buttonPanel.setFocusable(false);

        add(displayPanel);
        add(editTextField);
        add(buttonPanel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setVisible(true);
    }

    /**
     * Update the currently displayed receipt
     * @param receipt the current receipt model to be displayed
     */
    public void displayReceipt(DefaultListModel<String> receipt){
        displayPanel.remove(scrollPane); // Remove old version of scroll pane
        // Set up the list
        JList<Checklist.CheckboxListItem> list = Checklist.CheckboxListItem.generateList(receipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Checklist.getSelectedItems(e, currentSelection);
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
        editButton.setEnabled(currentSelection.size() == 1);
        addButton.setEnabled(currentSelection.size() == 1);
        deleteButton.setEnabled(!currentSelection.isEmpty());
        centerButton.setEnabled(!currentSelection.isEmpty());

        if (currentSelection.size() == 1) {
            editTextField.setText(receipt.get(currentSelection.get(0)));
        } else {
            editTextField.setText("Selected Line");
        }
    }
}
