package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CostsTab extends JPanel {
    final DefaultListModel<String> jList = new DefaultListModel<>();

    JComboBox<String> selectionBox = new JComboBox<>();

    final JScrollPane scrollPane = new JScrollPane(new JList<>(jList));
    JScrollPane tablePane = new JScrollPane();
    final JPanel displayPanel = new JPanel();
    final JPanel tablePanel = new JPanel();

    public CostsTab(Map<String, Double> labelMap, DefaultListModel<String> receiptModel) {
        scrollPane.setPreferredSize(new Dimension(380, 300));
        scrollPane.setBackground(Color.WHITE);
        tablePanel.setBackground(Color.WHITE);

        setComboBox(labelMap, receiptModel);
        setTable(labelMap, receiptModel);

        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);

        this.add(displayPanel);
        this.add(new JLabel("Label Breakdown"));
        this.add(tablePanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.setVisible(true);
    }

    // Get all lines that have the given label
    public List<String> getLabelledLines(String label, DefaultListModel<String> receiptModel){
        List<String> labelledLines = new ArrayList<>();

        for (int i=0;i < receiptModel.size();i++) {
            String line = receiptModel.get(i);
            if (line.contains(label)) {
                labelledLines.add(line);
            }
        }
        labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));  // Remove label prefix from lines
        return labelledLines;
    }

    public void setComboBox(Map<String, Double> labelMap, DefaultListModel<String> receiptModel){
        // Remove old versions
        displayPanel.remove(selectionBox);
        displayPanel.remove(scrollPane);

        List<String> labels = labelMap.keySet().stream().toList();

        // Setup combobox
        selectionBox = new JComboBox<>(labels.toArray(new String[0]));
        selectionBox.addActionListener(e -> {
            int selIndex = selectionBox.getSelectedIndex();
            List<String> labelledLines = getLabelledLines(labels.get(selIndex), receiptModel);

            jList.removeAllElements();
            jList.addAll(labelledLines);

            this.revalidate();
        });

        // Display first label contents by default
        if (!labels.isEmpty()){
            List<String> defaultLines = getLabelledLines(labels.get(0), receiptModel);
            jList.removeAllElements();
            jList.addAll(defaultLines);
        }

        // Refresh with updated display panel
        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void setTable(Map<String, Double> labelMap, DefaultListModel<String> receiptModel){
        tablePanel.remove(tablePane);
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Label"); // Label reference
        model.addColumn("No."); // Number of lines labelled with this reference
        model.addColumn("Cost"); // Total cost of this label, user decided

        List<String> labels = labelMap.keySet().stream().toList();
        int noRows = labelMap.size();

        for (int i=0; i < noRows; i++) {
            String currentLabel = labels.get(i);
            int labelCount = getLabelledLines(currentLabel, receiptModel).size();

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


