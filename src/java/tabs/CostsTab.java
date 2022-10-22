package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A tab providing a breakdown of the current labels added to the receipt and breakdown of basic statistics relevant to
 * the labels.
 */
public class CostsTab extends JPanel {
    final DefaultListModel<String> displayList = new DefaultListModel<>();

    JComboBox<String> selectionBox = new JComboBox<>();
    JScrollPane tablePane = new JScrollPane();
    final JScrollPane scrollPane = new JScrollPane(new JList<>(displayList));
    final JPanel displayPanel = new JPanel();
    final JPanel tablePanel = new JPanel();

    /**
     * Generate the tab to be displayed
     * @param labels a map containing the current labels and their respective total costs
     * @param receipt a list model containing the receipt
     */
    public CostsTab(Map<String, Double> labels, DefaultListModel<String> receipt) {
        scrollPane.setPreferredSize(new Dimension(380, 300));
        scrollPane.setBackground(Color.WHITE);
        tablePanel.setBackground(Color.WHITE);

        setupLabelSelection(labels, receipt);
        setupLabelBreakdown(labels, receipt);

        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);

        add(displayPanel);
        add(tablePanel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setVisible(true);
    }

    /**
     * Given a label and a receipt get all matching lines of the receipt containing the label
     * @param label the label to get the relevant lines for
     * @param receipt list model of the receipt
     * @return all the lines with the given label
     */
    // Get all lines that have the given label
    public static List<String> getLabelledLines(String label, DefaultListModel<String> receipt){
        List<String> labelledLines = new ArrayList<>();

        for (int i=0;i < receipt.size();i++) {
            String line = receipt.get(i);
            if (line.contains(label)) {
                labelledLines.add(line);
            }
        }
        labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));  // Remove label prefix from lines
        return labelledLines;
    }

    /**
     * Set up the combo box that can be used to switch the view of the current labels
     * @param labels the current state of the labels
     * @param receipt the list model representing the receipt
     */
    public void setupLabelSelection(Map<String, Double> labels, DefaultListModel<String> receipt){
        // Remove old versions
        displayPanel.remove(selectionBox);
        displayPanel.remove(scrollPane);

        List<String> labelText = labels.keySet().stream().toList();

        // Setup combobox
        selectionBox = new JComboBox<>(labelText.toArray(new String[0]));
        selectionBox.addActionListener(e -> {
            int index = selectionBox.getSelectedIndex();
            List<String> labelledLines = getLabelledLines(labelText.get(index), receipt);

            displayList.removeAllElements();
            displayList.addAll(labelledLines);

            this.revalidate();
        });

        // Display first label contents by default
        if (!labelText.isEmpty()){
            List<String> defaultLines = getLabelledLines(labelText.get(0), receipt);
            displayList.removeAllElements();
            displayList.addAll(defaultLines);
        }

        // Refresh with updated display panel
        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    /**
     * Set up the table to display the label details, containing the label names, amount of labels and their total cost
     * @param labelMap the current state of the labels
     * @param receipt the list model representing the receipt
     */
    public void setupLabelBreakdown(Map<String, Double> labelMap, DefaultListModel<String> receipt){
        tablePanel.remove(tablePane);
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Label"); // Label reference
        model.addColumn("No."); // Number of lines labelled with this reference
        model.addColumn("Cost"); // Total cost of this label, user decided

        List<String> labels = labelMap.keySet().stream().toList();
        int noRows = labelMap.size();

        for (int i=0; i < noRows; i++) {
            String currentLabel = labels.get(i);
            int labelCount = getLabelledLines(currentLabel, receipt).size();

            String clearLabel = currentLabel.substring(1, currentLabel.length() - 2); // Removing < >
            model.addRow(new Object[]{clearLabel, labelCount, labelMap.get(currentLabel)});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);

        model.addTableModelListener(e -> {
            if(table.isEditing() && table.getSelectedColumn() == 2){
                String label = labels.get(table.getSelectedRow());
                Double newCost = Double.parseDouble(table.getValueAt(table.getSelectedRow(), 2).toString());
                labelMap.put(label, newCost);
            }
            else{
                JOptionPane.showMessageDialog(this, "Only edits to total cost will be saved");
            }
        });

        tablePane = new JScrollPane(table);
        tablePane.setPreferredSize(new Dimension(380, 150));
        tablePanel.add(tablePane);
    }
}